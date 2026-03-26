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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R

/**
 * 学習レベル選択タブ
 */
@Composable
fun LevelTabs(
    currentLevel: String,
    fontSize: TextUnit,
    onLevelSelected: (String) -> Unit
) {
    val levels = stringArrayResource(R.array.levels_array).toList()

    if (levels.isEmpty()) {
        val errorMsg = stringArrayResource(R.array.levels_array_err).firstOrNull() ?: "Error"
        Log.e("LevelTabs", errorMsg)
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF495057)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(1.dp).height(50.dp).background(Color.White.copy(alpha = 0.5f)))
            }

            val isSelected = level.trim() == currentLevel.trim()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(if (isSelected) Color(0xFF1E3A8A) else Color.Transparent)
                    .clickable { if (!isSelected && level.isNotBlank()) onLevelSelected(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level,
                    color = Color.White,
                    fontSize = (fontSize.value * 0.65).sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )
                if (isSelected) {
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF228BE6)).align(Alignment.BottomCenter))
                }
            }
        }
    }
}
