package com.app.swagse.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class WebUtils {

    public static MultipartBody.Part getImagePart(String key, File file) {
        try {
            if (file != null) {
                Log.e(TAG, "getImagePart:file " + file);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                Log.e("File", " " + file);
                return MultipartBody.Part.createFormData(key, file.getName(), requestFile);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), "");
                Log.e("File", " " + file);
                return MultipartBody.Part.createFormData("img", "", requestFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MultipartBody.Part getImagePart1(String key, String url) {
        try {
                Log.e(TAG, "getImagePart:file " + url);
                RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), "");
                Log.e("File", " " + url);
                return MultipartBody.Part.createFormData(key, url, requestFile);
           /* } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), "");
                Log.e("File", " " + file);
                return MultipartBody.Part.createFormData("img", "", requestFile);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestBody createRequest(String value) {
        if (value != null) {
            Log.e("Not null", "success " + value);
            try {
                return RequestBody.create(MediaType.parse("text/plain"), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}