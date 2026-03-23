package com.example.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZundamonManual(
    isPlaying: Boolean,
    playbackSpeed: Float,
    onPlayPauseClick: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    Card(
        modifier = Modifier.padding(15.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 再生ボタン
                Button(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ED573)),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 20.sp)
                }
                Spacer(Modifier.width(10.dp))
                // 速度選択
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf(0.8f, 1.0f, 1.2f).forEach { speed ->
                        val active = playbackSpeed == speed
                        Button(
                            onClick = { onSpeedSelected(speed) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (active) Color(0xFF228BE6) else Color(0xFFF1F3F5)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("${speed}x", color = if (active) Color.White else Color(0xFFADB5BD), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Text(
                "VOICEVOX:ずんだもん",
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}