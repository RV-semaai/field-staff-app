package com.semaai.agent.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.semaai.agent.R;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ShowImageActivity extends AppCompatActivity {

    RoundedImageView imageView;
    ImageView ivBack;
    ConstraintLayout clBackAlert, cl, home, account;
    CardView cvYes, cvNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        findViewById();
        onClick();
        clBackAlert.setVisibility(View.GONE);
        if (Constant.whichCameraImage == 0) {
            imageView.setImageURI(Uri.fromFile(new File(Common.GetTempPath(ShowImageActivity.this))));
            ;
        } else if (Constant.whichCameraImage == 1) {
            imageView.setImageBitmap(Constant.cameraImage);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void onClick() {

        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clBackAlert.setVisibility(View.VISIBLE);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clBackAlert.setVisibility(View.VISIBLE);
            }
        });

        cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.openActivity(ShowImageActivity.this, DashboardActivity.class);
            }
        });

        cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clBackAlert.setVisibility(View.GONE);
            }
        });

    }

    private void findViewById() {
        imageView = findViewById(R.id.iv_storePic);
        ivBack = findViewById(R.id.iv_backArrow1);
        home = findViewById(R.id.cl_home);
        account = findViewById(R.id.cl_account);
        clBackAlert = findViewById(R.id.cl_BackAlert);
        cvYes = findViewById(R.id.cv_yes);
        cvNo = findViewById(R.id.cv_no);
        cl = findViewById(R.id.cl);
    }

    @Override
    public void onBackPressed() {
        if (clBackAlert.getVisibility() == View.GONE) {
            super.onBackPressed();
            finish();
        }
    }
}