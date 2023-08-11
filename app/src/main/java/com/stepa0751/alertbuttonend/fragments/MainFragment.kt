package com.stepa0751.alertbuttonend.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.stepa0751.alertbuttonend.R
import com.stepa0751.alertbuttonend.databinding.FragmentMainBinding
import com.stepa0751.alertbuttonend.location.LocationService
import com.stepa0751.alertbuttonend.utils.DialogManager
import com.stepa0751.alertbuttonend.utils.checkPermission
import com.stepa0751.alertbuttonend.utils.showToast


class MainFragment : Fragment() {

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    //    Создаем переменную binding
    private lateinit var binding: FragmentMainBinding
    private var isServiceRunning = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем binding c инфлейтером, который пришел нам в onCreateView см. выше
        binding = FragmentMainBinding.inflate(inflater, container, false)
        // полцчаем доступ ко всем элементам разметки
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()

    }


     fun setOnClicks() = with(binding){
        val listener = onClicks()
     startStop.setOnClickListener(listener)
    }

    private fun onClicks(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                R.id.start_stop -> startStopService()
            }
        }
    }

    private fun startStopService(){
        if(!isServiceRunning){
            startLocService()
        }else{
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.startStop.setImageResource(R.drawable.ic_disalarm_green)
        }
        isServiceRunning = !isServiceRunning
    }

    private fun checkServiceState(){
        isServiceRunning = LocationService.isRunning
        if(isServiceRunning){
            binding.startStop.setImageResource(R.drawable.ic_alarm_red)
        }
    }

    private fun startLocService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        }else{
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.startStop.setImageResource(R.drawable.ic_alarm_red)
    }




    private fun registerPermissions() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {

            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
//                initOsm()
                checkLocationEnabled()
            } else {
                showToast("You have not given permission for location tracking. The app don't work!")

            }
        }
    }


    private fun checkLocPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionAfter10()
        } else {
            checkPermissionBefore10()
        }
    }


    //   Если больше или равно 10 версии андроида
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAfter10() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//            initOsm()
            checkLocationEnabled()

        } else {
            pLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            )
        }
    }

    //   Если меньше 10 версии андроида
    private fun checkPermissionBefore10() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            initOsm()
            checkLocationEnabled()
        } else {
            pLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    //    Определение включен ли GPS и вызов диалог менеджера, если выключен
    private fun checkLocationEnabled(){
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isEnabled){
            DialogManager.showLocEnableDialog(activity as AppCompatActivity,
                object: DialogManager.Listener{
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                })
        }else{
            showToast("Location enabled")
        }
    }



    companion object {

        @JvmStatic
        fun newInstance() =  MainFragment()
    }
}