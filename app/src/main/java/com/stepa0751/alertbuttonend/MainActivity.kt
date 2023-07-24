package com.stepa0751.alertbuttonend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stepa0751.alertbuttonend.databinding.ActivityMainBinding
import com.stepa0751.alertbuttonend.fragments.MainFragment
import com.stepa0751.alertbuttonend.fragments.MapFragment
import com.stepa0751.alertbuttonend.fragments.SettingsFrag
import com.stepa0751.alertbuttonend.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//      Запускаем нашу функцию-слушатель нажатий из onCreate, так как она не запустится, если этого не сделать
        onBottomNavClicks()
        openFragment(MainFragment.newInstance())
    }

//    Слушатель нажатия на кнопки меню внизу экрана
    private fun onBottomNavClicks(){
        binding.bNaw.setOnItemSelectedListener {
            when(it.itemId){
//        обычные фрагменты запускаются вот так:
                R.id.id_home -> openFragment(MainFragment.newInstance())
                R.id.id_map -> openFragment(MapFragment.newInstance())
//        а фрагменты с настройками вот так:
                R.id.id_settings -> openFragment(SettingsFrag())
            }
            true
        }
    }

}