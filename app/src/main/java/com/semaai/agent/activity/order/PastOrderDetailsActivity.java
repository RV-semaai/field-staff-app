package com.semaai.agent.activity.order;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.adapter.order.PastOrderAdapter;
import com.semaai.agent.databinding.ActivityCustomerPastOrderDetailsBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.order.OrderDetailsModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.order.PastOrderDetailsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PastOrderDetailsActivity extends AppCompatActivity {
    PastOrderDetailsViewModel customerPastOrderDetailsViewModel;
    private ActivityCustomerPastOrderDetailsBinding binding;
    private Dialog progressDialog;
    ArrayList<OrderDetailsModel> orderDetailsListArray = new ArrayList();
    ArrayList<OrderDetailsModel> in_orderDetailsListArray = new ArrayList<>();
    private CustomerModel customerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerPastOrderDetailsViewModel = ViewModelProviders.of(PastOrderDetailsActivity.this).get(PastOrderDetailsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_past_order_details);
        binding.setLifecycleOwner(this);
        binding.setCustomerPastOrderDetailsViewModel(customerPastOrderDetailsViewModel);

        intiView();
        apiCall();
        onClick();
    }


    private void intiView() {
        //getData
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.clTitle.tvHeader.setText(getResources().getString(R.string.pastOrders));
        binding.clTitle.tvTopBar.setText(customerModel.getCustomerListModel().getName());
        Log.e("TAG", "intview: check model " + new Gson().toJson(customerModel.getOrderListModel()));

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
    }

    private void apiCall() {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.orderDetails + customerModel.getOrderListModel().getId())
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    private JSONArray productJsonArray;

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: >>> " + response);
                        try {
                            JSONArray dataJsonArray = response.getJSONArray("data");
                            Log.e("TAG", "onResponse: >>>> details " + dataJsonArray.length());
                            if (dataJsonArray.length() != 0) {
                                binding.tvNoRecord.setVisibility(View.GONE);
                                binding.rvPostalItemDetails.setVisibility(View.VISIBLE);
                                for (int i = 0; i < dataJsonArray.length(); i++) {
                                    JSONObject dataObject = dataJsonArray.getJSONObject(i);
                                    productJsonArray = dataObject.getJSONArray("product_detail");

                                    Log.e("TAG", "onResponse: >>>>>>" + productJsonArray.length());
                                    orderDetailsListArray.add(new OrderDetailsModel(dataObject.getString("id"),
                                            dataObject.getString("date"),
                                            dataObject.getString("invoice_id"),
                                            dataObject.getString("payment_status"),
                                            dataObject.getString("payment_method"),
                                            dataObject.getString("delivery_status"),
                                            dataObject.getString("delivery_date"),
                                            dataObject.getString("salesperson"),
                                            dataObject.getString("untaxed_amount"),
                                            dataObject.getString("tax"),
                                            dataObject.getString("total_amount"),
                                            dataObject.getString("product_detail"),
                                            dataObject.getString("loyalty_total")));

                                    Log.e("TAG", "onResponse: >>>>>" + new Gson().toJson(orderDetailsListArray));
                                }

                                if (productJsonArray.length() != 0) {
                                    for (int i = 0; i < productJsonArray.length(); i++) {
                                        JSONObject productDetailObject = productJsonArray.getJSONObject(i);
                                        in_orderDetailsListArray.add(new OrderDetailsModel(productDetailObject.getString("id"),
                                                productDetailObject.getString("product_cat_name"),
                                                productDetailObject.getString("name"),
                                                productDetailObject.getString("reference_code"),
                                                productDetailObject.getString("marketplace_seller"),
                                                productDetailObject.getString("image"),
                                                productDetailObject.getString("product_qty"),
                                                productDetailObject.getString("original_price"),
                                                productDetailObject.getString("price_paid")));
                                    }
                                }

                                LinearLayoutManager manager = new LinearLayoutManager(PastOrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
                                PastOrderAdapter customerPastOrderAdapter = new PastOrderAdapter(PastOrderDetailsActivity.this, orderDetailsListArray, in_orderDetailsListArray);
                                binding.rvPostalItemDetails.setLayoutManager(manager);
                                binding.rvPostalItemDetails.setAdapter(customerPastOrderAdapter);
                            } else {
                                binding.rvPostalItemDetails.setVisibility(View.GONE);
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "onResponse: catch >>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>> " + error.getMessage());
                    }
                });
    }

    private void onClick() {
        binding.clTitle.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
}