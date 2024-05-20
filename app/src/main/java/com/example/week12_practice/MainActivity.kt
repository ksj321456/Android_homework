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
import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

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

        findViewById<Button>(R.id.button)?.setOnClickListener {
            // 1. Started Service
            val intent = Intent(this, MyService::class.java)
            intent.putExtra("data", "Hello")
            startService(intent)    // 서비스 호P
        }

        // 2. WorkManager
        // Worker는 프로그램 실행 후 바로 실행됨
        val contraints = Constraints.Builder().apply{
            setRequiresBatteryNotLow(true)
        }.build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(contraints)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "MyWorker",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
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