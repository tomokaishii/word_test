package com.example.test.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * カテゴリー選択ドロップダウン
 */
@Composable
fun CategorySelector(
    currentCategory: String,
    categories: List<String>,
    fontSize: TextUnit,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayFontSize = (fontSize.value * 0.6).sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .zIndex(100f)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
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
                Text(text = currentCategory, fontSize = displayFontSize, color = Color.White)
                Text(text = if (expanded) "▲" else "▼", fontSize = (displayFontSize.value * 0.8).sp, color = Color.White)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category, fontSize = displayFontSize) },
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
