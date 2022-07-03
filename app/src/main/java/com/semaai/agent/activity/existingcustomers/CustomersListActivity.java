package com.semaai.agent.activity.existingcustomers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.semaai.agent.R;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.adapter.existingcustomers.CustomerListAdapter;
import com.semaai.agent.adapter.newCustomers.CountyAdaptor;
import com.semaai.agent.adapter.newCustomers.DistrictAdaptor;
import com.semaai.agent.adapter.newCustomers.ProvinceAdaptor;
import com.semaai.agent.adapter.newCustomers.VillageAdaptor;
import com.semaai.agent.databinding.ActivityCustomersListBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.model.RVItemModel;
import com.semaai.agent.model.existingcustomers.CustomerListModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.existingcustomers.CustomersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;


public class CustomersListActivity extends AppCompatActivity implements OnItemClickListener {

    String TAG = CustomersListActivity.class.getSimpleName() + "--->";
    CustomersViewModel customersViewModel;
    private ActivityCustomersListBinding binding;
    private Dialog progressDialog;
    private CustomerListAdapter customerListAdapter;
    ArrayList<CustomerListModel> nameList = new ArrayList<>();
    private Boolean isScrolling = true, showKeyboard = false;
    GridLayoutManager manager;
    int currentItems, totalItems, scrollOutItem, previousTotal, page = 1;
    String sortBy = "asc";
    ImageView downArrowProvince, upArrowProvince, downArrowDistrict, upArrowDistrict,
            downArrowCounty, upArrowCounty, downArrowVillage, upArrowVillage;
    ProvinceAdaptor provinceAdaptor;
    DistrictAdaptor districtAdaptor;
    CountyAdaptor countyAdaptor;
    VillageAdaptor villageAdaptor;
    RequestAPI requestAPI = new RequestAPI(this);
    RecyclerView rv_province, rv_district, rv_county, rv_village;
    ArrayList<Number> selectedProvinceList = new ArrayList<>();
    ArrayList<Number> selectedDistrictList = new ArrayList<>();
    ArrayList<Number> selectedSubDistrictList = new ArrayList<>();
    ArrayList<Number> selectedVillageList = new ArrayList<>();
    String applyFilter = "[" +
            "[\"sub-district\n\" , " + "[]" + "] ," +
            "[\"district\" , " + "[]" + "] ," +
            "[\"state\" , " + "[622]" + "]]";
    private String customerFlag = "me";
    private String keyword = "", nextPage;
    private Boolean checkNull = false;

    private int totalPage;

    private final String limit = String.valueOf(10);
    private boolean filterFlag = false;

    ArrayList<RVItemModel> itemsDistrict = new ArrayList<>();
    ArrayList<RVItemModel> itemsSubDistrict = new ArrayList<>();
    ArrayList<RVItemModel> itemsVillage = new ArrayList<>();


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customersViewModel = ViewModelProviders.of(this).get(CustomersViewModel.class);
        binding = DataBindingUtil.setContentView(CustomersListActivity.this, R.layout.activity_customers_list);
        binding.setLifecycleOwner(this);
        binding.setCustomerViewModel(customersViewModel);

