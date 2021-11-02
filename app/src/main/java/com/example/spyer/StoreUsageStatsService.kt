package com.example.spyer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/** 存储用户使用数据的Service
 *
 * @author 杜国胜
 */
class StoreUsageStatsService(
) : Service() {
    private lateinit var mUsageStatsManager: UsageStatsManager
    private lateinit var dbHelper: UsageStatsDBHelper

    private fun storeUsageStatsData() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        // 查询最近一年的数据
        val usageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(
            cal.timeInMillis,
            System.currentTimeMillis()
        )
        if (usageStatsMap.isEmpty()) {
            Looper.prepare()
            Toast.makeText(this, "暂无权限，请在设置中开启", Toast.LENGTH_SHORT).show()
            Looper.loop()
        } else
            dbHelper.storeStatsData(usageStatsMap)
    }

    // 在Service生成时初始化mUsageStatsManager和dbHelper
    // 同时开启前台Service
    override fun onCreate() {
        super.onCreate()
        mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        dbHelper = UsageStatsDBHelper(this, "CustomUsage.db", 1)
        // 使用前台Service
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "store_usage_stats",
            "正在获取应用使用数据",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        // 设置点击通知后跳转到主页面
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "store_usage_stats")
            .setContentTitle("Spyer")
            .setContentText("正在获取应用使用信息")
            .setContentIntent(pIntent)
            .build()
        startForeground(1, notification)
    }

    // 启动查询系统使用数据线程，并将结果存储到SQLite
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread {
            while (true)
                storeUsageStatsData()
            // stopSelf()  // 采集完成，停止Service
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
