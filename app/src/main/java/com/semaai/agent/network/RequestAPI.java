package com.semaai.agent.network;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.cardview.widget.CardView;

import com.semaai.agent.BuildConfig;
import com.semaai.agent.R;
import com.semaai.agent.utils.Constant;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class RequestAPI {

    String TAG = RequestAPI.class.getSimpleName() + "-->";
    private Context context;

    public RequestAPI(Context context) {

        this.context = context;
    }

    public void sendPostRequest(String URL, JSONObject input, APIResponse apiResponse) {
        Log.e("checkApi", "URL " + URL);
        Log.e("checkApi", "input " + input.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, input, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("checkApi", "sendPost " + response);
                apiResponse.onResponse(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("checkApi", "sendPost " + error.getMessage());
                        apiResponse.onError(error);
                    }
                }

        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                apiResponse.onNetworkResponse(response);
                return super.parseNetworkResponse(response);
            }
        };
        VolleySingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }

    public void sendPostRequestWithCookie(Dialog progressDialog, String URL, JSONObject input, APIResponse apiResponse) {
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.post(URL)
                .addJSONObjectBody(input)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("error")) {
                            progressDialogSet(context , progressDialog);
                            return;
                        }
                        apiResponse.onResponse(response);

                    }

                    @Override
                    public void onError(ANError error) {
                        //apiResponse.onError(error);
                    }
                });
    }

    public void sendPostRequestWithCookieWithAuthorization_Key(Dialog progressDialog, String URL, String input, APIResponse apiResponse) {
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.post(URL)
                .addStringBody(input)
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .addHeaders("Content-Type", "text/plain")
                .addHeaders("Authorization", BuildConfig.Authorization)
                .setTag("Test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("error")) {
                            progressDialogSet(context , progressDialog);
                            return;
                        }
                        apiResponse.onResponse(response);

                    }

                    @Override
                    public void onError(ANError error) {
                        //apiResponse.onError(error);
                    }
                });
    }

    private void progressDialogSet(Context context , Dialog progress) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_session_expired_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        CardView cvReLogin = progressDialog.findViewById(R.id.cv_reLogin);
        cvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(context, StaffLoginActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                context.startActivity(i);
                progress.dismiss();
                progressDialog.dismiss();

            }
        });
    }

    public void sendGetRequest(String URL, APIResponse apiResponse) {
        Log.e("checkApi", "URL " + URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("checkApi", "sendPost onResponse " + response);
                apiResponse.onResponse(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("checkApi", "sendPost onErrorResponse" + error.getMessage());
                        apiResponse.onError(error);
                    }
                }
        );
        VolleySingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }
}
