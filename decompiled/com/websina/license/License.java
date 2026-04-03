/*
 * Decompiled with CFR 0.152.
 */
package com.websina.license;

import com.websina.license.LicenseNotFoundException;
import com.websina.util.FileUtil;
import com.websina.util.UnicodeEncoder;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class License {
    public static final String NEVER = "never";
    private static final String LICENSE_FILE = "tango_patreon.lic";
    private static final String EXPIRATION = "Expiration";
    private static final String SIGNATURE = "Signature";
    private List names = new ArrayList();
    private Properties prop = new Properties();
    private String data = null;
    private String rawData = null;

    private License() {
    }

    public static License newLicense() {
        return new License();
    }

    public static License loadLicense() throws LicenseNotFoundException {
        License lic = new License();
        lic.readFile();
        return lic;
    }

    public static License loadLicense(String data) throws LicenseNotFoundException {
        License lic = new License();
        lic.readData(data);
        return lic;
    }

    public void create() throws Exception {
        if (this.data == null || this.getSignature() == null) {
            throw new Exception("License is not formated ....");
        }
        FileWriter out = new FileWriter(LICENSE_FILE);
        out.write(this.data);
        out.write(10);
        out.write(SIGNATURE);
        out.write(61);
        out.write(this.getSignature());
        out.flush();
        out.close();
    }

    public String format() {
        StringBuffer buf = new StringBuffer();
        for (String key : this.names) {
            if (key.length() == 0 || key.equals(SIGNATURE)) continue;
            String value = UnicodeEncoder.encode(this.prop.getProperty(key));
            buf.append(key).append('=').append(value).append('\n');
        }
        this.data = buf.toString();
        return this.data;
    }

    public String getRawData() {
        return this.rawData;
    }

    public String getFeature(String name) {
        return this.prop.getProperty(name);
    }

    public void setFeature(String name, String value) {
        if (!this.names.contains(name)) {
            this.names.add(name);
        }
        this.prop.setProperty(name, value);
    }

    public String getExpiration() {
        return this.prop.getProperty(EXPIRATION);
    }

    public void setExpiration(String date) {
        this.setFeature(EXPIRATION, date);
    }

    public String getSignature() {
        return this.prop.getProperty(SIGNATURE);
    }

    public void setSignature(String signature) {
        this.setFeature(SIGNATURE, signature);
    }

    private void readFile() throws LicenseNotFoundException {
        try {
            FileUtil fileUtil = new FileUtil(LICENSE_FILE);
            fileUtil.read(this.names, this.prop);
            this.rawData = fileUtil.getRawData();
        }
        catch (Exception e) {
            throw new LicenseNotFoundException();
        }
    }

    private void readData(String data) throws LicenseNotFoundException {
        try {
            new FileUtil(new ByteArrayInputStream(data.getBytes())).read(this.names, this.prop);
            this.rawData = data;
        }
        catch (Exception e) {
            throw new LicenseNotFoundException();
        }
    }
}

