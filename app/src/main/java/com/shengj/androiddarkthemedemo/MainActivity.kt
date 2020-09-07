package com.shengj.androiddarkthemedemo

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.round_more_horiz)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_light -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            R.id.action_dark -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            R.id.action_follow -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            R.id.action_battery -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
            R.id.light_indicator -> if (isNightMode()) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }
        }
        return true
    }

    /**
     * Check whether night mode is on or not
     *
     * @return if night mode on return true else false
     */
    fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

}