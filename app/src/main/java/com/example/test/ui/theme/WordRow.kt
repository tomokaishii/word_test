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
import androidx.compose.runtime.remember
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

private val MaskColor = Color(0xFFADB5BD)
private val BorderColor = Color(0xFFE9ECEF)
private val HeaderBg = Color(0xFF1E3A8A)

@Composable
fun WordTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().background(HeaderBg).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", Modifier.width(45.dp), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}

/**
 * 特定の文字の上にルビを振るためのコンポーネント
 */
@Composable
fun RubyText(
    fullText: String,
    rubySource: String,
    fontSize: TextUnit
) {
    val rubyFs = (fontSize.value * 0.45).sp
    val baseStyle = TextStyle(
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        textAlign = TextAlign.Center
    )

    // ルビの解析: "父|とう,母|かあ" -> mapOf("父" to "とう", "母" to "かあ")
    // また、古い形式 "とう,かあ" (カンマ区切り) にもフォールバック対応
    val rubyMap = remember(rubySource) {
        if (rubySource.isBlank()) emptyMap()
        else {
            val map = mutableMapOf<String, String>()
            rubySource.split(",").forEach { item ->
                val parts = item.split("|")
                if (parts.size == 2) {
                    map[parts[0]] = parts[1]
                }
            }
            map
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        // もし rubySource に "|" が含まれていない場合、従来通り単語全体に対して表示
        if (!rubySource.contains("|") && rubySource.isNotBlank()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = rubySource, style = baseStyle.copy(fontSize = rubyFs, color = Color.Gray), maxLines = 1)
                Text(text = fullText, style = baseStyle.copy(fontSize = fontSize, fontWeight = FontWeight.Bold))
                Text(text = rubySource, style = baseStyle.copy(fontSize = rubyFs, color = Color.Transparent), maxLines = 1)
            }
        } else {
            // 文字単位でのルビ表示
            fullText.forEach { char ->
                val charStr = char.toString()
                val ruby = rubyMap[charStr]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = ruby ?: " ",
                        style = baseStyle.copy(
                            fontSize = rubyFs,
                            color = if (ruby == null) Color.Transparent else Color.Gray
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = charStr,
                        style = baseStyle.copy(
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = ruby ?: " ",
                        style = baseStyle.copy(fontSize = rubyFs, color = Color.Transparent),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun WordRow(
    index: Int,
    word: Word,
    fontSize: TextUnit,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    val jpAlpha by animateFloatAsState(targetValue = if (word.jpHide) 1f else 0f, animationSpec = tween(300), label = "jp")
    val krAlpha by animateFloatAsState(targetValue = if (word.krHide) 1f else 0f, animationSpec = tween(300), label = "kr")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(0.5.dp, BorderColor)
            .background(Color.White)
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        Box(modifier = Modifier.width(45.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(text = (index + 1).toString(), fontSize = 14.sp, color = Color.Gray)
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onJpClick() }, contentAlignment = Alignment.Center) {
            RubyText(
                fullText = word.jp,
                rubySource = word.ruby,
                fontSize = fontSize
            )
            if (jpAlpha > 0f) {
                Box(modifier = Modifier.matchParentSize().padding(2.dp).background(MaskColor.copy(alpha = jpAlpha)))
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onKrClick() }, contentAlignment = Alignment.Center) {
            Text(
                text = word.kr,
                style = TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
            if (krAlpha > 0f) {
                Box(modifier = Modifier.matchParentSize().padding(2.dp).background(MaskColor.copy(alpha = krAlpha)))
            }
        }
    }
}
