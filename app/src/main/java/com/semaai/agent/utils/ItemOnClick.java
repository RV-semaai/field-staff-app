package com.semaai.agent.utils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.cardview.widget.CardView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.protobuf.Api;
import com.semaai.agent.R;
import com.semaai.agent.activity.existingcustomers.CustomerEditSaveActivity;
import com.semaai.agent.activity.existingcustomers.CustomersProfileDetailActivity;
import com.semaai.agent.activity.existingcustomers.CustomersProfileUpdateActivity;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.activity.newcustomer.ConfirmationActivity;
import com.semaai.agent.activity.newcustomer.CustomerAddressActivity;
import com.semaai.agent.activity.newcustomer.CustomerDetailActivity;
import com.semaai.agent.activity.newcustomer.CustomerRegistrationActivity;
import com.semaai.agent.databinding.ActivityCustomerRegistrationBinding;

import com.semaai.agent.databinding.ActivityStaffLoginBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.newcustomer.AddressDataModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.model.newcustomer.CustomerRegisterModel;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.viewmodel.login.LoginViewModel;
import com.semaai.agent.viewmodel.newcustomer.RegistrationViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class ItemOnClick {

    ApiStringModel apiStringModel = new ApiStringModel();
    public static ArrayList<String> name = new ArrayList<>();
    private String TAG = ItemOnClick.class.getSimpleName() + "-->";


    public void onLoginClick(RequestAPI apiManager, LoginViewModel loginViewModel, StaffLoginActivity staffLoginActivity, ToastMsg toastMsg, Dialog progressDialog, ActivityStaffLoginBinding binding) {
        try {
            JSONObject obj = new JSONObject();
            JSONObject child = new JSONObject();
            child.put("login", loginViewModel.PhoneNumber.getValue());
            child.put("password", loginViewModel.Password.getValue());
            obj.put(apiStringModel.params, child);
            apiManager.sendPostRequest(ApiEndPoints.logInURL, obj, new APIResponse() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject result = null;
                    try {
                        result = response.getJSONObject(apiStringModel.result);

                        if (result.getInt(apiStringModel.status) == 200 && result.getString(apiStringModel.message).equals("SUCCESS")) {
                            JSONObject data = result.getJSONObject("data");
                            Constant.userId = data.getString("user_id");
                            Constant.name = data.getString("name");
                            Constant.companyId = data.getString("company_id");
                            try {
                                Constant.employeeId = data.getString("employee_id");
                                Constant.salespersonPartnerId = data.getString("partner_id");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Constant.profileImage = data.getString("profile_image");
                            Log.e(TAG, "onResponse:>>>>>>> " + Constant.profileImage);
                            staffLoginActivity.setData(0);

                        } else if (result.getInt(apiStringModel.status) == 200 && result.getString(apiStringModel.message).equals("Not a existence User")) {
                            staffLoginActivity.setData(1);
                        } else {
                            staffLoginActivity.setData(2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        staffLoginActivity.setData(2);
                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    staffLoginActivity.setData(2);
                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {
                    try {
                        Log.i(TAG, "response " + response.headers.toString());
                        Map<String, String> responseHeaders = response.headers;
                        String rawCookies = responseHeaders.get("Set-Cookie").split(";")[0];
                        Log.i(TAG, "cookies " + rawCookies);
                        Constant.cookie = rawCookies;
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception ex) {
            Log.i(TAG, "onClick try catch");
            staffLoginActivity.setData(2);
        }
    }

    private void sessionId() {
        try {
            JSONObject jsonObject = new JSONObject("{}");
            AndroidNetworking.post("https://semaai.clienturls.com/field-app/web/session/get_session_info")
                    .addJSONObjectBody(jsonObject)
                    .addHeaders("Content-Type", "application/json")
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("TAG", "onResponse:22222222222 " + response);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("TAG", "onError:22222222222 " + error.getErrorBody());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onRegisterClick(RequestAPI apiManager, RegistrationViewModel registerNameViewModel, ActivityCustomerRegistrationBinding binding, CustomerRegistrationActivity customerRegistrationActivity, Dialog progressDialog) {
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");

        JSONObject params = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            object.put("username", registerNameViewModel.phone.getValue());
            params.put(apiStringModel.params, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ApiEndPoints.customerURL)
                .addJSONObjectBody(params)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onRegister===: " + separated[1] + " url " + ApiEndPoints.customerURL);
                        Log.e("TAG", "onResponse: >>>>>>>>>>>  " + response);
                        try {
                            if (response.has("error")) {
                                progressDialogSet(customerRegistrationActivity);
                                return;
                            }
                            JSONObject result = response.getJSONObject("result");
                            String msg = result.getString("data");
                            if (msg.equals("User already existed")) {
                                binding.clMessage.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            } else {
                                CustomerModel customerModel = new CustomerModel();
                                CustomerRegisterModel customerRegisterModel = new CustomerRegisterModel();
                                customerRegisterModel.setUserName(binding.etName.getText().toString());
                                customerRegisterModel.setUserPhone(binding.etPhone.getText().toString());
                                customerModel.setCustomerRegisterModel(customerRegisterModel);
                                progressDialog.dismiss();
                                Intent intent = new Intent(customerRegistrationActivity, CustomerAddressActivity.class);
                                intent.putExtra("sample", customerModel);
                                customerRegistrationActivity.startActivity(intent);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>>>>  " + error.getErrorBody());
                    }
                });
    }

    private void progressDialogSet(CustomerRegistrationActivity customerRegistrationActivity) {
        Dialog progressDialog = new Dialog(customerRegistrationActivity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_session_expired_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        CardView cvReLogIn = progressDialog.findViewById(R.id.cv_reLogin);
        cvReLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(customerRegistrationActivity, StaffLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                customerRegistrationActivity.startActivity(i);
                progressDialog.dismiss();
            }
        });
    }

    public void onApproveClick(CustomerModel customerModel, RequestAPI apiManager, CustomerDetailActivity customerDetailActivity, Dialog progressDialog) {
        try {
            CustomerRegisterModel customerRegisterModel = customerModel.getCustomerRegisterModel();
            AddressDataModel addressDataModel = customerModel.getAddressDataModel();
            CameraGPSDataModel cameraGPSDataModel = customerModel.getCameraGPSDataModel();

            String imageString = null;

            if (cameraGPSDataModel.getImgPath() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(cameraGPSDataModel.getImgPath()));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }

            JSONObject obj = new JSONObject();
            JSONObject child = new JSONObject();
            child.put("username", customerRegisterModel.getUserPhone());
            child.put("name", customerRegisterModel.getUserName());
//            child.put("country_id", "1");
            child.put("state_id", addressDataModel.getProvinceId());
            child.put("district_id", addressDataModel.getDistrictId());
            child.put("sub_district_id", addressDataModel.getSubDistrictId());
            child.put("village_id", addressDataModel.getVillageId());
            child.put("company_type", "company");
            child.put("street", addressDataModel.getStreetName());
            child.put("email", customerRegisterModel.getUserName().replace(" ", "") + "@gmail.com");
            child.put("mobile", customerRegisterModel.getUserPhone());
            child.put("customer_grp_id", addressDataModel.getGroupId());
            if (imageString != null) {
                child.put("image", imageString);
            } else {
                child.put("image", "");
            }
            child.put("zip", addressDataModel.getPostCode());
            child.put("partner_latitude", cameraGPSDataModel.getLatitude());
            child.put("partner_longitude", cameraGPSDataModel.getLongitude());
            child.put("user_id", Constant.userId);
            Log.e(TAG, "onApproveClick: =========" + cameraGPSDataModel.getLatitude() + "   " + cameraGPSDataModel.getLongitude());
            Log.e(TAG, "onApproveClick:check" + Constant.cookie);

            obj.put(apiStringModel.params, child);
            Log.e(TAG, "onApproveClick: " + obj.toString());
            apiManager.sendPostRequestWithCookie(progressDialog, ApiEndPoints.customerApproveURL, obj, new APIResponse() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse: 123456 " + response);
                    try {
                        JSONObject result = response.getJSONObject(ApiStringModel.result);
                        if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("User Approved")) {
                            progressDialog.dismiss();
                            Intent i = new Intent(customerDetailActivity, ConfirmationActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            customerDetailActivity.startActivity(i);
                        } else {
                            progressDialog.dismiss();
                            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                        }
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onError: 123456 " + volleyError.getMessage());
                    ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));

                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {
                    progressDialog.dismiss();
                    ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                }
            });
        } catch (Exception ex) {
            progressDialog.dismiss();
            Log.e(TAG, "onClick try catch 123456 " + ex.getMessage());
            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
        }
    }

    public void onApproveClickQA(CustomerModel customerModel,
                                 RequestAPI apiManager,
                                 CustomerDetailActivity customerDetailActivity,
                                 Dialog progressDialog,
                                 int userId,
                                 String pws) {
        try {
            CustomerRegisterModel customerRegisterModel = customerModel.getCustomerRegisterModel();
            AddressDataModel addressDataModel = customerModel.getAddressDataModel();
            CameraGPSDataModel cameraGPSDataModel = customerModel.getCameraGPSDataModel();

            String imageString = null;

            if (cameraGPSDataModel.getImgPath() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(cameraGPSDataModel.getImgPath()));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }

            JSONObject obj = new JSONObject();
            JSONObject child = new JSONObject();
            child.put("user_id", userId);
            child.put("staff_id", Constant.userId);
            child.put("user_password", pws);
            child.put("state_id", addressDataModel.getProvinceId());
            child.put("district_id", addressDataModel.getDistrictId());
            child.put("sub_district_id", addressDataModel.getSubDistrictId());
            child.put("village_id", addressDataModel.getVillageId());
            child.put("company_type", "company");
            child.put("street", addressDataModel.getStreetName());
            child.put("email", customerRegisterModel.getUserName().replace(" ", "") + "@gmail.com");
            child.put("mobile", customerRegisterModel.getUserPhone());
            if (imageString != null) {
                child.put("image", imageString);
            } else {
                child.put("image", "");
            }
            child.put("customer_grp_id", addressDataModel.getGroupId());
            child.put("zip", addressDataModel.getPostCode());
            child.put("partner_latitude", cameraGPSDataModel.getLatitude());
            child.put("partner_longitude", cameraGPSDataModel.getLongitude());
            Log.e(TAG, "onApproveClick: =========" + cameraGPSDataModel.getLatitude() + "   " + cameraGPSDataModel.getLongitude());

            Log.e(TAG, "onApproveClick:chekkkkk " + Constant.cookie);

            obj.put(apiStringModel.params, child);
            Log.e(TAG, "onApproveClick: " + obj.toString());
            apiManager.sendPostRequestWithCookie(progressDialog, ApiEndPoints.customerApproveURL, obj, new APIResponse() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse: 123456 " + response);
                    try {
                        JSONObject result = response.getJSONObject(ApiStringModel.result);
                        if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("User Approved")) {
                            progressDialog.dismiss();
                            Intent i = new Intent(customerDetailActivity, ConfirmationActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            customerDetailActivity.startActivity(i);
                        } else {
                            progressDialog.dismiss();
                            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                        }
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onError: 123456 " + volleyError.getMessage());
                    ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));

                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {
                    progressDialog.dismiss();
                    ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
                }
            });
        } catch (Exception ex) {
            progressDialog.dismiss();
            Log.e(TAG, "onClick try catch 123456 " + ex.getMessage());
            ToastMsg.showToast(customerDetailActivity, customerDetailActivity.getString(R.string.sessionExpired));
        }
    }

    public void onLoginApi(LoginViewModel loginViewModel) {
        JSONObject obj = new JSONObject();
        JSONObject child = new JSONObject();
        try {
            child.put("login", loginViewModel.PhoneNumber.getValue());
            child.put("password", loginViewModel.Password.getValue());
            obj.put(apiStringModel.params, child);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ApiEndPoints.logInURL)
                .addJSONObjectBody(obj)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("X-Openerp-Session-Id", "1cb46ec18c926e0eb8a0bcafde04453767871b24")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: >>>>>" + response);
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError: >>>>" + error);
                    }
                });
    }

    public void UpdateOnClick(CustomerModel customerModel, Dialog progressDialog, CustomersProfileUpdateActivity customersProfileUpdateActivity) {
        Log.e(TAG, "UpdateOnClick: " + customerModel.getUpdateCustomerModel().getSubDistrictId());
        Log.e(TAG, "UpdateOnClick: " + customerModel.getUpdateCustomerModel().getLatitude() + "  " + customerModel.getUpdateCustomerModel().getLongitude());
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        try {
            JSONObject object = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(ApiStringModel.id, customerModel.getCustomerListModel().getId());
            data.put(ApiStringModel.stateId, customerModel.getUpdateCustomerModel().getStateId());
            data.put(ApiStringModel.districtId, customerModel.getUpdateCustomerModel().getDistrictId());
            data.put(ApiStringModel.subDistrictId, customerModel.getUpdateCustomerModel().getSubDistrictId());
            data.put(ApiStringModel.villageId, customerModel.getUpdateCustomerModel().getVillageId());
            data.put(ApiStringModel.zip, customerModel.getUpdateCustomerModel().getZip());
            data.put(ApiStringModel.street, customerModel.getUpdateCustomerModel().getStreetName());
            data.put(ApiStringModel.partnerLatitude, customerModel.getUpdateCustomerModel().getLatitude());
            data.put(ApiStringModel.partnerLongitude, customerModel.getUpdateCustomerModel().getLongitude());
            data.put(ApiStringModel.image1920, customerModel.getUpdateCustomerModel().getImgPath());
            data.put(ApiStringModel.group, customerModel.getUpdateCustomerModel().getGroupId());
            params.put(ApiStringModel.data, data);
            object.put(ApiStringModel.params, params);
            Log.e(TAG, "UpdateOnClick: ///////  " + object.toString());
            AndroidNetworking.put(ApiEndPoints.updateCustomer)
                    .addJSONObjectBody(object) // posting json
                    .addHeaders("X-Openerp-Session-Id", separated[1])
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: >>>>>>>>>>>>>>>" + response);
                            try {
                                JSONObject result = response.getJSONObject(ApiStringModel.result);
                                if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Success")) {
                                    Intent i = new Intent(customersProfileUpdateActivity, CustomerEditSaveActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    customersProfileUpdateActivity.startActivity(i);
                                } else {
                                    ToastMsg.showToast(customersProfileUpdateActivity, customersProfileUpdateActivity.getString(R.string.sessionExpired));
                                }
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                ToastMsg.showToast(customersProfileUpdateActivity, customersProfileUpdateActivity.getString(R.string.sessionExpired));
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.e(TAG, "error: >>>>>>>>>>>>>>>" + error);
                            progressDialog.dismiss();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }

    }

    public void UpdateImageOnClick(CustomerModel customerModel, Dialog progressDialog, CustomersProfileDetailActivity customersProfileUpdateActivity) {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        try {
            JSONObject object = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(ApiStringModel.id, customerModel.getCustomerListModel().getId());
            data.put(ApiStringModel.stateId, customerModel.getUpdateCustomerModel().getStateId());
            data.put(ApiStringModel.districtId, customerModel.getUpdateCustomerModel().getDistrictId());
            data.put(ApiStringModel.subDistrictId, customerModel.getUpdateCustomerModel().getSubDistrictId());
            data.put(ApiStringModel.villageId, customerModel.getUpdateCustomerModel().getVillageId());
            data.put(ApiStringModel.zip, customerModel.getUpdateCustomerModel().getZip());
            data.put(ApiStringModel.street, customerModel.getUpdateCustomerModel().getStreetName());
            data.put(ApiStringModel.partnerLatitude, customerModel.getUpdateCustomerModel().getLatitude());
            data.put(ApiStringModel.partnerLongitude, customerModel.getUpdateCustomerModel().getLongitude());
            data.put(ApiStringModel.image1920, customerModel.getUpdateCustomerModel().getImgPath());
            data.put(ApiStringModel.group, customerModel.getUpdateCustomerModel().getGroupId());

            params.put(ApiStringModel.data, data);
            object.put(ApiStringModel.params, params);
            Log.e(TAG, "UpdateOnClick: ///////  " + object.toString());
            AndroidNetworking.put(ApiEndPoints.updateCustomer)
                    .addJSONObjectBody(object)
                    .addHeaders("X-Openerp-Session-Id", separated[1])
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: >>>>>>>>>>>>>>>" + response);
                            try {
                                JSONObject result = response.getJSONObject(ApiStringModel.result);
                                if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Success")) {
                                    Log.i(TAG, "Image Update");
                                } else {
                                    ToastMsg.showToast(customersProfileUpdateActivity, customersProfileUpdateActivity.getString(R.string.errorMessage));
                                }
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                ToastMsg.showToast(customersProfileUpdateActivity, customersProfileUpdateActivity.getString(R.string.errorMessage));
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "error: >>>>>>>>>>>>>>>" + error);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
