package com.semaai.agent.activity.newcustomer;

import static com.semaai.agent.activity.newcustomer.GPSViewActivity.latitude1;
import static com.semaai.agent.activity.newcustomer.GPSViewActivity.longitude1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

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
import com.semaai.agent.activity.ShowImageActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityCameraGpsBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.services.GpsTracker;
import com.semaai.agent.utils.CameraUtils;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.StringValidation;
import com.semaai.agent.viewmodel.newcustomer.CameraGPSViewModel;

import java.io.File;
import java.io.IOException;

public class CameraGPSActivity extends AppCompatActivity implements OnMapReadyCallback {
    CameraGPSViewModel registerPhotoLocationViewModel;
    ActivityCameraGpsBinding binding;
    public static final String GalleryDirectoryName = "Camera";
    public static final int MediaTypeImage = 1;
    public static final String ImageExtension = "jpg";
    private static final int CameraCaptureImageRequestCode = 100;
    private static final int REQUEST_CROP = 0;
    private String imageStoragePath;
    private Bitmap bitmap;
    private GoogleMap gMap;
    int itemClick;
    String TAG = CustomerAddressActivity.class.getSimpleName() + "--->";
    CameraGPSDataModel cameraGPSDataModel = new CameraGPSDataModel();
    StringValidation stringValidation = new StringValidation();
    CustomerModel customerModel;
    public static double latitude, longitude;
    boolean back = true, imageSet = false;
    private int checkClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPhotoLocationViewModel = ViewModelProviders.of(this).get(CameraGPSViewModel.class);
        binding = DataBindingUtil.setContentView(CameraGPSActivity.this, R.layout.activity_camera_gps);
        binding.setLifecycleOwner(this);
        binding.setRegisterPhotoLocationViewModel(registerPhotoLocationViewModel);


        intiView();
        onClick();
        checkEditMode();

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

    private void intiView() {
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg_map);
        binding.rlRegister.tvTopBar.setText(customerModel.getCustomerRegisterModel().getUserName());

        GpsTracker gpsTracker = new GpsTracker(CameraGPSActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude1 = gpsTracker.getLatitude();
            longitude1 = gpsTracker.getLongitude();
            latitude = latitude1;
            longitude = longitude1;
            cameraGPSDataModel.setLatitude(latitude1);
            cameraGPSDataModel.setLongitude(longitude1);
            Log.e("latitude1", "" + latitude1 + "   " + longitude1);
            mapFragment.getMapAsync(this);
        }

