package com.semaai.agent.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.semaai.agent.databinding.ActivityStaffLoginBinding;


public class ToastMsg {

    public void loginMsgShow(ActivityStaffLoginBinding binding, String string) {
        binding.clMessage.setVisibility(View.VISIBLE);
        binding.tvLoginValidationMsg.setText(string);
    }

    public void loginMsgClose(ActivityStaffLoginBinding binding) {
        binding.clMessage.setVisibility(View.GONE);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
