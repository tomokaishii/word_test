package com.example.test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 単語数（左端）と文字サイズ切り替えボタン（右端）を同列に配置するコンポーネント
 * 🌟 全体の文字サイズ設定に連動するように修正しました。
 */
@Composable
fun FontSizeAndCountRow(
    wordCount: Int,
    currentFontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit
) {
    // 🌟 全体の設定値（currentFontSize）を基準に、ラベルやボタンのサイズを比率計算
    val labelFontSize = (currentFontSize.value * 0.55).sp
    val buttonFontSize = (currentFontSize.value * 0.5).sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // --- [左端] 単語数表示 ---
        Text(
            text = "単語数: $wordCount",
            fontSize = labelFontSize, // 🌟 連動
            fontWeight = FontWeight.Bold,
            color = Color(0xFF495057)
        )

        // --- [右端] 文字サイズ切り替え（ボタン風） ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "文字サイズ:",
                fontSize = labelFontSize, // 🌟 連動
                color = Color.Gray
            )
            
            // 「小」と「大」のボタン表示
            listOf(20.sp to "小", 25.sp to "大").forEach { (size, label) ->
                val active = currentFontSize == size
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) Color(0xFF228BE6) else Color(0xFFE9ECEF))
                        .clickable { onFontSizeChange(size) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = buttonFontSize, // 🌟 連動
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else Color(0xFF495057)
                    )
                }
            }
        }
    }
}
