package com.example.test

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 学習レベル選択タブ (JLPT N5 〜 N1)
 *
 * 各レベルの間に1mm相当（約1dp）の白い縦線を追加し、区切りを明確にしました。
 */
@Composable
fun LevelTabs(
    levels: List<String>,
    currentLevel: String,
    onLevelSelected: (String) -> Unit
) {
    if (levels.isEmpty()) {
        Log.e("LevelTabs", "表示するレベルリストが空です。リソースを確認してください。")
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF495057)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            // 🌟 1mm相当（1dp）の白い縦線を、項目の間に挿入
            if (index > 0) {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp) // 完全に端まで伸ばさず、少し短くしてスタイリッシュに
                        .background(Color.White.copy(alpha = 0.5f)) // 少し透過させて馴染ませる
                )
            }

            val isSelected = level.trim() == currentLevel.trim()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(if (isSelected) Color(0xFF1E3A8A) else Color.Transparent)
                    .clickable {
                        if (!isSelected && level.isNotBlank()) {
                            onLevelSelected(level)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF228BE6))
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}