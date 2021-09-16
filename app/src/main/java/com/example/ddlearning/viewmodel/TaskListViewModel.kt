package com.example.ddlearning.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ddlearning.bean.NetworkResult
import com.example.ddlearning.bean.Task
import com.example.ddlearning.network.OkCallback
import com.example.ddlearning.network.ServiceCreator
import com.example.ddlearning.network.networkService
import com.example.ddlearning.util.UserBaseUtil
import retrofit2.Call
import kotlin.math.log

class TaskListViewModel : ViewModel() {

    private val TAG = "TaskListViewModel"

    val taskList = MutableLiveData<MutableList<Task>>()

    val loading = MutableLiveData<Boolean>(false)

    fun requestTaskList() {
        networkService.getTaskList(UserBaseUtil.getUserAccount())
            .enqueue(object : OkCallback<List<Task>>() {
                override fun onSuccess(data: List<Task>?) {
                    super.onSuccess(data)
                    taskList.value = data!!.toMutableList()
                }
            })
    }

    fun switchTask(position: Int, switch: Boolean) {
        loading.value = true
        taskList.value!!.get(position)._id?.let { taskId ->
            networkService.switchTask(taskId,switch).enqueue(object : OkCallback<Any>() {

                override fun onSuccess(data: Any?) {
                    super.onSuccess(data)

                    val taskData = taskList.value!!.toMutableList()
                    taskData[position].isOpened = switch
                    taskList.value = taskData
                }

                override fun onFailure(call: Call<NetworkResult<Any>>, t: Throwable) {
                    super.onFailure(call, t)

                    val taskData = taskList.value!!.toMutableList()
                    taskData[position].isOpened = !switch
                    taskList.value = taskData
                }

                override fun onFinally() {
                    super.onFinally()
                    loading.value = false
                }
            })
        }

    }

    fun addTask(task:Task){
        val data = taskList.value!!.toMutableList()
        data.add(0,task)
        taskList.value = data
    }

    fun modifyTask(task:Task){
        val data = taskList.value!!.toMutableList()
        var targetIndex = -1
        data.forEachIndexed { index, it ->
            if(it._id.equals(task._id)){
                targetIndex = index
                data.set(targetIndex,task)
                taskList.value = data
                return
            }
        }

    }

    fun deleteTask(index:Int){
        loading.value = true
        val data = taskList.value!!.toMutableList()
        networkService.deleteTask(data[index]._id!!).enqueue(object : OkCallback<Any>() {
            override fun onSuccess(response: Any?) {
                super.onSuccess(response)
                data.removeAt(index)
                taskList.value = data
            }

            override fun onFinally() {
                super.onFinally()
                loading.value = false
            }
        })
    }

}