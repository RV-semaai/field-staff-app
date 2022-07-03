package com.semaai.agent.activity.newcustomer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.databinding.ActivityCustomerRegistrationBinding;
import com.semaai.agent.model.newcustomer.CustomerRegisterModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.StringValidation;
import com.semaai.agent.viewmodel.newcustomer.RegistrationViewModel;

public class CustomerRegistrationActivity extends AppCompatActivity {

    private static final String TAG = CustomerRegistrationActivity.class.getSimpleName() + "-->";
    RegistrationViewModel registerNameViewModel;
    ConstraintLayout home, account;
    ActivityCustomerRegistrationBinding binding;
    StringValidation stringValidation = new StringValidation();
    private Dialog progressDialog;
    private int checkClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNameViewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
        binding = DataBindingUtil.setContentView(CustomerRegistrationActivity.this, R.layout.activity_customer_registration);
        binding.setLifecycleOwner(this);
        binding.setRegisterNameViewModel(registerNameViewModel);

        setView();
        onclick();
        chekPhoneNo();
        checkEditMode();
    }

    private void chekPhoneNo() {
        stringValidation.chekNumber(binding, getString(R.string.msg4));
    }


    private void setView() {
        binding.clBackAlert.setVisibility(View.GONE);
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        home = binding.cmBottom.clHome;
        account = binding.cmBottom.clAccount;

        binding.clBackAlert.setVisibility(View.GONE);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void onclick() {

        binding.etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.etPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.clEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.icBackDialog.cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClick==2)
                {
                    Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                    startActivity(intent);
                    binding.clBackAlert.setVisibility(View.GONE);
                }
                else if (checkClick==1)
                {
                    Common.openActivity(getApplicationContext(), DashboardActivity.class);
                    binding.clBackAlert.setVisibility(View.GONE);
                }
            }
        });

        binding.icBackDialog.cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.clTitle.ivBackArrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick=1;
                onBackPressed();
            }
        });

        binding.cvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clMessage.setVisibility(View.GONE);
                binding.clBackAlert.setVisibility(View.GONE);
                stringValidation.customerRegistration(binding, CustomerRegistrationActivity.this, registerNameViewModel,progressDialog);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkClick=1;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick=2;
                binding.clBackAlert.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        checkClick=1;
        binding.clBackAlert.setVisibility(View.VISIBLE);
    }

    private void checkEditMode() {
        if (Constant.editMode) {
            CustomerRegisterModel customerRegisterModel = Constant.customerModel.getCustomerRegisterModel();
            binding.etName.setText(customerRegisterModel.getUserName());
            binding.etPhone.setText(customerRegisterModel.getUserPhone());
        }
    }
}