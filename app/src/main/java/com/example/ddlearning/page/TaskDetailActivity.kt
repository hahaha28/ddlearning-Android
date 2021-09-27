package com.example.ddlearning.page

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ddlearning.R
import com.example.ddlearning.adapter.TaskDetailRVAdapter
import com.example.ddlearning.bean.NetworkResult
import com.example.ddlearning.bean.RoomDetail
import com.example.ddlearning.bean.Task
import com.example.ddlearning.databinding.ActivityTaskDetailBinding
import com.example.ddlearning.network.OkCallback
import com.example.ddlearning.network.networkService
import com.example.ddlearning.util.UserBaseUtil
import com.hi.dhl.binding.viewbind
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import com.kongzue.dialog.util.InputInfo
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.WaitDialog
import retrofit2.Call

class TaskDetailActivity : AppCompatActivity() {

    private val TAG = "TaskDetailActivity"

    private val binding by viewbind<ActivityTaskDetailBinding>()

    private val adapter = TaskDetailRVAdapter()

    private val showData: MutableList<Pair<String, String>> = mutableListOf()

    private val roomListData: MutableList<Pair<String, Int>> = mutableListOf()

    private val repeatModeList = listOf<String>("一次", "每天", "周一至周五", "周末")

    init {
        roomListData.add(Pair("二楼北自习室（202）", 35))
        roomListData.add(Pair("二楼南自习室（201）", 36))
        roomListData.add(Pair("三楼南自习室（301）", 31))
        roomListData.add(Pair("三楼北自习室（302）", 37))
    }

    // 选择的数据
    private var taskId: String? = null
    private var roomId: String? = null
    private var seatId: String? = null
    private var beginHour: Int? = null
    private var endHour: Int? = null
    private var repeatMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置RecyclerView
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val task = intent.getSerializableExtra("task") as Task?
        taskId = task?._id
        // 设置数据
        initShowData(task)

        // recyclerView点击事件
        adapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                // 选择自习室
                0 -> {
                    val data = Array<String>(roomListData.size) {
                        roomListData[it].first
                    }
                    BottomMenu.show(this, data) { text, index ->
                        roomId = roomListData[index].second.toString()
                        modifyShowData(0, text)
                    }
                }

                // 选择座位
                1 -> {
                    if(roomId == null){
                        MessageDialog.show(this,"提示","请先选择教室")
                        return@setOnItemClickListener
                    }
                    BottomMenu.show(this, arrayOf("系统推荐座位", "输入座位号")) { text, index ->
                        if (index == 0) {
                            seatId = null
                            modifyShowData(1, "系统推荐座位")
                        } else {
                            InputDialog.show(this, "提示", "请输入座位号", "确定", "取消")
                                .setInputInfo(InputInfo().setInputType(InputType.TYPE_CLASS_NUMBER))
                                .setHintText("请输入桌子上的座位号")
                                .setOnOkButtonClickListener { baseDialog, v, inputStr ->
                                    WaitDialog.show(this,"请稍候...")
                                    checkSeat(roomId!!,inputStr){ result ->
                                        if(result == null){
                                            WaitDialog.dismiss()
                                            MessageDialog.show(this,"提示","座位不存在")
                                        }else{
                                            WaitDialog.dismiss()
                                            modifyShowData(1,inputStr)
                                            seatId = result

                                            baseDialog.doDismiss()
                                        }
                                    }
                                    true
                                }
                        }
                    }
//                    MessageDialog.show(this,"提示","暂不支持自选座位")
                }

                // 输入开始时间
                2 -> {
                    InputDialog.show(this, "请输入开始的时间", "24小时制", "确定", "取消")
                        .setInputInfo(
                            InputInfo()
                                .setMAX_LENGTH(2)
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                        )
                        .setHintText("例:\"17\"")
                        .setOnOkButtonClickListener { baseDialog, v, inputStr ->
                            val hour = inputStr.toIntOrNull()
                            if (hour == null) {
                                MessageDialog.show(this, "错误", "非法输入")
                            } else {
                                beginHour = hour
                                modifyShowData(2, hour.toString())
                                baseDialog.doDismiss()
                            }

                            true
                        }

                }

                // 输入结束时间
                3 -> {
                    InputDialog.show(this, "请输入开始的时间", "24小时制", "确定", "取消")
                        .setInputInfo(
                            InputInfo()
                                .setMAX_LENGTH(2)
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                        )
                        .setHintText("例:\"17\"")
                        .setOnOkButtonClickListener { baseDialog, v, inputStr ->
                            val hour = inputStr.toIntOrNull()
                            if (hour == null) {
                                MessageDialog.show(this, "错误", "非法输入")
                            } else if (hour > 22) {
                                MessageDialog.show(this, "错误", "结束时间不能超过22点")
                            } else {
                                endHour = hour
                                modifyShowData(3, hour.toString())
                                baseDialog.doDismiss()
                            }

                            true
                        }
                }

