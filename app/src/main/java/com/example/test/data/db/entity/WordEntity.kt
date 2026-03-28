package com.example.test.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val japanese: String,       // 日本語単語
    val reading: String,        // ルビ(ひらがな)
    val meaning: String,        // 日本語の意味
    val korean: String,         // 韓国語
    val chinese: String,        // 中国語
    val english: String,        // 英語
    val level: String,          // N5~N1
    val createdAt: Long = System.currentTimeMillis(), // DBに追加した日時
    val updatedAt: Long = System.currentTimeMillis()  // 最終更新日時
)
