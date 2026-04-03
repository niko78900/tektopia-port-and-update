/*
 * Decompiled with CFR 0.152.
 */
package com.websina.util;

import java.io.ByteArrayOutputStream;

public final class ByteHex {
    private static final char[] hexs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String convert(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(hexs[bytes[i] >> 4 & 0xF]);
            sb.append(hexs[bytes[i] & 0xF]);
        }
        return sb.toString();
    }

    public static byte[] convert(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < hex.length(); i += 2) {
            char c1 = hex.charAt(i);
            if (i + 1 >= hex.length()) {
                throw new IllegalArgumentException();
            }
            char c2 = hex.charAt(i + 1);
            int b = 0;
            if (c1 >= '0' && c1 <= '9') {
                b = (byte)(b + (c1 - 48) * 16);
            } else if (c1 >= 'a' && c1 <= 'f') {
                b = (byte)(b + (c1 - 97 + 10) * 16);
            } else if (c1 >= 'A' && c1 <= 'F') {
                b = (byte)(b + (c1 - 65 + 10) * 16);
            } else {
                throw new IllegalArgumentException();
            }
            if (c2 >= '0' && c2 <= '9') {
                b = (byte)(b + (c2 - 48));
            } else if (c2 >= 'a' && c2 <= 'f') {
                b = (byte)(b + (c2 - 97 + 10));
            } else if (c2 >= 'A' && c2 <= 'F') {
                b = (byte)(b + (c2 - 65 + 10));
            } else {
                throw new IllegalArgumentException();
            }
            baos.write(b);
        }
        return baos.toByteArray();
    }
}

