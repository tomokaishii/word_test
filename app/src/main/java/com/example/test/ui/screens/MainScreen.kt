package com.example.test.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.R
import com.example.test.ui.components.*
import com.example.test.viewmodel.MainViewModel

/**
 * 🌟 爆速化の核心: 全体で共有する「環境変数 (CompositionLocal)」を定義
 */
// 現在のフォントサイズを取得するための関数を保持するプロバイダー
val LocalFontSizeProvider = compositionLocalOf { { 25.sp } }

// テキスト計算エンジンを保持するプロバイダー
@OptIn(ExperimentalTextApi::class)
val LocalTextMeasurer = staticCompositionLocalOf<TextMeasurer> { error("No TextMeasurer") }

/**
 * アプリのメイン画面
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun MainScreen(vm: MainViewModel) {
    val textMeasurer = rememberTextMeasurer()
    val levelsArray = stringArrayResource(R.array.levels_array)
    val defaultLevel = remember(levelsArray) { levelsArray.firstOrNull() ?: "N5" }

    LaunchedEffect(Unit) {
        if (vm.categories.isEmpty()) {
            vm.updateLevel(defaultLevel)
        }
    }

    CompositionLocalProvider(
        LocalFontSizeProvider provides { vm.fontSize.sp },
        LocalTextMeasurer provides textMeasurer
    ) {
        MainLayout(vm)
    }
}

/**
 * メインレイアウト
 */
@Composable
private fun MainLayout(vm: MainViewModel) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        // --- 固定ヘッダーエリア ---
        Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA))) {
                
                // 1. レベルタブ
                LevelTabs(vm.currentLevel) { level -> vm.updateLevel(level) }
                
                // 2. カテゴリー選択
                CategorySelector(
                    currentCategory = vm.currentCategory,
                    categories = vm.categories,
                    onCategorySelected = { category -> vm.updateCategory(category) }
                )
                
                Spacer(Modifier.height(10.dp))

                // 3. 操作ボタン
                MainActionButtons(
                    onShuffle = { vm.shuffle() },
                    onReset = { vm.loadWords() },
                    onAllJpToggle = { vm.toggleAllJp() },
                    onAllKrToggle = { vm.toggleAllKr() },
                    isAllJpHidden = vm.allJpHidden,
                    isAllKrHidden = vm.allKrHidden
                )

                // 4. プレイヤー
                // 現在のモードに応じた表示テキスト（日本語、韓国語、ルビ）を一括取得
                val (displayJp, displayKr, rubyText) = vm.getCurrentDisplayText()

                ZundamonPlayerArea(
                    isPlaying = vm.isPlaying,
                    currentSpeed = vm.playbackSpeed,
                    selectedDescription = vm.selectedDescription,
                    displayTextJp = displayJp,
                    displayTextKr = displayKr,
                    displayRuby = rubyText,
                    onDescriptionChange = { desc -> vm.selectedDescription = desc },
                    onPlayPause = { vm.togglePlay() },
                    onSpeedChange = { speed -> vm.playbackSpeed = speed },
                    onNext = { vm.next() },
                    onPrev = { vm.prev() }
                )

                // 5. ステータス行
                FontSizeAndCountRow(
                    wordCount = vm.wordList.size,
                    onFontSizeChange = { newSize: TextUnit -> vm.fontSize = newSize.value.toInt() }
                )
                
                Spacer(Modifier.height(4.dp))
                
                // 6. ヘッダー
                WordTableHeader()
            }
        }

        // --- スクロールリストエリア ---
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            itemsIndexed(
                items = vm.wordList,
                key = { _, word -> word.id },
                contentType = { _, _ -> "word_row" }
            ) { index, word ->
                WordRow(
                    index = index,
                    word = word,
                    onJpClick = { vm.toggleWordJp(index) },
                    onKrClick = { vm.toggleWordKr(index) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
