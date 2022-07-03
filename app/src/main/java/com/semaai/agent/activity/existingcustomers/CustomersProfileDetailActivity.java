package com.semaai.agent.activity.existingcustomers;

import static com.semaai.agent.activity.newcustomer.GPSViewActivity.latitude1;
import static com.semaai.agent.activity.newcustomer.GPSViewActivity.longitude1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.newcustomer.GPSViewActivity;
import com.semaai.agent.databinding.ActivityCustomersProfileDetailBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.existingcustomers.CustomerDetailsModel;
import com.semaai.agent.model.existingcustomers.UpdateCustomerModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.services.GpsTracker;
import com.semaai.agent.utils.CameraUtils;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ItemOnClick;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.existingcustomers.CustomersProfileDetailViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class CustomersProfileDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CustomersProfileDetailViewModel customersProfileDetailViewModel;
    private ActivityCustomersProfileDetailBinding binding;
    String TAG = CustomersProfileDetailActivity.class.getSimpleName() + "-->";
    private CustomerModel customerModel;
    private Dialog progressDialog;
    private CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
    private double latitudes, longitudes;
    private GoogleMap gMap;
    int itemClick;
    public static final int MediaTypeImage = 1;
    private static final int CameraCaptureImageRequestCode = 100;
    private String imageStoragePath;
    private Bitmap decodedByteBitmap;
    private boolean imgFlag;
    public static double lati, longi;
    boolean update = false;
    private static final int REQUEST_CROP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customersProfileDetailViewModel = ViewModelProviders.of(this).get(CustomersProfileDetailViewModel.class);
        binding = DataBindingUtil.setContentView(CustomersProfileDetailActivity.this, R.layout.activity_customers_profile_detail);
        binding.setLifecycleOwner(this);
        binding.setCustomersProfileDetailViewModel(customersProfileDetailViewModel);

        isPermissionGranted();
        intiView();
        apiCall();
        onClick();
    }

    private void apiCall() {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        Log.i("-->", "check cookie :" + separated[1]);
        AndroidNetworking.get(ApiEndPoints.existingCustomerDetails + customerModel.getCustomerListModel().getId())
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: customer " + response);
                        try {
                            JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                customerDetailsModel = new CustomerDetailsModel();
                                customerDetailsModel.setName(jsonObject.getString(ApiStringModel.name));
                                customerDetailsModel.setPhoneNumber(jsonObject.getString(ApiStringModel.mobileNumber));
                                customerDetailsModel.setCompanyName(jsonObject.getString(ApiStringModel.companyName));
                                customerDetailsModel.setStreet(jsonObject.getString(ApiStringModel.street));
                                customerDetailsModel.setZip(jsonObject.getString(ApiStringModel.zip));
                                customerDetailsModel.setLatitude(jsonObject.getString(ApiStringModel.latitude));
                                lati = Double.parseDouble(jsonObject.getString(ApiStringModel.latitude));
                                longi = Double.parseDouble(jsonObject.getString(ApiStringModel.longitude));
                                customerDetailsModel.setLongitude(jsonObject.getString(ApiStringModel.longitude));
                                customerDetailsModel.setStoreImage(jsonObject.getString(ApiStringModel.storeImage));
                                customerDetailsModel.setProfileImage(jsonObject.getString(ApiStringModel.profileImage));
                                customerDetailsModel.setSalesperson(jsonObject.getString(ApiStringModel.salesperson));

                                //group
                                try {
                                    JSONObject groupobject = jsonObject.getJSONObject(ApiStringModel.group);
                                    customerDetailsModel.setGroupID(groupobject.getString(ApiStringModel.id));
                                    customerDetailsModel.setGroup(groupobject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    customerDetailsModel.setGroup(getString(R.string.group));
                                    Log.e(TAG, "group not found");
                                }

                                //country
                                try {
                                    JSONObject countryObject = jsonObject.getJSONObject(ApiStringModel.country);
                                    customerDetailsModel.setCountryID(countryObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setCountry(countryObject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    Log.e(TAG, "country not found");
                                }

                                //state
                                try {
                                    JSONObject stateObject = jsonObject.getJSONObject(ApiStringModel.state);
                                    customerDetailsModel.setStateID(stateObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setState(stateObject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    customerDetailsModel.setState(getString(R.string.province));
                                    Log.e(TAG, "state not found");
                                }

                                //districts
                                try {
                                    JSONObject districtsObject = jsonObject.getJSONObject(ApiStringModel.districts);
                                    customerDetailsModel.setDistrictID(districtsObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setDistrict(districtsObject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    customerDetailsModel.setDistrict(getString(R.string.district));
                                    Log.e(TAG, "districts not found");
                                }
                                //sub_districts
                                try {
                                    JSONObject subDistrictsObject = jsonObject.getJSONObject(ApiStringModel.subDistricts);
                                    customerDetailsModel.setSubDistrictsID(subDistrictsObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setSubDistricts(subDistrictsObject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    customerDetailsModel.setSubDistricts(getString(R.string.subDistrict));
                                    Log.e(TAG, "sub_districts not found");
                                }
                                //village
                                try {
                                    JSONObject villageObject = jsonObject.getJSONObject(ApiStringModel.village);
                                    customerDetailsModel.setVillageID(villageObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setVillage(villageObject.getString(ApiStringModel.name));
                                } catch (Exception e) {
                                    customerDetailsModel.setVillage(getString(R.string.village));
                                    Log.e(TAG, "village not found");
                                }

                            }
                            setData();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            ToastMsg.showToast(getApplicationContext(), getString(R.string.errorMessage));
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>><<<" + error.getMessage().toString());
                        progressDialog.dismiss();
                        ToastMsg.showToast(getApplicationContext(), getString(R.string.errorMessage));
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (!customerDetailsModel.getProfileImage().equals("false")) {
            byte[] decodedString = Base64.decode(customerDetailsModel.getProfileImage(), Base64.DEFAULT);
            decodedByteBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this).load(decodedByteBitmap).encodeQuality(100).circleCrop().into(binding.ivProfile);
            Glide.with(this).load(decodedByteBitmap).encodeQuality(100).into(binding.ivStorePhoto);
        }
        Constant.whichCameraImage = 1;
        Constant.cameraImage = decodedByteBitmap;

        String number = customerDetailsModel.getPhoneNumber().replaceAll("\\w(?=\\w{3})", "*");
        binding.tvName.setText(customerDetailsModel.getName());
        binding.tvPhoneNumber.setText(number);
        binding.tvCompany.setText(customerDetailsModel.getCompanyName());
        binding.tvGroupName.setText(" " + customerDetailsModel.getGroup());
        binding.tvState.setText(customerDetailsModel.getState() + ", " + customerDetailsModel.getZip());
        binding.tvSubDistricts.setText(customerDetailsModel.getSubDistricts() + ", " + customerDetailsModel.getDistrict() + ",");
        binding.tvStreetName.setText(customerDetailsModel.getStreet() + ", " + customerDetailsModel.getVillage() + ",");
        setGoogleMap();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void setGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        GpsTracker gpsTracker = new GpsTracker(CustomersProfileDetailActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude1 = lati;
            longitude1 = longi;
        }
        mapFragment.getMapAsync(this);
    }

    private void intiView() {
        Constant.onUpdate = true;
        binding.rlRegister.tvHeader.setText(getString(R.string.profile));
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.rlRegister.tvTopBar.setText(customerModel.getCustomerListModel().getName());
        Log.e("TAG", "intiView: >>>" + customerModel.getCustomerListModel().getId());

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        if (Constant.whichCustomer.equals("my")) {
            binding.ivEdit.setVisibility(View.VISIBLE);
            binding.cvCameraEdit.setVisibility(View.VISIBLE);
            binding.cvLocationEdit.setVisibility(View.VISIBLE);
        } else {
            binding.ivEdit.setVisibility(View.GONE);
            binding.cvCameraEdit.setVisibility(View.GONE);
            binding.cvLocationEdit.setVisibility(View.GONE);
        }
    }

    private void onClick() {
        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerModel.setCustomerDetailsModel(customerDetailsModel);
                Intent intent = new Intent(CustomersProfileDetailActivity.this, CustomersProfileUpdateActivity.class);
                intent.putExtra("sample", customerModel);
                intent.putExtra("imgFlag", imgFlag);
                startActivity(intent);
            }
        });

        binding.ivGallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
            }
        });

        binding.ivStorePhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (!customerDetailsModel.getStoreImage().equals("false")) {
                    startActivity(new Intent(CustomersProfileDetailActivity.this, ShowImageActivity.class));
                }
            }
        });

        binding.cvCameraEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (Constant.whichCustomer.equals("my")) {
                    itemClick = 1;
                    onItemClick();
                }
            }
        });

        binding.cvLocationEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (Constant.whichCustomer.equals("my")) {
                    itemClick = 2;
                    onItemClick();
                }
            }
        });


        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        if (!Constant.whichCustomer.equals("my")) {
            gMap.getUiSettings().setScrollGesturesEnabled(false);
            gMap.getUiSettings().setZoomGesturesEnabled(false);
        }
        if (latitude1==0 && longitudes == 0){
            binding.clNoGPS.setVisibility(View.VISIBLE);
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
                if (!update) {
                    latitude1 = lati;
                    longitude1 = longi;
                }
                startActivity(new Intent(CustomersProfileDetailActivity.this, GPSViewActivity.class));
                itemClick = 0;
            }

        } else {
            Log.i("TAG", "Permission is not present * * * * ** *");
            ActivityCompat.requestPermissions(CustomersProfileDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);

        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MediaTypeImage);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityIfNeeded(intent, CameraCaptureImageRequestCode);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraCaptureImageRequestCode) {
            if (resultCode == RESULT_OK) {
                try {
                    ExifInterface ei = new ExifInterface(imageStoragePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Common.rotation = orientation;

                    Constant.whichCameraImage = 0;
                    Constant.cameraImage1 = Uri.fromFile(new File(imageStoragePath));
                    Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageStoragePath)));

                    Common.SetTempBitmap(CustomersProfileDetailActivity.this, Constant.cameraImage);
                    Intent intent = new Intent(CustomersProfileDetailActivity.this, CropActivity.class);
                    startActivityIfNeeded(intent, REQUEST_CROP);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("TAG", "onActivityResult:-------------- " + imageStoragePath);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.sorryFailedToCaptureImage), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                customerDetailsModel.setStoreImage(Common.GetTempPath(CustomersProfileDetailActivity.this));
                binding.ivStorePhoto.setImageURI(Uri.fromFile(new File(Common.GetTempPath(CustomersProfileDetailActivity.this))));

                Log.i(TAG, "EditImageSet");
                imgFlag = true;
                Glide.with(this)
                        .load(new File(Common.GetTempPath(CustomersProfileDetailActivity.this)))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(binding.ivProfile);
                imageUpdate();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            }
        }

        GpsTracker gpsTracker = new GpsTracker(CustomersProfileDetailActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude1 = gpsTracker.getLatitude();
            longitude1 = gpsTracker.getLongitude();
            customerDetailsModel.setLatitude(String.valueOf(latitude1));
            customerDetailsModel.setLongitude(String.valueOf(longitude1));
            Log.e("latitude1", "" + latitude1 + "   " + longitude1);
        }
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

    private void imageUpdate() {
        UpdateCustomerModel updateCustomerModel = new UpdateCustomerModel();
        String imageStringBase64;
        ItemOnClick itemOnClick = new ItemOnClick();
        if (imgFlag) {
            Constant.updateImagePath = customerDetailsModel.getStoreImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(customerDetailsModel.getStoreImage());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            imageStringBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            Log.e(TAG, "onClick: //..// if");
        } else {
            imageStringBase64 = customerDetailsModel.getStoreImage();
            Log.e(TAG, "onClick: //..// else");
        }

        Log.e(TAG, "onClick: //..// " + imageStringBase64);
        updateCustomerModel.setId(customerModel.getCustomerListModel().getId());
        updateCustomerModel.setStateId(customerDetailsModel.getStateID());
        updateCustomerModel.setDistrictId(customerDetailsModel.getDistrictID());
        updateCustomerModel.setSubDistrictId(customerDetailsModel.getSubDistrictsID());
        updateCustomerModel.setVillageId(customerDetailsModel.getVillageID());
        updateCustomerModel.setStreetName(customerDetailsModel.getStreet());
        updateCustomerModel.setLatitude(customerDetailsModel.getLatitude());
        updateCustomerModel.setLongitude(customerDetailsModel.getLongitude());
        updateCustomerModel.setImgPath(imageStringBase64);
        updateCustomerModel.setZip(customerDetailsModel.getZip());
        updateCustomerModel.setGroupId(customerDetailsModel.getGroupID());
        customerModel.setUpdateCustomerModel(updateCustomerModel);
        itemOnClick.UpdateImageOnClick(customerModel, progressDialog, CustomersProfileDetailActivity.this);
        customerModel.setUpdateCustomerModel(null);
        Constant.updateImage = true;
    }

    private void locationUpdate() {
        UpdateCustomerModel updateCustomerModel = new UpdateCustomerModel();
        String imageStringBase64;
        ItemOnClick itemOnClick = new ItemOnClick();
        if (imgFlag) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(customerDetailsModel.getStoreImage());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            imageStringBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            Log.e(TAG, "onClick: //..// if");
        } else {
            imageStringBase64 = customerDetailsModel.getStoreImage();
            Log.e(TAG, "onClick: //..// else");
        }

        Log.e(TAG, "onClick: //..// " + imageStringBase64);
        updateCustomerModel.setId(customerModel.getCustomerListModel().getId());
        updateCustomerModel.setStateId(customerDetailsModel.getStateID());
        updateCustomerModel.setDistrictId(customerDetailsModel.getDistrictID());
        updateCustomerModel.setSubDistrictId(customerDetailsModel.getSubDistrictsID());
        updateCustomerModel.setVillageId(customerDetailsModel.getVillageID());
        updateCustomerModel.setStreetName(customerDetailsModel.getStreet());
        updateCustomerModel.setLatitude(String.valueOf(latitude1));
        updateCustomerModel.setLongitude(String.valueOf(longitude1));
        updateCustomerModel.setImgPath(imageStringBase64);
        updateCustomerModel.setZip(customerDetailsModel.getZip());
        updateCustomerModel.setGroupId(customerDetailsModel.getGroupID());
        customerModel.setUpdateCustomerModel(updateCustomerModel);
        itemOnClick.UpdateImageOnClick(customerModel, progressDialog, CustomersProfileDetailActivity.this);
        customerModel.setUpdateCustomerModel(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: ");
        if (gMap != null) {
            Log.i("TAG", "onResume: if");
            if (Constant.locationChange) {
                locationUpdate();
                Constant.locationChange = false;
                update = true;
                Log.i(TAG, "location update");
            } else {
                if (!update) {
                    latitude1 = lati;
                    longitude1 = longi;
                }
            }

            gMap.clear();
            Log.i("TAG", "onResume: >>" + latitude1 + " " + lati);
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
            gMap.getFocusedBuilding();
            LatLng latLng = new LatLng(latitude1, longitude1);
            customerDetailsModel.setLatitude(String.valueOf(latitude1));
            customerDetailsModel.setLongitude(String.valueOf(longitude1));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            gMap.animateCamera(cameraUpdate);
            binding.clNoGPS.setVisibility(View.GONE);
            if (latitude1==0 && longitudes == 0){
                binding.clNoGPS.setVisibility(View.VISIBLE);
            }

        }
    }

}