param(
    [string]$BaseJar = "tektopia-1.1.0.jar",
    [string]$Version = "1.1.1",
    [string]$OutJar = "out/tektopia-1.1.1.jar"
)

$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $false

function Replace-AsciiInBinary {
    param(
        [string]$Path,
        [string]$Old,
        [string]$New
    )

    if ($Old.Length -ne $New.Length) {
        throw "Binary replacement requires equal-length strings."
    }

    [byte[]]$bytes = [System.IO.File]::ReadAllBytes($Path)
    [byte[]]$oldBytes = [System.Text.Encoding]::ASCII.GetBytes($Old)
    [byte[]]$newBytes = [System.Text.Encoding]::ASCII.GetBytes($New)
    $count = 0

    for ($i = 0; $i -le $bytes.Length - $oldBytes.Length; $i++) {
        $match = $true
        for ($j = 0; $j -lt $oldBytes.Length; $j++) {
            if ($bytes[$i + $j] -ne $oldBytes[$j]) {
                $match = $false
                break
            }
        }
        if ($match) {
            for ($j = 0; $j -lt $newBytes.Length; $j++) {
                $bytes[$i + $j] = $newBytes[$j]
            }
            $count++
            $i += $oldBytes.Length - 1
        }
    }

    [System.IO.File]::WriteAllBytes($Path, $bytes)
    return $count
}

if (-not (Test-Path $BaseJar)) {
    throw "Base jar not found: $BaseJar"
}

Add-Type -AssemblyName System.IO.Compression.FileSystem

$tmpRoot = "_tmp_release_1_1_1"
if (Test-Path $tmpRoot) {
    Remove-Item -Recurse -Force $tmpRoot
}
New-Item -ItemType Directory -Path $tmpRoot | Out-Null
New-Item -ItemType Directory -Path "$tmpRoot\classes" | Out-Null
New-Item -ItemType Directory -Path "$tmpRoot\jar" | Out-Null

$cp = @(
    $BaseJar,
    "tooling/libs/forge-1.12.2-14.23.5.2860-universal.jar",
    "tooling/libs/minecraft-client-1.12.2-srg.jar",
    "tooling/libs/minecraft-server-1.12.2-srg.jar",
    "tooling/libs/CraftStudioAPI-universal-1.0.1.95-mc1.12-alpha.jar"
) -join ";"

$javaSources = @(
    "decompiled/net/tangotek/tektopia/entities/EntityGuard.java",
    "decompiled/net/tangotek/tektopia/entities/EntityBlacksmith.java"
)

$javacErr = "$tmpRoot\\javac-stderr.log"
$javacOut = "$tmpRoot\\javac-stdout.log"
$javacHelp = & javac -help 2>&1
$bytecodeArgs = @()
if ($javacHelp -match "--release") {
    $bytecodeArgs = @("--release", "8")
} else {
    $bytecodeArgs = @("-source", "1.8", "-target", "1.8")
}
$javacArgs = $bytecodeArgs + @("-cp", $cp, "-d", "$tmpRoot\\classes") + $javaSources
$javacProc = Start-Process -FilePath "javac" -ArgumentList $javacArgs -NoNewWindow -PassThru -Wait -RedirectStandardError $javacErr -RedirectStandardOutput $javacOut
if ($javacProc.ExitCode -ne 0) {
    if (Test-Path $javacOut) {
        Get-Content $javacOut
    }
    if (Test-Path $javacErr) {
        Get-Content $javacErr
    }
    throw "javac failed while compiling patched classes."
}

[System.IO.Compression.ZipFile]::ExtractToDirectory((Resolve-Path $BaseJar).Path, "$tmpRoot\jar")

$entityOut = "$tmpRoot\jar\net\tangotek\tektopia\entities"
Copy-Item "$tmpRoot\classes\net\tangotek\tektopia\entities\EntityGuard*.class" $entityOut -Force
Copy-Item "$tmpRoot\classes\net\tangotek\tektopia\entities\EntityBlacksmith*.class" $entityOut -Force

$mcmodPath = "$tmpRoot\jar\mcmod.info"
$mcmod = Get-Content -Raw $mcmodPath
$mcmod = [regex]::Replace(
    $mcmod,
    '("version"\s*:\s*")[^"]+(")',
    { param($m) $m.Groups[1].Value + $Version + $m.Groups[2].Value },
    1
)
Set-Content -Path $mcmodPath -Value $mcmod -NoNewline

