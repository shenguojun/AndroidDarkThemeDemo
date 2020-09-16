package com.shengj.androiddarkthemedemo

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @description print activity life cycle
 *
 * @author shengj (shengj@rd.netease.com)
 * @date 9/15/20 18:54
 */
class LifeCyclePrinter(private val TAG: String) : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy")
    }
}