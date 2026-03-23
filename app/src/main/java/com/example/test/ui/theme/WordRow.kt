package com.example.test

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ヘッダー
@Composable
fun WordTableHeader() {
    val tableHeaderBg = Color(0xFF1E3A8A)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tableHeaderBg)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", Modifier.width(45.dp), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("日本語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
        Text("韓国語", Modifier.weight(1f), fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}

// メイン行
@Composable
fun WordRow(
    index: Int,
    word: Word,
    fontSize: TextUnit,
    onJpClick: () -> Unit,
    onKrClick: () -> Unit
) {
    val maskColor = Color(0xFFADB5BD)

    val jpAlpha by animateFloatAsState(
        targetValue = if (word.jpHide) 1f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    val krAlpha by animateFloatAsState(
        targetValue = if (word.krHide) 1f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(0.5.dp, Color(0xFFE9ECEF))
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {

        // No列
        Box(
            modifier = Modifier
                .width(45.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (index + 1).toString(),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // 日本語列（垂直位置パーフェクト版）
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onJpClick() }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 上にルビを表示
                if (word.ruby.isNotEmpty()) {
                    Text(
                        text = word.ruby,
                        fontSize = (fontSize.value * 0.45).sp,
                        color = Color.Gray
                    )
                }

                // 中央に日本語本体
                Text(
                    text = word.jp,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // 下に透明なルビを置いてバランスを取る
                if (word.ruby.isNotEmpty()) {
                    Text(
                        text = word.ruby,
                        fontSize = (fontSize.value * 0.45).sp,
                        color = Color.Transparent
                    )
                }
            }

            // 非表示マスク
            if (jpAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(3.dp)
                        .background(maskColor.copy(alpha = jpAlpha))
                )
            }
        }

        // 韓国語列
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onKrClick() }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word.kr,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // 非表示マスク
            if (krAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(3.dp)
                        .background(maskColor.copy(alpha = krAlpha))
                )
            }
        }
    }
}
