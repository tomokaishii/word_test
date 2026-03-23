package com.example.test

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🌟 名前を元の「WordTableHeader」に戻しました
@Composable
fun WordTableHeader() {
    val tableHeaderBg = Color(0xFF1E3A8A)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tableHeaderBg)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", Modifier.width(45.dp), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}

// 🌟 名前を元の「WordRow」に戻しました（枠固定フィルター実装）
@Composable
fun WordRow(
    word: Word,
    fontSize: TextUnit,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    val maskColor = Color(0xFFADB5BD)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFFE9ECEF))
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = word.id.toString(), modifier = Modifier.width(45.dp), fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)

        // 日本語列
        Box(
            modifier = Modifier.weight(1f).clickable { onJpClick() }.padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                word.jp.forEach { char ->
                    val charStr = char.toString()
                    val isKanji = charStr.matches(Regex("[\\u4E00-\\u9FA6]"))
                    if (isKanji && word.ruby.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = word.ruby, fontSize = (fontSize.value * 0.5).sp, color = Color.Gray)
                            Text(text = charStr, fontSize = fontSize, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        Text(text = charStr, fontSize = fontSize, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
            if (word.jpHide) {
                Box(modifier = Modifier.matchParentSize().padding(3.dp).background(maskColor))
            }
        }

        // 韓国語列
        Box(
            modifier = Modifier.weight(1f).clickable { onKrClick() }.padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = word.kr, fontSize = fontSize, fontWeight = FontWeight.Bold, color = Color.Black)

            if (word.krHide) {
                Box(modifier = Modifier.matchParentSize().padding(3.dp).background(maskColor))
            }
        }
    }
}

// 🌟 ActionButtons の定義（MainActivity 118行目のエラー対策）
@Composable
fun ActionButtons(
    onAddClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    // 空でも定義さえあればエラーは消えます
}