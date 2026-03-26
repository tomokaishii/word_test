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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.model.Word

/**
 * 漢字判定ロジック
 */
private fun isKanji(char: Char): Boolean = char in '\u4e00'..'\u9faf'

/**
 * 漢字強調表示コンポーネント
 */
@Composable
fun KanjiMarkerText(text: String, fontSize: TextUnit, markerColor: Color) {
    val textStyle = remember(fontSize) {
        TextStyle(
            fontSize = fontSize,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            textAlign = TextAlign.Center
        )
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.wrapContentHeight()
    ) {
        text.forEach { char ->
            val kanji = isKanji(char)
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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = char.toString(),
                    style = textStyle.copy(fontWeight = if (kanji) FontWeight.Bold else FontWeight.Normal)
                )
            }
        }
    }
}

/**
 * 単語リストの1行
 */
@Composable
fun WordRow(
    index: Int,
    word: Word,
    fontSize: TextUnit,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    val jpAlpha by animateFloatAsState(if (word.jpHide) 1f else 0f, tween(200), label = "jpAlpha")
    val krAlpha by animateFloatAsState(if (word.krHide) 1f else 0f, tween(200), label = "krAlpha")

    val baseStyle = remember(fontSize) {
        TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(0.5.dp, Color(0xFFE9ECEF))
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Box(Modifier.width(40.dp).fillMaxHeight(), Alignment.Center) {
            Text(text = "${index + 1}", fontSize = (fontSize.value * 0.5).sp, color = Color.Gray)
        }

        Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onJpClick), Alignment.Center) {
            KanjiMarkerText(text = word.jp, fontSize = fontSize, markerColor = Color(0xFF228BE6))
            if (jpAlpha > 0f) {
                Box(Modifier.matchParentSize().padding(2.dp).background(Color(0xFFADB5BD).copy(alpha = jpAlpha)))
            }
        }

        Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onKrClick), Alignment.Center) {
            Text(
                text = word.kr,
                style = baseStyle
            )
            if (krAlpha > 0f) {
                Box(Modifier.matchParentSize().padding(2.dp).background(Color(0xFFADB5BD).copy(alpha = krAlpha)))
            }
        }
    }
}

@Composable
fun WordTableHeader(fontSize: TextUnit) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF1E3A8A)).padding(vertical = 10.dp)) {
        val headerFs = (fontSize.value * 0.55).sp
        Text("No", Modifier.width(40.dp), color = Color.White, fontSize = headerFs, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), color = Color.White, fontSize = headerFs, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), color = Color.White, fontSize = headerFs, textAlign = TextAlign.Center)
    }
}
