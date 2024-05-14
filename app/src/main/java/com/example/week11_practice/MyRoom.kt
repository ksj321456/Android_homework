package com.example.week11_practice

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name : String,
    val address: String
)

@Dao
interface MyDAO{
    @Query("SELECT * FROM Item")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT * FROM Item")
    fun getAll_f(): Flow<List<Item>>

    @Insert
    suspend fun insert(item : Item)
}

@Database(entities = [Item::class], version = 1)
abstract class MyDatabase: RoomDatabase(){
    abstract fun getMyDAO(): MyDAO

    companion object{
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(context: Context): MyDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context, MyDatabase::class.java, "MyDataBase"
                ).build()
            }
            return INSTANCE as MyDatabase
        }
    }
}