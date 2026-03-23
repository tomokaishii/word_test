package com.example.test.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .zIndex(10f) // 🌟 これで手前に浮かせる
    ) {
        Column {
            // メインボタン
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                shape = if (expanded) RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp) else RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentCategory, fontSize = 18.sp, color = Color.White)
                    Text(text = if (expanded) "▲" else "▼", fontSize = 14.sp, color = Color.White)
                }
            }

            // 浮き出るリスト部分
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    color = Color.White,
                    shadowElevation = 8.dp // 影をつけて浮遊感を出す
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(1.dp, Color(0xFF1E3A8A)),
                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                            .padding(8.dp)
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        categories.forEach { category ->
                            val isSelected = category == currentCategory
                            TextButton(
                                onClick = {
                                    onCategorySelected(category)
                                    expanded = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (isSelected) Color(0xFFF0F4FF) else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF333333)
                                )
                            }
                            if (category != categories.last()) {
                                HorizontalDivider(color = Color(0xFFE9ECEF), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}