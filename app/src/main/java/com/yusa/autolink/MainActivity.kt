package com.yusa.autolink


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.yusa.autolink.navigation.AutoLinkNavGraph
import com.yusa.autolink.ui.theme.AutoLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoLinkTheme {
                val navController = rememberNavController()
                AutoLinkNavGraph(navController)
            }
        }
    }
}
