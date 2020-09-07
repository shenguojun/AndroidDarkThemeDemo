package com.shengj.androiddarkthemedemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_TIME
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        updateMode()
    }

    fun switchLightOrDark(view: View) {
        when (AppCompatDelegate.getDefaultNightMode()) {
            MODE_NIGHT_AUTO_TIME,
            MODE_NIGHT_AUTO_BATTERY,
            MODE_NIGHT_UNSPECIFIED -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
        }
        updateMode()
    }

    private fun updateMode() {
        btn_mode.text = getCurrentNightMode()
    }

    private fun getCurrentNightMode(): String {
        val mode = "Current Mode: "
        return when (AppCompatDelegate.getDefaultNightMode()) {
            MODE_NIGHT_YES -> return mode + "DARK"
            MODE_NIGHT_NO -> return mode + "LIGHT"
            MODE_NIGHT_FOLLOW_SYSTEM -> return mode + "FOLLOW SYSTEM"
            MODE_NIGHT_AUTO_BATTERY -> return mode + "AUTO BATTERY"
            MODE_NIGHT_UNSPECIFIED -> return mode + "UNSPECIFIED"
            MODE_NIGHT_AUTO_TIME -> return mode + "AUTO TIME"
            else -> mode
        }
    }
}