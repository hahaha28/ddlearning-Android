package com.example.ddlearning.adapter

import android.util.Log
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.ddlearning.R
import com.example.ddlearning.bean.Task

class TaskListRVAdapter:BaseQuickAdapter<Task,BaseViewHolder>(R.layout.item_task) {

    var switchCheckChangeListener: (Int,Boolean)->Unit = { i: Int, b: Boolean -> }

    private val roomListData:MutableList<Pair<String,Int>> = mutableListOf()

    private val repeatModeList = listOf<String>("一次","每天","周一至周五","周末")

    init {
        roomListData.add(Pair("二楼北自习室（202）",35))
        roomListData.add(Pair("二楼南自习室（201）",36))
        roomListData.add(Pair("三楼南自习室（301）",31))
        roomListData.add(Pair("三楼北自习室（302）",37))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val switchView = holder.getView<SwitchCompat>(R.id.taskSwitch)
        switchView.setOnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isPressed) {
                switchCheckChangeListener(position, b)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Task) {
        val room = roomListData.find {
            it.second == item.target!!.room.toInt()
        }?.first
        holder.setText(R.id.roomName,room?:"错误！请联系开发者")

        val endHour = item.target!!.beginHour+(item.target!!.duration/60/60).toInt()
        holder.setText(R.id.time,"${item.target!!.beginHour}:00 - ${endHour}:00")
        val switchView = holder.getView<SwitchCompat>(R.id.taskSwitch)
        switchView.isChecked = item.isOpened

        holder.setText(R.id.repeatMode, repeatModeList[item.repeatMode])
    }

}