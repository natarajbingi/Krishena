package com.krishe.govern.dbsrc

import androidx.lifecycle.LiveData
import androidx.room.*
/**
 * Created by Nataraj Bingi on Oct 24, 2021
 * */
@Dao
interface ReportItemDao {

    @Insert
    fun insertReportItem(rItem: ReportItemTable)

    @Update
    fun updateReportItem(rItem: ReportItemTable)

    @Delete
    fun deleteReportItem(rItem: ReportItemTable)

    @Query("delete from report_item_table")
    fun deleteAllReports()

    @Query("select * from report_item_table order by id desc")
    fun getAllReports(): LiveData<List<ReportItemTable>>

    @Query("SELECT * FROM report_item_table WHERE id LIKE :title")
    fun findByTitle(title: String): LiveData<List<ReportItemTable>>
}