                // 选择重复模式
                4 -> {
                    BottomMenu.show(this, repeatModeList.toTypedArray()) { text, index ->
                        repeatMode = index
                        modifyShowData(4, text)
                    }
                }
            }
        }
    }

    private fun checkSeat(room: String, seatTitle: String,result:(String?)->Unit) {
        networkService.getRoomDetail(
            UserBaseUtil.getUserAccount(),
            UserBaseUtil.getUserPassword(),
            room
        ).enqueue(object : OkCallback<RoomDetail>() {
            override fun onSuccess(data: RoomDetail?) {
                super.onSuccess(data)
                data!!.pois.forEach {
                    if(it.title.equals(seatTitle)){
                        Log.e("tag","find "+it)
                        result(it.id)
                        return
                    }
                }
                result(null)

            }

            override fun onFailureFinally() {
                super.onFailureFinally()
                result(null)
            }
        })
    }

    private fun initShowData(task: Task?) {
        task?.let {
            roomId = it.target!!.room
            seatId = it.target!!.seat
            beginHour = it.target!!.beginHour
            endHour = beginHour!! + (it.target!!.duration / 60 / 60).toInt()
            repeatMode = it.repeatMode
        }
        with(showData) {
            val roomName = if (roomId == null) {
                ""
            } else {
                roomListData.find {
                    it.second == roomId!!.toInt()
                }?.first ?: ""
            }
            add(Pair("自习室", roomName))
            add(Pair("座位", seatId ?: "系统推荐座位"))
            add(Pair("开始时间", "${beginHour ?: ""}"))
            add(Pair("结束时间", "${endHour ?: ""}"))
            add(Pair("重复", repeatModeList.get(repeatMode)))
        }
        adapter.setNewInstance(showData)
    }

    private fun modifyShowData(index: Int, value: String) {
        showData[index] = showData[index].copy(second = value)
        adapter.notifyItemChanged(index)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_right -> {
                over()
            }
        }
        return true
    }

    private fun over() {
        if (roomId == null) {
            dialog("请选择教室")
            return;
        }
        if (beginHour == null) {
            dialog("请选择开始时间")
            return;
        }
        if (endHour == null) {
            dialog("请选择结束时间")
            return;
        }
        if (endHour!! <= beginHour!!) {
            dialog("结束时间必须大于开始时间")
            return;
        }

        val task = Task(UserBaseUtil.getUserAccount(), UserBaseUtil.getUserPassword())
        task._id = taskId
        task.isOpened = true
        task.repeatMode = repeatMode
        task.target =
            Task.Target(roomId!!, seatId, beginHour!!, (endHour!! - beginHour!!) * 60 * 60L)
        task.executeRule = Task.ExecuteRule(0, 0, 5 * 60 * 1000L)

        WaitDialog.show(this, "请稍候...")
        if (taskId == null) {
            // 添加新任务
            networkService.addTask(task).enqueue(object : OkCallback<Task>() {
                override fun onSuccess(data: Task?) {
                    super.onSuccess(data)

                    LiveEventBus.get<Task>("new_task")
                        .post(data!!)
                    finish()
                }

                override fun onFailure(call: Call<NetworkResult<Task>>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog("没有网络~")
                }

                override fun onError(code: Int, msg: String) {
                    super.onError(code, msg)
                    dialog("添加失败，请联系开发者")
                }

                override fun onFailureFinally() {
                    super.onFailureFinally()
                    WaitDialog.dismiss()
                }
            })
        } else {
            // 修改任务
            networkService.replaceTask(task).enqueue(object : OkCallback<Any>() {
                override fun onSuccess(data: Any?) {
                    super.onSuccess(data)

                    Log.d(TAG, "onSuccess: sendEvent")
                    LiveEventBus.get<Task>("modify_task")
                        .post(task)
                    finish()
                }

                override fun onFailure(call: Call<NetworkResult<Any>>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog("没有网络~")
                }

                override fun onError(code: Int, msg: String) {
                    super.onError(code, msg)
                    dialog("添加失败，请联系开发者")
                }

                override fun onFailureFinally() {
                    super.onFailureFinally()
                    WaitDialog.dismiss()
                }
            })
        }

    }

    fun dialog(msg: String) {
        MessageDialog.show(this, "提示", msg, "确定")
    }
}