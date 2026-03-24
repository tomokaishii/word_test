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
 * MainActivity: UIの構築のみに集中し、ロジックはViewModelへ委譲
 */
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme { MainScreen(viewModel) } }
    }
}

@Composable
fun MainScreen(vm: MainViewModel) {
    val levels = stringArrayResource(R.array.levels_array).toList()
    
    // 初期ロード
    LaunchedEffect(Unit) {
        if (vm.categories.isEmpty()) {
            vm.updateLevel(levels.firstOrNull() ?: "N5")
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).statusBarsPadding()) {
        // --- ヘッダーエリア (固定) ---
        Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA))) {
                LevelTabs(levels, vm.currentLevel) { vm.updateLevel(it) }
                CategorySelector(vm.currentCategory, vm.categories) { vm.updateCategory(it) }
                
                Spacer(Modifier.height(10.dp))

                MainActionButtons(
                    onShuffle = { vm.shuffle() },
                    onReset = { vm.loadWords() },
                    onAllJpToggle = { vm.toggleAllJp() },
                    onAllKrToggle = { vm.toggleAllKr() },
                    jpLabel = if (vm.allJpHidden) "日本語 全表示" else "日本語 全非表示",
                    krLabel = if (vm.allKrHidden) "韓国語 全表示" else "韓国語 全非表示"
                )

                ZundamonPlayerArea(
                    isPlaying = vm.isPlaying,
                    currentSpeed = vm.playbackSpeed,
                    selectedDescription = vm.selectedDescription,
                    descriptions = listOf("単語帳の説明", "単語の発音", "例文の発音"),
                    currentWord = vm.wordList.getOrNull(vm.currentPlayingIndex),
                    onDescriptionChange = { vm.selectedDescription = it },
                    onPlayPause = { vm.togglePlay() },
                    onSpeedChange = { vm.playbackSpeed = it },
                    onNext = { vm.next() },
                    onPrev = { vm.prev() }
                )

                FontSizeAndCountRow(vm.wordList.size, vm.fontSize.sp) { vm.fontSize = it.value.toInt() }
                Spacer(Modifier.height(4.dp))
                WordTableHeader()
            }
        }

        // --- リストエリア (スクロール) ---
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
