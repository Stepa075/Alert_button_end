package com.stepa0751.alertbuttonend.fragments


import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stepa0751.alertbuttonend.databinding.FragmentMapBinding
import com.stepa0751.alertbuttonend.location.LocationService
import com.stepa0751.alertbuttonend.utils.DialogManager
import com.stepa0751.alertbuttonend.utils.checkPermission
import com.stepa0751.alertbuttonend.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        binding = FragmentMapBinding.inflate(inflater, container, false)
        // полцчаем доступ ко всем элементам разметки
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        }else{
            activity?.startService(Intent(activity, LocationService::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()
    }

    //  функция начальной настройки карт ОСМ. Запускать ее нужно ДО надувания инфлейта!!!
    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    //  Настройка отображения карт......... Посмотреть документацию!!!
    private fun initOsm() = with(binding) {
        map.controller.setZoom(20.0)
        val myLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(myLocProvider, map)
        myLocOverlay.enableMyLocation()
        myLocOverlay.enableFollowLocation()
        myLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(myLocOverlay)
        }
    }
//  Нихрена эта функция не срабатывает. Запрос на разрешения не появляется.
    private fun registerPermissions() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {

            if (it[ACCESS_FINE_LOCATION] == true) {
                initOsm()
                checkLocationEnabled()
            } else {
                showToast("You have not given permission for location tracking. The app don't work!")

            }
        }
    }
//   Функция выбора пермиссинов в зависимости от версии андроида
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
        if (checkPermission(ACCESS_FINE_LOCATION)
            && checkPermission(ACCESS_BACKGROUND_LOCATION)) {
            initOsm()
            checkLocationEnabled()
        } else {
            pLauncher.launch(arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

//   Если меньше 10 версии андроида
    private fun checkPermissionBefore10() {
        if (checkPermission(ACCESS_FINE_LOCATION)) {
            initOsm()
            checkLocationEnabled()
        } else {
            pLauncher.launch(arrayOf(ACCESS_FINE_LOCATION))
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
        fun newInstance() = MapFragment()
    }
}
