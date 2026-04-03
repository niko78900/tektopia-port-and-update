/*
 * Decompiled with CFR 0.152.
 */
package com.websina.tool;

import com.websina.util.ByteHex;
import com.websina.util.security.KeyUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Date;

public class KeyManager {
    public static void create() {
        KeyPair kp = KeyUtil.getKeyPair();
        byte[] pub = kp.getPublic().getEncoded();
        byte[] priv = kp.getPrivate().getEncoded();
        StringBuffer buf = new StringBuffer("######## Key pair created on:").append(new Date()).append("\npublic-key=").append(ByteHex.convert(pub)).append("\nprivate-key=").append(ByteHex.convert(priv)).append("\n\n");
        try {
            FileWriter out = new FileWriter("dsakey.cfg", true);
            out.write(buf.toString());
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        KeyManager.create();
    }
}