        intView();
        getData();
        onClick();
        apiCall();
        searchView();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void searchView() {
        binding.clTitle.etSearchViewDetails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                return false;
            }
        });

        binding.clTitle.etSearchViewDetails.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = binding.clTitle.etSearchViewDetails.getText().toString();
                    nameList.clear();
                    page = 1;
                    previousTotal = 0;
                    isScrolling = true;
                    totalItems = 0;

                    if (customerFlag.equals("me")) {
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView 1 ");
                            callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                        }
                    } else {
                        binding.rvCustomerListData.setVisibility(View.VISIBLE);
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView 2");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        binding.clTitle.ivSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.clTitle.etSearchViewDetails.getText().length() != 0) {
                    keyword = binding.clTitle.etSearchViewDetails.getText().toString();
                    nameList.clear();
                    page = 1;
                    previousTotal = 0;
                    isScrolling = true;
                    totalItems = 0;
                    if (customerFlag.equals("me")) {
                        binding.clTitle.clRegister.setVisibility(View.GONE);
                        binding.clBottomLayout.setVisibility(View.GONE);
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView text change 0 ");
                            callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                        }
                    } else {
                        binding.clTitle.clRegister.setVisibility(View.GONE);
                        binding.clBottomLayout.setVisibility(View.GONE);

                        binding.rvCustomerListData.setVisibility(View.GONE);
                        binding.rvCustomerListData.setVisibility(View.VISIBLE);
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView text change 0 ");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }
                    }
                }

            }
        });

        binding.clTitle.etSearchViewDetails.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = binding.clTitle.etSearchViewDetails.getText().toString();
                    nameList.clear();
                    page = 1;
                    previousTotal = 0;
                    isScrolling = true;
                    totalItems = 0;
                    if (customerFlag.equals("me")) {
                        binding.clTitle.clRegister.setVisibility(View.GONE);
                        binding.clBottomLayout.setVisibility(View.GONE);
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView text change 0 ");
                            callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                        }
                    } else {
                        binding.clTitle.clRegister.setVisibility(View.GONE);
                        binding.clBottomLayout.setVisibility(View.GONE);

                        binding.rvCustomerListData.setVisibility(View.GONE);
                        binding.rvCustomerListData.setVisibility(View.VISIBLE);
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView text change 0 ");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }

                    }
                    return true;
                }
                return false;
            }
        });

        binding.clTitle.etSearchViewDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i1, int i2, int i3) {
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                Log.i(TAG, "Search beforeTextChanged : " + " ch -" + s + " , i1 -" + i1 + " , i2 -" + i2 + " , i3 -" + i3);
            }

            @Override
            public void onTextChanged(CharSequence s, int i1, int i2, int i3) {
                Log.i(TAG, "Search onTextChanged : " + " ch -" + s + " , i1 -" + i1 + " , i2 -" + i2 + " , i3 -" + i3);
                showKeyboard = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void apiCall() {
        requestAPI.sendGetRequest(ApiEndPoints.statesURL + Constant.companyId, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.has("error")) {
                    progressDialogSet(CustomersListActivity.this);
                    return;
                }
                try {
                    ArrayList<RVItemModel> items = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                    for (int i = -1; i < jsonArray.length(); i++) {
                        RVItemModel rvItemModel = new RVItemModel();
                        if (i == -1) {
                            rvItemModel.setItem(getString(R.string.pleaseSelect));
                            rvItemModel.setId(i);
                            items.add(rvItemModel);
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {

                                rvItemModel.setItem(jsonObject.getString(ApiStringModel.name));
                                rvItemModel.setId(jsonObject.getInt(ApiStringModel.id));

                                items.add(rvItemModel);
                            }
                        }

                    }

                    provinceAdaptor = new ProvinceAdaptor(items, CustomersListActivity.this, 1);
                    rv_province.setAdapter(provinceAdaptor);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
                }

            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
    }

    private void getData() {

        manager = new GridLayoutManager(getApplicationContext(), 1);
        binding.rvCustomerListData.setLayoutManager(manager);

        callApi(ApiStringModel.typeValueMe, ApiStringModel.sortValue, ApiStringModel.pageValue, keyword, limit);

        setPagination();
    }

    private void setPagination() {
        binding.rvCustomerListData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);

                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItem = manager.findFirstVisibleItemPosition();

                Log.i("Api -->", "currentItems : " + currentItems + " , scrollOutItem : " + scrollOutItem + " , totalItems : " + totalItems);

                if (isScrolling) {
                    if (totalItems > previousTotal) {
                        previousTotal = totalItems;
                        page++;
                        isScrolling = false;
                    }
                }
                if (!isScrolling && (scrollOutItem + currentItems) >= totalItems) {
                    isScrolling = true;
                    if (filterFlag) {
                        callApiOtherDataWithFilter(customerFlag, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                    } else {
                        Log.i(TAG, "Api pagination ");
                        callApiOtherData(customerFlag, sortBy, String.valueOf(page), keyword, limit);
                    }
                }

            }
        });

    }

    private void callApiOtherData(String typeValue, String sortValue, String pageValue, String keyword, String limit) {

        Log.i(TAG, "Api Params callApiOtherData :- " + " type - " + typeValue +
                ", page - " + pageValue + "" +
                ", sort - " + sortValue + " " +
                ", keyword - " + keyword + " " +
                "limit - " + limit);

        if (Constant.clockInClick == 2) {
            typeValue = "clock-in";
        }
        Log.e(TAG, "type :" + typeValue);

        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.existInfCustomer)
                .addQueryParameter(ApiStringModel.type, typeValue)
                .addQueryParameter(ApiStringModel.sort, sortValue)
                .addQueryParameter(ApiStringModel.page, pageValue)
                .addQueryParameter(ApiStringModel.keyword, keyword)
                .addQueryParameter(ApiStringModel.limit, limit)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(ApiStringModel.data);
                            totalPage = Integer.parseInt(jsonObject.getString(ApiStringModel.totalPages));
                            nextPage = jsonObject.getString(ApiStringModel.next);
                            JSONArray jsonArrayResult = jsonObject.getJSONArray(ApiStringModel.result);
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                JSONObject nameObject = jsonArrayResult.getJSONObject(i);
                                nameList.add(new CustomerListModel(
                                        nameObject.getString(ApiStringModel.name),
                                        nameObject.getString(ApiStringModel.id),
                                        nameObject.getString(ApiStringModel.mobileNumber),
                                        nameObject.getString(ApiStringModel.districts),
                                        nameObject.getString(ApiStringModel.subDistricts),
                                        nameObject.getString(ApiStringModel.village),
                                        nameObject.getString(ApiStringModel.profileImage),
                                        nameObject.getString(ApiStringModel.companyName)));
                                Log.e("TAG", "onResponse: name >>>><<" + nameObject.getString("name"));
                            }
                            binding.rvCustomerListData.setVisibility(View.VISIBLE);
                            if (checkNull) {
                                customerListAdapter = new CustomerListAdapter(nameList, CustomersListActivity.this);
                                binding.rvCustomerListData.setAdapter(customerListAdapter);
                                Log.e(TAG, "catch error ==> ");
                                checkNull = false;
                            } else {
                                customerListAdapter.notifyDataSetChanged();
                            }

                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "data not found");
                            binding.rvCustomerListData.setVisibility(View.GONE);

                        }
                        Log.e("TAG", "onResponse: >>>>>>><<" + response.toString());
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>" + error.getMessage().toString());
                        progressDialog.dismiss();
                    }
                });

    }

    private void callApiOtherDataWithFilter(String typeValue, String sortValue, String pageValue, String keyword, String filter, String limit) {

        Log.i(TAG, "Api Params callApiOtherDataWithFilter :- " + " type - " + typeValue +
                ", page - " + pageValue + "" +
                ", sort - " + sortValue + " " +
                ", filter - " + filter + " " +
                ", keyword - " + keyword + " " +
                "limit - " + limit);

        if (Constant.clockInClick == 2) {
            typeValue = "clock-in";
        }
        Log.e(TAG, "type :" + typeValue);

        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.existInfCustomer)
                .addQueryParameter(ApiStringModel.type, typeValue)
                .addQueryParameter(ApiStringModel.sort, sortValue)
                .addQueryParameter(ApiStringModel.page, pageValue)
                .addQueryParameter(ApiStringModel.keyword, keyword)
                .addQueryParameter(ApiStringModel.filter, filter)
                .addQueryParameter(ApiStringModel.limit, limit)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(ApiStringModel.data);
                            totalPage = Integer.parseInt(jsonObject.getString(ApiStringModel.totalPages));
                            JSONArray jsonArrayResult = jsonObject.getJSONArray(ApiStringModel.result);
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                JSONObject nameObject = jsonArrayResult.getJSONObject(i);
                                nameList.add(new CustomerListModel(
                                        nameObject.getString(ApiStringModel.name),
                                        nameObject.getString(ApiStringModel.id),
                                        nameObject.getString(ApiStringModel.mobileNumber),
                                        nameObject.getString(ApiStringModel.districts),
                                        nameObject.getString(ApiStringModel.subDistricts),
                                        nameObject.getString(ApiStringModel.village),
                                        nameObject.getString(ApiStringModel.profileImage),
                                        nameObject.getString(ApiStringModel.companyName)));
                                Log.e("TAG", "onResponse: name >>>><<" + nameObject.getString("name"));
                            }
                            binding.rvCustomerListData.setVisibility(View.VISIBLE);
                            if (checkNull) {
                                customerListAdapter = new CustomerListAdapter(nameList, CustomersListActivity.this);
                                binding.rvCustomerListData.setAdapter(customerListAdapter);
                                Log.e(TAG, "catch error ==> ");
                                checkNull = false;
                            } else {
                                customerListAdapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "data not found");
                            binding.rvCustomerListData.setVisibility(View.GONE);

                        }
                        Log.e("TAG", "onResponse: >>>>>>><<" + response.toString());
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>" + error.getMessage().toString());
                        progressDialog.dismiss();
                    }
                });

    }


    private void callApi(String typeValueMe, String sortValue, String pageValue, String keyword, String limit) {

        Log.i(TAG, "Api Params callApi :- " + " type - " + typeValueMe +
                ", page - " + pageValue + "" +
                ", sort - " + sortValue + " " +
                ", keyword - " + keyword + " " +
                ", limit - " + limit);

        if (Constant.clockInClick == 1) {
            typeValueMe = "me";
        } else if (Constant.clockInClick == 2) {
            typeValueMe = "clock-in";
            Constant.clockInChangeNameNull = true;
        }

        Log.e(TAG, "type :" + typeValueMe);

        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.existInfCustomer)
                .addQueryParameter(ApiStringModel.type, typeValueMe)
                .addQueryParameter(ApiStringModel.sort, sortValue)
                .addQueryParameter(ApiStringModel.page, pageValue)
                .addQueryParameter(ApiStringModel.keyword, keyword)
                .addQueryParameter(ApiStringModel.limit, limit)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.has("error")) {
                            progressDialogSet(CustomersListActivity.this);
                            progressDialog.dismiss();
                            return;
                        }
                        try {

                            Log.e(TAG, "onResponse: <><><>" + totalPage);
                            JSONObject jsonObject = response.getJSONObject(ApiStringModel.data);
                            totalPage = Integer.parseInt(jsonObject.getString(ApiStringModel.totalPages));

                            JSONArray jsonArrayResult = jsonObject.getJSONArray(ApiStringModel.result);
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                JSONObject nameObject = jsonArrayResult.getJSONObject(i);
                                nameList.add(new CustomerListModel(nameObject.getString(ApiStringModel.name),
                                        nameObject.getString(ApiStringModel.id),
                                        nameObject.getString(ApiStringModel.mobileNumber),
                                        nameObject.getString(ApiStringModel.districts),
                                        nameObject.getString(ApiStringModel.subDistricts),
                                        nameObject.getString(ApiStringModel.village),
                                        nameObject.getString(ApiStringModel.profileImage),
                                        nameObject.getString(ApiStringModel.companyName)));
                                Log.e("TAG", "onResponse: name >>>>" + nameObject.getString("name"));
                            }
                            customerListAdapter = new CustomerListAdapter(nameList, CustomersListActivity.this);
                            binding.rvCustomerListData.setAdapter(customerListAdapter);
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "catch error ==>> " + e.getMessage());
                        }
                        Log.e("TAG", "onResponse: >>>>>>>" + response.toString());

                        String data = "No Record Found";
                        try {
                            if (data.equals(response.getString(ApiStringModel.data))) {
                                checkNull = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>" + error.getMessage().toString());
                    }
                });

    }

    private void intView() {

        Constant.whichCustomer = "my";
        if (Constant.clockInClick == 1) {
            binding.clTitle.clRegister.setBackgroundResource(R.drawable.ic_rectangle_brown);
        }

        if (Constant.clockInClick == 1) {
            binding.clTitle.clRegister.setBackgroundResource(R.drawable.ic_rectangle_brown);
        } else if (Constant.clockInClick == 2) {
            binding.clTitle.clRegister.setBackgroundResource(R.drawable.ic_rectangle_brown);
            binding.clTitle.tvHeader.setText(getString(R.string.changeCustomer));
            binding.clBottomLayout.setVisibility(View.GONE);
        }

        // filter province
        rv_province = binding.rvProvinceList;
        rv_province.setLayoutManager(new LinearLayoutManager(this));
        rv_province.setHasFixedSize(true);
        downArrowProvince = binding.ivProvinceDownIcon;
        upArrowProvince = binding.ivProvinceUpIcon;

        //district views
        rv_district = binding.rvDistrictList;
        rv_district.setLayoutManager(new LinearLayoutManager(this));
        rv_district.setHasFixedSize(true);
        downArrowDistrict = binding.ivDistrictDownIcon;
        upArrowDistrict = binding.ivDistrictUpIcon;

        //county views
        rv_county = binding.rvCountyList;
        rv_county.setLayoutManager(new LinearLayoutManager(this));
        rv_county.setHasFixedSize(true);
        downArrowCounty = binding.ivSubDistrictDownIcon;
        upArrowCounty = binding.ivSubDistrictUpIcon;

        //village views
        rv_village = binding.rvVillageList;
        rv_village.setLayoutManager(new LinearLayoutManager(this));
        rv_village.setHasFixedSize(true);
        downArrowVillage = binding.ivVillageDownIcon;
        upArrowVillage = binding.ivVillageUpIcon;

        //getData
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

        binding.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
            }
        });

        binding.clTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
            }
        });

        binding.clListingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
            }
        });

        binding.clList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
            }
        });


        binding.clTitle.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.provinceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rv_province.getVisibility() == View.VISIBLE) {
                    downArrowProvince.setVisibility(View.VISIBLE);
                    upArrowProvince.setVisibility(View.GONE);
                    rv_province.setVisibility(View.GONE);
                } else {
                    downArrowProvince.setVisibility(View.GONE);
                    upArrowProvince.setVisibility(View.VISIBLE);
                    rv_province.setVisibility(View.VISIBLE);
                }
                rv_district.setVisibility(View.GONE);
                rv_county.setVisibility(View.GONE);
                rv_village.setVisibility(View.GONE);
            }
        });

        binding.districtCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_district.getVisibility() == View.VISIBLE) {
                    downArrowDistrict.setVisibility(View.VISIBLE);
                    upArrowDistrict.setVisibility(View.GONE);
                    rv_province.setVisibility(View.GONE);
                    rv_district.setVisibility(View.GONE);
                } else {
                    downArrowDistrict.setVisibility(View.GONE);
                    upArrowDistrict.setVisibility(View.VISIBLE);
                    rv_district.setVisibility(View.VISIBLE);
                    rv_province.setVisibility(View.GONE);
                }
                rv_county.setVisibility(View.GONE);
                rv_village.setVisibility(View.GONE);
            }
        });

        binding.subDistrictCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_county.getVisibility() == View.VISIBLE) {
                    downArrowCounty.setVisibility(View.VISIBLE);
                    upArrowCounty.setVisibility(View.GONE);
                    rv_province.setVisibility(View.GONE);
                    rv_district.setVisibility(View.GONE);
                    rv_county.setVisibility(View.GONE);
                } else {
                    downArrowCounty.setVisibility(View.GONE);
                    upArrowCounty.setVisibility(View.VISIBLE);
                    rv_county.setVisibility(View.VISIBLE);
                    rv_province.setVisibility(View.GONE);
                    rv_district.setVisibility(View.GONE);
                }
                rv_village.setVisibility(View.GONE);
            }
        });

        binding.villageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_village.getVisibility() == View.VISIBLE) {
                    downArrowVillage.setVisibility(View.VISIBLE);
                    upArrowVillage.setVisibility(View.GONE);
                    rv_province.setVisibility(View.GONE);
                    rv_district.setVisibility(View.GONE);
                    rv_county.setVisibility(View.GONE);
                    rv_village.setVisibility(View.GONE);
                } else {
                    downArrowVillage.setVisibility(View.GONE);
                    upArrowVillage.setVisibility(View.VISIBLE);
                    rv_village.setVisibility(View.VISIBLE);
                    rv_province.setVisibility(View.GONE);
                    rv_district.setVisibility(View.GONE);
                    rv_county.setVisibility(View.GONE);
                }

            }
        });

        binding.cvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clFilterList.setVisibility(View.GONE);
                filterFlag = true;

                if (!selectedVillageList.isEmpty()) {
                    applyFilter = "[[\"village\" , " + selectedVillageList.toString() + "]," +
                            "[\"sub-district\" , " + selectedSubDistrictList.toString() + "] ," +
                            "[\"district\" , " + selectedDistrictList.toString() + "] ," +
                            "[\"state\" , " + selectedProvinceList.toString() + "]]";
                    Log.i(TAG, "filter  key - value :--  " + applyFilter);
                } else if (!selectedSubDistrictList.isEmpty()) {
                    applyFilter = "[[\"sub-district\" , " + selectedSubDistrictList.toString() + "] ," +
                            "[\"district\" , " + selectedDistrictList.toString() + "] ," +
                            "[\"state\" , " + selectedProvinceList.toString() + "]]";
                    Log.i(TAG, "filter  key - value :--  " + applyFilter);
                } else if (!selectedDistrictList.isEmpty()) {
                    applyFilter = "[[\"district\" , " + selectedDistrictList.toString() + "] ," +
                            "[\"state\" , " + selectedProvinceList.toString() + "]]";
                    Log.i(TAG, "filter  key - value :--  " + applyFilter);
                } else if (!selectedProvinceList.isEmpty()) {
                    applyFilter = "[[\"state\" , " + selectedProvinceList.toString() + "]]";
                    Log.i(TAG, "filter  key - value :--  " + applyFilter);
                } else {
                    applyFilter = "[[\"state\" , " + "[622]" + "]]";
                    Log.i(TAG, "filter  key - value :--  " + applyFilter);
                }

                if (binding.tvVillage.getText().toString().equals("Please Select") || binding.tvVillage.getText().toString().equals("Harap Pilih")) {
                    binding.tvVillage.setText(getString(R.string.village));
                    binding.tvVillage.setTextColor(Color.parseColor("#F39404"));
                    Log.e(TAG, "onClick: >>>>> check village");
                }
                if (binding.tvSubDistrict.getText().toString().equals("Please Select") || binding.tvSubDistrict.getText().toString().equals("Harap Pilih")) {
                    binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                    binding.tvSubDistrict.setTextColor(Color.parseColor("#F39404"));
                    Log.e(TAG, "onClick: >>>>> check SubDistrict");
                }
                if (binding.tvDistrict.getText().toString().equals("Please Select") || binding.tvDistrict.getText().toString().equals("Harap Pilih")) {
                    binding.tvDistrict.setText(getString(R.string.district));
                    binding.tvDistrict.setTextColor(Color.parseColor("#F39404"));
                    Log.e(TAG, "onClick: >>>>> check District");
                }
                if (binding.tvProvince.getText().toString().equals("Please Select") || binding.tvProvince.getText().toString().equals("Harap Pilih")) {
                    binding.tvProvince.setText(getString(R.string.province));
                    binding.tvProvince.setTextColor(Color.parseColor("#F39404"));
                    Log.e(TAG, "onClick: >>>>> check Province");
                }


                page = 1;
                previousTotal = 0;
                isScrolling = true;
                totalItems = 0;
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                nameList.clear();


                if (customerFlag.equals("me")) {
                    if (filterFlag) {
                        if (!selectedProvinceList.isEmpty()) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView filter apply 0");
                            callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                        }
                    } else {
                        Log.i(TAG, "Api searchView filter apply 1");
                        callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                    }
                } else {
                    if (!binding.clTitle.etSearchViewDetails.getText().toString().isEmpty()) {
                        if (filterFlag) {
                            if (!selectedProvinceList.isEmpty()) {
                                callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                            } else {
                                Log.i(TAG, "Api searchView filter apply 0");
                                callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                            }
                        } else {
                            Log.i(TAG, "Api searchView filter apply 2");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }
                    }
                }
            }
        });

        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(1);
            }
        });
        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });

        binding.clFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(view);
                if (binding.clFilterList.getVisibility() == View.GONE) {
                    layoutGoneView();
                    binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                    binding.clFilterList.setVisibility(View.VISIBLE);
                } else {
                    binding.clFilterList.setVisibility(View.GONE);
                }
            }
        });

        binding.cvOtherCustomer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                nameList.clear();
                setFilterTextColor();
                hideSoftKeyboard(view);
                page = 1;
                previousTotal = 0;
                isScrolling = true;
                totalItems = 0;
                filterFlag = false;
                binding.rvCustomerListData.setVisibility(View.GONE);

                selectedProvinceList.clear();
                selectedDistrictList.clear();
                selectedSubDistrictList.clear();
                selectedVillageList.clear();
                binding.tvProvince.setText(getString(R.string.province));
                binding.tvDistrict.setText(getString(R.string.district));
                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));

                Constant.whichCustomer = "other";
                customerFlag = ApiStringModel.typeValueOther;
                binding.clTitle.etSearchViewDetails.setText("");
                keyword = "";
                binding.cvOtherCustomer.setCardBackgroundColor(getColor(R.color.button_bg));
                binding.tvOtherCustomerText.setTextColor(getColor(R.color.white));
                binding.cvMyCustomer.setCardBackgroundColor(getColor(R.color.white));
                binding.tvMyCustomerText.setTextColor(getColor(R.color.black));


                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
        binding.cvMyCustomer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NewApi", "ResourceAsColor"})
            @Override
            public void onClick(View view) {
                nameList.clear();
                setFilterTextColor();
                hideSoftKeyboard(view);
                page = 1;
                previousTotal = 0;
                isScrolling = true;
                totalItems = 0;
                filterFlag = false;
                binding.rvCustomerListData.setVisibility(View.VISIBLE);
                selectedProvinceList.clear();
                selectedDistrictList.clear();
                selectedSubDistrictList.clear();
                selectedVillageList.clear();
                binding.tvProvince.setText(getString(R.string.province));
                binding.tvDistrict.setText(getString(R.string.district));
                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));

                Constant.whichCustomer = "my";

                layoutGoneView();
                customerFlag = ApiStringModel.typeValueMe;
                binding.clTitle.etSearchViewDetails.setText("");
                keyword = "";
                binding.cvOtherCustomer.setCardBackgroundColor(getColor(R.color.white));
                binding.tvOtherCustomerText.setTextColor(getColor(R.color.black));
                binding.cvMyCustomer.setCardBackgroundColor(getColor(R.color.button_bg));
                binding.tvMyCustomerText.setTextColor(getColor(R.color.white));
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                binding.rvCustomerListData.setVisibility(View.VISIBLE);


                if (filterFlag) {
                    callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                } else {
                    Log.i(TAG, "Api  miy customer");
                    callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                }
            }
        });
        binding.cvSortByCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                if (binding.cvSortByList.getVisibility() == View.GONE) {
                    layoutGoneView();
                    binding.cvSortByList.setVisibility(View.VISIBLE);
                    binding.ivMyCustomerArrowUp.setVisibility(View.VISIBLE);
                } else {
                    layoutGoneView();
                    binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.tvNameAZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                sortBy = "asc";
                previousTotal = 0;
                isScrolling = true;
                totalItems = 0;
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                nameList.clear();

                binding.tvSortBy.setText(getString(R.string.nameAz));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvNameAZ.setTextColor(getColor(R.color.black));
                    binding.tvSortBy.setTextColor(getColor(R.color.black));
                    binding.tvNameZA.setTextColor(getColor(R.color.button_bg));
                }


                if (customerFlag.equals("me")) {
                    if (filterFlag) {
                        callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);

                    } else {
                        Log.i(TAG, "Api searchView Az 0");
                        callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                    }
                } else {
                    if (!binding.clTitle.etSearchViewDetails.getText().toString().isEmpty()) {
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView Az 1");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }
                    }
                }
            }
        });
        binding.tvNameZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                sortBy = "desc";
                previousTotal = 0;
                isScrolling = true;
                totalItems = 0;
                layoutGoneView();
                binding.ivMyCustomerArrowDown.setVisibility(View.VISIBLE);
                nameList.clear();

                binding.tvSortBy.setText(getString(R.string.nameZa));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvNameAZ.setTextColor(getColor(R.color.button_bg));
                    binding.tvSortBy.setTextColor(getColor(R.color.black));
                    binding.tvNameZA.setTextColor(getColor(R.color.black));
                }

                if (customerFlag.equals("me")) {
                    if (filterFlag) {
                        callApiOtherDataWithFilter(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, applyFilter, limit);

                    } else {
                        Log.i(TAG, "Api searchView ZA 0");
                        callApiOtherData(ApiStringModel.typeValueMe, sortBy, String.valueOf(page), keyword, limit);
                    }
                } else {
                    if (!binding.clTitle.etSearchViewDetails.getText().toString().isEmpty()) {
                        if (filterFlag) {
                            callApiOtherDataWithFilter(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, applyFilter, limit);
                        } else {
                            Log.i(TAG, "Api searchView ZA 1");
                            callApiOtherData(ApiStringModel.typeValueOther, sortBy, String.valueOf(page), keyword, limit);
                        }
                    }
                }
            }
        });
    }

    private void onBackClick(int checkClick) {
        if (checkClick == 0) {
            finish();
        } else if (checkClick == 1) {
            finish();
        } else if (checkClick == 2) {
            Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
            startActivity(intent);
        }
    }

    private void fillDistrict(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.districtURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDialogSet(CustomersListActivity.this);
                    return;
                }
                try {
                    JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                    for (int i = -1; i < jsonArray.length(); i++) {
                        RVItemModel rvItemModel = new RVItemModel();
                        if (i == -1) {
                            rvItemModel.setItem(getString(R.string.pleaseSelect));
                            rvItemModel.setId(i);
                            itemsDistrict.add(rvItemModel);
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {

                                rvItemModel.setItem(jsonObject.getString(ApiStringModel.name));
                                rvItemModel.setId(jsonObject.getInt(ApiStringModel.id));
                                itemsDistrict.add(rvItemModel);
                            }
                        }
                    }
                    districtAdaptor = new DistrictAdaptor(itemsDistrict, CustomersListActivity.this, 1);
                    rv_district.setAdapter(districtAdaptor);
                    Log.i(TAG, "rv district set in to item");
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        countyAdaptor = new CountyAdaptor(new ArrayList<>(), CustomersListActivity.this, 1);
        rv_county.setAdapter(countyAdaptor);
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomersListActivity.this, 1);
        rv_village.setAdapter(villageAdaptor);
    }

    private void fillSubDistrict(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.subDistrictURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDialogSet(CustomersListActivity.this);
                    return;
                }
                try {
                    JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                    for (int i = -1; i < jsonArray.length(); i++) {
                        RVItemModel rvItemModel = new RVItemModel();
                        if (i == -1) {
                            rvItemModel.setItem(getString(R.string.pleaseSelect));
                            rvItemModel.setId(i);
                            itemsSubDistrict.add(rvItemModel);
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {

                                rvItemModel.setItem(jsonObject.getString(ApiStringModel.name));
                                rvItemModel.setId(jsonObject.getInt(ApiStringModel.id));

                                itemsSubDistrict.add(rvItemModel);

                            }
                        }
                    }
                    countyAdaptor = new CountyAdaptor(itemsSubDistrict, CustomersListActivity.this, 1);
                    rv_county.setAdapter(countyAdaptor);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
        villageAdaptor = new VillageAdaptor(new ArrayList<>(), CustomersListActivity.this, 1);
        rv_village.setAdapter(villageAdaptor);
    }

    private void fillVillage(Number id) {
        requestAPI.sendGetRequest(ApiEndPoints.villageURL + id, new APIResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    progressDialogSet(CustomersListActivity.this);
                    return;
                }
                try {
                    JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);

                    for (int i = -1; i < jsonArray.length(); i++) {
                        RVItemModel rvItemModel = new RVItemModel();
                        if (i == -1) {
                            rvItemModel.setItem(getString(R.string.pleaseSelect));
                            rvItemModel.setId(i);
                            itemsVillage.add(rvItemModel);
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean(ApiStringModel.isAvailable)) {

                                rvItemModel.setItem(jsonObject.getString(ApiStringModel.name));
                                rvItemModel.setId(jsonObject.getInt(ApiStringModel.id));

                                itemsVillage.add(rvItemModel);

                            }
                        }
                    }
                    villageAdaptor = new VillageAdaptor(itemsVillage, CustomersListActivity.this, 1);
                    rv_village.setAdapter(villageAdaptor);
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastMsg.showToast(CustomersListActivity.this, getString(R.string.errorMessage));
            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {

            }
        });
    }


    private void progressDialogSet(CustomersListActivity customerAddressActivity) {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(String rvname, RVItemModel item, int isFilter) {
        switch (rvname) {
            case "province": {
                downArrowProvince.setVisibility(View.VISIBLE);
                upArrowProvince.setVisibility(View.GONE);
                rv_province.setVisibility(View.GONE);
                selectedProvinceList.clear();
                selectedDistrictList.clear();
                selectedSubDistrictList.clear();
                selectedVillageList.clear();
                binding.tvProvince.setText(item.item);
                binding.tvProvince.setTag(item.id);

                binding.tvProvince.setTextColor(getColor(R.color.button_bg));
                binding.tvDistrict.setTextColor(getColor(R.color.button_bg));
                binding.tvSubDistrict.setTextColor(getColor(R.color.button_bg));
                binding.tvVillage.setTextColor(getColor(R.color.button_bg));

                selectedProvinceList.add(item.id);
                // duplicate item remove
                HashSet<Number> filter = new HashSet(selectedProvinceList);
                selectedProvinceList = new ArrayList<Number>(filter);

                Log.i(TAG, "selected item pre province:" + selectedProvinceList.toString());

                binding.tvDistrict.setText(getString(R.string.district));
                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));
                if (item.item.equals(getString(R.string.pleaseSelect))) {
                    itemsDistrict.clear();
                    itemsSubDistrict.clear();
                    itemsVillage.clear();
                    selectedProvinceList.clear();
                    selectedDistrictList.clear();
                    selectedSubDistrictList.clear();
                    selectedVillageList.clear();
                } else {
                    itemsDistrict.clear();
                    itemsSubDistrict.clear();
                    itemsVillage.clear();
                    progressDialog.show();
                    binding.tvProvince.setTextColor(getColor(R.color.black));
                    Log.i(TAG, "sssss >> " + item.item);
                    fillDistrict(item.id);
                }
                break;
            }
            case "district": {
                downArrowDistrict.setVisibility(View.VISIBLE);
                upArrowDistrict.setVisibility(View.GONE);
                rv_district.setVisibility(View.GONE);
                selectedDistrictList.clear();
                selectedSubDistrictList.clear();
                selectedVillageList.clear();
                binding.tvDistrict.setText(item.item);
                binding.tvDistrict.setTag(item.id);

                binding.tvDistrict.setTextColor(getColor(R.color.button_bg));
                binding.tvSubDistrict.setTextColor(getColor(R.color.button_bg));
                binding.tvVillage.setTextColor(getColor(R.color.button_bg));


                selectedDistrictList.add(item.id);
                // duplicate item remove
                HashSet<Number> filter = new HashSet(selectedDistrictList);
                selectedDistrictList = new ArrayList<Number>(filter);
                Log.i(TAG, "selected item pre district:" + selectedDistrictList.toString());


                binding.tvSubDistrict.setText(getString(R.string.subDistrict));
                binding.tvVillage.setText(getString(R.string.village));

                if (item.item.equals(getString(R.string.pleaseSelect))) {

                    itemsSubDistrict.clear();
                    itemsVillage.clear();
                    selectedDistrictList.clear();
                    selectedSubDistrictList.clear();
                    selectedVillageList.clear();
                } else {
                    itemsSubDistrict.clear();
                    itemsVillage.clear();
                    progressDialog.show();
                    binding.tvDistrict.setTextColor(getColor(R.color.black));
                    Log.i(TAG, "sssss >> " + item.item);
                    fillSubDistrict(item.id);
                }
                break;
            }
            case "country": {
                downArrowCounty.setVisibility(View.VISIBLE);
                upArrowCounty.setVisibility(View.GONE);
                rv_county.setVisibility(View.GONE);
                selectedSubDistrictList.clear();
                selectedVillageList.clear();
                binding.tvSubDistrict.setText(item.item);
                binding.tvSubDistrict.setTag(item.id);

                binding.tvSubDistrict.setTextColor(getColor(R.color.button_bg));
                binding.tvVillage.setTextColor(getColor(R.color.button_bg));

                selectedSubDistrictList.add(item.id);
                // duplicate item remove
                HashSet<Number> filter = new HashSet(selectedSubDistrictList);
                selectedSubDistrictList = new ArrayList<Number>(filter);
                Log.i(TAG, "selected item pre country:" + selectedSubDistrictList.toString());


                binding.tvVillage.setText(getString(R.string.village));

                if (item.item.equals(getString(R.string.pleaseSelect))) {
                    itemsVillage.clear();
                    selectedSubDistrictList.clear();
                    selectedVillageList.clear();
                } else {
                    itemsVillage.clear();
                    progressDialog.show();
                    binding.tvSubDistrict.setTextColor(getColor(R.color.black));
                    Log.i(TAG, "sssss >> " + item.item);
                    fillVillage(item.id);
                }

                break;
            }
            case "village": {
                downArrowVillage.setVisibility(View.VISIBLE);
                upArrowVillage.setVisibility(View.GONE);
                rv_village.setVisibility(View.GONE);

                selectedVillageList.clear();
                binding.tvVillage.setText(item.item);
                binding.tvVillage.setTag(item.id);

                binding.tvVillage.setTextColor(getColor(R.color.button_bg));

                selectedVillageList.add(item.id);

                HashSet<Number> filter = new HashSet(selectedVillageList);
                selectedVillageList = new ArrayList<Number>(filter);
                Log.i(TAG, "selected item pre village:" + selectedVillageList.toString());

                if (item.item.equals(getString(R.string.pleaseSelect))) {
                    selectedVillageList.clear();
                } else {
                    binding.tvVillage.setTextColor(getColor(R.color.black));
                }

                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setFilterTextColor() {
        binding.tvProvince.setTextColor(getColor(R.color.button_bg));
        binding.tvDistrict.setTextColor(getColor(R.color.button_bg));
        binding.tvSubDistrict.setTextColor(getColor(R.color.button_bg));
        binding.tvVillage.setTextColor(getColor(R.color.button_bg));
    }

    private void layoutGoneView() {
        binding.cvSortByList.setVisibility(View.GONE);
        binding.clFilterList.setVisibility(View.GONE);
        binding.ivMyCustomerArrowUp.setVisibility(View.GONE);
        binding.cvSortByList.setVisibility(View.GONE);
        binding.ivMyCustomerArrowDown.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (binding.clTitle.clRegister.getVisibility() == View.GONE) {
            binding.clTitle.clRegister.setVisibility(View.VISIBLE);
            binding.clBottomLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            finish();
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftKeyboard() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.clTitle.etSearchViewDetails, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                showKeyboard = false;
            }
        }, 100);
    }
}