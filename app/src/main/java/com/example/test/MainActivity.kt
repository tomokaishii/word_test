package com.example.test

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
    var currentPlayingIndex by remember { mutableIntStateOf(0) }

    val descriptions = listOf("単語帳の説明", "単語の発音", "例文の発音")
    var selectedDescription by remember { mutableStateOf(descriptions[0]) }

    val wordList = remember { mutableStateListOf<Word>() }
    val allJpHidden by remember { derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } } }
    val allKrHidden by remember { derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } } }

    val wordListFromXml = remember(currentLevel, currentCategory) {
        try {
            val lv = currentLevel.lowercase()
            val ct = currentCategory
            val jpId = res.getIdentifier("${lv}_${ct}_jp", "array", packageName)
            val krId = res.getIdentifier("${lv}_${ct}_kr", "array", packageName)
            val rubyId = res.getIdentifier("${lv}_${ct}_ruby", "array", packageName)

            if (jpId == 0 || krId == 0) emptyList<Word>()
            else {
                val jpArr = res.getStringArray(jpId)
                val krArr = res.getStringArray(krId)
                val rubyArr = if (rubyId != 0) res.getStringArray(rubyId) else Array(jpArr.size) { "" }
                jpArr.indices.map { i ->
                    Word(id = i + 1, jp = jpArr[i], ruby = rubyArr.getOrElse(i) { "" }, kr = krArr.getOrElse(i) { "" })
                }
            }
        } catch (e: Exception) { emptyList<Word>() }
    }

    LaunchedEffect(wordListFromXml) {
        wordList.clear()
        wordList.addAll(wordListFromXml)
        currentPlayingIndex = 0
    }

    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    fun playAudio() {
        mediaPlayer.value?.release()
        val audioResName = when (selectedDescription) {
            "単語帳の説明" -> "audio_description"
            "単語の発音" -> "${currentLevel.lowercase()}_${currentCategory}_word_${currentPlayingIndex + 1}"
            "例文の発音" -> "${currentLevel.lowercase()}_${currentCategory}_example_${currentPlayingIndex + 1}"
            else -> "audio_description"
        }
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer.value = MediaPlayer.create(context, audioId).apply {
                playbackParams = playbackParams.setSpeed(playbackSpeed)
                setOnCompletionListener { isPlaying = false }
                start()
                isPlaying = true
            }
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying && mediaPlayer.value == null) playAudio()
        else if (!isPlaying) mediaPlayer.value?.pause()
        else mediaPlayer.value?.start()
    }

    DisposableEffect(Unit) { onDispose { mediaPlayer.value?.release() } }

    var fontSize by remember { mutableStateOf(25.sp) }

    // --- レイアウト設計：全体をColumnにし、リスト部分だけスクロールさせる ---
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).statusBarsPadding()) {

        // --- 上部：固定ヘッダー（zIndexを活用してメニューを浮かせる） ---
        Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA))) {
                LevelTabs(levels, currentLevel) { currentLevel = it }

                // カテゴリー選択（独立したBoxで配置し、下のコンテンツを押し下げないようにする）
                Box(modifier = Modifier.fillMaxWidth().zIndex(11f)) {
                    CategorySelector(currentCategory, categories) { currentCategory = it }
                }

                // 下の要素との距離を最適化
                Spacer(modifier = Modifier.height(10.dp))

                MainActionButtons(
                    onShuffle = { val s = wordList.shuffled(); wordList.clear(); wordList.addAll(s) },
                    onReset = { wordList.clear(); wordList.addAll(wordListFromXml) },
                    onAllJpToggle = {
                        val target = !allJpHidden
                        val newList = wordList.map { it.copy(jpHide = target) }
                        wordList.clear(); wordList.addAll(newList)
                    },
                    onAllKrToggle = {
                        val target = !allKrHidden
                        val newList = wordList.map { it.copy(krHide = target) }
                        wordList.clear(); wordList.addAll(newList)
                    },
                    jpLabel = if (allJpHidden) "日本語 全表示" else "日本語 全非表示",
                    krLabel = if (allKrHidden) "韓国語 全表示" else "韓国語 全非表示"
                )

                ZundamonPlayerArea(
                    isPlaying = isPlaying,
                    currentSpeed = playbackSpeed,
                    selectedDescription = selectedDescription,
                    descriptions = descriptions,
                    currentWord = wordList.getOrNull(currentPlayingIndex),
                    onDescriptionChange = { selectedDescription = it },
                    onPlayPause = { isPlaying = !isPlaying },
                    onSpeedChange = { playbackSpeed = it },
                    onNext = { if (currentPlayingIndex < wordList.size - 1) { currentPlayingIndex++; playAudio() } },
                    onPrev = { if (currentPlayingIndex > 0) { currentPlayingIndex--; playAudio() } }
                )

                FontSizeAndCountRow(count = wordList.size, currentFontSize = fontSize, onFontSizeChange = { fontSize = it })
                Spacer(Modifier.height(4.dp))
                WordTableHeader()
            }
        }

        // --- 下部：スクロール可能な単語リスト ---
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            itemsIndexed(items = wordList, key = { _, word -> word.id }) { index, word ->
                WordRow(
                    index = index,
                    word = word,
                    fontSize = fontSize,
                    onJpClick = { wordList[index] = word.copy(jpHide = !word.jpHide) },
                    onKrClick = { wordList[index] = word.copy(krHide = !word.krHide) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MainActionButtons(onShuffle: () -> Unit, onReset: () -> Unit, onAllJpToggle: () -> Unit, onAllKrToggle: () -> Unit, jpLabel: String, krLabel: String) {
    Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAllJpToggle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA5252)), shape = RoundedCornerShape(8.dp)) { Text(jpLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            Button(onClick = onAllKrToggle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228BE6)), shape = RoundedCornerShape(8.dp)) { Text(krLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onReset, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAB005)), shape = RoundedCornerShape(8.dp)) { Text("リセット", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            Button(onClick = onShuffle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20C997)), shape = RoundedCornerShape(8.dp)) { Text("シャッフル", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZundamonPlayerArea(isPlaying: Boolean, currentSpeed: Float, selectedDescription: String, descriptions: List<String>, currentWord: Word?, onDescriptionChange: (String) -> Unit, onPlayPause: () -> Unit, onSpeedChange: (Float) -> Unit, onNext: () -> Unit, onPrev: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var isControlsVisible by remember { mutableStateOf(true) }

    Card(modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp).fillMaxWidth().zIndex(5f), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = if (isControlsVisible) Arrangement.SpaceBetween else Arrangement.End) {
                if (isControlsVisible) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = selectedDescription, onValueChange = {}, readOnly = true, label = { Text("単語帳の説明：", fontSize = 9.sp) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2ED573), unfocusedBorderColor = Color.LightGray), textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold), modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            descriptions.forEach { desc -> DropdownMenuItem(text = { Text(desc, fontSize = 11.sp) }, onClick = { onDescriptionChange(desc); expanded = false }) }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = { isControlsVisible = !isControlsVisible }, modifier = Modifier.size(32.dp).background(Color(0xFFF1F3F5), CircleShape)) {
                    Text(if (isControlsVisible) "︾" else "︽", color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isControlsVisible) {
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp)).border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(14.dp)).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = currentWord?.jp ?: "再生準備完了", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212529))
                    Spacer(Modifier.height(4.dp))
                    Text(text = currentWord?.kr ?: "---", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6C757D))
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Surface(onClick = onPrev, modifier = Modifier.size(44.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(10.dp), shadowElevation = 4.dp) { Box(contentAlignment = Alignment.Center) { Text("◀", color = Color.White, fontSize = 16.sp) } }
                    Spacer(Modifier.width(24.dp))
                    Surface(onClick = onPlayPause, modifier = Modifier.size(54.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(12.dp), shadowElevation = 6.dp) { Box(contentAlignment = Alignment.Center) { Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 24.sp) } }
                    Spacer(Modifier.width(24.dp))
                    Surface(onClick = onNext, modifier = Modifier.size(44.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(10.dp), shadowElevation = 4.dp) { Box(contentAlignment = Alignment.Center) { Text("▶", color = Color.White, fontSize = 16.sp) } }
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().height(34.dp).background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp)).padding(3.dp)) {
                    val speeds = listOf(0.8f to "ゆっくり", 1.0f to "ふつう", 1.2f to "はやい")
                    speeds.forEach { (speed, label) ->
                        val active = currentSpeed == speed
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(if (active) Color(0xFF2ED573) else Color.Transparent).clickable { onSpeedChange(speed) }, contentAlignment = Alignment.Center) { Text(label, color = if (active) Color.White else Color(0xFF6C757D), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold) }
                    }
                }
            }
            Text("VOICEVOX:ずんだもん", modifier = Modifier.fillMaxWidth().padding(top = 6.dp), textAlign = TextAlign.End, fontSize = 9.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FontSizeAndCountRow(count: Int, currentFontSize: TextUnit, onFontSizeChange: (TextUnit) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text("単語数: $count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("文字: ", fontSize = 12.sp, color = Color.Gray)
            listOf(20.sp, 25.sp).forEach { size ->
                val active = currentFontSize == size
                Text(text = if (size == 20.sp) "小" else "大", modifier = Modifier.padding(horizontal = 4.dp).border(1.dp, if (active) Color.Black else Color.LightGray, RoundedCornerShape(4.dp)).clickable { onFontSizeChange(size) }.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (active) Color.Black else Color.Gray)
            }
        }
    }
}
