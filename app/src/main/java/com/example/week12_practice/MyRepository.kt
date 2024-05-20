package com.example.week12_practice

import android.content.Context
import android.os.Environment
import java.io.File

class MyRepository(context : Context) {
    private val fileInternal = File(context.filesDir, "appfile1.txt")
    private val fileExternal =
        if(isExternalStorageMounted)
            File(context.getExternalFilesDir(null), "appfile2.txt")
        else
            fileInternal

    var valueInternal : String = readValue(fileInternal)
        set(v){
            field = v
            writeValue(fileInternal, v)
        }
    var valueExternal : String = readValue(fileExternal)
        set(v){
            field = v
            writeValue(fileExternal, v)
        }

    private fun readValue(file : File) : String{
        return try {
            println("$file")
            file.readText(Charsets.UTF_8)
        }catch (e:Exception){
            ""
        }
    }

    private fun writeValue(file : File, value : String){
        file.writeText(value, Charsets.UTF_8)
    }

    private val isExternalStorageMounted : Boolean
        get(){
            val state = Environment.getExternalStorageState()
            return state == Environment.MEDIA_MOUNTED
        }
}