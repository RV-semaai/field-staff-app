package com.semaai.agent.activity.newcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityConfirmationBinding;
import com.semaai.agent.utils.Common;
import com.semaai.agent.viewmodel.newcustomer.RegisterCompleteViewModel;

public class ConfirmationActivity extends AppCompatActivity {

    RegisterCompleteViewModel viewModel;
    ActivityConfirmationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(RegisterCompleteViewModel.class);
        binding = DataBindingUtil.setContentView(ConfirmationActivity.this, R.layout.activity_confirmation);
        binding.setLifecycleOwner(this);
        binding.setRegisterCompleteViewModel(viewModel);

        onClick();
    }

    private void onClick() {
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.openActivity(ConfirmationActivity.this, DashboardActivity.class);
            }
        });

        binding.cvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.openActivity(ConfirmationActivity.this, DashboardActivity.class);
            }
        });
        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.openActivity(ConfirmationActivity.this, DashboardActivity.class);
    }

}