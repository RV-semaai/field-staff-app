package com.semaai.agent.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.semaai.agent.R;
import com.semaai.agent.activity.clockinout.ClockDashboardActivity;
import com.semaai.agent.activity.clockinout.NewCustomerActivity;
import com.semaai.agent.activity.clockinout.OthersActivity;
import com.semaai.agent.activity.existingcustomers.CustomersListActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.newcustomer.CustomerRegistrationActivity;
import com.semaai.agent.activity.targettracking.TargetTrackingDashboardActivity;
import com.semaai.agent.databinding.ActivityDashboardBinding;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.DashboardViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DashboardActivity extends AppCompatActivity {

    private String TAG = DashboardActivity.class.getSimpleName() + "-->";
    private DashboardViewModel dashboardViewModel;
    private ActivityDashboardBinding binding;
    private static final int Check = 101;
    private Dialog progressDialog;
    ToastMsg toastMsg = new ToastMsg();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        binding = DataBindingUtil.setContentView(DashboardActivity.this, R.layout.activity_dashboard);
        binding.setLifecycleOwner(this);
        binding.setDashboardViewModel(dashboardViewModel);

        Log.e("TAG", "onCreate: >>> password 4 " + Constant.loginPassword);

        intiView();
        setProgress();
        getData();
    }
    private void setProgress() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            isPermissionGranted();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.GPS_msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        statusCheck();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusCheck();
        intiView();
        Constant.clockInClick = 0;
    }

    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "Permission is granted *********");
            } else {

                Log.i("TAG", "Permission is not present * * * * ** *");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }
    }

    private void intiView() {
        binding.tvJobTitle.setText(Constant.jobTitle);
        binding.tvName.setText(Constant.name);
        byte[] decodedString = Base64.decode(Constant.profileImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Glide.with(this).load(decodedByte).dontTransform().circleCrop().into(binding.ivSetProfile);
        binding.cvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.editMode = false;
                startActivity(new Intent(DashboardActivity.this, CustomerRegistrationActivity.class));
            }
        });

        binding.cvUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.editMode = false;
                startActivity(new Intent(DashboardActivity.this, CustomersListActivity.class));
            }
        });

        binding.cvClockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
                String actionFor = sh.getString("actionFor", "");

                if (actionFor.contentEquals("existing-customer")) {
                    Constant.clockInOutCheck = 2;
                    startActivity(new Intent(DashboardActivity.this, OthersActivity.class));
                } else if (actionFor.contentEquals("new-customer")) {
                    Constant.clockInOutCheck = 1;
                    startActivity(new Intent(DashboardActivity.this, NewCustomerActivity.class));
                } else if (actionFor.contentEquals("other-customer")) {
                    Constant.clockInOutCheck = 3;
                    startActivity(new Intent(DashboardActivity.this, OthersActivity.class));
                } else {
                    if (!Constant.employeeId.contentEquals("false")) {
                        Constant.clockInOutCheck = 0;
                        startActivity(new Intent(DashboardActivity.this, ClockDashboardActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.employeeNull), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, AccountDashboardActivity.class));
            }
        });

        binding.cvTargetTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TargetTrackingDashboardActivity.class));
            }
        });

    }

    private void getData() {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.profile + Constant.userId)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: >>> account " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject dataobject = jsonArray.getJSONObject(i);

                                    Constant.profileImage = dataobject.getString(ApiStringModel.image1920);
                                    byte[] decodedString = Base64.decode(Constant.profileImage, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    Glide.with(DashboardActivity.this).load(decodedByte).dontTransform().circleCrop().into(binding.ivSetProfile);

                                    if (!dataobject.getString(ApiStringModel.function).equals("false")) {
                                        Constant.jobTitle = dataobject.getString(ApiStringModel.function);
                                        binding.tvJobTitle.setText(Constant.jobTitle);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e(TAG, "onError: >>>>> " + e.getMessage());
                        }
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, "onError: >>>>> " + error.getMessage());
                        progressDialog.dismiss();
                        toastMsg.showToast(DashboardActivity.this, getString(R.string.errorMessage));
                    }
                });
    }

}