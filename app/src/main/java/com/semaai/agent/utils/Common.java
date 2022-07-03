package com.semaai.agent.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Random;

public class Common {

    public static String SP_KEY = "sp_key";

    public static void openActivity(Context context , Class cls) {
        Intent i = new Intent(context, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public static String currencyFormatOnlyZero(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(Double.parseDouble(amount));
    }

    public static String currencyFormatPoint(String amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.000");
        return formatter.format(Double.parseDouble(amount));
    }

    public static void SetTempBitmap(Context context, Bitmap bitmap) {
        File file = new File(context.getFilesDir(), "tempBitmap.JPEG");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SetTempCropBitmap(Context context, Bitmap bitmap) {
        File file = new File(context.getFilesDir(), "tempCrop.JPEG");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri GetTempUri(Context context) {
        String imageShapePath = context.getFilesDir() + "/" + "tempBitmap.JPEG";
        return Uri.fromFile(new File(imageShapePath));
    }

    public static String GetTempPath(Context context) {
        String imageShapePath = context.getFilesDir() + "/" + "tempCrop.JPEG";
        return imageShapePath;
    }

    public static int rotation = 1;

    public static String generatePassword() {

        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 6;
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static void setClockInData(Context context, String action, String name, String aId , String exiId , String time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("actionFor", action);
        myEdit.putString("name", name);
        myEdit.putString("AId", aId);
        myEdit.putString("ExiId", exiId);
        myEdit.putString("time",time);
        myEdit.commit();
    }

    public static String getClockInData(Context context) {
        SharedPreferences sh = context.getSharedPreferences(SP_KEY, context.MODE_PRIVATE);
        String actionFor = sh.getString("actionFor", "");
        String name = sh.getString("name", "none");
        String Aid = sh.getString("AId", "");
        String ExiId = sh.getString("ExiId", "");
        return actionFor;
    }

    public static class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '*'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };
}
