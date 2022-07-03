package com.semaai.agent.activity.targettracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.login.AccountActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.adapter.targettracking.MonthListAdaptor;
import com.semaai.agent.adapter.targettracking.MyTargetListAdapter;
import com.semaai.agent.adapter.targettracking.YearListAdapter;
import com.semaai.agent.databinding.ActivityTeamTargetBinding;
import com.semaai.agent.interfaces.OnItemClickListenerMonth;
import com.semaai.agent.interfaces.OnItemClickListenerYear;
import com.semaai.agent.model.targettracking.MonthListModel;
import com.semaai.agent.model.targettracking.MyTargerGoalsDescListModel;
import com.semaai.agent.model.targettracking.MyTargetChallengeListModel;
import com.semaai.agent.model.targettracking.YearListModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.targettracking.TeamTargetViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TeamTargetActivity extends AppCompatActivity implements OnItemClickListenerYear, OnItemClickListenerMonth {

    private ActivityTeamTargetBinding binding;
    private TeamTargetViewModel viewModel;
    private static final String TAG = MyTargetActivity.class.getSimpleName()+"-->";
    private MyTargetListAdapter myTargetListAdapter;
    private Dialog progressDialog;
    GridLayoutManager manager;
    MonthListAdaptor adaptor;
    YearListAdapter yearAdaptor;
    final Handler handler = new Handler();
    ArrayList<MonthListModel> list = new ArrayList<>();
    ArrayList<YearListModel> yearList = new ArrayList<>();

    ArrayList<MyTargetChallengeListModel> challengeListModels = new ArrayList<>();
    ArrayList<MyTargerGoalsDescListModel> goalsDescListModels = new ArrayList<>();
    int pp = 0 , refreshMonth = 0 , refreshYear = 0;
    String months = null, years = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TeamTargetViewModel.class);
        binding = DataBindingUtil.setContentView(TeamTargetActivity.this,R.layout.activity_team_target);
        binding.setLifecycleOwner(this);
        binding.setTeamTargetViewModel(viewModel);

        intview();
        onClick();
        getApiData(0 , 0);
    }

    private void intview() {

        manager = new GridLayoutManager(getApplicationContext(), 1);
        binding.rvProgressList.setLayoutManager(manager);

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date monthFirstDay = calendar.getTime();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date monthLastDay = calendar.getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String startDateStr = df.format(monthFirstDay);
        String endDateStr = df.format(monthLastDay);
        binding.tvTopMyTargetDate.setText(startDateStr +" - "+ endDateStr);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy; HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        binding.tvFullDate.setText(currentDateTime);
    }

    private void onClick() {

        binding.rlRegister.tvTopBar.setText(Constant.name);
        binding.cvCnst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });
        binding.clProgressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });
        binding.cnst5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.cvCustomMonthYear.getVisibility() ==View.VISIBLE){
                    binding.cvCustomMonthYear.setVisibility(View.INVISIBLE);

                }else
                    binding.cvCustomMonthYear.setVisibility(View.VISIBLE);
            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamTargetActivity.this, TargetTrackingDashboardActivity.class));
            }
        });
        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamTargetActivity.this, AccountDashboardActivity.class));
            }
        });
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamTargetActivity.this,DashboardActivity.class));
            }
        });

        binding.tvThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                getApiData(0,0);
                refreshMonth = 0;
                refreshYear = 0;
                binding.tvMyTargetDate.setText(getString(R.string.thisMonth));
                binding.tvThisMonth.setTextColor(getResources().getColor(R.color.black));
                gone();
            }
        });

        binding.tvLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                getApiData(-1,0);
                refreshMonth = -1;
                refreshYear = 0;
                binding.tvMyTargetDate.setText(R.string.lastMonth);
                binding.tvLastMonth.setTextColor(getResources().getColor(R.color.black));
                gone();
            }
        });

        binding.cnst7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cvCustom.getVisibility()== View.INVISIBLE){
                    setDefaultColor();
                    binding.cvCustom.setVisibility(View.VISIBLE);
                    binding.cvCustomMonthYear.setVisibility(View.GONE);
                    binding.tvCustomDate.setTextColor(getResources().getColor(R.color.black));
                    monthList();
                    yearList();
                }
            }
        });

        binding.ivTeamRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                getApiData(refreshMonth, refreshYear);
                gone();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy; HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());
                binding.tvFullDate.setText(currentDateTime);
            }
        });

        binding.commomMonthYear.cvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mm = binding.commomMonthYear.tvMonthText.getText().toString();
                String yy = binding.commomMonthYear.tvYearText.getText().toString();
                Log.i(TAG, "get mm-yy :"+mm+"-"+yy);

                DateFormat dateFormat = new SimpleDateFormat("MM");
                Date date = new Date();

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);

                Log.e(TAG,"Month "+dateFormat.format(date));
                Log.e(TAG,"Year "+year);

                int y = Integer.parseInt(yy);
                int m = Integer.parseInt(dateFormat.format(date).toString());

                int aYY = y - year;
                int aMM = (pp+1)-m;

                getApiData(aMM , aYY);
                refreshMonth = aMM;
                refreshYear = aYY;

                binding.tvMyTargetDate.setText(mm+ " "+ yy);
                gone();
            }
        });

    }

    private void setDefaultColor(){
        binding.tvThisMonth.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvLastMonth.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvCustomDate.setTextColor(getResources().getColor(R.color.button_bg));
    }

    private void getApiData(int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.YEAR , year);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date monthFirstDay = calendar.getTime();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date monthLastDay = calendar.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = df.format(monthFirstDay);
        String endDateStr = df.format(monthLastDay);

        Log.e(TAG,"start date :"+startDateStr+" , end date :"+endDateStr);

        SimpleDateFormat dff = new SimpleDateFormat("dd MMM yyyy");
        String startDate = dff.format(monthFirstDay);
        String endDate = dff.format(monthLastDay);
        binding.tvTopMyTargetDate.setText(startDate +" - "+ endDate);

        progressDialog.show();
        challengeListModels.clear();
        goalsDescListModels.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.challengesGoals)
                .addQueryParameter(ApiStringModel.type, ApiStringModel.team)
                .addQueryParameter(ApiStringModel.month, endDateStr)

                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.has("error")) {
                            progressdilogset(TeamTargetActivity.this);
                            progressDialog.dismiss();
                            return;
                        }
                        try {

                            JSONObject jsonObject = response.getJSONObject(ApiStringModel.data);

                            JSONArray challenges = jsonObject.getJSONArray(ApiStringModel.challenges);
                            JSONArray goals_desc = jsonObject.getJSONArray(ApiStringModel.goalsDesc);

                            Log.e(TAG , "get challenge :"+new Gson().toJson(challenges));
                            Log.e(TAG , "get goals_desc :"+new Gson().toJson(goals_desc));

                            for (int i=0; i<challenges.length(); i++){
                                JSONObject object = challenges.getJSONObject(i);

                                challengeListModels.add(new MyTargetChallengeListModel(
                                        object.getString(ApiStringModel.id),
                                        object.getString(ApiStringModel.name),
                                        object.getString(ApiStringModel.targetGoal),
                                        object.getString(ApiStringModel.definitionFullSuffix),
                                        object.getString(ApiStringModel.challengeId)));

                            }

                            for (int i=0; i<goals_desc.length(); i++){
                                JSONObject object = goals_desc.getJSONObject(i);

                                goalsDescListModels.add(new MyTargerGoalsDescListModel(
                                        object.getString(ApiStringModel.id),
                                        object.getString(ApiStringModel.completeness),
                                        object.getString(ApiStringModel.state),
                                        object.getString(ApiStringModel.definition_description),
                                        object.getString(ApiStringModel.lineId),
                                        object.getString(ApiStringModel.current),
                                        object.getString(ApiStringModel.targetGoal)));

                            }

                            if (challenges.length() == 0 || goals_desc.length() == 0){
                                ToastMsg.showToast(TeamTargetActivity.this,getString(R.string.errorMessage));
                            }
                            Log.i(TAG ,"challengeListModels data :"+ new Gson().toJson(challengeListModels));
                            Log.i(TAG ,"goalsDescListModels data :"+new Gson().toJson(goalsDescListModels));

                            myTargetListAdapter = new MyTargetListAdapter( TeamTargetActivity.this , challengeListModels , goalsDescListModels );
                            binding.rvProgressList.setAdapter(myTargetListAdapter);


                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastMsg.showToast(TeamTargetActivity.this,getString(R.string.errorMessage));
                            progressDialog.dismiss();
                            Log.e(TAG,"catch error ==>> "+e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>" + error.getMessage().toString());
                        ToastMsg.showToast(TeamTargetActivity.this,getString(R.string.errorMessage));
                        progressDialog.dismiss();
                        // handle error
                    }
                });
    }

    private void progressdilogset(Activity activity) {
        Dialog progressDialog = new Dialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_session_expired_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        CardView cvReLogin = progressDialog.findViewById(R.id.cv_reLogin);
        cvReLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, StaffLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                progressDialog.dismiss();
            }
        });
    }

    private void gone(){
        binding.cvCustomMonthYear.setVisibility(View.INVISIBLE);
        binding.cvCustom.setVisibility(View.INVISIBLE);
    }

    private void monthList() {
        list.clear();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (year>2022) {
            list.add(new MonthListModel(getString(R.string.january)));
            list.add(new MonthListModel(getString(R.string.february)));
            list.add(new MonthListModel(getString(R.string.march)));
        }
        list.add(new MonthListModel(getString(R.string.april)));
        list.add(new MonthListModel(getString(R.string.may)));
        list.add(new MonthListModel(getString(R.string.june)));
        list.add(new MonthListModel(getString(R.string.july)));
        list.add(new MonthListModel(getString(R.string.august)));
        list.add(new MonthListModel(getString(R.string.september)));
        list.add(new MonthListModel(getString(R.string.october)));
        list.add(new MonthListModel(getString(R.string.november)));
        list.add(new MonthListModel(getString(R.string.december)));


        adaptor = new MonthListAdaptor(list,TeamTargetActivity.this,TeamTargetActivity.this, months);
        binding.commomMonthYear.rvMonthList.setLayoutManager(new LinearLayoutManager(TeamTargetActivity.this));
        binding.commomMonthYear.rvMonthList.setHasFixedSize(true);
        binding.commomMonthYear.rvMonthList.setAdapter(adaptor);

    }

    private void yearList() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int minYear = 2022;
        int c = year - minYear;
        yearList.clear();
        int y = 2021;
        for (int i = 0; i<=c; i++){
            y = y +1;
            yearList.add(new YearListModel(String.valueOf(y)));
        }

        yearAdaptor = new YearListAdapter(yearList,TeamTargetActivity.this,TeamTargetActivity.this, years);
        binding.commomMonthYear.rvYearList.setHasFixedSize(true);
        binding.commomMonthYear.rvYearList.setLayoutManager(new LinearLayoutManager(TeamTargetActivity.this));
        binding.commomMonthYear.rvYearList.setAdapter(yearAdaptor);

    }

    @Override
    public void onItemMonthClick(String month , int p) {
        binding.commomMonthYear.tvMonthText.setText(month);
        months = month;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (year>2022) {
            pp = p;
        }else {
            pp = p+3;
        }
        monthList();
    }

    @Override
    public void onItemYearClick(String year) {
        binding.commomMonthYear.tvYearText.setText(year);
        years = year;
        yearList();
    }
}