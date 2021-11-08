package com.example.spyer

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * 数据库类，持久化存储收集的数据
 * @author 杜国胜
 */
class UsageDataDBHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val statsTableName = "UsageStats"                                          // 统计数据表名
    private val runTableName = "UsageRun"                                              // 运行数据表名
    private val usageStatsData = UsageStatsData()                                      // 统计数据对象
    private val usageRunData = UsageRunData()                                          // 运行数据对象
    private val createStatsTable = usageStatsData.generateCreateSql(statsTableName)    // 统计数据建表SQL
    private val createRunTable = usageRunData.generateCreateSql(runTableName)          // 运行数据建表SQL
    private val packageManager = context.packageManager                                // 包管理器，用于判断是否为系统应用，系统应用不入库
    private val appStatusMap = HashMap<String, AppStatus>()                            // 用户使用情况哈希表，与运行数据的计算有关

    /**
     * 用户上次launchCount发生变动时的使用情况，
     * 用于计算运行数据
     */
    inner class AppStatus(val startTime: Long, val totalTimeVisible: Long, val launchCount: Int) {}

    /**
     * 首次创建时执行
     * 建立数据库表
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatsTable)
        db?.execSQL(createRunTable)
    }

    /**
     * 升级操作，暂时未用到，毕竟不是真的发布应用
     * -.-!
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    /**
     * 判断是否为用户程序，只能说判断的并不是很准
     * -.-！
     */
    private fun isUserApp(packageName: String): Boolean {
        try {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_SYSTEM_ONLY
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return true
        }
        return false
    }

    /**
     * 存储统计数据，即根据系统API查询到的数据
     * @param usageStats 系统API中的统计数据对象
     */
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

    /**
     * 存储应用运行数据，以启动时间为主键
     * 包括【启动时间，运行时间，结束时间，包名】
     */
    private fun storeRunData(startTime: Long, endTime: Long, packageName: String) {
        val db = writableDatabase
        val value = usageRunData.contentValues(
            startTime, endTime, packageName
        )
        db.insert(runTableName, null, value)
    }

    /**
     * 供外部调用的存储数据API
     */
    fun storeData(usageStatsDataMap: Map<String, UsageStats>) {
        usageStatsDataMap.forEach { usageStatsMap ->
            val usageStats = usageStatsMap.value
            if (!isUserApp(usageStats.packageName))
                return@forEach
            storeStatsData(usageStats)
            val nowLaunchCount = usageStatsData.getLaunchCount(usageStats)
            val appStatus = appStatusMap[usageStats.packageName]
            if (appStatus == null) {
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
                val beginTime = appStatus.startTime
                val usedTime = usageStats.totalTimeVisible - appStatus.totalTimeVisible
                appStatusMap[usageStats.packageName] =
                    AppStatus(
                        System.currentTimeMillis(),
                        usageStats.totalTimeVisible,
                        nowLaunchCount
                    )
                storeRunData(beginTime, beginTime + usedTime, usageStats.packageName)
            }
        }
    }
}