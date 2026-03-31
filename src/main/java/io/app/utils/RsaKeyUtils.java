package io.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.nio.file.Files;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyUtils {

    public static RSAPrivateKey rsaPrivateKey() throws Exception {
        String key=read("private.pem")
                .replaceAll("-----BEGIN PRIVATE KEY-----","")
                .replaceAll("-----END PRIVATE KEY-----","")
                .replaceAll("\\s","");
        byte[] decode= Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decode));
    }

    public static RSAPublicKey rsaPublicKey() throws Exception {
        String key=read("public.pem")
                .replaceAll("-----BEGIN PUBLIC KEY-----","")
                .replaceAll("-----END PUBLIC KEY-----","")
                .replaceAll("\\s","");
        byte[] decode=Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decode));
    }

    private static String read(String file) throws Exception {
        InputStream is=RsaKeyUtils.class.getClassLoader().getResourceAsStream(file);
        return new String(is.readAllBytes());
    }
}
