package com.example.spyer

import android.content.ContentValues
import com.example.spyer.UsageData.Column

class UsageRunData : UsageData {
    private var startTime: Long = 0            // 启动时间为主键
    private var stopTime: Long = 0             // 应用终止时间
    private var runTime: Long = 0              // 运行时间
    private lateinit var packageName: String   // 应用包名

    override fun keyName(): String {
        return "startTime"
    }

    override fun keyValue(): String {
        return startTime.toString()
    }

    /**
     * 返回描述数据结构的字符串列表
     * 第一个元素是主键
     */
    override fun getColumns(): List<Column> {
        return listOf(
            Column("startTime", "integer"),
            Column("stopTime", "integer"),
            Column("runTime", "integer"),
            Column("packageName", "integer"),
        )
    }

    fun contentValues(startTime: Long, stopTime: Long, packageName: String): ContentValues {
        updateData(startTime, stopTime, packageName)
        return ContentValues().apply {
            put("startTime", startTime)
            put("stopTime", stopTime)
            put("runTime", runTime)
            put("packageName", packageName)
        }
    }

    private fun updateData(startTime: Long, stopTime: Long, packageName: String) {
        if (startTime != 0L) {
            this.startTime = startTime
            this.packageName = packageName
        }
        this.stopTime = stopTime
        this.runTime = this.stopTime - this.startTime
    }
}