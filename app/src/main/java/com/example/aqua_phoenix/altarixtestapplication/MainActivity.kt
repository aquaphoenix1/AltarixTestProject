package com.example.aqua_phoenix.altarixtestapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.aqua_phoenix.altarixtestapplication.map.initializeMap
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val init = async(CommonPool) {
            initializeMap()
        }

        launch(CommonPool){
            init.await()
            replaceFragment((MapFragment as Fragment)::class.java, supportFragmentManager)
        }
    }
}