package com.stepa0751.alertbuttonend.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.stepa0751.alertbuttonend.MainActivity
import com.stepa0751.alertbuttonend.R

//  Создали сервис для работы в фоновом режиме... В манифесте его нужно прописать!!!
class LocationService : Service() {
//    Эта переменная нужна для того, чтобы подключаться к провайдеру GPS и получать у него данные о местоположении

    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequest: LocationRequest
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        startLocationUpdates()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
//  Переменную "работает" делаем ложь
        isRunning = false
//  Отписываеся от обновлений местоположения
        locProvider.removeLocationUpdates(locCallBack)
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

    //  Инициализация клиента доступа к подписке на местоположение
    private fun initLocation(){
        //   Создаем объект
        locRequest =  LocationRequest.create()
        //  Назначаем интервал обновления
        locRequest.interval = 5000
        //  Максимальная скорость обновления местоположения
        locRequest.fastestInterval = 5000
        //  Назначаем приоритет "Высокая точность"
        locRequest.priority = PRIORITY_HIGH_ACCURACY
        //  Инициализируем сам locProvider
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)

    }

    // Сюда приходит информация о местоположении в lResult
    private val locCallBack = object : LocationCallback(){
        override fun onLocationResult(lResult: LocationResult) {
            super.onLocationResult(lResult)
            Log.d("MyLog", " Location: ${lResult.lastLocation?.latitude} : ${lResult.lastLocation?.longitude}")
        }
    }

    //  Функция запуска слушателя местоположения, для нее нужны несколько параметров:
    // И с начала в ней идет проверка на разрешение пользователем приложению доступа к местоположению
    //  Так просит котлин, потому что он не понимает что мы уже где-то раньше проверяли это.
    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locProvider.requestLocationUpdates(
            //  Этот параметр получаем в initLocation
            locRequest,
            // Сюда будет приходить информация о нашем местоположении
            locCallBack,
            //  И лупер нужен, чтобы повторять поток запроса инфы о местоположении,
            // т.к. поток закрывается после выполнения всех команд в нем.
            Looper.myLooper()
        )
    }

    companion object {
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
    }
}