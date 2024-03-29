package com.stepa0751.alertbuttonend.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.stepa0751.alertbuttonend.R
import com.stepa0751.alertbuttonend.utils.showToast

class SettingsFrag : PreferenceFragmentCompat(){
    private lateinit var timePref: Preference
    private lateinit var colorPref: Preference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)
        init()
    }

//    как найти элемент в настройках, который обозначается на экране
//    добавлять можно один и тот же слушатель для нескольких меню

    private fun init(){
        timePref = findPreference("update_time_key")!!
        colorPref = findPreference("color_key")!!
        val changeListener = onChangeListener()
        timePref.onPreferenceChangeListener = changeListener

        colorPref.onPreferenceChangeListener = changeListener
        initPrefs()
    }
//    пишем слушатель изменений в настройках
    private fun onChangeListener(): Preference.OnPreferenceChangeListener {
    return Preference.OnPreferenceChangeListener { pref, value ->
            when(pref.key){
                "update_time_key" -> onTimeChange(value.toString())
                    // здесь меняем цвет иконки лямбдой, даже не создавая отдельную функцию
                "color_key" -> pref.icon?.setTint(Color.parseColor(value.toString()))
            }
        true
    }
}
//   функция смены текста в менюхе настроек на экране фрагмента сразу же
    private fun onTimeChange(value: String){
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref.title.toString().substringBefore(":")
        timePref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
    }



//   настраиваем показ сохраненных цифр и всего прочего при запуске экрана с настройками
    private fun initPrefs(){
        val pref = timePref.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref.title
        timePref.title = "$title: ${nameArray[valueArray.indexOf(pref?.getString("update_time_key", "5000") )]}"

        val color = pref?.getString("color_key", "#0636C3")
        colorPref.icon?.setTint(Color.parseColor(color))
    }

}