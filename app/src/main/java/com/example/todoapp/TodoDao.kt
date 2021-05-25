package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao{
@Insert()
suspend fun insertTask(userModel: TodoModel):Long

@Query("Select * from TodoModel where isFinished!=0")
fun getTask():LiveData<List<TodoModel>>

@Query("update TodoModel Set isFinished=1 where id=:uid")
fun finishTask(uid:Long)

@Query("delete from TodoModel where id=:uid")
    fun DeleteTask(uid:Long)

}