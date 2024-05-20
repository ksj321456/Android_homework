package com.example.week12_practice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyService : Service() {
    private val binder = LocalBinder()
    inner class LocalBinder : Binder(){
        fun getService() = this@MyService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    var valueInit : Int = 0
        private set

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
        startForeground(10, createNotification())       // Foreground 서비스 시작, 알림 표시
        val repo = MyRepository(this)

        valueInit = intent?.getIntExtra("data", 0) ?: 0
        repo.valueInternal = valueInit.toString()

        return super.onStartCommand(intent, flags, startId)
    }
}