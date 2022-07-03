package com.semaai.agent.activity.newcustomer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityCustomerDetailBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.newcustomer.AddressDataModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.model.newcustomer.CustomerRegisterModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ItemOnClick;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.newcustomer.CustomerDetailViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CustomerDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = CustomerDetailActivity.class.getSimpleName() + "-->";
    private double latitude1;
    private double longitude1;
    private GoogleMap gMap;
    CustomerModel customerModel;
    private CustomerDetailViewModel viewModel;
    private ActivityCustomerDetailBinding binding;
    CameraGPSDataModel cameraGPSDataModel;
    ItemOnClick itemOnClick = new ItemOnClick();
    private Dialog progressDialog;
    private int checkClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CustomerDetailViewModel.class);
        binding = DataBindingUtil.setContentView(CustomerDetailActivity.this, R.layout.activity_customer_detail);
        binding.setLifecycleOwner(this);
        binding.setCustomerDetailViewModel(viewModel);

        intiView();
        setModel();
        setData();
        onClick();
    }

    private void intiView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.clBackAlert.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        checkClick = 0;
        binding.clBackAlert.setVisibility(View.VISIBLE);
    }

    private void setModel() {
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.title.tvTopBar.setText(customerModel.getCustomerRegisterModel().getUserName());
        Log.e(TAG, "setModel: >>>>>  " + new Gson().toJson(customerModel));
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        CustomerRegisterModel customerRegisterModel = customerModel.getCustomerRegisterModel();
        binding.tvName.setText(customerRegisterModel.getUserName());
        binding.tvPhone.setText(customerRegisterModel.getUserPhone());
        AddressDataModel addressDataModel = customerModel.getAddressDataModel();
        binding.tvGroup.setText(addressDataModel.getGroup());
        binding.tvStreetName.setText(addressDataModel.getStreetName());
        binding.tvFullAddress.setText(addressDataModel.getVillage() + ", " + addressDataModel.getSubDistrict() + ", " + addressDataModel.getDistrict() + ", " + addressDataModel.getProvince() + ", " + addressDataModel.getPostCode());
        cameraGPSDataModel = customerModel.getCameraGPSDataModel();

        if (cameraGPSDataModel.getImgPath() != null) {
            binding.ivStorePhoto.setImageURI(Uri.fromFile(new File(cameraGPSDataModel.getImgPath())));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        latitude1 = cameraGPSDataModel.getLatitude();
        longitude1 = cameraGPSDataModel.getLongitude();
        gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
        gMap.getFocusedBuilding();
        LatLng latLng = new LatLng(latitude1, longitude1);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

        binding.cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.cl2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
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

        binding.title.ivBackArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick = 0;
                onBackPressed();
            }
        });

        binding.cvBtnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "setModel: >>>>> 1 " + new Gson().toJson(customerModel));
                binding.clBackAlert.setVisibility(View.GONE);
                binding.cvBtnApprove.setOnClickListener(null);
                progressDialog.show();
                RequestAPI apiManager = new RequestAPI(CustomerDetailActivity.this);
                onMobikulAddUser(customerModel, apiManager, CustomerDetailActivity.this, progressDialog);
            }
        });
        binding.cvBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                Constant.editMode = true;
                Constant.customerModel = customerModel;
                Intent i = new Intent(CustomerDetailActivity.this, CustomerRegistrationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkClick = 1;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick = 2;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onMobikulAddUser(CustomerModel customerModel, RequestAPI apiManager, CustomerDetailActivity customerDetailActivity, Dialog progressDialog) {
        try {
            CustomerRegisterModel customerRegisterModel = customerModel.getCustomerRegisterModel();

            JSONObject data = new JSONObject();
            data.put("name", customerRegisterModel.getUserName());
            data.put("login", customerRegisterModel.getUserPhone());
            data.put("password", Common.generatePassword());
            data.put("referralCode", "");
            data.put("isSocialLogin", false);
            data.put("fcmToken", "");
            data.put("customerId", "");
            data.put("fcmDeviceId", "");
            Log.e(TAG, "onMobikulAddUser: " + data.toString());
            Log.e(TAG, "onMobikulAddUser url : " + ApiEndPoints.mobikulAddUser);
            apiManager.sendPostRequestWithCookieWithAuthorization_Key(progressDialog, ApiEndPoints.mobikulAddUser, data.toString(),
                    new APIResponse() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Mobikul  onResponse: " + response);
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e(TAG, "success :" + response.getBoolean("success"));
                                    Log.e(TAG, "userId :" + response.getInt("userId"));

                                    JSONObject cred = response.getJSONObject("cred");

                                    Log.e(TAG, "login :" + cred.getString("login"));
                                    Log.e(TAG, "pwd :" + cred.getString("pwd"));

                                    itemOnClick.onApproveClickQA(customerModel,
                                            apiManager,
                                            CustomerDetailActivity.this,
                                            progressDialog,
                                            response.getInt("userId"),
                                            cred.getString("pwd"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                                Log.e(TAG, "error :" + e.getMessage());
                            }
                        }

                        @Override
                        public void onError(VolleyError volleyError) {
                            Log.e(TAG, "onError: " + volleyError.getMessage());
                            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));

                        }

                        @Override
                        public void onNetworkResponse(NetworkResponse response) {
                            Log.e(TAG, "Mobikul  onNetworkResponse: " + response);
                            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                        }
                    });


        } catch (Exception e) {
            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
        }
    }


}