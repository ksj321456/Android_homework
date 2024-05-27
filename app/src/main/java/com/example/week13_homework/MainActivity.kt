package com.example.week13_homework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val broadcastReceiver = MyBroadcastReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestMultiplePermission(arrayOf(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_CALL_LOG))
    }

    override fun onStart() {
        super.onStart()
        IntentFilter().also {
            it.addAction(Intent.ACTION_POWER_CONNECTED)
            it.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            it.addAction(ACTION_MY_BROADCAST)
            ContextCompat.registerReceiver(this, broadcastReceiver, it, ContextCompat.RECEIVER_EXPORTED)
            sendBroadcast(Intent(ACTION_MY_BROADCAST))
            readCallLog()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            when(intent.action){
                Intent.ACTION_POWER_CONNECTED -> { showBroadcast(Intent.ACTION_POWER_CONNECTED) }
                ACTION_MY_BROADCAST -> {
                    println("@@@@@@@@@@@@${ACTION_MY_BROADCAST}")
                    findViewById<TextView>(R.id.textViewBroadcast).text = ACTION_MY_BROADCAST
                }
                else -> {showBroadcast(intent.action ?: "NO ACTION")}
            }
        }
        private fun showBroadcast(msg : String){
            println("@@@@@@@@@@@${msg}")
            findViewById<TextView>(R.id.textViewBroadcast).text = msg
        }
    }

    companion object {
        const val ACTION_MY_BROADCAST = "ACTION_MY_BROADCAST"
    }



    private fun readCallLog(){
        if(checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return
        val projection = arrayOf(CallLog.Calls._ID, CallLog.Calls.NUMBER)
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(CallLog.Calls.NUMBER)
            while (it.moveToNext()) {
                val number = it.getString(idx)
                println("number : $number")
            }
        }
    }

    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage("Warning")
                }.show()
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("Reason")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }

    private fun requestMultiplePermission(perms: Array<String>) {
        val requestPerms = perms.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (requestPerms.isEmpty())
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val noPerms = it.filter { item -> item.value == false }.keys.toMutableSet()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (it.contains(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) || it.contains(android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    // READ_MEDIA_VISUAL_USER_SELECTED 와 READ_MEDIA_IMAGES 는 둘 중에 하나만 권한 부여받게 됨
                    // 따라서 둘 중에 하나만 있다면 noPerms에 다른 권한은 제거
                    noPerms.remove(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    noPerms.remove(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            if (noPerms.isNotEmpty()) { // there is a permission which is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage("Warning")
                }.show()
            }
        }

        val showRationalePerms = requestPerms.filter {shouldShowRequestPermissionRationale(it)}
        if (showRationalePerms.isNotEmpty()) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("Reason")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(requestPerms.toTypedArray()) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(requestPerms.toTypedArray())
        }
    }
}