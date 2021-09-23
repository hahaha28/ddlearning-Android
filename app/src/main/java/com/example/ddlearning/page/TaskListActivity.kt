package com.example.ddlearning.page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ddlearning.R
import com.example.ddlearning.adapter.TaskListRVAdapter
import com.example.ddlearning.bean.Task
import com.example.ddlearning.databinding.ActivityTaskListBinding
import com.example.ddlearning.network.ServiceCreator
import com.example.ddlearning.network.networkService
import com.example.ddlearning.viewmodel.TaskListViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hi.dhl.binding.viewbind
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.header.MaterialHeader

class TaskListActivity : AppCompatActivity() {

    private val TAG = "TaskListActivity"

    private val binding by viewbind<ActivityTaskListBinding>()
    private val viewModel by viewModels<TaskListViewModel>()

    private val adapter = TaskListRVAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.smartRefreshLayout.setDragRate(1f)
        binding.smartRefreshLayout.autoRefresh()
        val materialHeader = binding.smartRefreshLayout.refreshHeader as MaterialHeader
        materialHeader.setColorSchemeColors(ContextCompat.getColor(this,R.color.theme))

        binding.smartRefreshLayout.setOnRefreshListener {
            viewModel.requestTaskList()
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this,TaskDetailActivity::class.java)
            intent.putExtra("task",adapter.data[position] as Task)
            startActivity(intent)
        }

        adapter.setOnItemLongClickListener { adapter, view, position ->
            MessageDialog.show(this,"提示","是否要删除这个程序?","确定","取消")
                .setOnOkButtonClickListener { baseDialog, v ->
                    viewModel.deleteTask(position)
                    baseDialog.doDismiss()
                    true
                }
            true
        }

        adapter.switchCheckChangeListener = { position ,check ->
            viewModel.switchTask(position,check)
        }

        viewModel.taskList.observe(this){
            Log.d(TAG, "taskList.observe new data:${it}")
            adapter.setNewInstance(it)
            binding.smartRefreshLayout.finishRefresh()
        }

        viewModel.loading.observe(this){
            if(it){
                WaitDialog.show(this,"请稍候...")
            }else{
                WaitDialog.dismiss()
            }
        }

        binding.newTaskButton.setOnClickListener {
            val intent = Intent(this,TaskDetailActivity::class.java)
            startActivity(intent)
        }

        binding.newTaskButton.setOnLongClickListener {
            InputDialog.show(this,"修改端口号","当前端口：${ServiceCreator.getPort()}")
                .setHintText("7000")
                .setOnOkButtonClickListener { baseDialog, v, inputStr ->
                    ServiceCreator.modifyPort(inputStr)
                    baseDialog.doDismiss()
                    MessageDialog.show(this,"提示","请重启生效！","确定")
                    true
                }
            true
        }

        // eventBus

        // 添加新任务
        LiveEventBus.get("new_task",Task::class.java)
            .observeForever {
                viewModel.addTask(it)
            }

        // 修改任务
        LiveEventBus.get("modify_task",Task::class.java)
            .observeForever {
                Log.d(TAG, "EventBus: modify_task:$it")
                viewModel.modifyTask(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}