package com.example.spyer

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class UsageStatsDBHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val statsTableName = "UsageStats"
    private val runTableName = "UsageRun"
    private val usageStatsData = UsageStatsData()
    private val usageRunData = UsageRunData()
    private val createStatsTable = usageStatsData.generateCreateSql(statsTableName)
    private val createRunTable = usageRunData.generateCreateSql(runTableName)
    private val packageManager = context.packageManager
    private val appStatusMap = HashMap<String, AppStatus>()

    inner class AppStatus(val startTime: Long, val totalTimeVisible: Long, val launchCount: Int) {}

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatsTable)
        db?.execSQL(createRunTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    /**
     * 判断是否为用户程序
     */
    private fun isUserApp(packageName: String): Boolean {
        // val appInfo: ApplicationInfo;
        try {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_SYSTEM_ONLY
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return true
        }
        return false
        // return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        //&& (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
    }

    @SuppressLint("Recycle")
    fun storeStatsData(usageStats: UsageStats) {
        // 不统计非用户程序
        val db = writableDatabase
        val value = usageStatsData.contentValues(usageStats)
        val cursor = db.query(
            statsTableName,
            null,
            "${usageStatsData.keyName()} = ?",
            arrayOf(usageStatsData.keyValue()),
            null,
            null,
            null
        )
        if (!cursor.moveToFirst()) {
            // 如果键值此前不存在，则插入
            db.insert(statsTableName, null, value)
        } else {
            // 否则更新数据
            value.remove(usageStatsData.keyName())
            db.update(
                statsTableName,
                value,
                "${usageStatsData.keyName()} = ?",
                arrayOf(usageStatsData.keyValue())
            )
        }
        cursor.close()
    }

    private fun storeRunData(startTime: Long, endTime: Long, packageName: String) {
        val db = writableDatabase
        val value = usageRunData.contentValues(
            startTime, endTime, packageName
        )
        db.insert(runTableName, null, value)
    }

    fun storeData(usageStatsDataMap: Map<String, UsageStats>) {
        usageStatsDataMap.forEach { usageStatsMap ->
            val usageStats = usageStatsMap.value
            if (!isUserApp(usageStats.packageName))
                return@forEach
            storeStatsData(usageStats)
            val nowLaunchCount = usageStatsData.getLaunchCount(usageStats)
            val appStatus = appStatusMap[usageStats.packageName]
            if (appStatus == null) {
                // Log.e("appStatus", "null ${usageStats.packageName}")
                // 存入的是当前时间，当前应用的总使用时长，当前启动次数
                appStatusMap[usageStats.packageName] =
                    AppStatus(
                        System.currentTimeMillis(),
                        usageStats.totalTimeVisible,
                        nowLaunchCount
                    )
                return@forEach
            }

            // 启动次数增加
            if (appStatus.launchCount < nowLaunchCount) {
                // Log.e("store", "count change ${appStatus.launchCount} $nowLaunchCount")
                val beginTime = appStatus.startTime
                val usedTime = usageStats.totalTimeVisible - appStatus.totalTimeVisible
                appStatusMap[usageStats.packageName] =
                    AppStatus(
                        System.currentTimeMillis(),
                        usageStats.totalTimeVisible,
                        nowLaunchCount
                    )
                storeRunData(beginTime, beginTime + usedTime, usageStats.packageName)
                // Log.e(
                //     "AppStatus",
                //     "${usageStats.packageName} $beginTime: ${usageRunData.longToDate(beginTime)}  $usedTime: ${
                //         usageRunData.longToTime(usedTime)
                //     }"
                // )
            }
        }
    }
}