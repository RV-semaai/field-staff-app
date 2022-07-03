package com.semaai.agent.activity.clockinout;


import static com.semaai.agent.activity.newcustomer.GPSViewActivity.latitude1;
import static com.semaai.agent.activity.newcustomer.GPSViewActivity.longitude1;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.semaai.agent.R;
import com.semaai.agent.activity.CropActivity;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.newcustomer.GPSViewActivity;
import com.semaai.agent.databinding.ActivityStorePhotoAndGpsActivityBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.services.GpsTracker;
import com.semaai.agent.utils.CameraUtils;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.clockinout.CustomerViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


public class StorePhotoAndGPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = StorePhotoAndGPSActivity.class.getSimpleName() + "--->";
    CustomerViewModel storePhotoGPSViewModel;
    ActivityStorePhotoAndGpsActivityBinding binding;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int REQUEST_CROP = 101;
    public static final int MEDIA_TYPE_IMAGE = 1;
    int itemClick;
    private String imageStoragePath;
    CustomerModel customerModel;
    private GoogleMap gMap;
    public static double lati, longi;
    private Dialog progressDialog;
    CameraGPSDataModel cameraGPSDataModel = new CameraGPSDataModel();
    String actionFor, name, aId, exiId, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storePhotoGPSViewModel = ViewModelProviders.of(this).get(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(StorePhotoAndGPSActivity.this, R.layout.activity_store_photo_and_gps_activity);
        binding.setLifecycleOwner(this);
        binding.setCustomerViewModel(storePhotoGPSViewModel);

        SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        actionFor = sh.getString("actionFor", "");
        name = sh.getString("name", "none");
        aId = sh.getString("AId", "");
        exiId = sh.getString("ExiId", "");
        time = sh.getString("time", "");

        intiView();
        onClick();
    }


    private void gone() {
        binding.tvText.setVisibility(View.GONE);
        binding.tvTime.setVisibility(View.GONE);
    }

    private void visible() {
        binding.tvText.setVisibility(View.VISIBLE);
        binding.tvTime.setVisibility(View.VISIBLE);
    }

    private void timeSpend() {
        Date userDob = null;
        Date today = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        try {
            userDob = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = today.getTime() - userDob.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) (diff / (1000 * 60 * 60));
        int minutes = (int) (diff / (1000 * 60));
        int seconds = (int) (diff / (1000));

        Constant.hh = hours;
        Constant.mm = minutes;
        Constant.ss = seconds;

        hours = seconds / 3600;
        minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        binding.tvTime.setText(timeString);
    }


    private void intiView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        //getData

        if (actionFor.contentEquals(getString(R.string.existingCustomer))) {
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            visible();
            timeSpend();
        } else if (actionFor.contentEquals(getString(R.string.newCustomer))) {
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            visible();
            timeSpend();
        } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            visible();
            timeSpend();
        } else {
            binding.rlRegister.tvHeader.setText(R.string.clockIn);
            gone();

        }

        binding.rlRegister.tvTopBar.setText(Constant.name);
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.tvSetCustomerName.setText(customerModel.getClockInOutCustomerModel().getNewCustomer());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg_map);

        GpsTracker gpsTracker = new GpsTracker(StorePhotoAndGPSActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude1 = gpsTracker.getLatitude();
            longitude1 = gpsTracker.getLongitude();
            lati = latitude1;
            longi = longitude1;
            Log.e("latitude1", "" + latitude1 + "   " + longitude1);
            mapFragment.getMapAsync(this);

        }

        binding.clBackAlert.setVisibility(View.GONE);
    }

    private void onClick() {
        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(0);
            }
        });
        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });

        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
            }
        });

        binding.cvCameraEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                itemClick = 1;
                onItemClick();
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                itemClick = 1;
                onItemClick();
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cvLocationEdit1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                itemClick = 2;
                onItemClick();
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cvConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (cameraGPSDataModel.getImgPath() != null) {
                    binding.cvConfirm.setOnClickListener(null);
                    progressDialog.show();
                    RequestAPI apiManager = new RequestAPI(StorePhotoAndGPSActivity.this);

                    if (actionFor.contentEquals(getString(R.string.existingCustomer))) {
                        sendDataApiCallClockOut(apiManager);
                    } else if (actionFor.contentEquals(getString(R.string.newCustomer))) {
                        if (Constant.clockInClick == 2) {
                            actionFor = getString(R.string.existingCustomer);
                            Constant.clockInClick = 0;
                        }
                        sendDataApiCallClockOut(apiManager);
                    } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {
                        sendDataApiCallClockOut(apiManager);
                    } else {
                        sendDataApiCallClockIn(apiManager);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.plsTackImage), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void sendDataApiCallClockIn(RequestAPI apiManager) {

        String imageString = null;

        if (cameraGPSDataModel.getImgPath() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(cameraGPSDataModel.getImgPath()));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        String otherData = "none";

        if (Constant.actionFor.contentEquals(getString(R.string.newCustomer))) {
            otherData = customerModel.getClockInOutCustomerModel().getNewCustomer();
        } else if (Constant.actionFor.contentEquals(getString(R.string.existingCustomer))) {
            otherData = "none";
        } else {

            otherData = customerModel.getClockInOutCustomerModel().getNewCustomer();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        //create UTC time
        String currentUTCTime = null;
        ZonedDateTime utcTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            utcTime = ZonedDateTime.now(ZoneOffset.UTC);
            currentUTCTime = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        try {
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject child = new JSONObject();


            child.put(ApiStringModel.actionFor, Constant.actionFor);
            child.put(ApiStringModel.customerId, customerModel.getClockInOutCustomerModel().getId());
            child.put(ApiStringModel.employeeId, Constant.employeeId);
            if (Constant.actionFor.contentEquals(getString(R.string.newCustomer)) ||
                    Constant.actionFor.contentEquals(getString(R.string.otherCustomer))) {
                child.put(ApiStringModel.other, otherData);
            }
            child.put(ApiStringModel.type, "clock-in");
            child.put(ApiStringModel.checkIn, currentUTCTime);
            child.put(ApiStringModel.checkInLog, currentDateTime);
            child.put(ApiStringModel.salespersonName, Constant.name);
            child.put(ApiStringModel.salespersonPartnerId, Constant.salespersonPartnerId);
            child.put(ApiStringModel.checkInLatitude, "" + lati);
            child.put(ApiStringModel.checkInLongitude, "" + longi);

            if (imageString != null) {
                child.put(ApiStringModel.checkInImage, imageString);
            } else {

                child.put(ApiStringModel.checkInImage, "");
            }

            data.put(ApiStringModel.data, child);

            params.put(ApiStringModel.params, data);

            Log.e(TAG, "Clockin-Out post Data :" + params);

            apiManager.sendPostRequestWithCookie(progressDialog, ApiEndPoints.clockInOut, params, new APIResponse() {

                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();

                    try {
                        JSONObject result = response.getJSONObject(ApiStringModel.result);

                        Log.e(TAG, "ClockIn-Out onResponse >> :" + response);

                        String msg = result.getString("message");

                        if (msg.contentEquals("Record created")) {

                            int data = result.getInt(ApiStringModel.data);
                            Log.e(TAG, "eten id :" + data);

                            Common.setClockInData(StorePhotoAndGPSActivity.this, Constant.actionFor,
                                    customerModel.getClockInOutCustomerModel().getNewCustomer(),
                                    String.valueOf(data),
                                    customerModel.getClockInOutCustomerModel().getId(),

                                    currentDateTime);

                            Intent intent = new Intent(StorePhotoAndGPSActivity.this, SucceedActivity.class);
                            intent.putExtra("sample", customerModel);

                            intent.putExtra("clock", "in");
                            startActivity(intent);
                        } else {

                            ToastMsg.showToast(StorePhotoAndGPSActivity.this, getString(R.string.sessionExpired));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e(TAG, "ClockIn-Out JSONException 1 :" + e.getMessage());
                        ToastMsg.showToast(StorePhotoAndGPSActivity.this, "" + e.getMessage());

                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Log.e(TAG, "ClockIn-Out onError :" + volleyError);

                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {

                    Log.e(TAG, "ClockIn-Out onNetworkResponse :" + response);
                    progressDialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();

            Log.e(TAG, "ClockIn-Out JSONException 2 :" + e.getMessage());

        }

    }

    private void sendDataApiCallClockOut(RequestAPI apiManager) {

        String imageString = null;

        if (cameraGPSDataModel.getImgPath() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(cameraGPSDataModel.getImgPath()));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        String otherData = "none";

        if (Constant.actionFor.contentEquals(getString(R.string.newCustomer))) {
            otherData = customerModel.getClockInOutCustomerModel().getNewCustomer();
        } else if (Constant.actionFor.contentEquals(getString(R.string.existingCustomer))) {
            otherData = "none";
        } else {

            otherData = customerModel.getClockInOutCustomerModel().getNewCustomer();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());


        //create UTC time
        String currentUTCTime = null;
        ZonedDateTime utcTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            utcTime = ZonedDateTime.now(ZoneOffset.UTC);
            currentUTCTime = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        try {
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject child = new JSONObject();

            child.put(ApiStringModel.actionFor, actionFor);
            child.put(ApiStringModel.attendanceId, aId);
            child.put(ApiStringModel.customerId, customerModel.getClockInOutCustomerModel().getId());
            child.put(ApiStringModel.employeeId, Constant.employeeId);
            if (Constant.actionFor.contentEquals(getString(R.string.newCustomer)) ||
                    Constant.actionFor.contentEquals(getString(R.string.otherCustomer))) {
                child.put(ApiStringModel.other, otherData);
            }
            child.put(ApiStringModel.type, "clock-out");
            child.put(ApiStringModel.workedHours, "00");
            child.put(ApiStringModel.salespersonName, Constant.name);
            child.put(ApiStringModel.salespersonPartnerId, Constant.salespersonPartnerId);
            child.put(ApiStringModel.checkOut, currentUTCTime);
            child.put(ApiStringModel.checkOutLog, currentDateTime);
            child.put(ApiStringModel.checkOutLatitude, "" + lati);
            child.put(ApiStringModel.checkOutLongitude, "" + longi);

            if (imageString != null) {
                child.put(ApiStringModel.checkOutImage, imageString);
            } else {
                child.put(ApiStringModel.checkOutImage, "");
            }

            data.put(ApiStringModel.data, child);

            params.put(ApiStringModel.params, data);

            Log.e(TAG, "Clockin-Out post Data :" + params);

            apiManager.sendPostRequestWithCookie(progressDialog, ApiEndPoints.clockInOut, params, new APIResponse() {

                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();

                    try {
                        JSONObject result = response.getJSONObject("result");
                        Log.e(TAG, "ClockIn-Out onResponse :" + response);

                        String msg = result.getString("message");
                        Log.e(TAG, "ClockIn-Out message :" + msg);

                        if (msg.contentEquals("Record Updated")) {

                            Intent intent = new Intent(StorePhotoAndGPSActivity.this, SucceedActivity.class);
                            intent.putExtra("sample", customerModel);
                            intent.putExtra("clock", "out");
                            intent.putExtra("clockInTime", time);
                            Common.setClockInData(StorePhotoAndGPSActivity.this, "", "", "", "", "");
                            startActivity(intent);

                        } else {

                            ToastMsg.showToast(StorePhotoAndGPSActivity.this, getString(R.string.sessionExpired));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastMsg.showToast(StorePhotoAndGPSActivity.this, "" + e.getMessage());

                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Log.e(TAG, "ClockIn-Out onError :" + volleyError);

                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {
                    Log.e(TAG, "ClockIn-Out onNetworkResponse :" + response);
                    progressDialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();

            Log.e(TAG, "ClockIn-Out JSONException :" + e.getMessage());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onItemClick() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission is granted *********");

            if (itemClick == 1) {
                captureImage();
                itemClick = 0;
            } else if (itemClick == 2) {
                latitude1 = lati;
                longitude1 = longi;
                startActivity(new Intent(StorePhotoAndGPSActivity.this, GPSViewActivity.class));
                itemClick = 0;
            }
        } else {
            Log.i("TAG", "Permission is not present * * * * * *");
            ActivityCompat.requestPermissions(StorePhotoAndGPSActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityIfNeeded(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        binding.ivCamera.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Camera Result
        if (CAMERA_CAPTURE_IMAGE_REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                try {

                    ExifInterface ei = new ExifInterface(imageStoragePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Common.rotation = orientation;
                    Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageStoragePath)));

                    Common.SetTempBitmap(StorePhotoAndGPSActivity.this, Constant.cameraImage);
                    Intent intent = new Intent(StorePhotoAndGPSActivity.this, CropActivity.class);
                    startActivityIfNeeded(intent, REQUEST_CROP);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {

                binding.ivCamera.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            } else {
                binding.ivCamera.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.sorryFailedToCaptureImage), Toast.LENGTH_SHORT).show();
            }
        }

        // Crop Result
        if (REQUEST_CROP == requestCode) {
            if (resultCode == RESULT_OK) {
                cameraGPSDataModel.setImgPath(Common.GetTempPath(StorePhotoAndGPSActivity.this));
                binding.ivStorePic.setImageURI(Uri.fromFile(new File(Common.GetTempPath(StorePhotoAndGPSActivity.this))));
                binding.ivCamera.setVisibility(View.GONE);

            }
            if (resultCode == RESULT_CANCELED) {
                binding.ivCamera.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    public void onBackPressed() {
        onBackClick(0);
    }

    private void onBackClick(int checkClick) {
        if (checkClick == 0) {
            finish();
        } else if (checkClick == 1) {
            Common.openActivity(getApplicationContext(), DashboardActivity.class);
        } else if (checkClick == 2) {
            Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
        gMap.getFocusedBuilding();
        LatLng latLng = new LatLng(latitude1, longitude1);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);
    }


    private void setTimer() {
        new CountDownTimer(1000 * 60 * 60 * 24, 1000) {
            public void onTick(long millisUntilFinished) {
                timeSpend();
            }

            public void onFinish() {
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (gMap != null) {
            Log.i(TAG, "onResume: if");
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
            gMap.getFocusedBuilding();
            LatLng latLng = new LatLng(latitude1, longitude1);
            lati = latitude1;
            longi = longitude1;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            gMap.animateCamera(cameraUpdate);
            binding.ivLocation.setVisibility(View.INVISIBLE);
            binding.ivGpsLocation.setVisibility(View.GONE);
            binding.clMapView.setVisibility(View.VISIBLE);
            Log.i(TAG, "onResume :" + latitude1 + " , " + longitude1);
        }

        if (actionFor.contentEquals(getString(R.string.existingCustomer))) {
            setTimer();
        } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {
            setTimer();
        } else if (actionFor.contentEquals(getString(R.string.newCustomer))) {
            setTimer();
        }

    }

}