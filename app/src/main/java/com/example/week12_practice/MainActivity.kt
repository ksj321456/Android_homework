package com.example.week12_practice

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.widget.TextView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestSinglePermission(POST_NOTIFICATIONS)
        }

        findViewById<Button>(R.id.buttonGet)?.setOnClickListener {
            // 1. Started Service
            val intent = Intent(this, MyService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                } else {
                    startService(it)
                }
            }
            intent.putExtra("data", 200)
            findViewById<TextView>(R.id.textView).text = "${MyService}"
            startService(intent)    // 서비스 호출
        }

        // 2. WorkManager
        // Worker는 프로그램 실행 후 바로 실행됨
        val contraints = Constraints.Builder().apply{
            setRequiresBatteryNotLow(true)
        }.build()


        // 반복 작업 하기
        val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
            .setConstraints(contraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MyWorker",
            ExistingPeriodicWorkPolicy.KEEP,    // 기존에 동일 이름의 worker가 있을 때 처리, keep은 기존 것을 유지한다는 의미
            repeatingRequest
        )
    }


    private var myService : MyService ?= null

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            myService = (service as MyService.LocalBinder).getService() // 바운드 됨
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MyService::class.java).also {
            bindService(it, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
    private fun requestSinglePermission(permission: String) {
        if(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return
        val requestPermLancher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it == false){
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage(getString(R.string.no_permission, permission))
                }.show()
            }
        }
        if(shouldShowRequestPermissionRationale(permission)){
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage(getString(R.string.req_permission_reason,permission))
                setPositiveButton("Allow") {_, _ -> requestPermLancher.launch(permission)}
                setNegativeButton("Deny"){_, _ ->}
            }.show()
        }
        else{
            requestPermLancher.launch(permission)
        }
    }
}