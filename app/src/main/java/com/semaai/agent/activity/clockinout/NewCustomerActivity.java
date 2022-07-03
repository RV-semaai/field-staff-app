package com.semaai.agent.activity.clockinout;


import static com.semaai.agent.utils.Constant.clockInClick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.existingcustomers.CustomersListActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityNewCustomerBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.clockinout.ClockInOutCustomerModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.clockinout.CustomerViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewCustomerActivity extends AppCompatActivity {

    String TAG = NewCustomerActivity.class.getSimpleName() + "--->";
    private CustomerViewModel customerViewModel;
    private ActivityNewCustomerBinding binding;
    ClockInOutCustomerModel clockInOutCustomerModel = new ClockInOutCustomerModel();
    public static CustomerModel customerModel = new CustomerModel();
    private int sec = 0;
    String actionFor, name, aId, exiId, time;
    private static final int REQUEST_CHANGE_NAME = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerViewModel = ViewModelProviders.of(this).get(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(NewCustomerActivity.this, R.layout.activity_new_customer);
        binding.setLifecycleOwner(this);
        binding.setCustomerViewModel(customerViewModel);

        intiView();
        onClick();
    }


    private void timeSpend() {
        Date userDob = null;
        Date today = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        try {
            userDob = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = today.getTime() - userDob.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) (diff / (1000 * 60 * 60));
        int minutes = (int) (diff / (1000 * 60));
        int seconds = (int) (diff / (1000));

        Constant.hh = hours;
        Constant.mm = minutes;
        Constant.ss = seconds;

        hours = seconds / 3600;
        minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        binding.tvTime.setText(timeString);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void intiView() {
        binding.rlRegister.tvTopBar.setText(Constant.name);
        binding.tvTime.setVisibility(View.GONE);
        binding.tvTest.setVisibility(View.GONE);
        binding.cvChangeCustomerName.setVisibility(View.GONE);

        SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        actionFor = sh.getString("actionFor", "");
        name = sh.getString("name", "none");
        aId = sh.getString("AId", "");
        exiId = sh.getString("ExiId", "");
        time = sh.getString("time", "");


        if (actionFor.contentEquals(getString(R.string.newCustomer))) {
            binding.tvClockOutName.setVisibility(View.VISIBLE);
            binding.tvClockOutName.setText(name);
            binding.tvClockBtn.setText(R.string.clockOut);
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            binding.tvNewCustomer.setVisibility(View.GONE);

            binding.cvChangeCustomerName.setVisibility(View.VISIBLE);
            binding.tvTime.setVisibility(View.VISIBLE);
            binding.tvTest.setVisibility(View.VISIBLE);

            timeSpend();
        }

    }

    private void onClick() {
        binding.cvClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
                String actionFor = sh.getString("actionFor", "");
                String name = sh.getString("name", "none");

                if (actionFor.contentEquals(getString(R.string.newCustomer))) {

                    if (clockInClick == 2) {
                        Constant.actionFor = getString(R.string.newCustomer);
                        Intent intent = new Intent(NewCustomerActivity.this, StorePhotoAndGPSActivity.class);
                        intent.putExtra("sample", customerModel);
                        startActivity(intent);
                    } else {
                        clockInOutCustomerModel.setNewCustomer(name);
                        clockInOutCustomerModel.setId("");
                        customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                        Constant.actionFor = getString(R.string.newCustomer);
                        Intent intent = new Intent(NewCustomerActivity.this, StorePhotoAndGPSActivity.class);
                        intent.putExtra("sample", customerModel);
                        startActivity(intent);
                    }

                } else {

                    if (binding.etSearchViewDetails.getText().toString().equals("")) {
                        binding.etSearchViewDetails.setError(getString(R.string.enterName));
                        binding.etSearchViewDetails.requestFocus();
                    } else {
                        clockInOutCustomerModel.setNewCustomer(binding.etSearchViewDetails.getText().toString());
                        clockInOutCustomerModel.setId("");
                        customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                        Constant.actionFor = getString(R.string.newCustomer);
                        Intent intent = new Intent(NewCustomerActivity.this, StorePhotoAndGPSActivity.class);
                        intent.putExtra("sample", customerModel);
                        startActivity(intent);
                    }
                }
            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(0);
            }
        });

        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });

        binding.icBottomMenu.ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
            }
        });

        binding.tvClockOutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvClockOutName.setOnClickListener(null);
            }
        });

        binding.cvChangeCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockInClick = 2;

                Intent intent = new Intent(NewCustomerActivity.this, CustomersListActivity.class);
                startActivityIfNeeded(intent, REQUEST_CHANGE_NAME);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_NAME) {
            if (resultCode == RESULT_OK) {
                binding.tvClockOutName.setText(customerModel.getClockInOutCustomerModel().getNewCustomer());
            }
        }
    }

    private void setTimer() {
        new CountDownTimer(1000 * 60 * 60 * 24, 1000) {
            public void onTick(long millisUntilFinished) {
                timeSpend();
            }

            public void onFinish() {
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.clockInChangeNameNull) {
            Constant.clockInChangeNameNull = false;
            clockInClick = 0;
        }
        if (actionFor.contentEquals(getString(R.string.newCustomer))) {
            setTimer();
        }

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