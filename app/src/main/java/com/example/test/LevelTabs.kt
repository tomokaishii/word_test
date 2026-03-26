package com.example.test

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 学習レベル選択タブ (JLPT N5 〜 N1)
 * ユーザーが学習したいレベルを選択するためのUIコンポーネントです。
 * 
 * @param currentLevel 現在選択されているレベル名
 * @param onLevelSelected レベルが選択されたときに実行されるコールバック関数
 */
@Composable
fun LevelTabs(
    currentLevel: String,
    onLevelSelected: (String) -> Unit
) {
    // XMLからレベルリスト（N1~N5）を直接取得
    val levels = stringArrayResource(R.array.levels_array).toList()

    // 🌟 データが空の場合、arrays_level.xml の levels_array_err からエラー文言を取得してログ出力
    if (levels.isEmpty()) {

        // arrays_level.xml の levels_array_err からエラーメッセージを取得
        val errorMsg = stringArrayResource(R.array.levels_array_err).firstOrNull()

        // nullチェック（念のため）
        if (errorMsg != null) {
            Log.e("LevelTabs", errorMsg)
        }

        return
    }

    // タブ全体を横に並べるコンテナ
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF495057)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 各レベルをループで描画
        levels.forEachIndexed { index, level ->
            
            // 1mm相当（1dp）の白い縦線を追加
            if (index > 0) {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }

            val isSelected = level.trim() == currentLevel.trim()

            // 各タブの表示とクリック領域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(if (isSelected) Color(0xFF1E3A8A) else Color.Transparent)
                    .clickable {
                        if (!isSelected && level.isNotBlank()) {
                            onLevelSelected(level)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF228BE6))
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
