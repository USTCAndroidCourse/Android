package com.example.spyer

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var storeUsageStatsService: StoreUsageStatsService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storeUsageStatsService = StoreUsageStatsService()
        // 开启Service
        startService(Intent(this, storeUsageStatsService::class.java))
    }
}