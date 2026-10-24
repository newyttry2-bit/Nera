package com.newyttry2bit.nera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.newyttry2bit.nera.ui.app.NeraApp
import com.newyttry2bit.nera.ui.theme.NeraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeraTheme {
                NeraApp()
            }
        }
    }
}
