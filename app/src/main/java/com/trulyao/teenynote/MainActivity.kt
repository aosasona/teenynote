package com.trulyao.teenynote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

private lateinit var path: String;

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RootView(applicationContext)
        }
    }

}