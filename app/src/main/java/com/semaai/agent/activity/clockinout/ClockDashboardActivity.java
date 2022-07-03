package com.semaai.agent.activity.clockinout;

import static com.semaai.agent.utils.Constant.clockInClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.existingcustomers.CustomersListActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityClockDashboardBinding;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.clockinout.ClockInOutDashboardViewModel;

public class ClockDashboardActivity extends AppCompatActivity {

    String TAG = ClockInOutDashboardViewModel.class.getSimpleName() + "-->";
    private ClockInOutDashboardViewModel clockInOutDashboardViewModel;
    private ActivityClockDashboardBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clockInOutDashboardViewModel = ViewModelProviders.of(this).get(ClockInOutDashboardViewModel.class);
        binding = DataBindingUtil.setContentView(ClockDashboardActivity.this, R.layout.activity_clock_dashboard);
        binding.setLifecycleOwner(this);
        binding.setClockDashboardViewModel(clockInOutDashboardViewModel);

        intiView();
        onClick();

    }

    private void intiView() {
        binding.rlRegister.tvTopBar.setText(Constant.name);
    }

    private void onClick() {

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                startActivity(intent);
            }
        });

        binding.cvClockNewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClockDashboardActivity.this, NewCustomerActivity.class));
            }
        });

        binding.cvClockExistingCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockInClick = 1;
                startActivity(new Intent(ClockDashboardActivity.this, CustomersListActivity.class));
            }
        });

        binding.cvClockOtherCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClockDashboardActivity.this, OthersActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockInClick = 0;
    }
}