package com.zk.spookynavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.zk.spookynavigation.ui.NavigationTestTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationTestTheme {
                MainScreen()
            }
        }
    }
}

