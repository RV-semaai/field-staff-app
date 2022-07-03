package com.semaai.agent.activity.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.databinding.ActivityStaffLoginBinding;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.StringValidation;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.login.LoginViewModel;


public class StaffLoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityStaffLoginBinding binding;
    String TAG = StaffLoginActivity.class.getSimpleName() + "-->";
    ToastMsg toastMsg = new ToastMsg();
    StringValidation stringValidation = new StringValidation();
    private Dialog progressDialog;
    boolean showPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding = DataBindingUtil.setContentView(StaffLoginActivity.this, R.layout.activity_staff_login);
        binding.setLifecycleOwner(this);
        binding.setLoginViewModel(loginViewModel);

        initView();
        onClick();
    }

    private void initView() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        binding.etPassword.setTransformationMethod(new Common.AsteriskPasswordTransformationMethod());

    }

    private void onClick() {
        binding.cvLogInOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringValidation.loginData(binding, StaffLoginActivity.this, loginViewModel, progressDialog);
            }
        });
        binding.ivDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMsg.loginMsgClose(binding);
            }
        });

        binding.tlPassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPass){
                    binding.etPassword.setTransformationMethod(new Common.AsteriskPasswordTransformationMethod());
                    showPass = false;
                }else {
                    binding.etPassword.setTransformationMethod(null);
                    showPass = true;
                }
            }
        });

    }

    public void setData(int i) {
        if (i == 0) {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.putString("number", binding.etPhoneNumber.getText().toString());
            editor.putString("password", binding.etPassword.getText().toString());
            editor.apply();
            Constant.loginPassword = binding.etPassword.getText().toString();
            Intent intent = new Intent(StaffLoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else if (i == 1) {
            toastMsg.loginMsgShow(binding, getString(R.string.msg1));
            progressDialog.dismiss();
        } else {
            toastMsg.loginMsgShow(binding, getString(R.string.msg2));
            progressDialog.dismiss();
        }
    }
}