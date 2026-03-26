package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.components.*
import com.example.test.ui.theme.WordRow
import com.example.test.ui.theme.WordTableHeader

/**
 * 【MainActivity: アプリのエントリポイント】
 * UIの構築のみに集中し、複雑なロジックや状態管理はViewModelに委譲しています。
 */
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { 
            MaterialTheme { 
                MainScreen(viewModel) 
            } 
        }
    }
}

/**
 * 【MainScreen: メイン画面のレイアウト構成】
 */
@Composable
fun MainScreen(vm: MainViewModel) {
    // 💡 初回起動時のデータロード処理
    val defaultLevel = stringArrayResource(R.array.levels_array).firstOrNull() ?: "N5"
    
    LaunchedEffect(Unit) {
        if (vm.categories.isEmpty()) {
            vm.updateLevel(defaultLevel)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        // --- [上部] 固定ヘッダーエリア ---
        Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA))) {
                
                // 1. レベル選択タブ (N1~N5)
                // 🌟 全体の文字サイズ設定を反映
                LevelTabs(vm.currentLevel, vm.fontSize.sp) { vm.updateLevel(it) }
                
                // 2. カテゴリー選択ドロップダウン
                // 🌟 全体の文字サイズ設定を反映
                CategorySelector(
                    currentCategory = vm.currentCategory,
                    categories = vm.categories,
                    fontSize = vm.fontSize.sp,
                    onCategorySelected = { vm.updateCategory(it) }
                )
                
                Spacer(Modifier.height(10.dp))

                // 3. 一括操作ボタン群
                // 🌟 全体の文字サイズ設定を反映
                MainActionButtons(
                    onShuffle = { vm.shuffle() },
                    onReset = { vm.loadWords() },
                    onAllJpToggle = { vm.toggleAllJp() },
                    onAllKrToggle = { vm.toggleAllKr() },
                    isAllJpHidden = vm.allJpHidden,
                    isAllKrHidden = vm.allKrHidden,
                    fontSize = vm.fontSize.sp
                )

                // 4. ずんだもんプレイヤー
                // 🌟 すでに vm.fontSize.sp を渡しているが、内部パーツも連動するように調整
                ZundamonPlayerArea(
                    isPlaying = vm.isPlaying,
                    currentSpeed = vm.playbackSpeed,
                    selectedDescription = vm.selectedDescription,
                    currentWord = vm.wordList.getOrNull(vm.currentPlayingIndex),
                    fontSize = vm.fontSize.sp,
                    onDescriptionChange = { vm.selectedDescription = it },
                    onPlayPause = { vm.togglePlay() },
                    onSpeedChange = { playbackSpeed -> vm.playbackSpeed = playbackSpeed },
                    onNext = { vm.next() },
                    onPrev = { vm.prev() }
                )

                // 5. 単語数表示と文字サイズ調整ボタン
                FontSizeAndCountRow(
                    wordCount = vm.wordList.size,
                    currentFontSize = vm.fontSize.sp,
                    onFontSizeChange = { vm.fontSize = it.value.toInt() }
                )
                
                Spacer(Modifier.height(4.dp))
                
                // 6. テーブルヘッダー
                // 🌟 ここにも文字サイズを反映させる
                WordTableHeader(vm.fontSize.sp)
            }
        }

        // --- [下部] スクロール可能な単語リスト ---
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            itemsIndexed(items = vm.wordList, key = { _, w -> w.id }) { index, word ->
                WordRow(
                    index = index,
                    word = word,
                    fontSize = vm.fontSize.sp,
                    onJpClick = { vm.toggleWordJp(index) },
                    onKrClick = { vm.toggleWordKr(index) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
