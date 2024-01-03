package com.example.myapplication.database.dao

import androidx.room.*
import com.example.myapplication.database.entity.User

/**
 * @date: 2022-06-22 16:54
 * @author: mayz
 * @version: 1.0
 */
@Dao
interface UserDao {
    @Query("select * from userinfo")
    fun queryAllUser(): MutableList<User>

    //根据姓名参数查询
    @Query("select * from userinfo where name = :name")
    fun queryFindUser(name: String): User?

    // 添加单条数据
    @Insert
    fun addUser(vararg user: User)

    // 添加批量数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBatchUser(list: MutableList<User>)

    // 更新某一个数据
    @Update
    fun updateUser(vararg user: User)

    //更新所有数据
    @Query("update userinfo set age='50'")
    fun updateAll()

    //删除某一个数据
    @Delete
    fun deleteSingle(vararg user: User)

    //删除表里所有数据
    @Query("delete from userinfo")
    fun deleteAllUser()

}