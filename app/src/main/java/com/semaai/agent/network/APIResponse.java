package com.semaai.agent.network;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface APIResponse {

    void onResponse(JSONObject response);
    void onError(VolleyError volleyError);
    void onNetworkResponse(NetworkResponse response);

}
