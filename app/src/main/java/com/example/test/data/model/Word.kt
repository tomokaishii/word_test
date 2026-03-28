package com.example.test.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 【Word: 単語データモデル】
 * アプリ内で扱う「単語」の最小単位を定義するエンティティです。
 * 
 * @param id 一意の識別番号（自動生成）。
 * @param level 日本語能力試験レベル（0:N1, 1:N2, 2:N3, 3:N4, 4:N5）。
 * @param genre ジャンル・カテゴリー（数値で管理）。
 * @param jp 日本語の表記。
 * @param ruby 振仮名（ルビ）。
 * @param translation 翻訳語のテキスト。
 * @param langType 言語タイプ（1:韓国語, 2:英語, 3:中国語）。
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val level: Int = 0,
    val genre: Int = 0,
    val jp: String,
    val ruby: String = "",
    val translation: String,
    val langType: Int = 1
)
