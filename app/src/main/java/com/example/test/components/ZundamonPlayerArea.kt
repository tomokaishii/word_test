package com.example.test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.test.Word

/**
 * ずんだもんプレイヤーエリア
 * 音声再生コントロール、再生速度変更、現在の単語表示などを行います。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZundamonPlayerArea(
    isPlaying: Boolean,
    currentSpeed: Float,
    selectedDescription: String,
    descriptions: List<String>,
    currentWord: Word?,
    onDescriptionChange: (String) -> Unit,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isControlsVisible by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 4.dp)
            .fillMaxWidth()
            .zIndex(5f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isControlsVisible) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (isControlsVisible) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedDescription,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("単語帳の説明：", fontSize = 9.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2ED573),
                                unfocusedBorderColor = Color.LightGray
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            descriptions.forEach { desc ->
                                DropdownMenuItem(
                                    text = { Text(desc, fontSize = 11.sp) },
                                    onClick = { onDescriptionChange(desc); expanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(
                    onClick = { isControlsVisible = !isControlsVisible },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F3F5), CircleShape)
                ) {
                    Text(
                        if (isControlsVisible) "︾" else "︽",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isControlsVisible) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE9ECEF), RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentWord?.jp ?: "再生準備完了",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF212529)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = currentWord?.kr ?: "---",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C757D)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onPrev,
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("◀", color = Color.White, fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(
                        onClick = onPlayPause,
                        modifier = Modifier.size(54.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 24.sp)
                        }
                    }
                    Spacer(Modifier.width(24.dp))
                    Surface(
                        onClick = onNext,
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFF2ED573),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("▶", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    val speeds = listOf(0.8f to "ゆっくり", 1.0f to "ふつう", 1.2f to "はやい")
                    speeds.forEach { (speed, label) ->
                        val active = currentSpeed == speed
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) Color(0xFF2ED573) else Color.Transparent)
                                .clickable { onSpeedChange(speed) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (active) Color.White else Color(0xFF6C757D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            Text(
                "VOICEVOX:ずんだもん",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textAlign = TextAlign.End,
                fontSize = 9.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
