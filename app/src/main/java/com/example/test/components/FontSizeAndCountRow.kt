package com.example.test.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * フォントサイズと単語数の表示・操作エリア
 */
@Composable
fun FontSizeAndCountRow(
    count: Int,
    currentFontSize: TextUnit,
    onFontSizeChange: (TextUnit) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "単語数: $count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("文字: ", fontSize = 12.sp, color = Color.Gray)
            listOf(20.sp, 25.sp).forEach { size ->
                val active = currentFontSize == size
                Text(
                    text = if (size == 20.sp) "小" else "大",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .border(
                            1.dp,
                            if (active) Color.Black else Color.LightGray,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onFontSizeChange(size) }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.Black else Color.Gray
                )
            }
        }
    }
}
