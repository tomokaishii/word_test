package com.example.test.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R
import com.example.test.ui.screens.LocalFontSizeProvider

/**
 * 学習レベル選択タブ
 * 🌟 爆速化: LocalFontSizeProviderからサイズ取得関数を取得し、描画フェーズで解決。
 */
@Composable
fun LevelTabs(
    currentLevel: String,
    onLevelSelected: (String) -> Unit
) {
    // 🌟 修正箇所: LocalFontSize ではなく LocalFontSizeProvider を使用
    val fontSizeProvider = LocalFontSizeProvider.current
    
    val levels = stringArrayResource(R.array.levels_array)
    val levelsList = remember(levels) { levels.toList() }

    if (levelsList.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF495057)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levelsList.forEachIndexed { index, level ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.3f)))
            }
            val isSelected = level.trim() == currentLevel.trim()
            Box(
                modifier = Modifier
                    .weight(1f).height(35.dp)
                    .background(if (isSelected) Color(0xFF1E3A8A) else Color.Transparent)
                    .clickable { if (!isSelected && level.isNotBlank()) onLevelSelected(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level,
                    color = Color.White,
                    // 🌟 ラムダを呼び出して最新のサイズを取得
                    fontSize = (fontSizeProvider().value * 0.6).sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )
                if (isSelected) {
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF228BE6)).align(Alignment.BottomCenter))
                }
            }
        }
    }
}
