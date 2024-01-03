package com.example.myapplication.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * @date: 2022-06-22 16:51
 * @author: mayz
 * @version: 1.0
 */
@Entity(tableName = "userinfo")
data class User(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var aliasName: String = "",
    @ColumnInfo(name = "age")
    var age: Int = 0,
    @ColumnInfo(name = "ads")
    var ads: String = "",
    @Ignore
    var avatar: String = "",
)