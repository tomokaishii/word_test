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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 学習レベル選択タブ (JLPT N5 〜 N1)
 *
 * 【設計のポイント】
 * 1. XML(strings.xml)からの動的リスト生成に対応し、多言語化やレベル変更を容易にしている。
 * 2. 画面幅をレベル数で均等に割る(weight)ことで、デバイスサイズを問わず最適なレイアウトを実現。
 * 3. 公開アプリとして必須の「連打防止」や「不正データガード」を実装。
 *
 * @param levels MainActivityでリソースから取得したレベルのリスト (例: ["N5", "N4"...])
 * @param currentLevel 現在選択中のレベル文字列。この値と一致するタブが強調表示される。
 * @param onLevelSelected タブ選択時に実行される処理。データの再ロードなどをここで行う。
 */
@Composable
fun LevelTabs(
    levels: List<String>,
    currentLevel: String,
    onLevelSelected: (String) -> Unit
) {
    // 【セキュリティ/安定性ガード】
    // 外部データ(XML等)が空の場合、Rowを描画すると計算エラー(Division by zero等)の
    // リスクがあるため、早期リターンでアプリのクラッシュを未然に防ぐ。
    if (levels.isEmpty()) {
        Log.e("LevelTabs", "表示するレベルリストが空です。リソースを確認してください。")
        return
    }

    // タブ全体のコンテナ。HTML/CSS版の「--lv-bg: #495057」を再現。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF495057))
    ) {
        levels.forEach { level ->
            // 【エラー防止】
            // 文字列の前後の空白(trim)を無視して比較。
            // XML記述ミスによる「"N5 "」などの不要なスペースが原因で選択状態にならない不具合を防ぐ。
            val isSelected = level.trim() == currentLevel.trim()

            Box(
                modifier = Modifier
                    .weight(1f) // 🌟 1/n の幅を自動計算。レベルが3つでも5つでも均等に並ぶ。
                    .height(54.dp)
                    // 選択中は濃い青、未選択は透過(背景色が見える)
                    .background(if (isSelected) Color(0xFF1E3A8A) else Color.Transparent)
                    .clickable {
                        // 【UX/パフォーマンス最適化】
                        // 1. すでに選択されているタブを再度押しても何もしない（無駄なAPI呼び出しやロードを遮断）。
                        // 2. 空白のデータが混入していた場合の誤作動を防止。
                        if (!isSelected && level.isNotBlank()) {
                            try {
                                onLevelSelected(level)
                            } catch (e: Exception) {
                                // 万が一呼び出し側の処理(onLevelSelected)で例外が発生しても、
                                // UIスレッドを落とさずにログへ記録する。
                                Log.e("LevelTabs", "タブ '${level}' への切り替えに失敗しました: ${e.message}")
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // レベル名 (N5, N4...)
                Text(
                    text = level,
                    color = Color.White,
                    fontSize = 16.sp,
                    // 選択中は視認性を高めるため、極太(Black)に設定
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )

                // 【UX向上】選択中のタブを視覚的に強調する下部インジケーター(青線)
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF228BE6)) // 明るい青でアクセント
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}