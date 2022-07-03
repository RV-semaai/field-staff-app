package com.semaai.agent.adapter.targettracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.databinding.ItemYearListBinding;
import com.semaai.agent.interfaces.OnItemClickListenerYear;
import com.semaai.agent.model.targettracking.YearListModel;

import java.util.ArrayList;

public class YearListAdapter extends RecyclerView.Adapter<YearListAdapter.ViewHolder> {

    ArrayList<YearListModel> list;
    Context context;
    OnItemClickListenerYear itemClickListenerMonthYear;
    String year;

    public YearListAdapter(ArrayList<YearListModel> list, Context context, OnItemClickListenerYear itemClickListenerMonthYear, String year) {
        this.list = list;
        this.context = context;
        this.itemClickListenerMonthYear = itemClickListenerMonthYear;
        this.year = year;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemYearListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        YearListModel model = list.get(position);
        holder.binding.tvYearListText.setText(model.getYearName());
        if (year != null && year.contentEquals(model.getYearName())) {
            holder.binding.tvYearListText.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListenerMonthYear.onItemYearClick(list.get(position).getYearName());
                holder.binding.tvYearListText.setTextColor(context.getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemYearListBinding binding;

        public ViewHolder(ItemYearListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
