package com.krishe.govern.dbsrc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ReportItemTable::class], version = 1)
abstract class ReportDatabase : RoomDatabase() {

    abstract fun repoItemDao(): ReportItemDao

    companion object {
        private var instance: ReportDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): ReportDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, ReportDatabase::class.java,
                    "report_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: ReportDatabase) {
            val repoItemDao = db.repoItemDao()
            // subscribeOnBackground {
            repoItemDao.insertReportItem(
                    ReportItemTable(
                        1, "title 1", "desc 1", "desc 1",
                        "desc 1", "desc 1", "desc 1", 0.0,
                        0.0, "desc 1","desc 1",
                        "desc 1",
                    )
                )
            repoItemDao.insertReportItem(
                    ReportItemTable(
                        2, "title 2", "desc 2", "desc 2",
                        "desc 2", "desc 2", "desc 2", 0.0,
                        0.0, "desc 2","desc 2",
                        "desc 2",
                    )
                )
            repoItemDao.insertReportItem(
                    ReportItemTable(
                        3, "title 3", "desc 3", "desc 3",
                        "desc 3", "desc 3", "desc 3", 0.0,
                        0.0, "desc 3","desc 3",
                        "desc 3",
                    )
                )

            // }
        }
    }


}