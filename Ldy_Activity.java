package com.example.myapplication2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void Welcome(View view) {
        Toast.makeText(this, "欢迎来到安卓世界", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        TextView tv=(TextView) findViewById(R.id.text1);
        tv.setText("srnodd");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String longToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }
    public static String longToTime(long lo){
        String ms=(lo%1000+"ms");
        lo=lo/1000;
        String s=(lo%60+"s ");
        lo=lo/60;
        String m=(lo%60+"m ");
        lo=lo/60;
        String h=(lo+"h ");
        return h+m+s+ms;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AppInfo(View view) {
        UsageStatsManager usm = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_WEEK, -2);//
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> list = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,startTime,endTime);
        if(list.size()==0)
        {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                try {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(this,"无法开启允许查看使用情况的应用界面",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }else{
            for(UsageStats usageStats :list){
                String name=usageStats.getPackageName();//获取包名
                if(name.contains("android")||name.contains("google")) continue;
                Log.d("name:",name);
                Log.d("FirstTime:",longToDate(usageStats.getFirstTimeStamp()+8*3600*1000)); //第一次启动时间
                Log.d("LastTime:",longToDate(usageStats.getLastTimeStamp()+8*3600*1000)); //最近一次启动时间
                Log.d("TotalTime:",longToTime(usageStats.getTotalTimeInForeground())); //总时间
                try{
                    Field field = usageStats.getClass().getDeclaredField("mLaunchCount");//获取应用启动次数，UsageStats未提供方法来获取，只能通过反射来拿到
                    field.setAccessible(true);
                    Log.d("times",":"+field.getInt(usageStats));//启动次数
                }catch (Exception e)
                {
                    e.printStackTrace();;
                }
            }
        }

    }
}