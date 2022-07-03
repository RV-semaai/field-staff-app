package com.semaai.agent.activity.existingcustomers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import com.semaai.agent.activity.newcustomer.CustomerAddressActivity;
import com.semaai.agent.adapter.newCustomers.CountyAdaptor;
import com.semaai.agent.adapter.newCustomers.DistrictAdaptor;
import com.semaai.agent.adapter.newCustomers.GroupAdaptor;
import com.semaai.agent.adapter.newCustomers.ProvinceAdaptor;
import com.semaai.agent.adapter.newCustomers.VillageAdaptor;
import com.semaai.agent.databinding.ActivityCustomersProfileUpdateBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.RVItemModel;
import com.semaai.agent.model.existingcustomers.UpdateCustomerModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ItemOnClick;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.existingcustomers.CustomersProfileUpdateViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class CustomersProfileUpdateActivity extends AppCompatActivity implements OnItemClickListener {

    private CustomersProfileUpdateViewModel customersProfileUpdateViewModel;
    private ActivityCustomersProfileUpdateBinding binding;
    private CustomerModel customerModel;
    private Dialog progressDialog;
    RequestAPI requestAPI = new RequestAPI(this);
    ProvinceAdaptor provinceAdaptor;
    DistrictAdaptor districtAdaptor;
    CountyAdaptor countyAdaptor;
    VillageAdaptor villageAdaptor;
    GroupAdaptor groupAdaptor;
    RecyclerView rvProvince, rvDistrict, rvCounty, rvVillage, rvGroup;
    TextView txtProvince, txtDistrict, txtCounty, txtVillage, txtGroup;
    String editProvince, editDistrict, editSubDistrict, editVillage, editGroup, editStreetName, editCompanyName;
    ImageView downArrowProvince, upArrowProvince, downArrowDistrict, upArrowDistrict, downArrowCounty, upArrowCounty,
            downArrowVillage, upArrowVillage, downArrowGroup, upArrowGroup;
    String TAG = CustomerAddressActivity.class.getSimpleName() + "-->";
    ItemOnClick itemOnClick = new ItemOnClick();
    UpdateCustomerModel updateCustomerModel = new UpdateCustomerModel();
    private boolean imgFlag, msgShow = true;
    private String imageStringBase64;
    private int checkClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customersProfileUpdateViewModel = ViewModelProviders.of(CustomersProfileUpdateActivity.this).get(CustomersProfileUpdateViewModel.class);
        binding = DataBindingUtil.setContentView(CustomersProfileUpdateActivity.this, R.layout.activity_customers_profile_update);
        binding.setLifecycleOwner(this);
        binding.setCustomersProfileUpdateViewModel(customersProfileUpdateViewModel);

        setViews();
        intiView();
        onClick();
    }

    private void intiView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        binding.rlRegister.tvHeader.setText(getText(R.string.profile));
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        imgFlag = i.getBooleanExtra("imgFlag", false);
        Log.e(TAG, "intView: ///////// " + imgFlag + " " + customerModel.getCustomerDetailsModel().getStoreImage());
        Log.e(TAG, "intView: <><>>>>" + customerModel.getCustomerDetailsModel().getStoreImage());
        setData();
    }


    private void setData() {
        Log.e(TAG, "setData: <<<<<" + customerModel.getCustomerDetailsModel().getLatitude() + "  " + customerModel.getCustomerDetailsModel().getLongitude());
        String number = customerModel.getCustomerDetailsModel().getPhoneNumber().replaceAll("\\w(?=\\w{3})", "*");
        binding.etName.setText(customerModel.getCustomerDetailsModel().getName());
        binding.etPhonenumber.setText(number);
        binding.etCompany.setText(customerModel.getCustomerDetailsModel().getCompanyName());
       editCompanyName = customerModel.getCustomerDetailsModel().getCompanyName();
        binding.tvProvince.setText(customerModel.getCustomerDetailsModel().getState());
        editProvince = customerModel.getCustomerDetailsModel().getState();
        binding.tvDistrict.setText(customerModel.getCustomerDetailsModel().getDistrict());
        Log.i(TAG , "get dddd : "+ customerModel.getCustomerDetailsModel().getDistrict());
        editDistrict = customerModel.getCustomerDetailsModel().getDistrict();
        binding.tvSubDistricts.setText(customerModel.getCustomerDetailsModel().getSubDistricts());
        editSubDistrict = customerModel.getCustomerDetailsModel().getSubDistricts();
        binding.tvVillage.setText(customerModel.getCustomerDetailsModel().getVillage());
        editVillage = customerModel.getCustomerDetailsModel().getVillage();
        binding.etPostCode.setText(customerModel.getCustomerDetailsModel().getZip());
        binding.etStreetName.setText(customerModel.getCustomerDetailsModel().getStreet());
        editStreetName = customerModel.getCustomerDetailsModel().getStreet();
        binding.tvGroup.setText(customerModel.getCustomerDetailsModel().getGroup());
        editGroup = customerModel.getCustomerDetailsModel().getGroup();
        binding.etSalesPerson.setText(customerModel.getCustomerDetailsModel().getSalesperson());
        //setId
        updateCustomerModel.setStateId(customerModel.getCustomerDetailsModel().getStateID());
        updateCustomerModel.setDistrictId(customerModel.getCustomerDetailsModel().getDistrictID());
        updateCustomerModel.setSubDistrictId(customerModel.getCustomerDetailsModel().getSubDistrictsID());
        updateCustomerModel.setVillageId(customerModel.getCustomerDetailsModel().getVillageID());
        try {
            updateCustomerModel.setGroupId(customerModel.getCustomerDetailsModel().getGroupID());
        } catch (Exception e) {
            Log.e(TAG, "Group not found");
        }
        Log.e(TAG, "setData: check" + customerModel.getCustomerDetailsModel().getStateID() + "  " + customerModel.getCustomerDetailsModel().getDistrictID());
        apiCall();
    }

    private void onClick() {

        binding.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cl1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nsv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    binding.clBackAlert.setVisibility(View.GONE);
                }
            });
        }

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


        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.cvProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cvProvince.setStrokeWidth(0);
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                layoutViewGone();
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
                binding.cvDistrict.setStrokeWidth(0);
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                layoutViewGone();
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
                binding.cvCounty.setStrokeWidth(0);
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                layoutViewGone();
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
                binding.cvVillage.setStrokeWidth(0);
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                layoutViewGone();
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
                binding.cvGroup.setStrokeWidth(0);
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                layoutViewGone();
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

        binding.etStreetName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutViewGone();
                binding.tvPleaseCom.setVisibility(View.INVISIBLE);
                binding.etStreetName.setBackgroundResource(R.drawable.ic_edit_details);
                return false;
            }
        });

        binding.cvSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: >>>");
                if (binding.tvProvince.getText().toString().equals("") || binding.tvProvince.getText().toString().equals(getString(R.string.province))) {
                    binding.cvProvince.setStrokeColor(getColor(R.color.red));
                    layoutViewGone();
                    binding.ivProvince.setVisibility(View.VISIBLE);
                    binding.cvProvince.setStrokeWidth(5);
                    binding.tvProvince.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else if (binding.tvDistrict.getText().toString().equals("") || binding.tvDistrict.getText().toString().equals(getString(R.string.district))) {
                    binding.cvDistrict.setStrokeColor(getColor(R.color.red));
                    layoutViewGone();
                    binding.ivDistrict.setVisibility(View.VISIBLE);
                    binding.cvDistrict.setStrokeWidth(5);
                    binding.tvDistrict.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else if (binding.tvSubDistricts.getText().toString().equals("") || binding.tvSubDistricts.getText().toString().equals(getString(R.string.subDistrict))) {
                    binding.cvCounty.setStrokeColor(getColor(R.color.red));
                    layoutViewGone();
                    binding.ivSubDistrict.setVisibility(View.VISIBLE);
                    binding.cvCounty.setStrokeWidth(5);
                    binding.tvSubDistricts.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else if (binding.tvVillage.getText().toString().equals("") || binding.tvVillage.getText().toString().equals(getString(R.string.village))) {
                    binding.cvVillage.setStrokeColor(getColor(R.color.red));
                    layoutViewGone();
                    binding.ivVillage.setVisibility(View.VISIBLE);
                    binding.cvVillage.setStrokeWidth(5);
                    binding.tvVillage.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else if (binding.tvGroup.getText().toString().equals("") || binding.tvGroup.getText().toString().equals(getString(R.string.group))) {
                    binding.cvGroup.setStrokeColor(getColor(R.color.red));
                    layoutViewGone();
                    binding.ivGroup.setVisibility(View.VISIBLE);
                    binding.cvGroup.setStrokeWidth(5);
                    binding.tvGroup.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else if (binding.etStreetName.getText().toString().equals("")) {
                    binding.etStreetName.setBackgroundResource(R.drawable.red_border);
                    layoutViewGone();
                    binding.ivStreetName.setVisibility(View.VISIBLE);
                    binding.etStreetName.requestFocus();
                    binding.tvPleaseCom.setVisibility(View.VISIBLE);
                } else {
                    if (imgFlag) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeFile(customerModel.getCustomerDetailsModel().getStoreImage());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                        byte[] imageBytes = baos.toByteArray();
                        imageStringBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        Log.e(TAG, "onClick: //..// if");
                    } else {
                        imageStringBase64 = customerModel.getCustomerDetailsModel().getStoreImage();
                        Log.e(TAG, "onClick: //..// else");
                    }

                    Log.e(TAG, "onClick: //..// " + imageStringBase64);
                    updateCustomerModel.setId(customerModel.getCustomerListModel().getId());
                   updateCustomerModel.setCompanyName(binding.etCompany.getText().toString());
                    updateCustomerModel.setStreetName(binding.etStreetName.getText().toString());
                    updateCustomerModel.setLatitude(customerModel.getCustomerDetailsModel().getLatitude());
                    updateCustomerModel.setLongitude(customerModel.getCustomerDetailsModel().getLongitude());
                    if (!customerModel.getCustomerDetailsModel().getStoreImage().equals("false")) {
                        updateCustomerModel.setImgPath(imageStringBase64);
                    } else {
                        updateCustomerModel.setImgPath("");
                    }
                    updateCustomerModel.setZip(binding.etPostCode.getText().toString());
                    customerModel.setUpdateCustomerModel(updateCustomerModel);
                    itemOnClick.UpdateOnClick(customerModel, progressDialog, CustomersProfileUpdateActivity.this);
                }
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
            public void onClick(View view) {
                checkClick = 2;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });
    }

    private void layoutViewGone() {
        binding.clBackAlert.setVisibility(View.GONE);
        binding.ivProvince.setVisibility(View.GONE);
        binding.ivDistrict.setVisibility(View.GONE);
        binding.ivSubDistrict.setVisibility(View.GONE);
        binding.ivVillage.setVisibility(View.GONE);
        binding.ivGroup.setVisibility(View.GONE);
        binding.ivStreetName.setVisibility(View.GONE);
    }

    private void apiCall() {
        progressDialog.show();
        requestAPI.sendGetRequest(ApiEndPoints.statesURL + Constant.companyId, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomersProfileUpdateActivity.this);
                    progressDialog.dismiss();
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
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                    }
                    provinceAdaptor = new ProvinceAdaptor(items, CustomersProfileUpdateActivity.this, 2);
                    binding.rvProvinceList.setAdapter(provinceAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
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
                            groupAdaptor = new GroupAdaptor(items, CustomersProfileUpdateActivity.this, 2);
                            rvGroup.setAdapter(groupAdaptor);
                            progressDialog.dismiss();
                            Log.i(TAG, "onResponse: " + array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: group" + anError);
                    }
                });
        try {
            fillDistrict(Integer.parseInt(customerModel.getCustomerDetailsModel().getStateID()));
            fillSubDistrict(Integer.parseInt(customerModel.getCustomerDetailsModel().getDistrictID()));
            fillVillage(Integer.parseInt(customerModel.getCustomerDetailsModel().getSubDistrictsID()));
        } catch (Exception e) {
            Log.e(TAG, "profile not found");
        }

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
        txtCounty = binding.tvSubDistricts;
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

    }


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
                binding.tvSubDistricts.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                updateCustomerModel.setStateId(String.valueOf(rvItemModel.id));
                Log.e(TAG, "onResponse: <><><><><" + ApiEndPoints.districtURL + rvItemModel.id);
                fillDistrict((Integer) rvItemModel.id);
                break;
            case "district":
                progressDialog.show();
                txtDistrict.setText(rvItemModel.item);
                txtDistrict.setTag(rvItemModel.id);
                downArrowDistrict.setVisibility(View.VISIBLE);
                upArrowDistrict.setVisibility(View.GONE);
                rvDistrict.setVisibility(View.GONE);
                binding.tvSubDistricts.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                updateCustomerModel.setDistrictId(String.valueOf(rvItemModel.id));
                fillSubDistrict((Integer) rvItemModel.id);
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
                updateCustomerModel.setSubDistrictId(String.valueOf(rvItemModel.id));
                fillVillage((Integer) rvItemModel.id);
                break;
            case "village":
                txtVillage.setText(rvItemModel.item);
                txtVillage.setTag(rvItemModel.id);
                binding.etPostCode.setText(rvItemModel.zipCode);
                downArrowVillage.setVisibility(View.VISIBLE);
                upArrowVillage.setVisibility(View.GONE);
                rvVillage.setVisibility(View.GONE);
                updateCustomerModel.setVillageId(String.valueOf(rvItemModel.id));
                break;
            case "group":
                txtGroup.setText(rvItemModel.item);
                txtGroup.setTag(rvItemModel.id);
                downArrowGroup.setVisibility(View.VISIBLE);
                upArrowGroup.setVisibility(View.GONE);
                rvGroup.setVisibility(View.GONE);
                updateCustomerModel.setGroupId(String.valueOf(rvItemModel.id));
                break;
        }
    }

    private void fillDistrict(int id) {
        Log.e(TAG, "fillDistrict: >>>>>>>>>>>" + id);
        requestAPI.sendGetRequest(ApiEndPoints.districtURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomersProfileUpdateActivity.this);
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
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    districtAdaptor = new DistrictAdaptor(items, CustomersProfileUpdateActivity.this, 2);
                    rvDistrict.setAdapter(districtAdaptor);
                    progressDialog.dismiss();
                    Log.e(TAG, "onResponse: district" + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        countyAdaptor = new CountyAdaptor(new ArrayList<>(), CustomersProfileUpdateActivity.this, 2);
        rvCounty.setAdapter(countyAdaptor);
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomersProfileUpdateActivity.this, 2);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillSubDistrict(int id) {
        requestAPI.sendGetRequest(ApiEndPoints.subDistrictURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomersProfileUpdateActivity.this);
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
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    countyAdaptor = new CountyAdaptor(items, CustomersProfileUpdateActivity.this, 2);
                    rvCounty.setAdapter(countyAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomersProfileUpdateActivity.this, 2);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillVillage(int id) {
        requestAPI.sendGetRequest(ApiEndPoints.villageURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDiaLogSet(CustomersProfileUpdateActivity.this);
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
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    villageAdaptor = new VillageAdaptor(items, CustomersProfileUpdateActivity.this, 2);
                    rvVillage.setAdapter(villageAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersProfileUpdateActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
    }

    private void progressDiaLogSet(CustomersProfileUpdateActivity customersProfileUpdateActivity) {
        Dialog progressDialog = new Dialog(customersProfileUpdateActivity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_session_expired_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        CardView cvReLogin = progressDialog.findViewById(R.id.cv_reLogin);
        cvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(customersProfileUpdateActivity, StaffLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        checkClick = 0;
        if (!editProvince.contentEquals(binding.tvProvince.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editDistrict.contentEquals(binding.tvDistrict.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editSubDistrict.contentEquals(binding.tvSubDistricts.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editVillage.contentEquals(binding.tvVillage.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editStreetName.contentEquals(binding.etStreetName.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editGroup.contentEquals(binding.tvGroup.getText())) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else if (!editCompanyName.contentEquals(binding.etCompany.getText())){
            binding.clBackAlert.setVisibility(View.VISIBLE);
        }else {
            finish();
        }
    }
}