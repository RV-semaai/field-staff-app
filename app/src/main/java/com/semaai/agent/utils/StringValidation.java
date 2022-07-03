package com.semaai.agent.utils;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.activity.newcustomer.CameraGPSActivity;
import com.semaai.agent.activity.newcustomer.CustomerAddressActivity;
import com.semaai.agent.activity.newcustomer.CustomerDetailActivity;
import com.semaai.agent.activity.newcustomer.CustomerRegistrationActivity;
import com.semaai.agent.databinding.ActivityCustomerAddressBinding;
import com.semaai.agent.databinding.ActivityCustomerRegistrationBinding;
import com.semaai.agent.databinding.ActivityStaffLoginBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.newcustomer.AddressDataModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.viewmodel.login.LoginViewModel;
import com.semaai.agent.viewmodel.newcustomer.RegistrationViewModel;

public class StringValidation {

    private static final String TAG = StringValidation.class.getSimpleName() + "-->";
    ToastMsg toastMsg = new ToastMsg();
    ItemOnClick itemOnClick = new ItemOnClick();
    AddressDataModel addressDataModel;


    public void loginData(ActivityStaffLoginBinding binding, StaffLoginActivity staffLoginActivity, LoginViewModel loginViewModel, Dialog progressDialog) {
        toastMsg.loginMsgClose(binding);
        if (binding.etPhoneNumber.getText().toString().equals("")) {
            binding.etPhoneNumber.setError(staffLoginActivity.getString(R.string.enterNo));
            binding.etPhoneNumber.requestFocus();
        } else if (binding.etPassword.getText().toString().equals("")) {
            binding.etPassword.setError(staffLoginActivity.getString(R.string.enterPassword));
            binding.etPassword.requestFocus();
        } else {
            progressDialog.show();
            RequestAPI apiManager = new RequestAPI(staffLoginActivity);
            itemOnClick.onLoginClick(apiManager, loginViewModel, staffLoginActivity, toastMsg, progressDialog, binding);
        }
    }

    public void customerRegistration(ActivityCustomerRegistrationBinding binding, CustomerRegistrationActivity customerRegistrationActivity, RegistrationViewModel registerNameViewModel, Dialog progressDialog) {
        if (binding.etName.getText().toString().equals("")) {
            binding.etName.setError(customerRegistrationActivity.getString(R.string.enterName));
            binding.etName.requestFocus();
        } else if (binding.etPhone.getText().toString().equals("")) {
            binding.etPhone.setError(customerRegistrationActivity.getString(R.string.enterNo));
            binding.etPhone.requestFocus();
        } else if (!binding.etPhone.getText().toString().startsWith("08")) {
            binding.etPhone.setError(customerRegistrationActivity.getString(R.string.msg4));
            binding.etPhone.requestFocus();
        } else {
            progressDialog.show();
            registerNameViewModel.phone.setValue(binding.etPhone.getText().toString());
            registerNameViewModel.name.setValue(binding.etName.getText().toString());
            RequestAPI apiManager = new RequestAPI(customerRegistrationActivity);
            itemOnClick.onRegisterClick(apiManager, registerNameViewModel, binding, customerRegistrationActivity, progressDialog);
        }
    }

    public void customerAddress(ActivityCustomerAddressBinding binding, CustomerAddressActivity customerAddressActivity, CustomerModel customerModel, Dialog progressDialog) {
        String province = customerAddressActivity.getString(R.string.province);
        String district = customerAddressActivity.getString(R.string.district);
        String subDistrict = customerAddressActivity.getString(R.string.subDistrict);
        String village = customerAddressActivity.getString(R.string.village);
        String group = customerAddressActivity.getString(R.string.group);
        String postCode = customerAddressActivity.getString(R.string.postCode);
        String streetName = customerAddressActivity.getString(R.string.streetName1);
        if (binding.tvProvince.getText().equals(province)) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.selected) + " " + province);
        } else if (binding.tvDistrict.getText().equals(district)) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.selected) + " " + district);
        } else if (binding.tvCounty.getText().equals(subDistrict)) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.selected) + " " + subDistrict);
        } else if (binding.tvVillage.getText().equals(village)) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.selected) + " " + village);
        } else if (binding.tvGroup.getText().equals(group)) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.selected) + " " + group);
        } else if (binding.etPostCode.getText().toString().equals("")) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.enter) + " " + postCode);
        } else if (binding.etStreetName.getText().toString().equals("")) {
            toastMsg.showToast(customerAddressActivity, customerAddressActivity.getString(R.string.enter) + " " + streetName);
        } else {
            progressDialog.show();
            addressDataModel = new AddressDataModel();
            addressDataModel.setProvince(binding.tvProvince.getText().toString());
            addressDataModel.setProvinceId(binding.tvProvince.getTag().toString());
            addressDataModel.setDistrict(binding.tvDistrict.getText().toString());
            addressDataModel.setDistrictId(binding.tvDistrict.getTag().toString());
            addressDataModel.setSubDistrict(binding.tvCounty.getText().toString());
            addressDataModel.setSubDistrictId(binding.tvCounty.getTag().toString());
            addressDataModel.setVillage(binding.tvVillage.getText().toString());
            addressDataModel.setVillageId(binding.tvVillage.getTag().toString());
            addressDataModel.setPostCode(binding.etPostCode.getText().toString());
            addressDataModel.setStreetName(binding.etStreetName.getText().toString());
            addressDataModel.setGroup(binding.tvGroup.getText().toString());
            addressDataModel.setGroupId(binding.tvGroup.getTag().toString());
            customerModel.setAddressDataModel(addressDataModel);
            Intent intent = new Intent(customerAddressActivity, CameraGPSActivity.class);
            intent.putExtra("sample", customerModel);
            customerAddressActivity.startActivity(intent);
            progressDialog.dismiss();
        }
    }

    public void chekNumber(ActivityCustomerRegistrationBinding binding, String string) {
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i == 0) {
                    if (!binding.etPhone.getText().toString().startsWith("0")) {
                        binding.etPhone.setError(string);
                    }

                } else if (i == 1) {
                    if (!binding.etPhone.getText().toString().startsWith("08")) {
                        binding.etPhone.setError(string);
                    }
                } else if (!binding.etPhone.getText().toString().startsWith("08")) {
                    binding.etPhone.setError(string);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    public void onPhotoLocationClick(CameraGPSActivity cameraGPSActivity, CameraGPSDataModel cameraGPSDataModel, CustomerModel customerModel) {
        customerModel.setCameraGPSDataModel(cameraGPSDataModel);
        Log.e(TAG, "setModel: >>>>> 11 " + new Gson().toJson(cameraGPSDataModel));
        Intent intent = new Intent(cameraGPSActivity, CustomerDetailActivity.class);
        intent.putExtra("sample", customerModel);
        cameraGPSActivity.startActivity(intent);
    }

}
