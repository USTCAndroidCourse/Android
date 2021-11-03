package com.example.spyer

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.ContentValues
import com.example.spyer.UsageData.Column

open class UsageStatsData : UsageData {
    private lateinit var packageName: String              // 应用包名
    private var firstTimeStamp: Long = 0                  // 查询起始时间戳
    private var lastTimeStamp: Long = 0                   // 查询结束时间戳
    private var lastTimeForegroundServiceUsed: Long = 0   // 上一次前台服务使用的时间
    private var lastTimeUsed: Long = 0                    // 上次使用的时间
    private var lastTimeVisible: Long = 0                 // 软件的Activity上次可见的时间
    private var totalTimeForegroundServiceUsed: Long = 0  // 前台服务使用总时间
    private var totalTimeInForeground: Long = 0           // 在前台花费的总时间，以毫秒为单位
    private var totalTimeVisible: Long = 0                // 获取此包的活动在UI中可见的总时间，以毫秒为单位
    private var launchCount: Int = 0                      // 应用启动次数

    override fun keyName(): String {
        return "packageName"
    }

    override fun keyValue(): String {
        return packageName
    }

    override fun getColumns(): List<Column> {
        return listOf(
            Column("packageName", "text"),
            Column("firstTimeStamp", "integer"),
            Column("lastTimeStamp", "integer"),
            Column("lastTimeForegroundServiceUsed", "integer"),
            Column("lastTimeUsed", "integer"),
            Column("lastTimeVisible", "integer"),
            Column("totalTimeForegroundServiceUsed", "integer"),
            Column("totalTimeInForeground", "integer"),
            Column("totalTimeVisible", "integer"),
            Column("launchCount", "integer")
        )
    }

    fun contentValues(usageStats: UsageStats): ContentValues {
        updateData(usageStats)
        return ContentValues().apply {
            put("packageName", packageName)
            put("firstTimeStamp", firstTimeStamp)
            put("lastTimeStamp", lastTimeStamp)
            put("lastTimeForegroundServiceUsed", lastTimeForegroundServiceUsed)
            put("lastTimeUsed", lastTimeUsed)
            put("lastTimeVisible", lastTimeVisible)
            put("totalTimeForegroundServiceUsed", totalTimeForegroundServiceUsed)
            put("totalTimeInForeground", totalTimeInForeground)
            put("totalTimeVisible", totalTimeVisible)
            put("launchCount", launchCount)
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun getLaunchCount(usageStats: UsageStats): Int {
        val field = usageStats.javaClass.getDeclaredField("mLaunchCount")
        field.isAccessible = true
        return field.getInt(usageStats)
    }

    private fun updateData(usageStats: UsageStats) {
        packageName = usageStats.packageName
        firstTimeStamp = usageStats.firstTimeStamp
        lastTimeStamp = usageStats.lastTimeStamp
        lastTimeForegroundServiceUsed = usageStats.lastTimeForegroundServiceUsed
        lastTimeUsed = usageStats.lastTimeUsed
        lastTimeVisible = usageStats.lastTimeVisible
        totalTimeForegroundServiceUsed = usageStats.totalTimeForegroundServiceUsed
        totalTimeInForeground = usageStats.totalTimeInForeground
        totalTimeVisible = usageStats.totalTimeVisible
        launchCount = getLaunchCount(usageStats)
    }
}