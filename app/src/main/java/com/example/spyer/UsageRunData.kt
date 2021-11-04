package com.example.spyer

import android.content.ContentValues
import com.example.spyer.UsageData.Column

/**
 * 用户运行时间类
 * @author 杜国胜
 */
class UsageRunData : UsageData {
    private var startTime: Long = 0            // 启动时间为主键

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
            Column("packageName", "text"),
        )
    }

    fun contentValues(startTime: Long, stopTime: Long, packageName: String): ContentValues {
        this.startTime = startTime
        return ContentValues().apply {
            put("startTime", startTime)
            put("stopTime", stopTime)
            put("runTime", stopTime - startTime)
            put("packageName", packageName)
        }
    }

}