$langPath = "$tmpRoot\jar\assets\tektopia\lang\en_us.lang"
if (Test-Path $langPath) {
    $langContent = Get-Content -Raw $langPath
    if ($langContent -notmatch "(?m)^ai\\.filter\\.equip_gold_armor=") {
        if (-not $langContent.EndsWith("`n")) {
            $langContent += "`r`n"
        }
        $langContent += "ai.filter.equip_gold_armor=Equip Gold Armor`r`n"
        [System.IO.File]::WriteAllText($langPath, $langContent, [System.Text.Encoding]::ASCII)
    }
}

$manifestPath = "$tmpRoot\jar\META-INF\MANIFEST.MF"
$manifest = Get-Content -Raw $manifestPath
if ($manifest -match "Implementation-Version:") {
    $manifest = [regex]::Replace($manifest, "Implementation-Version:\s*[^\r\n]+", "Implementation-Version: $Version")
} else {
    $manifest += "`r`nImplementation-Version: $Version`r`n"
}
Set-Content -Path $manifestPath -Value $manifest -NoNewline

$buildInfoClass = "$tmpRoot\jar\net\tangotek\tektopia\BuildInfo.class"
$tekVillagerClass = "$tmpRoot\jar\net\tangotek\tektopia\TekVillager.class"

$buildReplacements = Replace-AsciiInBinary -Path $buildInfoClass -Old "1.1.0" -New $Version
$tekReplacements = Replace-AsciiInBinary -Path $tekVillagerClass -Old "1.1.0" -New $Version

if ($buildReplacements -lt 1) {
    throw "BuildInfo.class version marker was not patched."
}
if ($tekReplacements -lt 1) {
    throw "TekVillager.class version marker was not patched."
}

$outDir = Split-Path -Parent $OutJar
if ([string]::IsNullOrWhiteSpace($outDir)) {
    $outDir = "."
}
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$outJarFull = [System.IO.Path]::GetFullPath($OutJar)
if (Test-Path $outJarFull) {
    Remove-Item -Force $outJarFull
}

$jarExe = $null
$jarCmd = Get-Command jar -ErrorAction SilentlyContinue
if ($jarCmd -ne $null) {
    $jarExe = $jarCmd.Source
} else {
    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME "bin\\jar.exe"
        if (Test-Path $candidate) {
            $jarExe = $candidate
        }
    }
    if (-not $jarExe -and (Test-Path "C:\\Program Files\\Java")) {
        $candidate = Get-ChildItem "C:\\Program Files\\Java" -Directory -Filter "jdk*" |
            Sort-Object Name -Descending |
            ForEach-Object { Join-Path $_.FullName "bin\\jar.exe" } |
            Where-Object { Test-Path $_ } |
            Select-Object -First 1
        if ($candidate) {
            $jarExe = $candidate
        }
    }
}
if (-not $jarExe) {
    throw "Could not find jar.exe. Install a full JDK and retry."
}

Push-Location "$tmpRoot\jar"
try {
    & $jarExe --create --file $outJarFull --manifest "META-INF\MANIFEST.MF" .
    if ($LASTEXITCODE -ne 0) {
        throw "jar.exe failed while creating $outJarFull"
    }
} finally {
    Pop-Location
}

$verifyZip = [System.IO.Compression.ZipFile]::OpenRead($outJarFull)
$manifestEntry = $verifyZip.GetEntry("META-INF/MANIFEST.MF")
if ($manifestEntry -eq $null) {
    $manifestEntry = $verifyZip.GetEntry("META-INF\\MANIFEST.MF")
}
$mcmodEntry = $verifyZip.GetEntry("mcmod.info")
$manifestReader = New-Object System.IO.StreamReader($manifestEntry.Open())
$mcmodReader = New-Object System.IO.StreamReader($mcmodEntry.Open())
$manifestText = $manifestReader.ReadToEnd()
$mcmodText = $mcmodReader.ReadToEnd()
$manifestReader.Dispose()
$mcmodReader.Dispose()
$verifyZip.Dispose()

Write-Host "Built jar: $outJarFull"
Write-Host "Version checks:"
Write-Host ($manifestText | Select-String -Pattern "Implementation-Version:" | Select-Object -First 1).Line
Write-Host ($mcmodText | Select-String -Pattern '"version"' | Select-Object -First 1).Line
