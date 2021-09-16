package com.example.ddlearning.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.ddlearning.R

class TaskDetailRVAdapter: BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(R.layout.item_task_detail) {

    override fun convert(holder: BaseViewHolder, item: Pair<String, String>) {
        holder.setText(R.id.key,item.first)
        holder.setText(R.id.value,item.second)
    }
}