package com.semaai.agent.Imagecropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

public class Data {

    private static final String TAG = Data.class.getSimpleName() + "-->";

    public static void SetImage(Context cntx, Bitmap bitmap) {
        try {
            File filename = new File(cntx.getFilesDir(), "temp");
            FileOutputStream fos = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap GetImage(Context cntx) {
        return BitmapFactory.decodeFile(cntx.getFilesDir() + "/" + "temp");
    }

}

