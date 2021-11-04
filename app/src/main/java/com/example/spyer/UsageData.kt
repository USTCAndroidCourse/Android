package com.example.spyer

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 用户数据的类型接口
 * @author 杜国胜
 */
interface UsageData {
    // 描述一列，包含名称，类型
    class Column(val name: String, val type: String) {}

    /**
     * 返回主键名称
     */
    fun keyName(): String

    /**
     * 返回主键值
     */
    fun keyValue(): String

    /**
     * 返回列属性列表
     */
    fun getColumns(): List<Column>

    /**
     * 根据数据库表名和列属性生成建表语句
     * @author 杜国胜
     * @param tableName 表名
     * @param columns 属性列表
     * @return 建表SQL语句
     */
    fun generateCreateSql(tableName: String, columns: List<Column>): String {
        var createSql = "create table $tableName ( "
        for (i in columns.indices) {
            createSql += when (i) {
                0 -> "${columns[i].name} ${columns[i].type} primary key, "
                columns.size - 1 -> "${columns[i].name} ${columns[i].type} )"
                else -> "${columns[i].name} ${columns[i].type}, "
            }
        }
        return createSql
    }

    /**
     * 生成建表的SQL语句，列属性将取决于自身的成员变量
     * 由getColumns()生成
     * @author 杜国胜
     * @param tableName 表名
     * @return 建表SQL语句
     */
    fun generateCreateSql(tableName: String): String {
        return generateCreateSql(tableName, getColumns())
    }

    /**
     * 根据时间戳生成时间字符串
     * @author 刘东阳
     * @param lo 长整型时间戳
     * @return 时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    fun longToDate(lo: Long): String? {
        val date = Date(lo)
        val sd = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sd.format(date)
    }

    /**
     * 根据代表毫秒的长整型变量生成时间长度字符串
     * @author 刘东阳
     * @param lo 长整型时间
     * @return 时间字符串
     */
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