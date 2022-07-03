package com.semaai.agent.activity.clockinout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivitySucceedBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.clockinout.CustomerViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SucceedActivity extends AppCompatActivity {
    CustomerViewModel customerViewModel;
    ActivitySucceedBinding binding;
    private CustomerModel customerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerViewModel = ViewModelProviders.of(this).get(CustomerViewModel.class);
        binding = DataBindingUtil.setContentView(SucceedActivity.this, R.layout.activity_succeed);
        binding.setLifecycleOwner(this);
        binding.setCustomerViewModel(customerViewModel);

        intiView();
        onClick();

    }


    private void intiView() {
        binding.rlRegister.clRegister.setVisibility(View.GONE);
        binding.rlRegister.tvTopBar.setText(Constant.name);
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        String clock = i.getStringExtra("clock");
        if (clock.contentEquals("out")) {

            binding.tvSucceedLocation.setVisibility(View.GONE);
            binding.tvSucceedOut.setVisibility(View.VISIBLE);

            Date userDob = null;
            Date today = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            try {
                userDob = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(i.getStringExtra("clockInTime"));
                today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long diff = today.getTime() - userDob.getTime();
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            int hours = (int) (diff / (1000 * 60 * 60));
            int minutes = (int) (diff / (1000 * 60));
            int seconds = (int) (diff / (1000));

            hours = seconds / 3600;
            minutes = (seconds % 3600) / 60;
            seconds = seconds % 60;

            if (seconds != 0) {
                String s = getString(R.string.succeedMagClockOut) + " " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " " + getString(R.string.ssAt);
                binding.tvSucceedOut.setText(s);
                if (minutes != 0) {
                    String m = getString(R.string.succeedMagClockOut) + " " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " " + getString(R.string.mmAt);
                    binding.tvSucceedOut.setText(m);
                    if (hours != 0) {
                        String h = getString(R.string.succeedMagClockOut) + " " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " " + getString(R.string.hhAt);
                        binding.tvSucceedOut.setText(h);

                    }
                }
            }
            Typeface typeface = ResourcesCompat.getFont(SucceedActivity.this, R.font.open_sans_semibold);
            binding.tvSetusername.setTypeface(typeface);
            Constant.ss = 0;
        }
        binding.tvSetusername.setText(customerModel.getClockInOutCustomerModel().getNewCustomer());

    }

    private void onClick() {
        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openActivity(SucceedActivity.this, DashboardActivity.class);
            }
        });

        binding.cvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.openActivity(SucceedActivity.this, DashboardActivity.class);
            }
        });
        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.openActivity(SucceedActivity.this, DashboardActivity.class);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.openActivity(SucceedActivity.this, DashboardActivity.class);
    }
}