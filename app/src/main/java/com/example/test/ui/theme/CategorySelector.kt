package com.example.test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun CategorySelector(
    currentCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .zIndex(100f) // 🌟 他の要素より確実に前面に
    ) {
        // メインの表示エリア（ボタン）
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp) // 🌟 ボタンを小さく
                .clickable { expanded = !expanded },
            color = Color(0xFF1E3A8A),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentCategory, fontSize = 14.sp, color = Color.White)
                Text(text = if (expanded) "▲" else "▼", fontSize = 12.sp, color = Color.White)
            }
        }

        // 🌟 DropdownMenu を使うことで、下のコンテンツを押し下げずに「浮かせる」
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f) // 画面幅に合わせる
                .background(Color.White)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, fontSize = 14.sp) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (category == currentCategory) Color(0xFF1E3A8A) else Color.Black
                    )
                )
            }
        }
    }
}
