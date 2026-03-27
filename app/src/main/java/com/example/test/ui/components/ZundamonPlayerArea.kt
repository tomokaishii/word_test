package com.example.test.ui.components

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.ui.screens.LocalFontSizeProvider
import com.example.test.ui.screens.LocalTextMeasurer

/**
 * ずんだもんプレイヤーエリア
 * 🌟 モード切り替え（説明/単語/例文）に応じて、渡されたテキストを表示します。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZundamonPlayerArea(
    isPlaying: Boolean,
    currentSpeed: Float,
    selectedDescription: String,
    displayTextJp: String, // 🌟 追加: 表示用の日本語
    displayTextKr: String, // 🌟 追加: 表示用の韓国語
    displayRuby: String,   // 🌟 追加: 表示用のルビ
    onDescriptionChange: (String) -> Unit,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    val fontSizeProvider = LocalFontSizeProvider.current
    val fontSize = fontSizeProvider()
    
    var expanded by remember { mutableStateOf(false) } 
    var isControlsVisible by remember { mutableStateOf(false) } 
    var showRuby by remember { mutableStateOf(false) } 

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isControlsVisible = !isControlsVisible },
                    modifier = Modifier.size(32.dp).background(Color(0xFFF1F3F5), CircleShape)
                ) {
                    Text(if (isControlsVisible) "︽" else "︾", color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(text = "音声ガイド", fontSize = (fontSize.value * 0.55).sp, fontWeight = FontWeight.Bold, color = Color(0xFF495057))
            }

            if (isControlsVisible) {
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = selectedDescription, fontSize = (fontSize.value * 0.5).sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    }
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("単語帳の説明", "単語の発音", "例文の発音").forEach { desc ->
                            DropdownMenuItem(
                                text = { Text(text = desc, fontSize = (fontSize.value * 0.5).sp) },
                                onClick = { onDescriptionChange(desc); expanded = false },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🌟 渡されたdisplayTextJpを表示
                    KanjiMarkerArea(
                        text = displayTextJp,
                        ruby = displayRuby,
                        showRuby = showRuby
                    )
                    Spacer(Modifier.height(12.dp))
                    // 🌟 渡されたdisplayTextKrを表示
                    Text(
                        text = displayTextKr,
                        fontSize = (fontSize.value * 0.7).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C757D)
                    )

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Text(text = "ふりがな：", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Surface(
                                onClick = { showRuby = !showRuby },
                                modifier = Modifier.height(26.dp),
                                color = if (showRuby) Color(0xFF228BE6) else Color(0xFFADB5BD),
                                shape = RoundedCornerShape(6.dp),
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                    Text(text = if (showRuby) "ON" else "OFF", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Surface(onClick = onPrev, modifier = Modifier.size(44.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(10.dp), shadowElevation = 4.dp) {
                        Box(contentAlignment = Alignment.Center) { Text("◀", color = Color.White, fontSize = 16.sp) }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(onClick = onPlayPause, modifier = Modifier.size(54.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(12.dp), shadowElevation = 6.dp) {
                        Box(contentAlignment = Alignment.Center) { Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 24.sp) }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(onClick = onNext, modifier = Modifier.size(44.dp), color = Color(0xFF2ED573), shape = RoundedCornerShape(10.dp), shadowElevation = 4.dp) {
                        Box(contentAlignment = Alignment.Center) { Text("▶", color = Color.White, fontSize = 16.sp) }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth().height(34.dp).background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp)).padding(3.dp)) {
                    val speeds = listOf(0.8f to "ゆっくり", 1.0f to "ふつう", 1.2f to "はやい")
                    speeds.forEach { (speed, label) ->
                        val active = currentSpeed == speed
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(if (active) Color(0xFF2ED573) else Color.Transparent).clickable { onSpeedChange(speed) }, contentAlignment = Alignment.Center) {
                            Text(text = label, color = if (active) Color.White else Color(0xFF6C757D), fontSize = (fontSize.value * 0.45).sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
            Text("VOICEVOX:ずんだもん", modifier = Modifier.fillMaxWidth().padding(top = 6.dp), textAlign = TextAlign.End, fontSize = 9.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun KanjiMarkerArea(text: String, ruby: String, showRuby: Boolean) {
    val fontSizeProvider = LocalFontSizeProvider.current
    val textMeasurer = LocalTextMeasurer.current
    val density = LocalDensity.current
    val textStyle = remember(fontSizeProvider()) { TextStyle(fontSize = fontSizeProvider(), fontWeight = FontWeight.Bold, platformStyle = PlatformTextStyle(includeFontPadding = false), textAlign = TextAlign.Center) }
    val rubyTextStyle = remember(fontSizeProvider()) { TextStyle(fontSize = (fontSizeProvider().value * 0.4).sp, fontWeight = FontWeight.Bold, color = Color(0xFF228BE6), textAlign = TextAlign.Center) }
    val annotatedString = remember(text) { buildAnnotatedString { text.forEach { char -> withStyle(SpanStyle(fontWeight = if (char in '\u4e00'..'\u9faf') FontWeight.ExtraBold else FontWeight.Bold)) { append(char) } } } }

    Spacer(
        modifier = Modifier.fillMaxWidth().height(with(density) { (fontSizeProvider().value * 2.5).sp.toDp() }).drawWithCache {
            val layoutResult = textMeasurer.measure(text = annotatedString, style = textStyle, constraints = Constraints(maxWidth = size.width.toInt()))
            val rubyParts = ruby.split(",")
            val measuredRubyParts = if (showRuby && ruby.isNotEmpty()) { rubyParts.map { part -> if (part.trim().isNotEmpty()) textMeasurer.measure(text = part.trim(), style = rubyTextStyle) else null } } else emptyList()
            val xOffset = (size.width - layoutResult.size.width) / 2f
            val yOffset = (size.height - layoutResult.size.height) / 0.7f

            onDrawBehind {
                var kanjiCounter = 0
                text.forEachIndexed { i, char ->
                    if (char in '\u4e00'..'\u9faf') {
                        val rect = layoutResult.getBoundingBox(i)
                        val currentRubyLayout = measuredRubyParts.getOrNull(kanjiCounter)
                        if (showRuby && currentRubyLayout != null) {
                            val ry = (yOffset + rect.top - (rect.height * 0.2f)).toFloat()
                            val rx = (xOffset + rect.left + (rect.width - currentRubyLayout.size.width) / 2f).toFloat()
                            drawText(textLayoutResult = currentRubyLayout, topLeft = Offset(x = rx, y = ry))
                        }
                        kanjiCounter++
                    }
                }
                drawText(textLayoutResult = layoutResult, topLeft = Offset(x = xOffset.toFloat(), y = yOffset.toFloat()))
            }
        }
    )
}
