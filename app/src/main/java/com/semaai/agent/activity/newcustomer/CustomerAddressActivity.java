package com.semaai.agent.activity.newcustomer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.adapter.newCustomers.CountyAdaptor;
import com.semaai.agent.adapter.newCustomers.DistrictAdaptor;
import com.semaai.agent.adapter.newCustomers.GroupAdaptor;
import com.semaai.agent.adapter.newCustomers.ProvinceAdaptor;
import com.semaai.agent.adapter.newCustomers.VillageAdaptor;
import com.semaai.agent.databinding.ActivityCustomerAddressBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.RVItemModel;
import com.semaai.agent.model.newcustomer.AddressDataModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.StringValidation;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.newcustomer.AddressViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerAddressActivity extends AppCompatActivity implements OnItemClickListener {

    AddressViewModel registerAddressViewModel;
    CardView registerAddressSave;
    ActivityCustomerAddressBinding binding;
    ProvinceAdaptor provinceAdaptor;
    DistrictAdaptor districtAdaptor;
    CountyAdaptor countyAdaptor;
    VillageAdaptor villageAdaptor;
    GroupAdaptor groupAdaptor;
    RecyclerView rvProvince, rvDistrict, rvCounty, rvVillage, rvGroup;
    TextView txtProvince, txtDistrict, txtCounty, txtVillage, txtGroup;
    ImageView downArrowProvince, upArrowProvince, downArrowDistrict, upArrowDistrict, downArrowCounty, upArrowCounty,
            downArrowVillage, upArrowVillage, downArrowGroup, upArrowGroup;
    String TAG = CustomerAddressActivity.class.getSimpleName() + "-->";
    StringValidation stringValidation = new StringValidation();
    RequestAPI requestAPI = new RequestAPI(this);
    CustomerModel customerModel;
    private Dialog progressDialog;
    boolean back = true, msgShow = true;
    private int checkClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerAddressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        binding = DataBindingUtil.setContentView(CustomerAddressActivity.this, R.layout.activity_customer_address);
        binding.setLifecycleOwner(this);
        binding.setRegisterAddressViewModel(registerAddressViewModel);

        intiView();
        apiCall();
        setViews();
        onclick();
        checkEditMode();
    }

    private void intiView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.clTitle.tvTopBar.setText(customerModel.getCustomerRegisterModel().getUserName());

        binding.clBackAlert.setVisibility(View.GONE);
    }

    private void apiCall() {
        progressDialog.show();
        requestAPI.sendGetRequest(ApiEndPoints.statesURL + Constant.companyId, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomerAddressActivity.this);
                    progressDialog.dismiss();
                    return;
                }
                try {
                    ArrayList<RVItemModel> items = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {
                            items.add(new RVItemModel(jsonObject.getString(ApiStringModel.name), jsonObject.getInt(ApiStringModel.id)));
                        }
                    }
                    provinceAdaptor = new ProvinceAdaptor(items, CustomerAddressActivity.this, 0);
                    rvProvince.setAdapter(provinceAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });

        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.groupURL)
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: group" + response);
                        try {
                            ArrayList<RVItemModel> items = new ArrayList<>();

                            JSONArray data = response.getJSONArray(ApiStringModel.data);
                            JSONArray array = (JSONArray) data.get(0);
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray singleArray = (JSONArray) array.get(i);
                                Log.e(TAG, "onResponse: group >>> " + singleArray);

                                if (i == 0) {
                                    if (Constant.setLanguage.equals("in")) {
                                        items.add(new RVItemModel("Kelompok Biasa", (Number) singleArray.get(1)));
                                    } else {
                                        items.add(new RVItemModel("Default Group", (Number) singleArray.get(1)));
                                    }
                                } else if (i == 1) {
                                    if (Constant.setLanguage.equals("in")) {
                                        items.add(new RVItemModel("Toko Tani", (Number) singleArray.get(1)));
                                    } else {
                                        items.add(new RVItemModel("Retailer", (Number) singleArray.get(1)));
                                    }
                                } else if (i == 2) {
                                    if (Constant.setLanguage.equals("in")) {
                                        items.add(new RVItemModel("Kelompok Tani", (Number) singleArray.get(1)));
                                    } else {
                                        items.add(new RVItemModel("Farmer Group", (Number) singleArray.get(1)));
                                    }
                                } else {
                                    items.add(new RVItemModel(singleArray.get(0).toString(), (Number) singleArray.get(1)));
                                }

                            }
                            groupAdaptor = new GroupAdaptor(items, CustomerAddressActivity.this, 1);
                            rvGroup.setAdapter(groupAdaptor);
                            progressDialog.dismiss();
                            Log.i(TAG, "onResponse: " + array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: group" + anError);
                        progressDialog.dismiss();
                    }
                });
    }

    private void progressDiaLogSet(CustomerAddressActivity customerAddressActivity) {
        Dialog progressDialog = new Dialog(customerAddressActivity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_session_expired_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        CardView cvReLogin = progressDialog.findViewById(R.id.cv_reLogin);
        cvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(customerAddressActivity, StaffLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                progressDialog.dismiss();
            }
        });
    }

    private void onclick() {

        binding.cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.cl1.setOnTouchListener(new View.OnTouchListener() {
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
                    Common.openActivity(CustomerAddressActivity.this, DashboardActivity.class);
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
                checkClick = 0;
            }
        });

        binding.clTitle.ivBackArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick = 0;
                onBackPressed();
            }
        });

        binding.cvProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (rvProvince.getVisibility() == View.VISIBLE) {
                    downArrowProvince.setVisibility(View.VISIBLE);
                    upArrowProvince.setVisibility(View.GONE);
                    rvProvince.setVisibility(View.GONE);
                } else {
                    downArrowProvince.setVisibility(View.GONE);
                    upArrowProvince.setVisibility(View.VISIBLE);
                    rvProvince.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cvDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (rvDistrict.getVisibility() == View.VISIBLE) {
                    downArrowDistrict.setVisibility(View.VISIBLE);
                    upArrowDistrict.setVisibility(View.GONE);
                    rvDistrict.setVisibility(View.GONE);
                } else {
                    downArrowDistrict.setVisibility(View.GONE);
                    upArrowDistrict.setVisibility(View.VISIBLE);
                    rvDistrict.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cvCounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (rvCounty.getVisibility() == View.VISIBLE) {
                    downArrowCounty.setVisibility(View.VISIBLE);
                    upArrowCounty.setVisibility(View.GONE);
                    rvCounty.setVisibility(View.GONE);
                } else {
                    downArrowCounty.setVisibility(View.GONE);
                    upArrowCounty.setVisibility(View.VISIBLE);
                    rvCounty.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cvVillage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (rvVillage.getVisibility() == View.VISIBLE) {
                    downArrowVillage.setVisibility(View.VISIBLE);
                    upArrowVillage.setVisibility(View.GONE);
                    rvVillage.setVisibility(View.GONE);
                } else {
                    downArrowVillage.setVisibility(View.GONE);
                    upArrowVillage.setVisibility(View.VISIBLE);
                    rvVillage.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cvGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (rvGroup.getVisibility() == View.VISIBLE) {
                    downArrowGroup.setVisibility(View.VISIBLE);
                    upArrowGroup.setVisibility(View.GONE);
                    rvGroup.setVisibility(View.GONE);
                } else {
                    downArrowGroup.setVisibility(View.GONE);
                    upArrowGroup.setVisibility(View.VISIBLE);
                    rvGroup.setVisibility(View.VISIBLE);
                }

            }
        });

        registerAddressSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                stringValidation.customerAddress(binding, CustomerAddressActivity.this, customerModel, progressDialog);
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


    @Override
    public void onBackPressed() {
        checkClick = 0;
        binding.clBackAlert.setVisibility(View.VISIBLE);
    }

    private void setViews() {
        //province views
        rvProvince = binding.rvProvinceList;
        rvProvince.setLayoutManager(new LinearLayoutManager(this));
        rvProvince.setHasFixedSize(true);
        txtProvince = binding.tvProvince;
        downArrowProvince = binding.ivDownArrowProvince;
        upArrowProvince = binding.ivUpArrowProvince;

        //district views
        rvDistrict = binding.rvDistrictList;
        rvDistrict.setLayoutManager(new LinearLayoutManager(this));
        rvDistrict.setHasFixedSize(true);
        txtDistrict = binding.tvDistrict;
        downArrowDistrict = binding.ivDownArrowDistrict;
        upArrowDistrict = binding.ivUpArrowDistrict;

        //county views
        rvCounty = binding.rvCountyList;
        rvCounty.setLayoutManager(new LinearLayoutManager(this));
        rvCounty.setHasFixedSize(true);
        txtCounty = binding.tvCounty;
        downArrowCounty = binding.ivDownArrowCounty;
        upArrowCounty = binding.ivUpArrowCounty;

        //village views
        rvVillage = binding.rvVillageList;
        rvVillage.setLayoutManager(new LinearLayoutManager(this));
        rvVillage.setHasFixedSize(true);
        txtVillage = binding.tvVillage;
        downArrowVillage = binding.ivDownArrowVillage;
        upArrowVillage = binding.ivUpArrowVillage;

        //group views
        rvGroup = binding.rvGroupList;
        rvGroup.setLayoutManager(new LinearLayoutManager(this));
        rvGroup.setHasFixedSize(true);
        txtGroup = binding.tvGroup;
        downArrowGroup = binding.ivDownArrowGroup;
        upArrowGroup = binding.ivUpArrowGroup;

        //save button
        registerAddressSave = binding.cvRegisterAddressSave;
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(String name, RVItemModel rvItemModel, int isFilter) {
        switch (name) {
            case "province":
                progressDialog.show();
                txtProvince.setText(rvItemModel.item);
                txtProvince.setTag(rvItemModel.id);
                downArrowProvince.setVisibility(View.VISIBLE);
                upArrowProvince.setVisibility(View.GONE);
                rvProvince.setVisibility(View.GONE);
                binding.tvDistrict.setText(getString(R.string.district));
                binding.tvCounty.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                fillDistrict(rvItemModel.id);
                break;
            case "district":
                progressDialog.show();
                txtDistrict.setText(rvItemModel.item);
                txtDistrict.setTag(rvItemModel.id);
                downArrowDistrict.setVisibility(View.VISIBLE);
                upArrowDistrict.setVisibility(View.GONE);
                rvDistrict.setVisibility(View.GONE);
                binding.tvCounty.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                fillSubDistrict(rvItemModel.id);
                break;
            case "country":
                progressDialog.show();
                txtCounty.setText(rvItemModel.item);
                txtCounty.setTag(rvItemModel.id);
                downArrowCounty.setVisibility(View.VISIBLE);
                upArrowCounty.setVisibility(View.GONE);
                rvCounty.setVisibility(View.GONE);
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                fillVillage(rvItemModel.id);
                break;
            case "village":
                txtVillage.setText(rvItemModel.item);
                txtVillage.setTag(rvItemModel.id);
                binding.etPostCode.setText(rvItemModel.zipCode);
                downArrowVillage.setVisibility(View.VISIBLE);
                upArrowVillage.setVisibility(View.GONE);
                rvVillage.setVisibility(View.GONE);
                break;
            case "group":
                txtGroup.setText(rvItemModel.item);
                txtGroup.setTag(rvItemModel.id);
                downArrowGroup.setVisibility(View.VISIBLE);
                upArrowGroup.setVisibility(View.GONE);
                rvGroup.setVisibility(View.GONE);
                break;
        }
    }

    private void fillDistrict(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.districtURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomerAddressActivity.this);
                    return;
                }
                try {
                    ArrayList<RVItemModel> items = new ArrayList<>();
                    if (response.getInt(ApiStringModel.status) == 200 && response.getString(ApiStringModel.message).equals("Pass")) {
                        JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {
                                items.add(new RVItemModel(jsonObject.getString(ApiStringModel.name), jsonObject.getInt(ApiStringModel.id)));
                            }
                        }
                    } else {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    districtAdaptor = new DistrictAdaptor(items, CustomerAddressActivity.this, 0);
                    rvDistrict.setAdapter(districtAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        countyAdaptor = new CountyAdaptor(new ArrayList<>(), CustomerAddressActivity.this, 0);
        rvCounty.setAdapter(countyAdaptor);
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomerAddressActivity.this, 0);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillSubDistrict(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.subDistrictURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomerAddressActivity.this);
                    return;
                }
                try {
                    ArrayList<RVItemModel> items = new ArrayList<>();
                    if (response.getInt(ApiStringModel.status) == 200 && response.getString(ApiStringModel.message).equals("Pass")) {
                        JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {
                                items.add(new RVItemModel(jsonObject.getString(ApiStringModel.name), jsonObject.getInt(ApiStringModel.id)));
                            }
                        }
                    } else {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    countyAdaptor = new CountyAdaptor(items, CustomerAddressActivity.this, 0);
                    rvCounty.setAdapter(countyAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomerAddressActivity.this, 0);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillVillage(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.villageURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomerAddressActivity.this);
                    return;
                }
                try {
                    ArrayList<RVItemModel> items = new ArrayList<>();
                    if (response.getInt(ApiStringModel.status) == 200 && response.getString(ApiStringModel.message).equals("Pass")) {

                        JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {
                                items.add(new RVItemModel(jsonObject.getString(ApiStringModel.name), jsonObject.getInt(ApiStringModel.id), jsonObject.getString(ApiStringModel.zip)));
                            }
                        }
                    } else {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    villageAdaptor = new VillageAdaptor(items, CustomerAddressActivity.this, 0);
                    rvVillage.setAdapter(villageAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomerAddressActivity.this, getString(R.string.errorMessage));
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
    }

    private void checkEditMode() {
        if (Constant.editMode) {
            AddressDataModel addressDataModel = Constant.customerModel.getAddressDataModel();
            txtProvince.setText(addressDataModel.getProvince());
            txtProvince.setTag(addressDataModel.getProvinceId());
            txtDistrict.setText(addressDataModel.getDistrict());
            txtDistrict.setTag(addressDataModel.getDistrictId());
            txtCounty.setText(addressDataModel.getSubDistrict());
            txtCounty.setTag(addressDataModel.getSubDistrictId());
            txtVillage.setText(addressDataModel.getVillage());
            txtVillage.setTag(addressDataModel.getVillageId());
            txtGroup.setText(addressDataModel.getGroup());
            txtGroup.setTag(addressDataModel.getGroupId());
            registerAddressViewModel.PostCode.setValue(addressDataModel.getPostCode());
            registerAddressViewModel.StreetName.setValue(addressDataModel.getStreetName());
            fillDistrict(Integer.parseInt(addressDataModel.getProvinceId()));
            fillSubDistrict(Integer.parseInt(addressDataModel.getDistrictId()));
            fillVillage(Integer.parseInt(addressDataModel.getSubDistrictId()));
        }
    }

}