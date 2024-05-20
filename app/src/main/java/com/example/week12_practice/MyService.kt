package com.example.week12_practice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private val channelID = "service_channel"

    override fun onCreate() {
        super.onCreate()

        // 채널 생성
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID, "service channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "notification channel for service."
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    // 알림 객체를 만드는 코드
    private fun createNotification() = NotificationCompat.Builder(this, channelID)
        .setContentTitle("Foreground Service")
        .setContentText("Foreground Service Running")
        .setSmallIcon(R.drawable.baseline_3d_rotation_24)
        .setOnlyAlertOnce(true)
        // .setProgress(100 ,progress, false)
        .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(10, createNotification())
        val repo = MyRepository(this)

        val str = intent?.getStringExtra("data")
        repo.valueInternal = str ?: ""

        return super.onStartCommand(intent, flags, startId)
    }
}