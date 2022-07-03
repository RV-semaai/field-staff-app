package com.semaai.agent.activity.targettracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityTargetTrackingDashboardBinding;
import com.semaai.agent.viewmodel.targettracking.TargetTrackingDashboardViewModel;

public class TargetTrackingDashboardActivity extends AppCompatActivity {

    private TargetTrackingDashboardViewModel targetTrackingDashboardViewModel;
    private ActivityTargetTrackingDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        targetTrackingDashboardViewModel = ViewModelProviders.of(this).get(TargetTrackingDashboardViewModel.class);
        binding = DataBindingUtil.setContentView(TargetTrackingDashboardActivity.this, R.layout.activity_target_tracking_dashboard);
        binding.setLifecycleOwner(this);
        binding.setTargetTrackingDashboardViewModel(targetTrackingDashboardViewModel);

        intview();
    }

    private void intview() {

        binding.cvMyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetTrackingDashboardActivity.this, MyTargetActivity.class));
            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetTrackingDashboardActivity.this, DashboardActivity.class));
            }
        });

        binding.icBottomMenu.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetTrackingDashboardActivity.this, AccountDashboardActivity.class));
            }
        });
        binding.icBottomMenu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetTrackingDashboardActivity.this, DashboardActivity.class));
            }
        });

        binding.cvTeamTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetTrackingDashboardActivity.this, TeamTargetActivity.class));
            }
        });

    }
}