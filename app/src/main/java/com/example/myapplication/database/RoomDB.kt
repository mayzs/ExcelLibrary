package com.example.myapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.database.dao.UserDao
import com.example.myapplication.database.entity.User

/**
 * @date: 2022-06-22 16:35
 * @author: mayz
 * @version: 1.0
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class RoomDB: RoomDatabase() {
    //创建userDao
    abstract fun userDao():UserDao
}