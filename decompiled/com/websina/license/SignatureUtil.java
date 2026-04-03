/*
 * Decompiled with CFR 0.152.
 */
package com.websina.license;

import com.websina.util.ByteHex;
import com.websina.util.security.KeyUtil;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SignatureUtil {
    private static final String algorithm = "DSA";

    private SignatureUtil() {
    }

    public static String sign(String data, byte[] encodedPrivateKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance(algorithm);
        PrivateKey key = KeyUtil.getPrivate(encodedPrivateKey);
        sig.initSign(key);
        sig.update(data.getBytes());
        byte[] result = sig.sign();
        return ByteHex.convert(result);
    }

    public static boolean verify(String data, byte[] signature, byte[] encodedPublicKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance(algorithm);
        PublicKey key = KeyUtil.getPublic(encodedPublicKey);
        sig.initVerify(key);
        sig.update(data.getBytes());
        return sig.verify(signature);
    }
}

