package com.semaai.agent.adapter.targettracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.databinding.ItemTotalInvoicedCustomCardBinding;
import com.semaai.agent.interfaces.OnClickTarget;
import com.semaai.agent.model.targettracking.TotalInvoicedModel;

import java.util.ArrayList;

public class TotalInvoicedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<TotalInvoicedModel> list;
    Context context;
    OnClickTarget onClickTarget;

    public TotalInvoicedAdapter(ArrayList<TotalInvoicedModel> list, Context context, OnClickTarget onClickTarget) {
        this.list = list;
        this.context = context;
        this.onClickTarget = onClickTarget;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTotalInvoicedCustomCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_total_invoiced_custom_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.binding.tvTotalInvoiceCustomCard.setText(list.get(position).getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTarget.onItemTargetClick(list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTotalInvoicedCustomCardBinding binding;

        public ViewHolder(ItemTotalInvoicedCustomCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
