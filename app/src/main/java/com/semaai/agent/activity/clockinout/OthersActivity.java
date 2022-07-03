package com.semaai.agent.activity.clockinout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityOthersBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.clockinout.ClockInOutCustomerModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.clockinout.OtherActivityViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OthersActivity extends AppCompatActivity {

    private OtherActivityViewModel otherActivityViewModel;
    private ActivityOthersBinding binding;
    ClockInOutCustomerModel clockInOutCustomerModel = new ClockInOutCustomerModel();
    CustomerModel customerModel = new CustomerModel();
    String actionFor, name, Aid, ExiId, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherActivityViewModel = ViewModelProviders.of(this).get(OtherActivityViewModel.class);
        binding = DataBindingUtil.setContentView(OthersActivity.this, R.layout.activity_others);
        binding.setLifecycleOwner(this);
        binding.setOtherActivityViewModel(otherActivityViewModel);

        intview();
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


    private void intview() {
        binding.rlRegister.tvTopBar.setText(Constant.name);
        binding.tvTime.setVisibility(View.GONE);
        binding.tvTest.setVisibility(View.GONE);

        SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
        actionFor = sh.getString("actionFor", "");
        name = sh.getString("name", "none");
        Aid = sh.getString("AId", "");
        ExiId = sh.getString("ExiId", "");
        time = sh.getString("time", "");

        if (actionFor.contentEquals(getString(R.string.existingCustomer))) {
            binding.tvClockOutName.setVisibility(View.VISIBLE);
            binding.tvClockOutName.setText(name);
            binding.tvClockBtn.setText(R.string.clockOut);
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            binding.tvOther.setVisibility(View.GONE);

            binding.tvTime.setVisibility(View.VISIBLE);
            binding.tvTest.setVisibility(View.VISIBLE);

            timeSpend();

        } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {
            binding.tvClockOutName.setVisibility(View.VISIBLE);
            binding.tvClockOutName.setText(name);
            binding.etSearchNameViewDetails.setText(name, TextView.BufferType.EDITABLE);
            binding.tvClockBtn.setText(R.string.clockOut);
            binding.rlRegister.tvHeader.setText(R.string.clockOut);
            binding.tvOther.setVisibility(View.GONE);

            binding.tvTime.setVisibility(View.VISIBLE);
            binding.tvTest.setVisibility(View.VISIBLE);


            timeSpend();

        }
    }

    private void onClick() {
        binding.cvClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getApplicationContext().getSharedPreferences(Common.SP_KEY, Context.MODE_PRIVATE);
                String actionFor = sh.getString("actionFor", "");
                String name = sh.getString("name", "none");
                String Aid = sh.getString("AId", "");
                String ExiId = sh.getString("ExiId", "");

                if (actionFor.contentEquals(getString(R.string.existingCustomer))) {

                    clockInOutCustomerModel.setNewCustomer(name);
                    clockInOutCustomerModel.setId(ExiId);
                    customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                    Constant.actionFor = getString(R.string.existingCustomer);
                    Intent intent = new Intent(OthersActivity.this, StorePhotoAndGPSActivity.class);
                    intent.putExtra("sample", customerModel);
                    startActivity(intent);

                } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {

                    clockInOutCustomerModel.setNewCustomer(name);
                    clockInOutCustomerModel.setId("");
                    customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                    Constant.actionFor = getString(R.string.otherCustomer);
                    Intent intent = new Intent(OthersActivity.this, StorePhotoAndGPSActivity.class);
                    intent.putExtra("sample", customerModel);
                    startActivity(intent);

                } else {

                    if (binding.etSearchNameViewDetails.getText().toString().equals("")) {
                        binding.etSearchNameViewDetails.setError(getString(R.string.enterName));
                        binding.etSearchNameViewDetails.requestFocus();
                    } else {
                        clockInOutCustomerModel.setNewCustomer(binding.etSearchNameViewDetails.getText().toString());
                        clockInOutCustomerModel.setId("");
                        customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                        Constant.actionFor = getString(R.string.otherCustomer);
                        Intent intent = new Intent(OthersActivity.this, StorePhotoAndGPSActivity.class);
                        intent.putExtra("sample", customerModel);
                        startActivity(intent);
                    }
                }
            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(0);
            }
        });

        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(1);
            }
        });


        binding.tvClockOutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvClockOutName.setOnClickListener(null);
            }
        });


        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(2);
            }
        });
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
        if (actionFor.contentEquals(getString(R.string.existingCustomer))) {
            setTimer();
        } else if (actionFor.contentEquals(getString(R.string.otherCustomer))) {
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