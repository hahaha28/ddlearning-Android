package com.example.ddlearning.bean

import org.bson.types.ObjectId
import java.io.Serializable
import java.util.*

class Task(
    /**
     * 学号
     */
    var userAccount:String,

    /**
     * 密码
     */
    var userPassword:String
):Serializable {

    var _id: String? = null

    /**
     * 目标座位相关信息
     */
    var target: Target? = null

    /**
     * 执行策略
     */
    var executeRule: ExecuteRule? = null

    /**
     * 这个任务是否开启（用户是否开启）
     */
    var isOpened: Boolean = true

    /**
     * 重复模式：一次（1），每天（2），工作日（3），周末（4）
     */
    var repeatMode: Int = REPEAT_MODE_ONCE

    companion object{
        const val REPEAT_MODE_ONCE = 0
        const val REPEAT_MODE_EVERYDAY = 1
        const val REPEAT_MODE_WORKDAY = 2
        const val REPEAT_MODE_WEEKEND = 3
    }


    /**
     * 座位信息
     */
    data class Target(

        /**
         * 房间号
         */
        val room:String,

        /**
         * 座位号
         */
        val seat:String?,

        /**
         * 开始的小时，24小时制
         */
        val beginHour:Int,

        /**
         * 时长，单位：秒
         */
        val duration: Long,

    ):Serializable{
        /**
         * 开始时间，单位：秒
         */
        val beginTime: Long
        get() {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_MONTH,1)
            c.set(Calendar.HOUR_OF_DAY,0)
            c.set(Calendar.MINUTE,0)
            c.set(Calendar.SECOND,0)
            c.set(Calendar.MILLISECOND,0)
            return c.timeInMillis/1000 + beginHour*60*60
        }
    }

    /**
     * 执行策略
     */
    class ExecuteRule(
        /**
         * 提前几毫秒开始执行，单位，毫秒
         */
        val aheadTime:Long,

        /**
         * 如果失败，间隔几毫秒再次执行
         */
        val intervalTime: Long,

        /**
         * 如果失败，最长持续时间,单位，毫秒
         */
        val duration: Long
    ):Serializable{

    }

    override fun equals(other: Any?): Boolean {
        if(other is Task){
            if(this.target == null || other.target == null){
                return false
            }
            if(this.target!!.equals(other.target)
                && this.userAccount.equals(other.userAccount)){
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "Task(userAccount='$userAccount', userPassword='$userPassword', _id=$_id, target=$target, executeRule=$executeRule, isOpened=$isOpened, repeatMode=$repeatMode)"
    }


}

fun main(){

}
