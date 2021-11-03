package com.example.spyer

import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    var stopService: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 开启Service
        startService(Intent(this, StoreUsageStatsService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, StoreUsageStatsService::class.java))
    }
}