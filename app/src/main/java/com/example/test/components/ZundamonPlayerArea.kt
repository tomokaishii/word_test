package com.example.test.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.Word

/**
 * ずんだもんプレイヤーエリア
 * 音声再生コントロール、再生速度変更、現在の単語表示などを行います。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZundamonPlayerArea(
    isPlaying: Boolean,
    currentSpeed: Float,
    selectedDescription: String,
    descriptions: List<String>,
    currentWord: Word?,
    onDescriptionChange: (String) -> Unit,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isControlsVisible by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 4.dp)
            .fillMaxWidth()
            .zIndex(5f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // --- [上段] メニューと表示切替 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isControlsVisible) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (isControlsVisible) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .height(45.dp), // 少し高さを広げてラベルスペースを確保
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalArrangement = Arrangement.Top // 左上に寄せる
                                ) {
                                    // 🌟 メインの選択値を少し上に持ち上げて配置
                                    Text(
                                        text = selectedDescription,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.offset(y = (-2).dp)
                                    )
                                }
                                // 下向き矢印アイコン
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        }

                        // ドロップダウンメニュー本体
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            descriptions.forEach { desc ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = desc,
                                            fontSize = 14.sp,          // 少し大きめで標準サイズ
                                            fontWeight = FontWeight.Normal, // 普通の太さ
                                            color = Color.Black        // 標準文字色
                                        )
                                    },
                                    onClick = { onDescriptionChange(desc); expanded = false },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp) // 少し余白
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                // 表示・非表示の切り替えボタン
                IconButton(
                    onClick = { isControlsVisible = !isControlsVisible },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F3F5), CircleShape)
                ) {
                    Text(
                        if (isControlsVisible) "︾" else "︽",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isControlsVisible) {
                Spacer(Modifier.height(8.dp))
                // --- [中段] 単語表示エリア ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    KanjiMarkerArea(
                        text = currentWord?.jp ?: "再生準備完了",
                        fontSize = 18.sp,
                        markerColor = Color(0xFF228BE6)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = currentWord?.kr ?: "---",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C757D)
                    )
                }
                
                Spacer(Modifier.height(10.dp))
                
                // --- [下段] 再生コントロール ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onPrev,
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("◀", color = Color.White, fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(
                        onClick = onPlayPause,
                        modifier = Modifier.size(54.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 6.dp) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 24.sp)
                        }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(
                        onClick = onNext,
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("▶", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(Modifier.height(10.dp))
                
                // --- [最下段] 再生速度切り替え ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    val speeds = listOf(0.8f to "ゆっくり", 1.0f to "ふつう", 1.2f to "はやい")
                    speeds.forEach { (speed, label) ->
                        val active = currentSpeed == speed
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) Color(0xFF2ED573) else Color.Transparent)
                                .clickable { onSpeedChange(speed) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (active) Color.White else Color(0xFF6C757D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            Text(
                "VOICEVOX:ずんだもん",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textAlign = TextAlign.End,
                fontSize = 9.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 漢字強調表示コンポーネント
 */
@Composable
fun KanjiMarkerArea(text: String, fontSize: androidx.compose.ui.unit.TextUnit, markerColor: Color) {
    Row(verticalAlignment = Alignment.Bottom) {
        text.forEach { char ->
            val kanji = char in '\u4e00'..'\u9faf'
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, 
                modifier = Modifier.padding(horizontal = 0.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width((fontSize.value * 0.7).dp)
                        .height(4.dp)
                        .background(if (kanji) markerColor else Color.Transparent)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = char.toString(),
                    fontSize = fontSize,
                    fontWeight = if (kanji) FontWeight.Bold else FontWeight.Normal,
                    color = Color(0xFF212529)
                )
            }
        }
    }
}
