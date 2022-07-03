package com.semaai.agent.activity.login;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.CropActivity;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.adapter.newCustomers.CountyAdaptor;
import com.semaai.agent.adapter.newCustomers.DistrictAdaptor;
import com.semaai.agent.adapter.newCustomers.ProvinceAdaptor;
import com.semaai.agent.adapter.newCustomers.VillageAdaptor;
import com.semaai.agent.databinding.ActivityAccountBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.model.RVItemModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.CameraUtils;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.login.AccountViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity implements OnItemClickListener {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private AccountViewModel accountViewModel;
    private ActivityAccountBinding binding;
    RequestAPI requestAPI = new RequestAPI(this);
    ProvinceAdaptor provinceAdaptor;
    DistrictAdaptor districtAdaptor;
    CountyAdaptor countyAdaptor;
    VillageAdaptor villageAdaptor;
    RecyclerView rvProvince, rvDistrict, rvCounty, rvVillage;
    TextView txtProvince, txtDistrict, txtCounty, txtVillage;
    ImageView downArrowProvince, upArrowProvince, downArrowDistrict, upArrowDistrict, downArrowCounty, upArrowCounty,
            downArrowVillage, upArrowVillage;
    private Dialog progressDialog;
    String TAG = AccountActivity.class.getSimpleName() + "-->";
    private String currentPass, newPass, conNewPass;
    private boolean showPass = true, msgShow = true;
    private SharedPreferences.Editor editor;

    private String acName;
    private String acMobileNumber;
    private String langCode;
    private String userId;
    private String langName;
    private String stateId;
    private String districtId;
    private String subDistrictId;
    private String villageId;
    private String acStreet;
    private String acEmail;
    private String acGender;
    private String acBdate;
    private String partnerId;
    private String partnerName;
    private String imgPath;
    private String teamId;
    private String acStateId;
    private String acDistrictId;
    private String acSubDistrictId;
    private String acVillageId;
    ToastMsg toastMsg = new ToastMsg();
    public static final int MEDIA_TYPE_IMAGE = 1;
    private String imageStoragePath;
    private boolean checkBack = false;
    private boolean checkDataFill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        binding = DataBindingUtil.setContentView(AccountActivity.this, R.layout.activity_account);
        binding.setLifecycleOwner(this);
        binding.setAccountViewModel(accountViewModel);

        editor = getSharedPreferences("Language", MODE_PRIVATE).edit();

        setViews();
        intView();
        onClick();
    }

    private void addressApiCall() {
        requestAPI.sendGetRequest(ApiEndPoints.statesURL + Constant.companyId, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressdilogset(AccountActivity.this);
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
                    provinceAdaptor = new ProvinceAdaptor(items, AccountActivity.this, 2);
                    binding.rvProvinceList.setAdapter(provinceAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
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
                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                Log.e("TAG", "onResponse: <><><><><" + ApiEndPoints.districtURL + rvItemModel.id);
                stateId = String.valueOf(rvItemModel.id);
                fillDistrict((Integer) rvItemModel.id);
                break;
            case "district":
                progressDialog.show();
                txtDistrict.setText(rvItemModel.item);
                txtDistrict.setTag(rvItemModel.id);
                downArrowDistrict.setVisibility(View.VISIBLE);
                upArrowDistrict.setVisibility(View.GONE);
                rvDistrict.setVisibility(View.GONE);
                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                binding.etPostCode.setText(null);
                districtId = String.valueOf(rvItemModel.id);
                fillSubDistrict((Integer) rvItemModel.id);
                Log.e("TAG", "onResponse: <><><><>< dis" + ApiEndPoints.subDistrictURL + rvItemModel.id);
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
                subDistrictId = String.valueOf(rvItemModel.id);
                fillVillage((Integer) rvItemModel.id);
                break;
            case "village":
                txtVillage.setText(rvItemModel.item);
                txtVillage.setTag(rvItemModel.id);
                binding.etPostCode.setText(rvItemModel.zipCode);
                downArrowVillage.setVisibility(View.VISIBLE);
                upArrowVillage.setVisibility(View.GONE);
                rvVillage.setVisibility(View.GONE);
                villageId = String.valueOf(rvItemModel.id);
                break;
        }
    }

    private void fillDistrict(int id) {
        Log.e(TAG, "fillDistrict: >>>>>>>>>>>" + id);
        requestAPI.sendGetRequest(ApiEndPoints.districtURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressdilogset(AccountActivity.this);
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
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    districtAdaptor = new DistrictAdaptor(items, AccountActivity.this, 2);
                    rvDistrict.setAdapter(districtAdaptor);
                    progressDialog.dismiss();
                    Log.e(TAG, "onResponse: district" + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        countyAdaptor = new CountyAdaptor(new ArrayList<>(), AccountActivity.this, 2);
        rvCounty.setAdapter(countyAdaptor);
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), AccountActivity.this, 2);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillSubDistrict(int id) {
        requestAPI.sendGetRequest(ApiEndPoints.subDistrictURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressdilogset(AccountActivity.this);
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
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    countyAdaptor = new CountyAdaptor(items, AccountActivity.this, 2);
                    rvCounty.setAdapter(countyAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), AccountActivity.this, 2);
        rvVillage.setAdapter(villageAdaptor);
    }

    private void fillVillage(int id) {
        requestAPI.sendGetRequest(ApiEndPoints.villageURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressdilogset(AccountActivity.this);
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
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                        msgShow = false;
                    }
                    if (items.isEmpty() && msgShow) {
                        ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                    }
                    msgShow = true;
                    villageAdaptor = new VillageAdaptor(items, AccountActivity.this, 2);
                    rvVillage.setAdapter(villageAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
    }

    private void intView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        if (Constant.setLanguage.equals("in")) {
            binding.cvEn.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.cvIn.setCardBackgroundColor(Color.parseColor("#F39404"));
            binding.tvId.setTextColor(Color.parseColor("#FFFFFF"));
            binding.tvEn.setTextColor(Color.parseColor("#000000"));
        } else {
            binding.cvIn.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.cvEn.setCardBackgroundColor(Color.parseColor("#F39404"));
            binding.tvEn.setTextColor(Color.parseColor("#FFFFFF"));
            binding.tvId.setTextColor(Color.parseColor("#000000"));
        }

        apiCall();
    }

    private void apiCall() {
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
                        Log.e(TAG, "onResponse: >>> account " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                checkDataFill = true;
                                addressApiCall();
                                JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject dataobject = jsonArray.getJSONObject(i);

                                    if (!dataobject.getString(ApiStringModel.image1920).equals("false")) {
                                        imgPath = dataobject.getString(ApiStringModel.image1920);
                                        Constant.profileImage = imgPath;
                                        byte[] decodedString = Base64.decode(dataobject.getString(ApiStringModel.image1920), Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        Glide.with(AccountActivity.this).load(decodedByte).dontTransform().circleCrop().into(binding.ivProfileImg);
                                    }

                                    if (!dataobject.getString(ApiStringModel.name).equals("false")) {
                                        binding.etName.setText(acName = dataobject.getString(ApiStringModel.name));
                                    }
                                    if (!dataobject.getString(ApiStringModel.acPhoneNumber).equals("false")) {
                                        binding.etPhone.setText(acMobileNumber = dataobject.getString(ApiStringModel.acPhoneNumber));
                                    }
                                    JSONArray partnerobject = dataobject.getJSONArray(ApiStringModel.partnerId);
                                    if (!partnerobject.equals("false")) {
                                        partnerId = partnerobject.getString(0);
                                        partnerName = partnerobject.getString(1);

                                    }
                                    langCode = dataobject.getString(ApiStringModel.langCode);
                                    userId = dataobject.getString(ApiStringModel.userId);
                                    langName = dataobject.getString(ApiStringModel.langName);
                                    //state
                                    JSONArray stateobject = dataobject.getJSONArray(ApiStringModel.stateId);
                                    if (!stateobject.equals("false")) {
                                        stateId = stateobject.getString(0);
                                        acStateId = stateId;
                                        binding.tvProvince.setText(stateobject.getString(1));
                                        fillDistrict(Integer.parseInt(stateId));
                                    }
                                    //districts
                                    JSONArray districtsobject = dataobject.getJSONArray(ApiStringModel.districtId);
                                    if (!districtsobject.equals("false")) {
                                        districtId = districtsobject.getString(0);
                                        acDistrictId = districtId;
                                        binding.tvDistrict.setText(districtsobject.getString(1));
                                        fillSubDistrict(Integer.parseInt(districtId));

                                    }
                                    //sub_districts
                                    JSONArray sub_disobject = dataobject.getJSONArray(ApiStringModel.subDistrictId);
                                    if (!sub_disobject.equals("false")) {
                                        subDistrictId = sub_disobject.getString(0);
                                        acSubDistrictId = subDistrictId;
                                        binding.tvSubDistrict.setText(sub_disobject.getString(1));
                                        fillVillage(Integer.parseInt(subDistrictId));
                                    }
                                    //village
                                    JSONArray villageobject = dataobject.getJSONArray(ApiStringModel.villageId);
                                    if (!villageobject.equals("false")) {
                                        villageId = villageobject.getString(0);
                                        acVillageId = villageId;
                                        binding.tvVillage.setText(villageobject.getString(1));
                                    }

                                    if (!dataobject.getString(ApiStringModel.street).equals("false")) {
                                        binding.etAcEtxtStreet.setText(acStreet = dataobject.getString(ApiStringModel.street));
                                    }

                                    if (!dataobject.getString(ApiStringModel.zip).equals("false")) {
                                        binding.etPostCode.setText(dataobject.getString(ApiStringModel.zip));
                                    }

                                    if (!dataobject.getString(ApiStringModel.email).equals("false")) {
                                        binding.etAcEtxtEmail.setText(acEmail = dataobject.getString(ApiStringModel.email));
                                    }

                                    if (!dataobject.getString(ApiStringModel.function).equals("false")) {
                                        binding.etJobTitle.setText(dataobject.getString(ApiStringModel.function));
                                    }

                                    if (!dataobject.getString(ApiStringModel.gender).equals("false")) {
                                        String upperString = dataobject.getString(ApiStringModel.gender).substring(0, 1).toUpperCase() +
                                                dataobject.getString(ApiStringModel.gender).substring(1).toLowerCase();
                                        if (Constant.setLanguage.equals("in")) {
                                            if (upperString.equals("Male")) {
                                                acGender = getResources().getString(R.string.male);
                                                binding.tvGender.setText(R.string.male);
                                            } else if (upperString.equals("Female")) {
                                                acGender = getResources().getString(R.string.female);
                                                binding.tvGender.setText(R.string.female);
                                            } else if (upperString.equals("Other")) {
                                                acGender = getResources().getString(R.string.acOther);
                                                binding.tvGender.setText(R.string.acOther);
                                            } else {
                                                binding.tvGender.setText(R.string.acGender);
                                            }
                                        } else {
                                            binding.tvGender.setText(acGender = upperString);
                                        }
                                    } else {
                                        acGender = getResources().getString(R.string.acGender);
                                    }


                                    String checkDate = dataobject.getString(ApiStringModel.birthday).toLowerCase(Locale.ROOT);
                                    if (!checkDate.contains("false")) {
                                        String deliveryDate = dataobject.getString(ApiStringModel.birthday);
                                        Log.e(TAG, "onResponse: >>>> check date 22 " + deliveryDate);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date d = null;
                                        try {
                                            d = simpleDateFormat.parse(deliveryDate);
                                        } catch (ParseException e) {
                                            Log.e(TAG, "onResponse: catch >> " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                        SimpleDateFormat dateFormat;
                                        String uDate;
                                        if (Constant.setLanguage.equals("in")) {
                                            dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                            uDate = dateFormat.format(d);

                                            dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("in"));


                                        }else {
                                             dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                             uDate = dateFormat.format(d);
                                        }
                                        Log.e("TAG", "onBindViewHolder: check date " + d);
                                        String changedDate = dateFormat.format(d);
                                        Log.e(TAG, "onResponse: >>>> check date " + changedDate);
                                        acBdate = changedDate;
                                        binding.tvBdate.setText(changedDate);
                                        Constant.uDate = uDate;
                                    } else {
                                        acBdate = getResources().getString(R.string.bDate);
                                    }

                                    JSONArray teamObject = dataobject.getJSONArray(ApiStringModel.teamId);
                                    if (!teamObject.equals("false")) {
                                        teamId = teamObject.getString(0);
                                        binding.etAcEtxtTeam.setText(teamObject.getString(1));
                                    }

                                    Log.e(TAG, "onResponse: >>> jsonarray " + stateobject.getString(1));
                                }
                            } else {
                                checkDataFill = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            checkDataFill = false;
                            toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                        }
                        Log.e(TAG, "onResponse: >>>>> " + response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError: >>>>> " + error.getMessage());
                        checkDataFill = false;
                        progressDialog.dismiss();
                        toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        binding.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.icBackDialog.cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.icBackDialog.cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cvProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        binding.clPasswordTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onCreate: >>> password 2 " + Constant.loginPassword);
                showPass = false;
                binding.inChangepass.etCurrentPassword.setText("");
                binding.inChangepass.etNewPassword.setText("");
                binding.inChangepass.etConfirmNewPassword.setText("");
                binding.clProfile.setVisibility(View.GONE);
                binding.clChangepassword.setVisibility(View.VISIBLE);
            }
        });

        binding.inChangepass.cvAccountConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPass = binding.inChangepass.etCurrentPassword.getText().toString();
                newPass = binding.inChangepass.etNewPassword.getText().toString();
                conNewPass = binding.inChangepass.etConfirmNewPassword.getText().toString();
                Log.e("TAG", "onCreate: >>> password 3 " + Constant.loginPassword);
                Log.e(TAG, "onClick: >>>> " + Constant.loginPassword + " " + currentPass + " " + newPass + " " + conNewPass);

                if (!Constant.loginPassword.equals(currentPass)) {
                    toastMsg.showToast(AccountActivity.this, getString(R.string.currNotMatch));
                } else if (currentPass.equals(newPass)) {
                    toastMsg.showToast(AccountActivity.this, getString(R.string.curNewSame));
                } else if (!newPass.equals(conNewPass)) {
                    toastMsg.showToast(AccountActivity.this, getString(R.string.newConfNotSame));
                } else {
                    if (currentPass.equals("")) {
                        toastMsg.showToast(AccountActivity.this, getString(R.string.plzEnterCurr));
                    } else if (newPass.equals("")) {
                        toastMsg.showToast(AccountActivity.this, getString(R.string.plzEnterNew));
                    } else if (conNewPass.equals("")) {
                        toastMsg.showToast(AccountActivity.this, getString(R.string.plzEnterConf));
                    } else {
                        passwordApiCall(newPass);
                    }
                }

            }
        });
        binding.menu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.cvEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.setLanguage = "";
                binding.cvIn.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.cvEn.setCardBackgroundColor(Color.parseColor("#F39404"));
                binding.tvEn.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvId.setTextColor(Color.parseColor("#000000"));
                editor.putString("lang", Constant.setLanguage);
                editor.apply();
                Intent intent = new Intent(AccountActivity.this, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.cvIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.setLanguage = "in";
                binding.cvEn.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                binding.cvIn.setCardBackgroundColor(Color.parseColor("#F39404"));
                binding.tvId.setTextColor(Color.parseColor("#FFFFFF"));
                binding.tvEn.setTextColor(Color.parseColor("#000000"));
                editor.putString("lang", Constant.setLanguage);
                editor.apply();
                Intent intent = new Intent(AccountActivity.this, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.cvAccountSave.setOnClickListener(new View.OnClickListener() {
            private String changedDate;

            @Override
            public void onClick(View view) {
                String deliveryDate = Constant.uDate;
                Log.e(TAG, "onClick: >>>  " + deliveryDate);
                if (!deliveryDate.contentEquals(getResources().getString(R.string.bDate))) {
                    SimpleDateFormat dateFormatprev = new SimpleDateFormat("dd MMMM yyyy");
                    Date d = null;
                    try {
                        d = dateFormatprev.parse(deliveryDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    changedDate = dateFormat.format(d);
                    Log.e(TAG, "onClick: >>>>>>>>>>>>>>>>>>>>>>>> " + dateFormatprev + " >><< " + deliveryDate);
                    Log.i("TAG", "onClick: >>>>>>>>>>>>>>>>>>>>>>>> date " + changedDate);
                } else {
                    changedDate = "";
                }
                sendDataApiCall(changedDate);
            }
        });

        binding.svNested.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutGoneSetTouch();
                return false;
            }
        });

        binding.ivProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {

                    ExifInterface ei = new ExifInterface(imageStoragePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Common.rotation = orientation;

                    Constant.whichCameraImage = 0;
                    Constant.cameraImage1 = Uri.fromFile(new File(imageStoragePath));
                    Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageStoragePath)));
                    Common.SetTempBitmap(AccountActivity.this, Constant.cameraImage);
                    checkBack = true;
                    Intent intent = new Intent(AccountActivity.this, CropActivity.class);
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

                binding.ivProfileImg.setImageURI(Uri.fromFile(new File(Common.GetTempPath(AccountActivity.this))));

                Log.i(TAG, "EditImageSet");
                Glide.with(this)
                        .load(new File(Common.GetTempPath(AccountActivity.this)))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(binding.ivProfileImg);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(Common.GetTempPath(AccountActivity.this));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] imageBytes = baos.toByteArray();
                imgPath = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void passwordApiCall(String password) {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");

        JSONObject object = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(ApiStringModel.password, password);
            params.put(ApiStringModel.data, data);
            object.put(ApiStringModel.params, params);
            AndroidNetworking.put(ApiEndPoints.password)
                    .addJSONObjectBody(object) // posting json
                    .addHeaders("X-Openerp-Session-Id", separated[1])
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: >>>>>>>>>>>>>>>" + response);
                            try {
                                JSONObject result = response.getJSONObject(ApiStringModel.result);
                                if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Success")) {
                                    binding.clChangepassword.setVisibility(View.GONE);
                                    binding.clProfile.setVisibility(View.VISIBLE);
                                    SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                                    editor.putString("password", password);
                                    editor.apply();
                                    Intent intent = new Intent(AccountActivity.this, StaffLoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    ToastMsg.showToast(AccountActivity.this, getString(R.string.passwordChange));
                                } else {
                                    ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                                }
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                ToastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "error: >>>>>>>>>>>>>>>" + error);
                            toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                            progressDialog.dismiss();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void layoutGoneSetTouch() {
        binding.clGenderList.setVisibility(View.GONE);
        binding.ivUpArrowgender.setVisibility(View.GONE);
        binding.rvProvinceList.setVisibility(View.GONE);
        binding.ivDownArrowProvince.setVisibility(View.VISIBLE);
        binding.ivUpArrowProvince.setVisibility(View.GONE);
        binding.rvDistrictList.setVisibility(View.GONE);
        binding.ivDownArrowDistrict.setVisibility(View.VISIBLE);
        binding.ivUpArrowDistrict.setVisibility(View.GONE);
        binding.rvCountyList.setVisibility(View.GONE);
        binding.ivDownArrowCounty.setVisibility(View.VISIBLE);
        binding.ivUpArrowCounty.setVisibility(View.GONE);
        binding.rvVillageList.setVisibility(View.GONE);
        binding.ivDownArrowVillage.setVisibility(View.VISIBLE);
        binding.ivUpArrowVillage.setVisibility(View.GONE);
    }

    private void showDateDialog(TextView tvBdate) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(AccountActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                tvBdate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void sendDataApiCall(String changedDate) {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");

        JSONObject object = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(ApiStringModel.partnerId, partnerId);
            data.put(ApiStringModel.name, binding.etName.getText().toString());
            data.put(ApiStringModel.langCode, langCode);
            data.put(ApiStringModel.stateId, stateId);
            data.put(ApiStringModel.districtId, districtId);
            data.put(ApiStringModel.subDistrictId, subDistrictId);
            data.put(ApiStringModel.villageId, villageId);
            data.put(ApiStringModel.street, binding.etAcEtxtStreet.getText().toString());
            data.put(ApiStringModel.street2, "");
            data.put(ApiStringModel.zip, binding.etPostCode.getText().toString());
            data.put(ApiStringModel.email, binding.etAcEtxtEmail.getText().toString());
            if (Constant.setLanguage.equals("in")) {
                if (binding.tvGender.getText().toString().equals("Pria")) {
                    data.put(ApiStringModel.gender, "male");
                } else if (binding.tvGender.getText().toString().equals("Wanita")) {
                    data.put(ApiStringModel.gender, "female");
                } else if (binding.tvGender.getText().toString().equals("Other")) {
                    data.put(ApiStringModel.gender, "other");
                } else {
                    data.put(ApiStringModel.gender, "");
                }
            } else {
                Log.e(TAG, "sendDataApiCall: >>>>> " + binding.tvGender.getText().toString() + " " + getResources().getString(R.string.acGender).toLowerCase(Locale.ROOT));
                if (!binding.tvGender.getText().toString().equals(getResources().getString(R.string.acGender))) {
                    data.put(ApiStringModel.gender, binding.tvGender.getText().toString().toLowerCase(Locale.ROOT));
                } else {
                    data.put(ApiStringModel.gender, "");
                }
            }
            data.put(ApiStringModel.birthday, changedDate);
            data.put(ApiStringModel.image1920, imgPath);
            params.put(ApiStringModel.data, data);
            object.put(ApiStringModel.params, params);
            Log.e(TAG, "sendDataApiCall: >>>> " + new Gson().toJson(object));
            AndroidNetworking.put(ApiEndPoints.profile)
                    .addJSONObjectBody(object) // posting json
                    .addHeaders("X-Openerp-Session-Id", separated[1])
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: >>>>>>>>>>>>>>>" + response);
                            try {
                                JSONObject result = response.getJSONObject(ApiStringModel.result);
                                if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Success")) {
                                    toastMsg.showToast(AccountActivity.this, getString(R.string.updateSuccess));
                                    Constant.profileImage = imgPath;
                                    Constant.name = binding.etName.getText().toString();
                                    checkBackSetData();
                                    checkBack = false;
                                } else {
                                    ToastMsg.showToast(AccountActivity.this, getString(R.string.fillDetails));
                                }
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            toastMsg.showToast(AccountActivity.this, getString(R.string.errorMessage));
                            Log.e(TAG, "error: >>>>>>>>>>>>>>>" + error);
                            progressDialog.dismiss();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void layoutGone() {
        binding.clGenderList.setVisibility(View.GONE);
        binding.ivDownArrowgendr.setVisibility(View.GONE);
        binding.ivUpArrowgender.setVisibility(View.GONE);
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
        txtCounty = binding.tvSubDistrict;
        downArrowCounty = binding.ivDownArrowCounty;
        upArrowCounty = binding.ivUpArrowCounty;

        //village views
        rvVillage = binding.rvVillageList;
        rvVillage.setLayoutManager(new LinearLayoutManager(this));
        rvVillage.setHasFixedSize(true);
        txtVillage = binding.tvVillage;
        downArrowVillage = binding.ivDownArrowVillage;
        upArrowVillage = binding.ivUpArrowVillage;

    }

    @Override
    public void onBackPressed() {
        if (checkDataFill) {
            checkChanges();
        } else {
            finish();
        }
    }

    private void progressdilogset(AccountActivity customersProfileUpdateActivity) {
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

    public void checkChanges() {
        Log.e(TAG, "checkChanges: >>> " + acGender + "  " + " " + acBdate + " " + stateId + " " + binding.tvGender.getText().toString());
        if (!acName.equals(binding.etName.getText().toString())) {
            checkBack = true;
        } else if (!acGender.equals(binding.tvGender.getText().toString())) {
            checkBack = true;
        } else if (!acBdate.equals(binding.tvBdate.getText().toString())) {
            checkBack = true;
        } else if (!stateId.equals(acStateId)) {
            checkBack = true;
        } else if (!districtId.equals(acDistrictId)) {
            checkBack = true;
        } else if (!subDistrictId.equals(acSubDistrictId)) {
            checkBack = true;
        } else if (!villageId.equals(acVillageId)) {
            checkBack = true;
        } else if (!acStreet.equals(binding.etAcEtxtStreet.getText().toString())) {
            checkBack = true;
        } else if (!acEmail.equals(binding.etAcEtxtEmail.getText().toString())) {
            checkBack = true;
        }

        if (checkBack) {
            binding.clBackAlert.setVisibility(View.VISIBLE);
        } else {
            if (showPass) {
                finish();
            } else {
                binding.clChangepassword.setVisibility(View.GONE);
                binding.clProfile.setVisibility(View.VISIBLE);
                showPass = true;
            }
        }
    }

    private void checkBackSetData() {
        acName = binding.etName.getText().toString();
        acGender = binding.tvGender.getText().toString();
        acBdate = binding.tvBdate.getText().toString();
        stateId = acStateId;
        districtId = acDistrictId;
        subDistrictId = acSubDistrictId;
        villageId = acVillageId;
        acStreet = binding.etAcEtxtStreet.getText().toString();
        acEmail = binding.etAcEtxtEmail.getText().toString();
    }
}