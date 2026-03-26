package com.example.test.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * メインアクションボタン群
 */
@Composable
fun MainActionButtons(
    onShuffle: () -> Unit,
    onReset: () -> Unit,
    onAllJpToggle: () -> Unit,
    onAllKrToggle: () -> Unit,
    isAllJpHidden: Boolean,
    isAllKrHidden: Boolean,
    fontSize: TextUnit
) {
    val jpLabel = if (isAllJpHidden) "日本語 全表示" else "日本語 全非表示"
    val krLabel = if (isAllKrHidden) "韓国語 全表示" else "韓国語 全非表示"
    val buttonFontSize = (fontSize.value * 0.5).sp

    Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAllJpToggle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA5252)), shape = RoundedCornerShape(8.dp)) {
                Text(jpLabel, fontSize = buttonFontSize, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(onClick = onAllKrToggle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228BE6)), shape = RoundedCornerShape(8.dp)) {
                Text(krLabel, fontSize = buttonFontSize, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onReset, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAB005)), shape = RoundedCornerShape(8.dp)) {
                Text("リセット", fontSize = buttonFontSize, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(onClick = onShuffle, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20C997)), shape = RoundedCornerShape(8.dp)) {
                Text("シャッフル", fontSize = buttonFontSize, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
