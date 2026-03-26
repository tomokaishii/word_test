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
 * アプリケーションの開始地点（エントリポイント）です。
 * 
 * Android開発の設計原則（Single Responsibility Principle）に基づき、
 * ここではUIの初期化とMainScreenの呼び出しのみを行い、
 * 複雑な描画ロジックや状態管理は他のクラスに委譲しています。
 */
class MainActivity : ComponentActivity() {
    /**
     * MainViewModel のインスタンスを生成・保持します。
     * viewModels() デリゲートを使用することで、画面回転時などの構成変更時でも
     * 状態（State）が保持されるようになります。
     */
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        /**
         * エッジ・トゥ・エッジ（画面端まで描画）を有効にします。
         * ステータスバーやナビゲーションバーの背後まで背景を広げ、モダンなUIを実現します。
         */
        enableEdgeToEdge()

        /**
         * Jetpack Compose を使用してUIを設定します。
         * MaterialTheme でアプリ全体のテーマを適用し、MainScreen に ViewModel を渡して描画を開始します。
         */
        setContent { 
            MaterialTheme { 
                MainScreen(viewModel) 
            } 
        }
    }
}
