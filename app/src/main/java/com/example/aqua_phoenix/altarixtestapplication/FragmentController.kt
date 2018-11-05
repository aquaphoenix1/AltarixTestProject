package com.example.aqua_phoenix.altarixtestapplication

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

fun replaceFragment(fragmentClass: Class<Fragment>, fragmentManager: FragmentManager){
//fun replaceFragment(fragmentClass: Class<MapFragment>, fragmentManager: FragmentManager){
    fragmentManager
        .beginTransaction()
        .replace(R.id.mainLayout, fragmentClass.newInstance())
        .commit()
}