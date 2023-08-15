package com.stepa0751.alertbuttonend.location

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

import com.stepa0751.alertbuttonend.MainActivity
import com.stepa0751.alertbuttonend.R

//  Создали сервис для работы в фоновом режиме... В манифесте его нужно прописать!!!
class LocationService : Service() {
//    Эта переменная нужна для того, чтобы подключаться к провайдеру GPS и получать у него данные о местоположении
//  И она нихрена без параметров не регистрируется в классе!!!
    @SuppressLint("VisibleForTests")
   private var locProvider = getFusedLocationProviderClient(baseContext)
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
//   Все что ниже - темный лес, но нужен он для запуска сервиса и отображения его в фореграунд.
    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }
        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            10,
            nIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm tracker running")
            .setContentIntent(pIntent).build()
        startForeground(99, notification)
    }


    private fun initLocation(){
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdates(){
//        locProvider.requestLocationUpdates()
    }

    companion object {
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
    }
}