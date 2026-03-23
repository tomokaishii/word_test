package com.example.test

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.components.CategorySelector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val res = context.resources
    val packageName = context.packageName

    // 1. 状態管理
    val levels = stringArrayResource(R.array.levels_array).toList()
    var currentLevel by remember { mutableStateOf(levels.firstOrNull() ?: "N5") }

    val categories = remember(currentLevel) {
        val catId = res.getIdentifier("categories_${currentLevel.lowercase()}", "array", packageName)
        if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")
    }
    var currentCategory by remember(categories) { mutableStateOf(categories.first()) }

    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // 2. データの取得 (ルビ対応)
    val wordListFromXml: List<Word> = remember(currentLevel, currentCategory) {
        try {
            val lv = currentLevel.lowercase()
            val ct = currentCategory
            val jpId = res.getIdentifier("${lv}_${ct}_jp", "array", packageName)
            val krId = res.getIdentifier("${lv}_${ct}_kr", "array", packageName)
            val rubyId = res.getIdentifier("${lv}_${ct}_ruby", "array", packageName)

            if (jpId == 0 || krId == 0) emptyList()
            else {
                val jpArr = res.getStringArray(jpId)
                val krArr = res.getStringArray(krId)
                val rubyArr = if (rubyId != 0) res.getStringArray(rubyId) else Array(jpArr.size) { "" }
                jpArr.indices.map { i ->
                    Word(id = i + 1, jp = jpArr[i], ruby = rubyArr.getOrElse(i) { "" }, kr = krArr.getOrElse(i) { "" })
                }
            }
        } catch (e: Exception) { emptyList() }
    }

    var wordList by remember(wordListFromXml) { mutableStateOf(wordListFromXml) }
    var fontSize by remember { mutableStateOf(25.sp) } // CSSの --font-table: 25px に合わせる

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // --- CSSのヘッダー部分の再現 ---
            LevelTabs(levels, currentLevel) { currentLevel = it }

            Spacer(modifier = Modifier.height(70.dp)) // セレクター用の隙間

            // --- CSSの .audio-controls-row (ずんだもんエリア) ---
            ZundamonPlayerArea(
                isPlaying = isPlaying,
                currentSpeed = playbackSpeed,
                onPlayPause = { isPlaying = !isPlaying },
                onSpeedChange = { playbackSpeed = it }
            )

            // --- CSSの .btn-grid (メイン操作ボタン) ---
            MainActionButtons(
                onShuffle = { wordList = wordList.shuffled() },
                onReset = { wordList = wordListFromXml },
                onAllJpToggle = {
                    val anyVisible = wordList.any { !it.jpHide }
                    wordList = wordList.map { it.copy(jpHide = anyVisible) }
                },
                onAllKrToggle = {
                    val anyVisible = wordList.any { !it.krHide }
                    wordList = wordList.map { it.copy(krHide = anyVisible) }
                },
                // ボタンの文字を actions.js の _updateAllBtnLabel ロジックで動的に決定
                jpLabel = if (wordList.all { it.jpHide }) "日本語 全表示" else "日本語 全非表示",
                krLabel = if (wordList.all { it.krHide }) "韓国語 全表示" else "韓国語 全非表示"
            )

            // --- CSSの .word-count-badge 内のフォント操作 ---
            FontSizeAndCountRow(
                count = wordList.size,
                currentFontSize = fontSize,
                onFontSizeChange = { fontSize = it }
            )

            Spacer(Modifier.height(10.dp))
            WordTableHeader()

            wordList.forEach { word ->
                WordRow(
                    word = word,
                    fontSize = fontSize,
                    onJpClick = { wordList = wordList.map { if (it.id == word.id) it.copy(jpHide = !it.jpHide) else it } },
                    onKrClick = { wordList = wordList.map { if (it.id == word.id) it.copy(krHide = !it.krHide) else it } }
                )
            }
            Spacer(Modifier.height(100.dp))
        }

        // --- CSSの #pagination-container (最前面) ---
        Column(modifier = Modifier.fillMaxWidth().padding(top = 55.dp)) {
            CategorySelector(currentCategory, categories) { currentCategory = it }
        }
    }
}

// --- 以下、CSSのクラスに基づいたコンポーネント定義 ---

@Composable
fun MainActionButtons(
    onShuffle: () -> Unit,
    onReset: () -> Unit,
    onAllJpToggle: () -> Unit,
    onAllKrToggle: () -> Unit,
    jpLabel: String,
    krLabel: String
) {
    // CSSの .btn-grid (2カラム)
    Column(modifier = Modifier.padding(15.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 日本語一括 (var(--btn-jp): #fa5252)
            Button(onClick = onAllJpToggle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA5252)),
                shape = RoundedCornerShape(12.dp)) {
                Text(jpLabel, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            // 韓国語一括 (var(--btn-kr): #228be6)
            Button(onClick = onAllKrToggle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228BE6)),
                shape = RoundedCornerShape(12.dp)) {
                Text(krLabel, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // リセット (var(--btn-reset): #fab005)
            Button(onClick = onReset, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAB005)),
                shape = RoundedCornerShape(12.dp)) {
                Text("リセット", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            // シャッフル (var(--btn-shuffle): #20c997)
            Button(onClick = onShuffle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20C997)),
                shape = RoundedCornerShape(12.dp)) {
                Text("シャッフル", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}

@Composable
fun ZundamonPlayerArea(
    isPlaying: Boolean,
    currentSpeed: Float,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    // CSSの .audio-controls-row
    Card(
        modifier = Modifier.padding(15.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 再生ボタン (CSS: .play-btn)
                Button(
                    onClick = onPlayPause,
                    modifier = Modifier.size(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED573)),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 20.sp)
                }

                Spacer(Modifier.width(10.dp))

                // 速度ボタン (CSS: .speed-btns)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf(0.8f, 1.0f, 1.2f).forEach { speed ->
                        val active = currentSpeed == speed
                        Button(
                            onClick = { onSpeedChange(speed) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (active) Color(0xFF228BE6) else Color(0xFFF1F3F5)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("${speed}x", color = if (active) Color.White else Color(0xFFADB5BD), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            // ずんだもんクレジット (CSS: .voice-credit)
            Text(
                "VOICEVOX:ずんだもん",
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun FontSizeAndCountRow(
    count: Int,
    currentFontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit
) {
    // CSSの .word-count-badge
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("単語数: $count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("文字: ", fontSize = 14.sp, color = Color.Gray)
            // CSSの body.font-small / font-large 切り替えを模倣
            listOf(20.sp, 25.sp).forEach { size ->
                val active = currentFontSize == size
                Text(
                    text = if (size == 20.sp) "小" else "大",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .border(1.dp, if (active) Color.Black else Color.LightGray, RoundedCornerShape(4.dp))
                        .clickable { onFontSizeChange(size) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.Black else Color.Gray
                )
            }
        }
    }
}