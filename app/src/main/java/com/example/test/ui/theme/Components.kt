package com.example.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- [CSS] 色の定義 ---
val BgColor = Color(0xFFF8F9FA)        // 背景
val TabBg = Color(0xFF495057)         // タブ背景
val CategoryNavy = Color(0xFF1E3A8A)   // カテゴリーボタン（紺）
val BtnJp = Color(0xFFFA5252)          // 赤
val BtnKr = Color(0xFF228BE6)          // 青
val BtnReset = Color(0xFFFAB005)       // 黄
val BtnShuffle = Color(0xFF20C997)     // 緑
val BorderColor = Color(0xFFE9ECEF)    // テーブル枠線
val TableHeaderBg = Color(0xFF1E3A8A)
val TextGray = Color(0xFFADB5BD)

// --- [CSS] 共通パーツ（SmallBtn） ---
@Composable
fun SmallBtn(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}