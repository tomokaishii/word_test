package com.example.test.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.test.data.model.Word
import com.example.test.ui.screens.LocalFontSizeProvider
import com.example.test.ui.screens.LocalTextMeasurer

/**
 * 漢字判定
 */
private fun isKanji(char: Char): Boolean = char in '\u4e00'..'\u9faf'

/**
 * 【究極爆速エンジン】
 * 🌟 状態読み取りを遅延させ、描画レイヤーのみでサイズを反映します。
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun KanjiMarkerText(text: String, markerColor: Color) {
    val fontSizeProvider = LocalFontSizeProvider.current
    val textMeasurer = LocalTextMeasurer.current
    val density = LocalDensity.current
    
    val annotatedString = remember(text) {
        buildAnnotatedString {
            text.forEach { char ->
                val kanji = isKanji(char)
                withStyle(SpanStyle(fontWeight = if (kanji) FontWeight.ExtraBold else FontWeight.Bold)) {
                    append(char)
                }
            }
        }
    }
    val kanjiIndices = remember(text) { text.indices.filter { isKanji(text[it]) } }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(density) { (fontSizeProvider().value * 1.6).sp.toDp() })
            .drawWithCache {
                val currentSize = fontSizeProvider()
                val textStyle = TextStyle(
                    fontSize = currentSize, 
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    textAlign = TextAlign.Center
                )
                
                val layoutResult = textMeasurer.measure(
                    text = annotatedString,
                    style = textStyle,
                    constraints = Constraints(maxWidth = size.width.toInt())
                )
                val xOffset = (size.width - layoutResult.size.width) / 2f
                val yOffset = (size.height - layoutResult.size.height) / 2f
                val mHeight = 3.dp.toPx()
                val mGap = 2.dp.toPx()

                val markerRects = kanjiIndices.map { i ->
                    val rect = layoutResult.getBoundingBox(i)
                    Offset(xOffset + rect.left + rect.width * 0.1f, yOffset + rect.top - mHeight - mGap) to 
                    Size(rect.width * 0.8f, mHeight)
                }

                onDrawBehind {
                    markerRects.forEach { (pos, sz) -> drawRect(color = markerColor, topLeft = pos, size = sz) }
                    drawText(layoutResult, topLeft = Offset(xOffset, yOffset))
                }
            }
    )
}

/**
 * 単語リストの1行
 */
@Composable
fun WordRow(
    index: Int,
    word: Word,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    val fontSizeProvider = LocalFontSizeProvider.current
    val jpAlpha by animateFloatAsState(if (word.jpHide) 1f else 0f, tween(100), label = "jpAlpha")
    val krAlpha by animateFloatAsState(if (word.krHide) 1f else 0f, tween(100), label = "krAlpha")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(0.5.dp, Color(0xFFE9ECEF))
            .background(Color.White)
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        Box(Modifier.width(40.dp).fillMaxHeight(), Alignment.Center) {
            Text(text = "${index + 1}", fontSize = (fontSizeProvider().value * 0.5).sp, color = Color.Gray)
        }

        Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onJpClick), Alignment.Center) {
            KanjiMarkerText(word.jp, Color(0xFF228BE6))
            if (jpAlpha > 0f) {
                Box(Modifier.matchParentSize().padding(2.dp).background(Color(0xFFADB5BD).copy(alpha = jpAlpha)))
            }
        }

        Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onKrClick), Alignment.Center) {
            Text(
                text = word.kr,
                fontSize = fontSizeProvider(),
                fontWeight = FontWeight.Bold,
                style = TextStyle(textAlign = TextAlign.Center, platformStyle = PlatformTextStyle(false)),
                modifier = Modifier.padding(vertical = 6.dp)
            )
            if (krAlpha > 0f) {
                Box(Modifier.matchParentSize().padding(2.dp).background(Color(0xFFADB5BD).copy(alpha = krAlpha)))
            }
        }
    }
}

/**
 * テーブルヘッダー
 */
@Composable
fun WordTableHeader() {
    val fontSizeProvider = LocalFontSizeProvider.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1E3A8A)).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fs = (fontSizeProvider().value * 0.55).sp
        Text("No", Modifier.width(40.dp), color = Color.White, fontSize = fs, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), color = Color.White, fontSize = fs, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), color = Color.White, fontSize = fs, textAlign = TextAlign.Center)
    }
}