        binding.clBackAlert.setVisibility(View.GONE);
    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "Permission is granted *********");
                return true;
            } else {

                Log.i("TAG", "Permission is not present * * * * ** *");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return false;
            }
        }
        return true;
    }

    private void onClick() {

        binding.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.icBackDialog.cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClick == 0) {
                    finish();
                    binding.clBackAlert.setVisibility(View.GONE);
                } else if (checkClick == 1) {
                    Common.openActivity(getApplicationContext(), DashboardActivity.class);
                    binding.clBackAlert.setVisibility(View.GONE);
                } else if (checkClick == 2) {
                    Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                    startActivity(intent);
                    binding.clBackAlert.setVisibility(View.GONE);
                }
            }
        });

        binding.icBackDialog.cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });


        binding.rlRegister.ivBackArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick = 0;
                onBackPressed();
            }
        });

        binding.cvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringValidation.onPhotoLocationClick(CameraGPSActivity.this, cameraGPSDataModel, customerModel);

            }
        });

        binding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                itemClick = 1;
                onItemClick();
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });
        binding.ivStorePic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (imageSet) {
                    Constant.whichCameraImage = 0;
                    startActivity(new Intent(CameraGPSActivity.this, ShowImageActivity.class));
                } else {
                    itemClick = 1;
                    onItemClick();
                }
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

        binding.cvLocationEdit1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                itemClick = 2;
                onItemClick();
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });
        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkClick = 1;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });

        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick = 2;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        checkClick = 0;
        binding.clBackAlert.setVisibility(View.VISIBLE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MediaTypeImage);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(CameraGPSActivity.this, file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityIfNeeded(intent, CameraCaptureImageRequestCode);
        binding.ivCamera.setVisibility(View.INVISIBLE);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraCaptureImageRequestCode) {
            if (resultCode == RESULT_OK) {
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                try {

                    ExifInterface ei = new ExifInterface(imageStoragePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Common.rotation = orientation;

                    Log.i(TAG, "get orientation :" + orientation);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            Log.i(TAG, "get orientation 90 :" + orientation);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            Log.i(TAG, "get orientation 180 :" + orientation);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            Log.i(TAG, "get orientation 270 :" + orientation);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            Log.i(TAG, "get orientation :" + orientation);

                    }


                    Constant.whichCameraImage = 0;
                    Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageStoragePath)));

                    Common.SetTempBitmap(CameraGPSActivity.this, Constant.cameraImage);
                    Intent intent = new Intent(CameraGPSActivity.this, CropActivity.class);
                    startActivityIfNeeded(intent, REQUEST_CROP);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "onActivityResult:-------------- " + imageStoragePath);
            } else if (resultCode == RESULT_CANCELED) {
                if (!imageSet) {
                    binding.ivCamera.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            } else {
                binding.ivCamera.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.sorryFailedToCaptureImage), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                cameraGPSDataModel.setImgPath(Common.GetTempPath(CameraGPSActivity.this));
                binding.ivStorePic.setBackground(null);
                binding.ivStorePic.setImageURI(Uri.fromFile(new File(Common.GetTempPath(CameraGPSActivity.this))));
                Log.i(TAG, "EditImageSet");
                imageSet = true;
            } else if (resultCode == RESULT_CANCELED) {
                if (!imageSet) {
                    binding.ivCamera.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        gMap = googleMap;
        if (Constant.editMode) {
            CameraGPSDataModel cameraGPSDataModel = Constant.customerModel.getCameraGPSDataModel();
            latitude1 = cameraGPSDataModel.getLatitude();
            longitude1 = cameraGPSDataModel.getLongitude();
            Log.i(TAG, "onMapReady: if:" + latitude1 + " " + longitude1);
            if (gMap != null) {
                gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
                gMap.getFocusedBuilding();
                LatLng latLng = new LatLng(latitude1, longitude1);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                gMap.animateCamera(cameraUpdate);
                binding.ivLocation.setVisibility(View.INVISIBLE);
                binding.ivGpsLocation.setVisibility(View.INVISIBLE);
                binding.clMapView.setVisibility(View.VISIBLE);
            }
        } else {
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
            gMap.getFocusedBuilding();
            LatLng latLng = new LatLng(latitude1, longitude1);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            gMap.animateCamera(cameraUpdate);
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

                latitude1 = latitude;
                longitude1 = longitude;
                Log.e("TAG", "intView: >" + latitude + " " + longitude);
                startActivity(new Intent(CameraGPSActivity.this, GPSViewActivity.class));
                itemClick = 0;
            }

        } else {
            Log.i("TAG", "Permission is not present * * * * ** *");
            ActivityCompat.requestPermissions(CameraGPSActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusCheck();
        Log.i(TAG, "onResume: ");
        if (gMap != null) {
            Log.i(TAG, "onResume: if");
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
            gMap.getFocusedBuilding();
            LatLng latLng = new LatLng(latitude1, longitude1);
            cameraGPSDataModel.setLatitude(latitude1);
            cameraGPSDataModel.setLongitude(longitude1);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            gMap.animateCamera(cameraUpdate);
            binding.ivLocation.setVisibility(View.INVISIBLE);
            binding.ivGpsLocation.setVisibility(View.GONE);
            binding.clMapView.setVisibility(View.VISIBLE);
            Log.i(TAG, "onResume :" + latitude1 + " , " + longitude1);
        }
    }

    private void checkEditMode() {
        if (Constant.editMode) {
            CameraGPSDataModel cameraGPSViewModel = Constant.customerModel.getCameraGPSDataModel();
            this.cameraGPSDataModel = cameraGPSViewModel;
            if (cameraGPSDataModel.getImgPath() != null) {
                bitmap = BitmapFactory.decodeFile(cameraGPSViewModel.getImgPath());
                binding.ivStorePic.setImageBitmap(bitmap);
                binding.ivCamera.setVisibility(View.INVISIBLE);
            }
        }
    }

}