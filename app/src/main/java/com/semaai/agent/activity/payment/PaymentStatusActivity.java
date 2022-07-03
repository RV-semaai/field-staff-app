package com.semaai.agent.activity.payment;

import android.annotation.SuppressLint;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityPaymentStatusBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.payment.PaymentModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.payment.PaymentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PaymentStatusActivity extends AppCompatActivity {

    private ActivityPaymentStatusBinding binding;
    private PaymentViewModel paymentViewModel;
    private CustomerModel customerModel;
    private Dialog progressDialog;
    ArrayList<PaymentModel> paymentArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_status);
        binding.setLifecycleOwner(this);
        binding.setPaymentViewModel(paymentViewModel);

        intView();
        onClick();
        apiCall();

    }

    private void onClick() {
        binding.clTitle.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(0);
            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
            }
        });
    }

    private void intView() {
        //getData
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.clTitle.tvHeader.setText(getResources().getString(R.string.paymentStatus));
        binding.clTitle.tvTopBar.setText(customerModel.getCustomerListModel().getName());

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
        AndroidNetworking.get(ApiEndPoints.salesOrder)
                .addQueryParameter(ApiStringModel.customerId, customerModel.getCustomerListModel().getId())
                .addHeaders("X-Openerp-Session-Id", separated[1])
//                .addHeaders("X-Openerp-Session-Id", "a97128348c27f1958dd95cdd99779f4ff15afb75")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: >>> " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject dataObject = response.getJSONObject(ApiStringModel.data);
                                String count = dataObject.getString(ApiStringModel.count);
                                JSONArray resultJsonArray = dataObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    Iterator keys = object.keys();
                                    String currentDynamicKey = "";
                                    while (keys.hasNext()) {
                                        currentDynamicKey = (String) keys.next();
                                    }
                                    JSONObject paidObject = object.getJSONObject(currentDynamicKey);
                                    paymentArray.add(new PaymentModel(paidObject.getDouble("total_amount"), paidObject.getInt("count")));
                                    if (i == 0) {
                                        binding.tvPaidCount.setText(Integer.toString(paidObject.getInt("count")));
                                        binding.tvPaidAmount.setText("Rp " + Common.currencyFormatOnlyZero(Double.toString(paidObject.getDouble("total_amount"))));
                                    } else if (i == 1) {
                                        binding.tvUnpaidCount.setText(Integer.toString(paidObject.getInt("count")));
                                        binding.tvUnpaidAmount.setText("Rp " + Common.currencyFormatOnlyZero(Double.toString(paidObject.getDouble("total_amount"))));
                                    } else if (i == 2) {
                                        binding.tvPartiallyPaidCount.setText(Integer.toString(paidObject.getInt("count")));
                                        binding.tvPartiallyPaidAmount.setText("Rp " + Common.currencyFormatOnlyZero(Double.toString(paidObject.getDouble("total_amount"))));
                                    } else if (i == 3) {
                                        binding.tvOrderUnderProcessCount.setText(Integer.toString(paidObject.getInt("count")));
                                        binding.tvOrderUnderProcessAmount.setText("Rp " + Common.currencyFormatOnlyZero(Double.toString(paidObject.getDouble("total_amount"))));
                                    }
                                }
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>> " + error);
                        progressDialog.dismiss();
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