package com.stepa0751.alertbuttonend.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepa0751.alertbuttonend.R
import com.stepa0751.alertbuttonend.databinding.FragmentMainBinding


class MainFragment : Fragment() {
//    Создаем переменную binding
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем binding c инфлейтером, который пришел нам в onCreateView см. выше
        binding = FragmentMainBinding.inflate(inflater, container, false)
        // полцчаем доступ ко всем элементам разметки
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =  MainFragment()
    }
}