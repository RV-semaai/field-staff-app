package com.semaai.agent.adapter.newCustomers;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;

import com.semaai.agent.databinding.ItemRvDropDownListBinding;
import com.semaai.agent.databinding.ItemRvUpdateDropDownListBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.model.RVItemModel;

import java.util.List;

public class GroupAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<RVItemModel> rvItemModelList;
    OnItemClickListener onItemClickListener;
    String TAG = GroupAdaptor.class.getSimpleName() + "-->";
    int check;

    public GroupAdaptor(List<RVItemModel> rvItemModelList, OnItemClickListener onItemClickListener, int check) {
        this.rvItemModelList = rvItemModelList;
        this.onItemClickListener = onItemClickListener;
        this.check = check;
    }

    @Override
    public int getItemViewType(int position) {
        if (check == 1) {
            //address
            return 1;
        } else {
            //update
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (check==1)
        {
            ItemRvDropDownListBinding rvDropdownListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_rv_drop_down_list, parent, false);
            return new GroupViewHolder(rvDropdownListBinding);
        }
        else
        {
            ItemRvUpdateDropDownListBinding rvUpdateDropDownListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_rv_update_drop_down_list, parent, false);
            return new GroupViewHolderUpdate(rvUpdateDropDownListBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (check==1)
        {
            GroupViewHolder viewHolder = (GroupViewHolder) holder;
            Log.i(TAG, "onBindViewHolder" + rvItemModelList.get(position).item);
            viewHolder.rvDropdownListBinding.setRVItemModel(rvItemModelList.get(position));
            viewHolder.rvDropdownListBinding.tvListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "data" + rvItemModelList.get(position).item);
                    onItemClickListener.onItemClick("group", rvItemModelList.get(position), 1);
                }
            });
        }
        else
        {
            GroupViewHolderUpdate viewHolder = (GroupViewHolderUpdate) holder;
            Log.i(TAG, "onBindViewHolder" + rvItemModelList.get(position).item);
            viewHolder.rvUpdateDropDownListBinding.setRVItemModel(rvItemModelList.get(position));
            viewHolder.rvUpdateDropDownListBinding.tvListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "data" + rvItemModelList.get(position).item);
                    onItemClickListener.onItemClick("group", rvItemModelList.get(position), 1);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        Log.i(TAG, " count: " + rvItemModelList.size());
        return rvItemModelList.size();
    }

    private class GroupViewHolder extends RecyclerView.ViewHolder {
        ItemRvDropDownListBinding rvDropdownListBinding;

        public GroupViewHolder(ItemRvDropDownListBinding rvDropdownListBinding) {
            super(rvDropdownListBinding.getRoot());
            this.rvDropdownListBinding = rvDropdownListBinding;
        }
    }

    private class GroupViewHolderUpdate extends RecyclerView.ViewHolder {
        ItemRvUpdateDropDownListBinding rvUpdateDropDownListBinding;

        public GroupViewHolderUpdate(ItemRvUpdateDropDownListBinding rvUpdateDropDownListBinding) {
            super(rvUpdateDropDownListBinding.getRoot());
            this.rvUpdateDropDownListBinding = rvUpdateDropDownListBinding;
        }
    }
}
