package com.example.spyer

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.ApplicationInfo
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
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatsTable)
        db?.execSQL(createRunTable)
    }

    /**
     * 判断是否为用户程序
     */
    private fun isUserApp(packageName: String): Boolean {
        val appInfo: ApplicationInfo;
        try {
            appInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_UNINSTALLED_PACKAGES
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
    }

    @SuppressLint("Recycle")
    fun storeStatsData(usageStatsDataMap: Map<String, UsageStats>) {
        val db = writableDatabase
        storeRunData()
        // db.delete(statsTableName, null, null)
        usageStatsDataMap.forEach { usageStatsMap ->
            val usageStats = usageStatsMap.value
            // 不统计非用户程序
            if (!isUserApp(usageStats.packageName))
                return@forEach

            Log.e("HAHA", "Insert Data")
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
        }
    }

    private fun storeRunData() {
        val processes = activityManager.runningAppProcesses
        processes.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}