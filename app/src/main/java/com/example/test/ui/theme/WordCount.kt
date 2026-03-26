package com.example.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 5. 単語数（閉じカッコを修正し、Modifierを追加して使いやすくしました）
@Composable
fun WordCount(wordCount: Int, modifier: Modifier = Modifier) {
    Text(
        text = "単語数: $wordCount",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = modifier
    )
}