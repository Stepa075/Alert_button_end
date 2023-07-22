package com.stepa0751.alertbuttonend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.stepa0751.alertbuttonend.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//      Запускаем нашу функцию-слушатель нажатий из onCreate, так как она не запустится, если этого не сделать
        onBottomNavClicks()
    }

//    Слушатель нажатия на кнопки меню внизу экрана
    private fun onBottomNavClicks(){
        binding.bNaw.setOnItemSelectedListener {
            when(it.itemId){
                R.id.id_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.id_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

}