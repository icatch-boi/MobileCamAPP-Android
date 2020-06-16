package com.icatch.mobilecam.utils.imageloader;

import com.icatch.mobilecam.Log.AppLog;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.L;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5FileNameGeneratorMatchFaceName implements FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(String imageUri) {
        String invariantUri;
        if(TutkUriUtil.isTutkUri(imageUri)){
            invariantUri = TutkUriUtil.getKey(imageUri);
        }else {
            invariantUri = imageUri;
        }

//        AppLog.d("Md5FileNameGeneratorMatchFaceName", "generate: imageUri = [%s], " + imageUri
//                + "\n invariantUri = " + invariantUri
//        );
        byte[] md5 = getMD5(invariantUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            L.e(e);
        }
        return hash;
    }
}
