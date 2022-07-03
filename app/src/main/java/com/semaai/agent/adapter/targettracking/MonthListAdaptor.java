package com.semaai.agent.adapter.targettracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.databinding.ItemMonthListBinding;
import com.semaai.agent.interfaces.OnItemClickListenerMonth;
import com.semaai.agent.model.targettracking.MonthListModel;

import java.util.ArrayList;

public class MonthListAdaptor extends RecyclerView.Adapter<MonthListAdaptor.ViewHolder> {

    ArrayList<MonthListModel> list;
    Context context;
    OnItemClickListenerMonth itemClickListenerMonth;
    String month;

    public MonthListAdaptor(ArrayList<MonthListModel> list, Context context, OnItemClickListenerMonth itemClickListenerMonth, String month) {
        this.list = list;
        this.context = context;
        this.itemClickListenerMonth = itemClickListenerMonth;
        this.month = month;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMonthListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        MonthListModel model = list.get(position);
        holder.binding.tvMonthListText.setText(model.getMonthName());
        if (month != null && month.contentEquals(model.getMonthName())) {
            holder.binding.tvMonthListText.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                itemClickListenerMonth.onItemMonthClick(list.get(position).getMonthName(), position);
                holder.binding.tvMonthListText.setTextColor(context.getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMonthListBinding binding;

        public ViewHolder(ItemMonthListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
