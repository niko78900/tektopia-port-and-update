# Tektopia Port and Update - Phase Log

Date: 2026-04-03

## Completed Phases

1. Phase 1 (`bd15f03`)
- Repository hygiene and source-only tracking.
- Java/classpath import blockers addressed.

2. Phase 2 (`6d98493`)
- Runtime license gating switched to fail-open behavior for QoL.

3. Phase 3 (`b2cec24`)
- Decompiler type/cast stabilization pass on core entity/crafting paths.

4. Phase 4 (`fc56cf3`)
- Villager crafting/inventory QoL improvements:
  - transactional craft input handling,
  - stack-aware capacity checks,
  - success-only penalties.

5. Phase 5 (`d47b928`)
- Configurable crafting QoL toggles and recipe need prioritization.

6. Phase 6 (this phase)
- Final validation and cleanup pass.
- Documented current known blockers and completed scope.
- Added ignore coverage for local `_tmp*` validation artifacts.

## QoL Runtime Toggles (System Properties)

- `tektopia.qol.craft.prioritize_need` (default: `true`)
- `tektopia.qol.craft.enforce_personal_limit` (default: `true`)

Accepted `true` values: `1`, `true`, `yes`, `on`.

## Validation Snapshot

- Import audit: `MISSING_NON_JDK_IMPORT_COUNT=0`
- Full compile sweep still reports errors (`Xmaxerrs=200`) due to decompile/mapping/API-surface mismatches not yet resolved.

Top remaining error groups:
- Forge/Minecraft signature visibility mismatches in village generation handlers (`StructureVillagePieces.Village` access).
- Obfuscated bridge/mapping mismatch classes (e.g. `bi`, `oq`, `bk`, `amu`, `aed`, `awt`).
- Decompiled raw-type inference issues in world/entity loops and scheduler paths.
- API surface mismatches around older SRG methods (`isBed`, `isBedFoot`, `markAndNotifyBlock`, capability storage signatures).

## Scope Notes

- The QoL feature set is implemented and committed phase-by-phase.
- Remaining compile blockers are part of deeper remap/decompile correction work and are tracked for future stabilization passes.
