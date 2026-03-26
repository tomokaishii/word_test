package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.test.ui.screens.MainScreen
import com.example.test.viewmodel.MainViewModel

/**
 * 【MainActivity】
 * アプリの入り口です。UIの構成は MainScreen に委譲しています。
 */
class MainActivity : ComponentActivity() {
    // 画面の状態を保持する ViewModel を取得
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 画面端まで描画
        setContent { 
            MaterialTheme { 
                // 整理された ui.screens パッケージの MainScreen を呼び出す
                MainScreen(viewModel) 
            } 
        }
    }
}
