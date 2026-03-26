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
                LevelTabs(vm.currentLevel) { vm.updateLevel(it) }
                
                // 2. カテゴリー選択ドロップダウン
                CategorySelector(vm.currentCategory, vm.categories) { vm.updateCategory(it) }
                
                Spacer(Modifier.height(10.dp))

                // 3. 一括操作ボタン群
                MainActionButtons(
                    onShuffle = { vm.shuffle() },
                    onReset = { vm.loadWords() },
                    onAllJpToggle = { vm.toggleAllJp() },
                    onAllKrToggle = { vm.toggleAllKr() },
                    jpLabel = if (vm.allJpHidden) "日本語 全表示" else "日本語 全非表示",
                    krLabel = if (vm.allKrHidden) "韓国語 全表示" else "韓国語 全非表示"
                )

                // 4. ずんだもんプレイヤー
                ZundamonPlayerArea(
                    isPlaying = vm.isPlaying,
                    currentSpeed = vm.playbackSpeed,
                    selectedDescription = vm.selectedDescription,
                    descriptions = listOf("単語帳の説明", "単語の発音", "例文の発音"),
                    currentWord = vm.wordList.getOrNull(vm.currentPlayingIndex),
                    onDescriptionChange = { vm.selectedDescription = it },
                    onPlayPause = { vm.togglePlay() },
                    onSpeedChange = { playbackSpeed -> vm.playbackSpeed = playbackSpeed },
                    onNext = { vm.next() },
                    onPrev = { vm.prev() }
                )

                // 5. 文字サイズ調整
                FontSizeAndCountRow(vm.wordList.size, vm.fontSize.sp) { vm.fontSize = it.value.toInt() }
                
                Spacer(Modifier.height(4.dp))
                WordTableHeader()
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
