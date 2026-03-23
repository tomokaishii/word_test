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

@Composable
fun ActionButtons(
    wordList: List<Word>,
    onShuffle: () -> Unit,
    onReset: () -> Unit,
    onAllJpToggle: () -> Unit,
    onAllKrToggle: () -> Unit
) {
    // JSの _updateAllBtnLabel ロジックを再現
    val jpLabel = if (wordList.all { it.jpHide }) "日本語 全表示" else "日本語 全非表示"
    val krLabel = if (wordList.all { it.krHide }) "韓国語 全表示" else "韓国語 全非表示"

    Column(modifier = Modifier.padding(15.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 日本語一括 (--btn-jp)
            Button(onClick = onAllJpToggle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA5252)),
                shape = RoundedCornerShape(12.dp)) {
                Text(jpLabel, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            // 韓国語一括 (--btn-kr)
            Button(onClick = onAllKrToggle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228BE6)),
                shape = RoundedCornerShape(12.dp)) {
                Text(krLabel, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // リセット (--btn-reset)
            Button(onClick = onReset, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAB005)),
                shape = RoundedCornerShape(12.dp)) {
                Text("リセット", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            // シャッフル (--btn-shuffle)
            Button(onClick = onShuffle, modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20C997)),
                shape = RoundedCornerShape(12.dp)) {
                Text("シャッフル", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}