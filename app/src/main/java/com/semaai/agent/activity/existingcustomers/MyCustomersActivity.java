package com.semaai.agent.activity.existingcustomers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.order.OrdersListActivity;
import com.semaai.agent.activity.payment.PaymentStatusActivity;
import com.semaai.agent.databinding.ActivityMyCustomersBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.existingcustomers.CustomerDetailsModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.existingcustomers.MyCustomerViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyCustomersActivity extends AppCompatActivity {

    private MyCustomerViewModel myCustomerViewModel;
    private ActivityMyCustomersBinding binding;
    private CustomerModel customerModel;
    private Dialog progressDialog;
    private CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
    public static double lati, longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCustomerViewModel = ViewModelProviders.of(this).get(MyCustomerViewModel.class);
        binding = DataBindingUtil.setContentView(MyCustomersActivity.this, R.layout.activity_my_customers);
        binding.setLifecycleOwner(this);
        binding.setMyCustomerViewModel(myCustomerViewModel);

        intiView();
        onClick();
        apiCall();

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
                                //group
                                try {
                                    JSONObject groupObject = jsonObject.getJSONObject(ApiStringModel.group);
                                    customerDetailsModel.setGroupID(groupObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setGroup(groupObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //country
                                try {
                                    JSONObject countryObject = jsonObject.getJSONObject(ApiStringModel.country);
                                    customerDetailsModel.setCountryID(countryObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setCountry(countryObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //state
                                try {
                                    JSONObject stateObject = jsonObject.getJSONObject(ApiStringModel.state);
                                    customerDetailsModel.setStateID(stateObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setState(stateObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //districts
                                try {
                                    JSONObject districtsObject = jsonObject.getJSONObject(ApiStringModel.districts);
                                    customerDetailsModel.setDistrictID(districtsObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setDistrict(districtsObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //sub_districts
                                try {
                                    JSONObject sub_districtsObject = jsonObject.getJSONObject(ApiStringModel.subDistricts);
                                    customerDetailsModel.setSubDistrictsID(sub_districtsObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setSubDistricts(sub_districtsObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //village
                                try {
                                    JSONObject villageObject = jsonObject.getJSONObject(ApiStringModel.village);
                                    customerDetailsModel.setVillageID(villageObject.getString(ApiStringModel.id));
                                    customerDetailsModel.setVillage(villageObject.getString(ApiStringModel.name));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                                customerDetailsModel.setStreet(jsonObject.getString(ApiStringModel.street));
                                customerDetailsModel.setZip(jsonObject.getString(ApiStringModel.zip));
                                customerDetailsModel.setLatitude(jsonObject.getString(ApiStringModel.latitude));
                                lati = Double.parseDouble(jsonObject.getString(ApiStringModel.latitude));
                                longi = Double.parseDouble(jsonObject.getString(ApiStringModel.longitude));
                                customerDetailsModel.setLongitude(jsonObject.getString(ApiStringModel.longitude));
                                customerDetailsModel.setStoreImage(jsonObject.getString(ApiStringModel.storeImage));
                                customerDetailsModel.setProfileImage(jsonObject.getString(ApiStringModel.profileImage));
                                customerDetailsModel.setSalesperson(jsonObject.getString(ApiStringModel.salesperson));
                                Log.e("TAG", "onResponse: <><><><>" + jsonObject.getString("name"));

                            }
                            if (!customerDetailsModel.getStoreImage().equals("false")) {
                                byte[] decodedString = Base64.decode(customerDetailsModel.getProfileImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(MyCustomersActivity.this)
                                        .load(decodedByte)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .dontTransform()
                                        .circleCrop()
                                        .into(binding.ivProfile);
                            }
                            progressDialog.dismiss();
                            Log.i("-->", "testing" + new Gson().toJson(customerDetailsModel));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastMsg.showToast(getApplicationContext(), getString(R.string.errorMessage));
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>><<<" + error.getMessage().toString());
                        ToastMsg.showToast(getApplicationContext(), getString(R.string.errorMessage));
                        progressDialog.dismiss();
                    }
                });
    }

    private void intiView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        if (Constant.whichCustomer.equals("my")) {
            binding.rlRegister.tvHeader.setText(R.string.myCustomers);
        } else {
            binding.rlRegister.tvHeader.setText(R.string.otherCustomers);
        }
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        if (!customerModel.getCustomerListModel().getProfileImage().equals("false")) {
            byte[] decodedString = Base64.decode(customerModel.getCustomerListModel().getProfileImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this)
                    .load(decodedByte)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontTransform()
                    .circleCrop()
                    .into(binding.ivProfile);
        }
        binding.tvCustomerName.setText(customerModel.getCustomerListModel().getName());
        binding.rlRegister.tvTopBar.setText(customerModel.getCustomerListModel().getName());
    }

    private void onClick() {
        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.cvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (customerDetailsModel.getGroup() != null) {
                    Intent intent = new Intent(MyCustomersActivity.this, CustomersProfileDetailActivity.class);
                    intent.putExtra("sample", customerModel);
                    startActivity(intent);
//                } else {
//
//
//                    Toast.makeText(getApplicationContext(), "" + getResources().getString(R.string.notCompleteDate), Toast.LENGTH_SHORT).show();
//                }
            }
        });

        binding.cvOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCustomersActivity.this, OrdersListActivity.class);
                intent.putExtra("sample", customerModel);
                startActivity(intent);
            }
        });

        binding.cvPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCustomersActivity.this, PaymentStatusActivity.class);
                intent.putExtra("sample", customerModel);
                startActivity(intent);
            }
        });
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
                Intent i = new Intent(MyCustomersActivity.this, DashboardActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });
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
    protected void onResume() {
        super.onResume();
        if (Constant.updateImage) {
            Glide.with(this)
                    .load(Constant.updateImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontTransform()
                    .circleCrop()
                    .into(binding.ivProfile);
            Constant.updateImage = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackClick(0);
    }
}