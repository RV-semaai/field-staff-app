package com.semaai.agent.activity.targettracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
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
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.activity.login.StaffLoginActivity;
import com.semaai.agent.adapter.targettracking.MonthListAdaptor;
import com.semaai.agent.adapter.targettracking.TotalInvoicedAdapter;
import com.semaai.agent.adapter.targettracking.YearListAdapter;
import com.semaai.agent.databinding.ActivityTotalInvoicedBinding;
import com.semaai.agent.interfaces.OnClickTarget;
import com.semaai.agent.interfaces.OnItemClickListenerMonth;
import com.semaai.agent.interfaces.OnItemClickListenerYear;
import com.semaai.agent.model.targettracking.MonthListModel;
import com.semaai.agent.model.targettracking.TotalInvoicedModel;
import com.semaai.agent.model.targettracking.YearListModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.targettracking.TotalInvoicedViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TotalInvoicedActivity extends AppCompatActivity implements OnItemClickListenerYear, OnItemClickListenerMonth , OnClickTarget {

    public String TAG = TotalInvoicedActivity.class.getSimpleName()+"-->";
    private TotalInvoicedViewModel viewModel;
    private ActivityTotalInvoicedBinding binding;
    TotalInvoicedAdapter adapterCustomCard;

    final Handler handler = new Handler();

    MonthListAdaptor adaptor;
    YearListAdapter yearAdaptor;

    ArrayList<TotalInvoicedModel> listCustomCard = new ArrayList<>();

    ArrayList<MonthListModel> list = new ArrayList<>();
    ArrayList<YearListModel> yearList = new ArrayList<>();

    private Dialog progressDialog;
    Intent intent;
    String refreshId;

    int pp = 0 , refreshMonth = 0 , refreshYear = 0;
    String months = null, years = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TotalInvoicedViewModel.class);
        binding = DataBindingUtil.setContentView(TotalInvoicedActivity.this,R.layout.activity_total_invoiced);
        binding.setLifecycleOwner(this);
        binding.setTotalInvoicedViewModel(viewModel);

        intview();
        onClick();
        getApiData(0, 0 , intent.getStringExtra("sample"));

    }

    private void intview() {

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        intent = getIntent();
        refreshId = intent.getStringExtra("sample");
        Log.e(TAG , "get Intent :"+intent.getStringExtra("sample"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy; HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        binding.tvDateMonth.setText(currentDateTime);
    }

    private void onClick() {

        binding.cnstCircular1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });

        binding.cvCnst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });

        binding.ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApiData(refreshMonth, refreshYear , refreshId);
                gone();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy; HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());
                binding.tvDateMonth.setText(currentDateTime);
            }
        });

        binding.cvTotalInvoiced11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.rvTotalInvoicedCard.getVisibility() == View.GONE){
                    binding.rvTotalInvoicedCard.setVisibility(View.VISIBLE);
                    binding.ivTotalInvoicedArrowUp.setVisibility(View.VISIBLE);
                    binding.ivTotalInvoicedArrowDown.setVisibility(View.GONE);
                }else {
                    binding.rvTotalInvoicedCard.setVisibility(View.GONE);
                    binding.ivTotalInvoicedArrowUp.setVisibility(View.GONE);
                    binding.ivTotalInvoicedArrowDown.setVisibility(View.VISIBLE);
                }

            }
        });

        binding.rlRegister.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TotalInvoicedActivity.this,MyTargetActivity.class));
            }
        });

        binding.cvMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.linearMonthYearInvoiced.getVisibility() == View.GONE){
                    binding.linearMonthYearInvoiced.setVisibility(View.VISIBLE);
                    binding.ivMonthYearArrowUp.setVisibility(View.VISIBLE);
                    binding.ivMonthYearArrowDown.setVisibility(View.GONE);
                }else {
                    binding.linearMonthYearInvoiced.setVisibility(View.GONE);
                    binding.ivMonthYearArrowUp.setVisibility(View.GONE);
                    binding.ivMonthYearArrowDown.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.tvCustomDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                binding.cvCustom.setVisibility(View.VISIBLE);
                binding.tvCustomDate.setTextColor(getResources().getColor(R.color.black));
                yearList();
                monthList();
            }
        });

        binding.tvThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                getApiData(0,0 , refreshId);
                refreshMonth = 0;
                refreshYear = 0;
                binding.tvMonthYear.setText(getString(R.string.thisMonth));
                binding.tvThisMonth.setTextColor(getResources().getColor(R.color.black));
                gone();
            }
        });

        binding.tvLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultColor();
                getApiData(-1,0 , refreshId);
                refreshMonth = -1;
                refreshYear = 0;
                binding.tvMonthYear.setText(getString(R.string.lastMonth));
                binding.tvLastMonth.setTextColor(getResources().getColor(R.color.black));
                gone();
            }
        });

        binding.commomMonthYear.cvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mm = binding.commomMonthYear.tvMonthText.getText().toString();
                String yy = binding.commomMonthYear.tvYearText.getText().toString();

                binding.tvMonthYear.setText(mm+ " "+ yy);
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

                getApiData(aMM , aYY , refreshId);
                refreshMonth = aMM;
                refreshYear = aYY;
                gone();
            }
        });

        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TotalInvoicedActivity.this, DashboardActivity.class));
            }
        });

        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TotalInvoicedActivity.this, AccountDashboardActivity.class));
            }
        });

    }

    private void setDefaultColor(){
        binding.tvThisMonth.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvLastMonth.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvCustomDate.setTextColor(getResources().getColor(R.color.button_bg));
    }


    private void gone(){
        binding.cvCustom.setVisibility(View.GONE);
        binding.linearMonthYearInvoiced.setVisibility(View.GONE);
        binding.rvTotalInvoicedCard.setVisibility(View.GONE);
        binding.ivMonthYearArrowUp.setVisibility(View.GONE);
        binding.ivMonthYearArrowDown.setVisibility(View.VISIBLE);
        binding.ivTotalInvoicedArrowUp.setVisibility(View.GONE);
        binding.ivTotalInvoicedArrowDown.setVisibility(View.VISIBLE);
    }


    private void getApiData(int month , int year , String id) {

        listCustomCard.clear();
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

        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.challengesGoals)
                .addQueryParameter(ApiStringModel.type, ApiStringModel.me)
                .addQueryParameter(ApiStringModel.month, endDateStr)

                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.has("error")) {
                            progressdilogset(TotalInvoicedActivity.this);
                            progressDialog.dismiss();
                            return;
                        }
                        try {

                            JSONObject jsonObject = response.getJSONObject(ApiStringModel.data);

                            JSONArray challenges = jsonObject.getJSONArray(ApiStringModel.challenges);
                            JSONArray goals_desc = jsonObject.getJSONArray(ApiStringModel.goalsDesc);

                            Log.e(TAG , "get challenge :"+new Gson().toJson(challenges));
                            Log.e(TAG , "get goals_desc :"+new Gson().toJson(goals_desc));

                            String intentId = id;
                            String suffix = null;
                            for (int i=0; i<challenges.length(); i++){
                                JSONObject object = challenges.getJSONObject(i);

                                listCustomCard.add(new TotalInvoicedModel(object.getString(ApiStringModel.id),object.getString(ApiStringModel.name)));

                                if (intentId.equals(object.getString(ApiStringModel.id))){
                                    Log.i(TAG , "get sample id = "+object.getString(ApiStringModel.id));

                                    Log.i(TAG,"customer list data"+object.getString(ApiStringModel.name));
                                    int tg =  NumberFormat.getInstance().parse(object.getString(ApiStringModel.targetGoal)).intValue();
                                    suffix = object.getString(ApiStringModel.definitionFullSuffix);
                                    binding.tvTarget.setText(getString(R.string.target)+" "+suffix+" "+ Common.currencyFormatOnlyZero(String.valueOf(tg)));
                                    binding.rlRegister.tvHeader.setText(object.getString(ApiStringModel.name));
                                    binding.tvItemTarget.setText(object.getString(ApiStringModel.name));
                                }

                            }

                            for (int i=0; i<goals_desc.length(); i++){
                                JSONObject object = goals_desc.getJSONObject(i);
                                JSONArray jsonArray = object.getJSONArray(ApiStringModel.lineId);
                                String cId = String.valueOf(jsonArray.getInt(0));
                                if (intentId.equals(cId)){
                                    int complete =  NumberFormat.getInstance().parse(object.getString(ApiStringModel.completeness)).intValue();
                                    int current =  NumberFormat.getInstance().parse(object.getString(ApiStringModel.current)).intValue();
                                    binding.circularProgress.setProgressCompat(complete,true);
                                    binding.tvCurrent.setText(suffix+" "+Common.currencyFormatOnlyZero(String.valueOf(current)));
                                    binding.tvProgressDone.setText(complete+"%");
                                    int notFill = 100-complete;
                                    binding.tvProgressNotDone.setText(notFill+"%");
                                }
                            }

                            if (challenges.length()== 0 || goals_desc.length() == 0){
                                ToastMsg.showToast(TotalInvoicedActivity.this,getString(R.string.errorMessage));

                                binding.tvTarget.setText(getString(R.string.target)+" -- ");
                                binding.circularProgress.setProgressCompat(0,true);
                                binding.tvCurrent.setText("--");
                                binding.tvProgressDone.setText("");
                                binding.tvProgressNotDone.setText("");
                                binding.tvItemTarget.setText("None");
                            }

                            adapterCustomCard = new TotalInvoicedAdapter(listCustomCard,TotalInvoicedActivity.this, TotalInvoicedActivity.this);
                            binding.rvTotalInvoicedCard.setLayoutManager(new LinearLayoutManager(TotalInvoicedActivity.this));
                            binding.rvTotalInvoicedCard.setHasFixedSize(true);
                            binding.rvTotalInvoicedCard.setAdapter(adapterCustomCard);

                            progressDialog.dismiss();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"catch error ==>> "+e.getMessage());
                            ToastMsg.showToast(TotalInvoicedActivity.this,getString(R.string.errorMessage));
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: >>>>>>>" + error.getMessage().toString());
                        ToastMsg.showToast(TotalInvoicedActivity.this,getString(R.string.errorMessage));
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

        adaptor = new MonthListAdaptor(list,this,TotalInvoicedActivity.this, months);
        binding.commomMonthYear.rvMonthList.setLayoutManager(new LinearLayoutManager(TotalInvoicedActivity.this));
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

        yearAdaptor = new YearListAdapter(yearList,TotalInvoicedActivity.this ,TotalInvoicedActivity.this, years);
        binding.commomMonthYear.rvYearList.setHasFixedSize(true);
        binding.commomMonthYear.rvYearList.setLayoutManager(new LinearLayoutManager(TotalInvoicedActivity.this));
        binding.commomMonthYear.rvYearList.setAdapter(yearAdaptor);
    }

    @Override
    public void onItemTargetClick(String id) {
        getApiData(refreshMonth,refreshYear, id);
        refreshId = id;
        gone();
    }
}