package com.attri.kns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.attri.kns.auth_screen.NavGraph
import com.attri.kns.ui.theme.KNSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KNSTheme {
                NavGraph()
            }
        }
    }
}
