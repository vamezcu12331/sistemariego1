package com.example.sistemariego1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.sistemariego1.ui.screens.MainScreen
import com.example.sistemariego1.ui.theme.SistemaRiegoTheme
import com.example.sistemariego1.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaRiegoTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
