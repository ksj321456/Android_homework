package com.example.week11_practice

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModel(private val repository: MyRepository) : ViewModel() {
    val myData = repository.myDao.getAll()

    fun add(){
        repository.addData("test")
       // myData.value = repository.readData()
    }

}

class MyViewModelFactory(private val context:Context) : ViewModelProvider.Factory{
    override fun <T:ViewModel> create(modelClass: Class<T>):T{
        return if(modelClass.isAssignableFrom(MyViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            MyViewModel(MyRepository(context)) as T
        else
            throw IllegalArgumentException()
    }
}