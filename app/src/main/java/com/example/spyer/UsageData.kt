package com.example.spyer

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

interface UsageData {
    // 描述一列，包含名称，类型
    class Column(val name: String, val type: String) {}

    fun keyName(): String
    fun keyValue(): String
    fun getColumns(): List<Column>

    // 根据数据库表名和列属性生成建表语句
    fun generateCreateSql(tableName: String, columns: List<Column>): String {
        var createSql = "create table $tableName ( ";
        for (i in columns.indices) {
            createSql += when (i) {
                0 -> "${columns[i].name} ${columns[i].type} primary key, "
                columns.size - 1 -> "${columns[i].name} ${columns[i].type} )"
                else -> "${columns[i].name} ${columns[i].type}, "
            }
        }
        return createSql
    }

    fun generateCreateSql(tableName: String): String {
        return generateCreateSql(tableName, getColumns())
    }

    @SuppressLint("SimpleDateFormat")
    fun longToDate(lo: Long): String? {
        val date = Date(lo)
        val sd = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sd.format(date)
    }

    fun longToTime(lo: Long): String {
        var tmp = lo
        val ms = (tmp % 1000).toString() + "ms"
        tmp /= 1000
        val s = (tmp % 60).toString() + "s "
        tmp /= 60
        val m = (tmp % 60).toString()
        tmp /= 60
        val h = tmp.toString() + "h "
        return h + m + s + ms
    }
}