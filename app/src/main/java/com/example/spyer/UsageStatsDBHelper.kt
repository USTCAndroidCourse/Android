package com.example.spyer

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsageStatsDBHelper(private val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createTable = "create table UsageStats (" +
            "packageName text primary key," +           // 将包名设为主键
            "firstTimeStamp integer," +                 // 查询起始时间戳
            "lastTimeStamp integer," +                  // 查询结束时间戳
            "LastTimeForegroundServiceUsed integer," +  //上一次前台服务使用的时间
            "lastTimeUsed integer," +                   // 上次使用的时间
            "lastTimeVisible integer," +                // 软件的Activity上次可见的时间
            "totalTimeForegroundServiceUsed integer," + // 前台服务使用总时间
            "totalTimeInForeground integer," +          // 在前台花费的总时间，以毫秒为单位
            "totalTimeVisible integer)"                 // 获取此包的活动在UI中可见的总时间，以毫秒为单位

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTable)
    }

    @SuppressLint("Recycle")
    fun storeUsageData(usageStatsDataMap: Map<String, UsageStats>) {
        val db = writableDatabase
        usageStatsDataMap.forEach { usageStats ->
            val value = ContentValues().apply {
                put("packageName", usageStats.key)
                val data = usageStats.value
                put("firstTimeStamp", data.firstTimeStamp)
                put("lastTimeStamp", data.lastTimeStamp)
                put("LastTimeForegroundServiceUsed", data.lastTimeForegroundServiceUsed)
                put("lastTimeUsed", data.lastTimeUsed)
                put("lastTimeVisible", data.lastTimeVisible)
                put("totalTimeForegroundServiceUsed ", data.totalTimeForegroundServiceUsed)
                put("totalTimeInForeground", data.totalTimeInForeground)
                put("totalTimeVisible", data.totalTimeVisible)
            }
            val cursor =
                db.query("UsageStats", null, "packageName = ?", arrayOf(usageStats.key), null, null,null)
            if (!cursor.moveToFirst()) {
                db.insert("UsageStats", null, value)
            } else {
                value.remove("packageName")
                db.update("UsageStats", value, "packageName = ?", arrayOf(usageStats.key))
            }
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}