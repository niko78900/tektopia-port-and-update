/*
 * Decompiled with CFR 0.152.
 */
package com.websina.util;

public class UnicodeEncoder {
    private static StringBuffer charAccumulator = new StringBuffer(2);
    private static final char MIN_HIGH_SURROGATE = '\ud800';
    private static final char MAX_HIGH_SURROGATE = '\udbff';
    private static final char MIN_LOW_SURROGATE = '\udc00';
    private static final char MAX_LOW_SURROGATE = '\udfff';
    private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

    public static String encode(String s) {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] <= '\u007f') {
                sb.append(chars[i]);
                continue;
            }
            int codePoint = Integer.MIN_VALUE;
            if (UnicodeEncoder.isLowSurrogate(chars[i]) || UnicodeEncoder.isHighSurrogate(chars[i])) {
                charAccumulator.append(chars[i]);
            } else {
                codePoint = chars[i];
            }
            if (charAccumulator.length() == 2) {
                codePoint = UnicodeEncoder.toCodePoint(charAccumulator.charAt(0), charAccumulator.charAt(1));
                charAccumulator.setLength(0);
            }
            if (charAccumulator.length() != 0) continue;
            sb.append(UnicodeEncoder.encode(codePoint));
        }
        return sb.toString();
    }

    public static String encode(int c) {
        StringBuffer sb = new StringBuffer(10);
        sb.append(Integer.toHexString(c));
        while (sb.length() < 4) {
            sb.insert(0, '0');
        }
        sb.insert(0, "\\u");
        return sb.toString();
    }

    private static boolean isHighSurrogate(char ch) {
        return ch >= '\ud800' && ch <= '\udbff';
    }

    private static boolean isLowSurrogate(char ch) {
        return ch >= '\udc00' && ch <= '\udfff';
    }

    private static int toCodePoint(char high, char low) {
        return (high - 55296 << 10) + (low - 56320) + 65536;
    }
}

