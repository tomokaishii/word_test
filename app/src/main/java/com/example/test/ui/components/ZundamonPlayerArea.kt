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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.data.model.Word
import com.example.test.ui.screens.LocalFontSizeProvider
import com.example.test.ui.screens.LocalTextMeasurer

/**
 * ずんだもんプレイヤーエリア
 * 再生コントロール、速度調整、現在の単語表示を行うメインコンポーネントです。
 * 🌟 爆速化: LocalFontSizeProvider を使用し、再構築を最小限に抑えています。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZundamonPlayerArea(
    isPlaying: Boolean,
    currentSpeed: Float,
    selectedDescription: String,
    descriptions: List<String> = listOf("単語帳の説明","単語の発音", "例文の発音"),
    currentWord: Word?,
    onDescriptionChange: (String) -> Unit,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    // CompositionLocal からフォントサイズ取得関数を取得
    val fontSizeProvider = LocalFontSizeProvider.current
    val fontSize = fontSizeProvider()
    
    // UI状態管理
    var expanded by remember { mutableStateOf(false) } // ドロップダウンの開閉状態
    var isControlsVisible by remember { mutableStateOf(true) } // コントロールパネルの表示・非表示

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
            // ヘッダー：モード選択と表示切り替えボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isControlsVisible) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (isControlsVisible) {
                    // 再生コンテンツ（単語/例文など）の切り替えドロップダウン
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
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
                                Text(
                                    text = selectedDescription,
                                    fontSize = (fontSize.value * 0.6).sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            descriptions.forEach { desc ->
                                DropdownMenuItem(
                                    text = { Text(text = desc, fontSize = (fontSize.value * 0.6).sp) },
                                    onClick = { onDescriptionChange(desc); expanded = false },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                // エリアの開閉ボタン
                IconButton(
                    onClick = { isControlsVisible = !isControlsVisible },
                    modifier = Modifier.size(32.dp).background(Color(0xFFF1F3F5), CircleShape)
                ) {
                    Text(if (isControlsVisible) "︽" else "︾", color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 表示エリア（表示設定がONの場合のみ）
            if (isControlsVisible) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🌟 爆速描画エンジン：漢字に自動でマーカー（点）を付与
                    KanjiMarkerArea(
                        text = currentWord?.jp ?: "再生準備完了",
                        markerColor = Color(0xFF228BE6)
                    )
                    Spacer(Modifier.height(4.dp))
                    // 韓国語（訳語）の表示
                    Text(
                        text = currentWord?.kr ?: "---",
                        fontSize = (fontSize.value * 0.7).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C757D)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // 再生コントロール：前へ、再生/一時停止、次へ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                // 再生速度の切り替えバー
                Row(modifier = Modifier.fillMaxWidth().height(34.dp).background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp)).padding(3.dp)) {
                    val speeds = listOf(0.8f to "ゆっくり", 1.0f to "ふつう", 1.2f to "はやい")
                    speeds.forEach { (speed, label) ->
                        val active = currentSpeed == speed
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(if (active) Color(0xFF2ED573) else Color.Transparent).clickable { onSpeedChange(speed) }, contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                color = if (active) Color.White else Color(0xFF6C757D),
                                fontSize = (fontSize.value * 0.45).sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            // クレジット
            Text("VOICEVOX:ずんだもん", modifier = Modifier.fillMaxWidth().padding(top = 6.dp), textAlign = TextAlign.End, fontSize = 9.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * 漢字強調表示コンポーネント（再生エリア用）
 * 🌟 パフォーマンス最適化：TextMeasurer と drawWithCache を使用し、直接 Canvas に描画します。
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun KanjiMarkerArea(text: String, markerColor: Color) {
    val fontSizeProvider = LocalFontSizeProvider.current
    val textMeasurer = LocalTextMeasurer.current
    val density = LocalDensity.current
    
    // 現在のフォントサイズに基づいたスタイル
    val textStyle = remember(fontSizeProvider()) {
        TextStyle(
            fontSize = fontSizeProvider(),
            fontWeight = FontWeight.Bold,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            textAlign = TextAlign.Center
        )
    }
    
    // 漢字のみ FontWeight を変えた装飾テキストを生成
    val annotatedString = remember(text) {
        buildAnnotatedString {
            text.forEach { char ->
                val kanji = char in '\u4e00'..'\u9faf'
                withStyle(SpanStyle(fontWeight = if (kanji) FontWeight.ExtraBold else FontWeight.Bold)) {
                    append(char)
                }
            }
        }
    }

    // 描画処理
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(density) { (fontSizeProvider().value * 1.6).sp.toDp() })
            .drawWithCache {
                // テキストのレイアウト情報を取得
                val layoutResult = textMeasurer.measure(
                    text = annotatedString,
                    style = textStyle,
                    constraints = Constraints(maxWidth = size.width.toInt())
                )
                // 中央配置用の座標計算
                val xOffset = (size.width - layoutResult.size.width) / 2f
                val yOffset = (size.height - layoutResult.size.height) / 2f
                val markerHeight = 3.dp.toPx()
                val markerGap = 2.dp.toPx()

                onDrawBehind {
                    // 各文字をチェックし、漢字の場合はその上にマーカー（四角形）を描画
                    text.forEachIndexed { i, char ->
                        if (char in '\u4e00'..'\u9faf') {
                            val rect = layoutResult.getBoundingBox(i)
                            drawRect(
                                color = markerColor,
                                topLeft = Offset(xOffset + rect.left + rect.width * 0.1f, yOffset + rect.top - markerHeight - markerGap),
                                size = Size(rect.width * 0.8f, markerHeight)
                            )
                        }
                    }
                    // テキスト自体を描画
                    drawText(layoutResult, topLeft = Offset(xOffset, yOffset))
                }
            }
    )
}
