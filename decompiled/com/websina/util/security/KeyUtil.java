/*
 * Decompiled with CFR 0.152.
 */
package com.websina.util.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtil {
    private static KeyPairGenerator kpg;
    private static KeyFactory kf;

    public static PublicKey getPublic(byte[] encodedKey) throws InvalidKeySpecException {
        return kf.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    public static PrivateKey getPrivate(byte[] encodedKey) throws InvalidKeySpecException {
        return kf.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static KeyPair getKeyPair() {
        return kpg.genKeyPair();
    }

    static {
        try {
            kpg = KeyPairGenerator.getInstance("DSA");
            SecureRandom sr = new SecureRandom();
            kpg.initialize(1024, new SecureRandom(sr.generateSeed(8)));
            kf = KeyFactory.getInstance("DSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

