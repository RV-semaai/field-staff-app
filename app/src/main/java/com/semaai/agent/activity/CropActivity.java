package com.semaai.agent.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.semaai.agent.Imagecropper.CropImageView;
import com.semaai.agent.R;
import com.semaai.agent.utils.Common;

public class CropActivity extends AppCompatActivity {
    CardView done, cancel, rotate90;
    CropImageView cropImage;
    Bitmap cropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        cropImage = findViewById(R.id.crop_image);
        cancel = findViewById(R.id.cv_close);
        done = findViewById(R.id.cv_start);
        rotate90 = findViewById(R.id.cv_rotate90);

        Log.i("-->", "rotation123 :" + Common.rotation);

        cropImage.setImageUriAsync(Common.GetTempUri(CropActivity.this));
        cropImage.setAspectRatio(1, 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (Common.rotation == 6) {
                    cropImage.rotateImage(90);
                    Log.i("-->", "rotation 90 :" + Common.rotation);
                } else if (Common.rotation == 3) {
                    cropImage.rotateImage(180);
                    Log.i("-->", "rotation 180 :" + Common.rotation);
                } else if (Common.rotation == 8) {
                    cropImage.rotateImage(270);
                    Log.i("-->", "rotation 270 :" + Common.rotation);
                }
            }
        }, 200);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropped = cropImage.getCroppedImage();
                Common.SetTempCropBitmap(CropActivity.this, cropped);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        rotate90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage.rotateImage(90);
            }
        });

    }
}