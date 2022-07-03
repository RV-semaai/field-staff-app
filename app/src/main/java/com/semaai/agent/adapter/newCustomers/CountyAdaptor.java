package com.semaai.agent.adapter.newCustomers;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.databinding.ItemRvDropDownListBinding;
import com.semaai.agent.databinding.ItemRvFilterDropDownListBinding;
import com.semaai.agent.databinding.ItemRvFilterUpdateDropDownListBinding;
import com.semaai.agent.interfaces.OnItemClickListener;
import com.semaai.agent.R;
import com.semaai.agent.model.RVItemModel;

import java.util.List;

public class CountyAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<RVItemModel> rvItemModelList;
    OnItemClickListener onItemClickListener;
    String TAG = CountyAdaptor.class.getSimpleName() + "-->";
    int check;

    public CountyAdaptor(List<RVItemModel> rvItemModelList, OnItemClickListener onItemClickListener, int check) {
        this.rvItemModelList = rvItemModelList;
        this.onItemClickListener = onItemClickListener;
        this.check = check;
    }

    @Override
    public int getItemViewType(int position) {
        if (check == 0) {
            //address
            return 0;
        } else if (check == 1) {
            //filter
            return 1;
        } else {
            //update
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            ItemRvDropDownListBinding rvDropDownListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_rv_drop_down_list, parent, false);
            return new CountryViewHolder(rvDropDownListBinding);
        } else if (viewType == 1){
            ItemRvFilterDropDownListBinding rvFilterDropDownListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_rv_filter_drop_down_list, parent, false);
            return new FilterHolder(rvFilterDropDownListBinding);
        } else
        {
            ItemRvFilterUpdateDropDownListBinding rvFilterUpdateDropDownListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_rv_filter_update_drop_down_list, parent, false);
            return new FilterHolderUpdate(rvFilterUpdateDropDownListBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if (getItemViewType(position) == 0) {
            CountryViewHolder viewHolder = (CountryViewHolder) holder;
            Log.i(TAG, "onBindViewHolder" + rvItemModelList.get(position));
            viewHolder.rvDropdownListBinding.setRVItemModel(rvItemModelList.get(position));
            viewHolder.rvDropdownListBinding.tvListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "data" + rvItemModelList.get(position));
                    onItemClickListener.onItemClick("country", rvItemModelList.get(position), 0);
                }
            });
        } else  if (getItemViewType(position) == 1){
            FilterHolder viewHolder = (FilterHolder) holder;
            Log.i(TAG, "onBindViewHolder" + rvItemModelList.get(position));
            viewHolder.rvFilterDropDownListBinding.setRVItemModel(rvItemModelList.get(position));
            viewHolder.rvFilterDropDownListBinding.tvListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i(TAG, "data" + rvItemModelList.get(position));
                    onItemClickListener.onItemClick("country", rvItemModelList.get(position), 1);
//                    viewHolder.rvfilterdropdoenlistBinding.tvListtext.setTextColor(Color.BLACK);

                }
            });
        }
        else
        {
            FilterHolderUpdate viewHolder = (FilterHolderUpdate) holder;
            Log.i(TAG, "onBindViewHolder" + rvItemModelList.get(position));
            viewHolder.rvFilterUpdateDropDownListBinding.setRVItemModel(rvItemModelList.get(position));
            viewHolder.rvFilterUpdateDropDownListBinding.tvListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "data" + rvItemModelList.get(position));
                    onItemClickListener.onItemClick("country", rvItemModelList.get(position), 0);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        Log.i(TAG, " count: " + rvItemModelList.size());
        return rvItemModelList.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder {
        ItemRvDropDownListBinding rvDropdownListBinding;

        public CountryViewHolder(ItemRvDropDownListBinding rvDropdownListBinding) {
            super(rvDropdownListBinding.getRoot());
            this.rvDropdownListBinding = rvDropdownListBinding;
        }
    }

    public class FilterHolder extends RecyclerView.ViewHolder {

        ItemRvFilterDropDownListBinding rvFilterDropDownListBinding;

        public FilterHolder(ItemRvFilterDropDownListBinding rvFilterDropDownListBinding) {
            super(rvFilterDropDownListBinding.getRoot());
            this.rvFilterDropDownListBinding = rvFilterDropDownListBinding;
        }
    }
    public class FilterHolderUpdate extends RecyclerView.ViewHolder {

        ItemRvFilterUpdateDropDownListBinding rvFilterUpdateDropDownListBinding;

        public FilterHolderUpdate(ItemRvFilterUpdateDropDownListBinding rvFilterUpdateDropDownListBinding) {
            super(rvFilterUpdateDropDownListBinding.getRoot());
            this.rvFilterUpdateDropDownListBinding = rvFilterUpdateDropDownListBinding;
        }
    }
}

