/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.websina.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public class FileUtil {
    private InputStream is;
    private String rawData;

    public FileUtil(String filename) throws FileNotFoundException {
        File inFile = new File(Minecraft.func_71410_x().field_71412_D, filename);
        this.is = new FileInputStream(inFile);
    }

    public FileUtil(InputStream is) {
        this.is = is;
    }

    public String getRawData() {
        return this.rawData;
    }

    public void read(List names, Properties prop) throws IOException {
        String line;
        if (this.is == null) {
            throw new IOException("There is nothing to read from ...");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.is));
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            String value;
            String name;
            if (line.trim().indexOf(35) == 0 || line.trim().indexOf(33) == 0) continue;
            builder.append(line + System.getProperty("line.separator"));
            int index = line.indexOf(61);
            if (index > 0) {
                name = line.substring(0, index).trim();
                value = line.substring(++index).trim();
            } else {
                name = line.trim();
                value = "";
            }
            names.add(name);
            prop.setProperty(name, this.loadConvert(value));
            this.rawData = builder.toString();
        }
    }

    private String loadConvert(String theString) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        int x = 0;
        while (x < len) {
            int aChar;
            if ((aChar = theString.charAt(x++)) == 92) {
                if ((aChar = theString.charAt(x++)) == 117) {
                    int value = 0;
                    block6: for (int i = 0; i < 4; ++i) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 53: 
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: {
                                value = (value << 4) + aChar - 48;
                                continue block6;
                            }
                            case 97: 
                            case 98: 
                            case 99: 
                            case 100: 
                            case 101: 
                            case 102: {
                                value = (value << 4) + 10 + aChar - 97;
                                continue block6;
                            }
                            case 65: 
                            case 66: 
                            case 67: 
                            case 68: 
                            case 69: 
                            case 70: {
                                value = (value << 4) + 10 + aChar - 65;
                                continue block6;
                            }
                            default: {
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                            }
                        }
                    }
                    outBuffer.append((char)value);
                    continue;
                }
                if (aChar == 116) {
                    aChar = 9;
                } else if (aChar == 114) {
                    aChar = 13;
                } else if (aChar == 110) {
                    aChar = 10;
                } else if (aChar == 102) {
                    aChar = 12;
                }
                outBuffer.append((char)aChar);
                continue;
            }
            outBuffer.append((char)aChar);
        }
        return outBuffer.toString();
    }
}

