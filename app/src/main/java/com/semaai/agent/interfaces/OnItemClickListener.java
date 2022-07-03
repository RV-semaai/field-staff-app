package com.semaai.agent.interfaces;


import com.semaai.agent.model.RVItemModel;

public interface OnItemClickListener {
    void onItemClick(String rvName, RVItemModel item , int isFilter);
}
