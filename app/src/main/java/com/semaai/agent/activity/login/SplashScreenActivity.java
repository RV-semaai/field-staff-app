package com.semaai.agent.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.network.APIResponse;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.network.RequestAPI;
import com.semaai.agent.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    private String number, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        Constant.setLanguage = sharedPreferences.getString("lang", "in");
        setLocale(this, Constant.setLanguage);


        setContentView(R.layout.activity_splashscreen);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        number = prefs.getString("number", "");
        password = prefs.getString("password", "");
        Constant.loginPassword = password;
        Log.e("TAG", "onCreate: >>> password " + Constant.loginPassword);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!number.equals("") && !password.equals("")) {
                    Log.e("TAG", "run: >>>" + number + " " + password);
                    RequestAPI apiManager = new RequestAPI(SplashScreenActivity.this);
                    apiCall(apiManager);
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
                    finish();
                }

            }
        }, 2000);
    }

    private void apiCall(RequestAPI apiManager) {
        try {
            JSONObject obj = new JSONObject();
            JSONObject child = new JSONObject();
            child.put("login", number);
            child.put("password", password);
            obj.put(ApiStringModel.params, child);
            apiManager.sendPostRequest(ApiEndPoints.logInURL, obj, new APIResponse() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject result = null;
                    try {
                        result = response.getJSONObject(ApiStringModel.result);

                        if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("SUCCESS")) {
                            Log.e("TAG", "onCreate: >>> password 1 " + Constant.loginPassword);
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
                            Log.e("TAG", "onResponse:>>>>>>> " + Constant.profileImage);
                            startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
                            finish();
                        } else if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Not a existence User")) {
                            startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
                    finish();
                }

                @Override
                public void onNetworkResponse(NetworkResponse response) {
                    try {
                        Log.i("TAG", "response " + response.headers.toString());
                        Map<String, String> responseHeaders = response.headers;
                        String rawCookies = responseHeaders.get("Set-Cookie").split(";")[0];
                        Log.i("TAG", "cookies " + rawCookies);
                        Constant.cookie = rawCookies;
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception ex) {
            Log.i("TAG", "onClick try catch");
            startActivity(new Intent(SplashScreenActivity.this, StaffLoginActivity.class));
            finish();
        }
    }

    public static void setLocale(Activity activity, String languageCode) {
        Constant.setLanguage = languageCode;
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}