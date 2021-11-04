package com.example.spyer

import android.annotation.SuppressLint
import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

/**
 * 存储用户使用数据的前台Service，当用户主界面被销毁时
 * Service也将被终止
 *
 * @author 杜国胜
 */
class StoreUsageStatsService : Service() {
    private lateinit var mUsageStatsManager: UsageStatsManager
    private lateinit var dbHelper: UsageStatsDBHelper
    private lateinit var storeDataThread: Thread

    /**
     * 调用UsageStatsDBHelper中的API存储用户数据
     * 包括UsageStats和UsageRun两种数据
     * 前者是直接通过系统API获取的，以包名为主键
     * 后者是计算而来的，以启动时间为主键
     */
    private fun storeUsageData() {
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
            dbHelper.storeData(usageStatsMap)
    }

    /**
     * 在Service生成时初始化mUsageStatsManager和dbHelper
     * 同时开启前台Service
     */
    @SuppressLint("UnspecifiedImmutableFlag")
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

    /**
     * 启动查询系统使用数据线程，并将结果存储到SQLite
     * 每隔一秒线程查询一次
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        storeDataThread = Thread() {
            try {
                while (true) {
                    storeUsageData()
                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {
                stopSelf()
            }
        }
        storeDataThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 退出子线程
        storeDataThread.interrupt()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
