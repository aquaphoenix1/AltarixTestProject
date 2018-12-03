package com.example.aqua_phoenix.altarixtestapplication

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

fun changeSplashScreenFragment(
    @IdRes containerId: Int, oldFragment: Fragment,
    addFragment: Fragment,
    fragmentManager: FragmentManager
) {
    fragmentManager
        .beginTransaction()
        .remove(oldFragment)
        .add(containerId, addFragment)
        .commit()
}

fun deleteFragment(removedFragment: Fragment, fragmentManager: FragmentManager) {
    fragmentManager.beginTransaction()
        .remove(removedFragment)
        .commit()
}

fun replaceFragment(@IdRes containerId: Int, addFragment: Fragment, fragmentManager: FragmentManager) {
    fragmentManager.beginTransaction()
        //.replace(containerId, addFragment)
        .add(containerId, addFragment)
        .addToBackStack(null)
        .commit()
}

fun addFragment(
    @IdRes containerId: Int,
    addFragment: Fragment,
    fragmentManager: FragmentManager
) {
    fragmentManager.beginTransaction()
        .replace(containerId, addFragment)
        .commit()
}