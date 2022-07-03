package com.semaai.agent.adapter.targettracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.activity.targettracking.MyTargetActivity;
import com.semaai.agent.activity.targettracking.TotalInvoicedActivity;
import com.semaai.agent.databinding.ItemTargetProgressBinding;
import com.semaai.agent.model.targettracking.MyTargerGoalsDescListModel;
import com.semaai.agent.model.targettracking.MyTargetChallengeListModel;
import com.semaai.agent.utils.Common;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyTargetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = MyTargetListAdapter.class.getSimpleName() + "-->";
    LayoutInflater inflater;
    Context context;
    ArrayList<MyTargetChallengeListModel> challengeList;
    ArrayList<MyTargerGoalsDescListModel> goalsDescList;


    public MyTargetListAdapter(Context context, ArrayList<MyTargetChallengeListModel> challengeList,
                               ArrayList<MyTargerGoalsDescListModel> goalsDescList) {

        this.inflater = LayoutInflater.from(context);
        this.challengeList = challengeList;
        this.goalsDescList = goalsDescList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTargetProgressBinding itemTargetProgressBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_target_progress, parent, false);

        return new ViewHolder(itemTargetProgressBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.itemTargetProgressBinding.tvTargetName.setText(challengeList.get(position).getName());
        try {
            int tg = NumberFormat.getInstance().parse(challengeList.get(position).getTargetGoal()).intValue();
            if (challengeList.get(position).getDefinitionFullSuffix().contentEquals("Rp")) {
                viewHolder.itemTargetProgressBinding.tvTargetValue.setText(" " + challengeList.get(position).getDefinitionFullSuffix() + " " +
                        Common.currencyFormatOnlyZero(String.valueOf(tg)));
            } else {
                viewHolder.itemTargetProgressBinding.tvTargetValue.setText(" " + tg);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String c_id = challengeList.get(position).getId();
        for (int j = 0; j < goalsDescList.size(); j++) {
            String input = goalsDescList.get(j).getLineId();

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(input);
                if (c_id.contentEquals(jsonArray.getString(0))) {

                    int tg = NumberFormat.getInstance().parse(goalsDescList.get(j).getTargetGoal()).intValue();
                    if (challengeList.get(position).getDefinitionFullSuffix().contentEquals("Rp")) {
                        viewHolder.itemTargetProgressBinding.tvTargetValue.setText(" " + challengeList.get(position).getDefinitionFullSuffix() + " " +
                                Common.currencyFormatOnlyZero(String.valueOf(tg)));
                    } else {
                        viewHolder.itemTargetProgressBinding.tvTargetValue.setText(" " + tg);
                    }

                    int p = NumberFormat.getInstance().parse(goalsDescList.get(j).getCompleteness()).intValue();
                    int c = NumberFormat.getInstance().parse(goalsDescList.get(j).getCurrent()).intValue();
                    viewHolder.itemTargetProgressBinding.lpProgress.setProgressCompat(p, true);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    double pp = Double.parseDouble(decimalFormat.format(Double.parseDouble(goalsDescList.get(j).getCompleteness())));
                    String date = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
                    int cDate = Integer.parseInt(date);


                    if (cDate > 15) {
                        if (p < 50) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro1to25));
                        }
                        if (p >= 50 && p < 100) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro25to90));
                        }
                        if (p >= 100) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro90more));
                        }
                    } else {
                        if (p > 0 && p <= 25) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro1to25));
                        }
                        if (p > 25 && p <= 50) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro25to90));
                        }
                        if (p > 50) {
                            viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro90more));
                        }
                    }

                    if (pp > 0 && pp <= 1) {
                        viewHolder.itemTargetProgressBinding.lpProgress.setIndicatorColor(ContextCompat.getColor(context, R.color.pro1to25));
                        viewHolder.itemTargetProgressBinding.lpProgress.setProgressCompat(1, true);
                    }

                    if (challengeList.get(position).getDefinitionFullSuffix().contentEquals("Rp")) {
                        viewHolder.itemTargetProgressBinding.tvProgressValue.setText(" " + challengeList.get(position).getDefinitionFullSuffix() + " " + Common.currencyFormatOnlyZero(String.valueOf(c)) + "");
                    } else {
                        viewHolder.itemTargetProgressBinding.tvProgressValue.setText(c + "");
                    }
                    viewHolder.itemTargetProgressBinding.tvProgressPercent.setText(pp + "%");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context.getClass().getSimpleName().equals(MyTargetActivity.class.getSimpleName())) {
                    Intent intent = new Intent(context, TotalInvoicedActivity.class);
                    intent.putExtra("sample", challengeList.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTargetProgressBinding itemTargetProgressBinding;

        public ViewHolder(ItemTargetProgressBinding itemTargetProgressBinding) {
            super(itemTargetProgressBinding.getRoot());
            this.itemTargetProgressBinding = itemTargetProgressBinding;
        }
    }

}
