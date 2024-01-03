package com.example.myapplication.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @date: 2022-06-22 16:33
 * @author: mayz
 * @version: 1.0
 */
class DbManager {

    companion object{
        //数据库名
        private const val dbName: String = "dbroom"
        @JvmStatic
        @Volatile
        private var INSTANCE: RoomDB? = null
        fun initDB(mContext: Context):RoomDB{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                //注释3处
                val instance = Room.databaseBuilder(mContext.applicationContext, RoomDB::class.java,
                    dbName)
                    .allowMainThreadQueries()//允许在主线程操作
                    .addCallback(DbCreateCallBack)//增加回调监听
                    .addMigrations(ZMigration)//增加数据库迁移
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
    private object DbCreateCallBack : RoomDatabase.Callback() {
        //第一次创建数据库时调用
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.e("TAG", "first onCreate db version: " + db.version)
        }
    }
    /**
     * 数据库升级
     */
    private object ZMigration : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            Log.e("Tag", "执行数据库升级: ")
            //loginUser表中增加字段gender
            //database.execSQL("ALTER TABLE loginUser ADD gender INTEGER Default 1 not null")
            //新建汽车数据表
            //database.exceSQL("CREATE TABLE Car (id INTEGER, name TEXT, PRIMARY KEY(id))")
        }
    }
}