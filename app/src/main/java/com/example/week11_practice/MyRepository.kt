package com.example.week11_practice

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class MyRepository(val context : Context) {

    private val file = File(context.filesDir, "data.txt")
    private var data = mutableListOf<String>()

    val myDao = MyDatabase.getDatabase(context).getMyDAO()

    /*fun readData(): List<String>{

        var list = listOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            myDao.getAll_f().map {
                it.map {
                    it.name
                }
            }.collect(){
                list = it
            }
        }
        return list
    }*/

    fun addData(value: String){
        CoroutineScope(Dispatchers.IO).launch {
            myDao.insert(Item(0, value, "address"))
        }
    }

    /*fun readData() : List<String>{
        return if(file.exists()) {
            val txt = file.readText(Charsets.UTF_8)
            data = txt.split("/").toMutableList()
            data
            //  "abc/def" -> "abc", "def"
        }
        else
            listOf()
    }

    fun addData(value : String){
        data.add(value)
        val str = data.joinToString("/")
        file.writeText(str, Charsets.UTF_8)
    }*/

}