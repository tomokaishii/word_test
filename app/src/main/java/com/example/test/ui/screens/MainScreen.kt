package com.example.test.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.R
import com.example.test.ui.components.*
import com.example.test.viewmodel.MainViewModel

/**
 * アプリのメイン画面
 */
@Composable
fun MainScreen(vm: MainViewModel) {
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
        // --- 固定ヘッダー ---
        Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA))) {

                LevelTabs(vm.currentLevel, vm.fontSize.sp) { level ->
                    vm.updateLevel(level)
                }

                CategorySelector(
                    currentCategory = vm.currentCategory,
                    categories = vm.categories,
                    fontSize = vm.fontSize.sp,
                    onCategorySelected = { category ->
                        vm.updateCategory(category)
                    }
                )

                Spacer(Modifier.height(10.dp))

                MainActionButtons(
                    onShuffle = { vm.shuffle() },
                    onReset = { vm.loadWords() },
                    onAllJpToggle = { vm.toggleAllJp() },
                    onAllKrToggle = { vm.toggleAllKr() },
                    isAllJpHidden = vm.allJpHidden,
                    isAllKrHidden = vm.allKrHidden,
                    fontSize = vm.fontSize.sp
                )

                ZundamonPlayerArea(
                    isPlaying = vm.isPlaying,
                    currentSpeed = vm.playbackSpeed,
                    selectedDescription = vm.selectedDescription,
                    currentWord = vm.wordList.getOrNull(vm.currentPlayingIndex),
                    fontSize = vm.fontSize.sp,
                    onDescriptionChange = { desc -> vm.selectedDescription = desc },
                    onPlayPause = { vm.togglePlay() },
                    onSpeedChange = { speed -> vm.playbackSpeed = speed },
                    onNext = { vm.next() },
                    onPrev = { vm.prev() }
                )

                FontSizeAndCountRow(
                    wordCount = vm.wordList.size,
                    currentFontSize = vm.fontSize.sp,
                    onFontSizeChange = { newSize ->
                        vm.fontSize = newSize.value.toInt()
                    }
                )

                Spacer(Modifier.height(4.dp))
                WordTableHeader(vm.fontSize.sp)
            }
        }

        // --- スクロールリスト ---
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
