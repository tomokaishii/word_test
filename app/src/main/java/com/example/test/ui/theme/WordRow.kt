package com.example.test.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.test.Word
import androidx.compose.runtime.remember

/**
 * 漢字かどうかを判定する（Unicode: 4E00-9FAF）
 */
private fun isKanji(char: Char): Boolean = char in '\u4e00'..'\u9faf'

/**
 * 漢字の上に色付きのマーカー（空白と色）を表示するコンポーネント
 */

/**
 * 漢字の上にルビ（ひらがな）を表示するコンポーネント
 */
@Composable
fun KanjiMarkerText(
    text: String,
    rubyStr: String,
    fontSize: TextUnit,
    rubyColor: Color = Color(0xFF228BE6) // ← 追加
) {
    // ルビをカンマで分割してリスト化
    val rubies = remember(rubyStr) {
        if (rubyStr.isBlank()) emptyList() else rubyStr.split(",")
    }

    var rubyIndex = 0

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        text.forEach { char ->
            val isKanji = char in '\u4e00'..'\u9faf'

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.wrapContentWidth()
            ) {
                // --- ルビ表示エリア ---
                if (isKanji && rubyIndex < rubies.size) {
                    Text(
                        text = rubies[rubyIndex],
                        fontSize = (fontSize.value * 0.4f).sp, // メイン文字の40%のサイズ
                        color = Color(0xFF228BE6), // 青色
                        fontWeight = FontWeight.Normal,
                        lineHeight = (fontSize.value * 0.4f).sp
                    )
                    rubyIndex++ // 次の漢字のためにインデックスを進める
                } else {
                    // 漢字以外、またはルビが足りない場合は高さを確保するための空スペース
                    Spacer(modifier = Modifier.height((fontSize.value * 0.4f).dp))
                }

                // --- メインの文字（日本語） ---
                Text(
                    text = char.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}

/**
 * 単語リストの1行を表示するコンポーネント
 */
@Composable
fun WordRow(
    index: Int,
    word: Word,
    fontSize: TextUnit,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    // 表示・非表示のアニメーション設定
    val jpAlpha by animateFloatAsState(
        targetValue = if (word.jpHide) 1f else 0f,
        animationSpec = tween(300),
        label = "jpAlpha"
    )
    val krAlpha by animateFloatAsState(
        targetValue = if (word.krHide) 1f else 0f,
        animationSpec = tween(300),
        label = "krAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(0.5.dp, Color(0xFFE9ECEF))
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        // --- No (番号) ---
        Box(
            modifier = Modifier.width(45.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${index + 1}", fontSize = 14.sp, color = Color.Gray)
        }

        // --- 日本語エリア ---
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().clickable { onJpClick() },
            contentAlignment = Alignment.Center
        ) {
            // 💡 漢字の上に色付きマーカーを表示するコンポーネントに変更
            KanjiMarkerText(
                text = word.jp,
                rubyStr = word.ruby,
                fontSize = fontSize,
                rubyColor = Color.Red
            )

            // 目隠しマスク
            if (jpAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(2.dp)
                        .background(Color(0xFFADB5BD).copy(alpha = jpAlpha))
                )
            }
        }

        // --- 韓国語エリア ---
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().clickable { onKrClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word.kr,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
            // 目隠しマスク
            if (krAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(2.dp)
                        .background(Color(0xFFADB5BD).copy(alpha = krAlpha))
                )
            }
        }
    }
}

/**
 * テーブルの見出し
 */
@Composable
fun WordTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E3A8A))
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", Modifier.width(45.dp), color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
