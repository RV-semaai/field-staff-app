package com.semaai.agent.activity.existingcustomers;

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
import com.semaai.agent.databinding.ActivityCustomerEditSaveBinding;
import com.semaai.agent.utils.Common;
import com.semaai.agent.viewmodel.existingcustomers.CustomerEditSaveViewModel;

public class CustomerEditSaveActivity extends AppCompatActivity {

    private CustomerEditSaveViewModel customerEditSaveViewModel;
    private ActivityCustomerEditSaveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerEditSaveViewModel = ViewModelProviders.of(this).get(CustomerEditSaveViewModel.class);
        binding = DataBindingUtil.setContentView(CustomerEditSaveActivity.this, R.layout.activity_customer_edit_save);
        binding.setLifecycleOwner(this);
        binding.setCustomerEditSaveViewModel(customerEditSaveViewModel);

        intView();
        onClick();
    }

    private void intView() {
        binding.rlRegister.tvHeader.setText(getText(R.string.profile));
        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void onClick() {
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
        onBackClick(1);
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