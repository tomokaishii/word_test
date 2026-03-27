package com.example.test.data.model

/**
 * 【Word: 単語データモデル】
 * アプリ内で扱う「単語」の最小単位を定義します。
 * 音声ガイドの3つのモード（説明・単語・例文）に対応するテキストを全て保持します。
 */
data class Word(
    val id: Int,
    
    // 1. 「単語の発音」モード用
    val jp: String,
    val kr: String,
    val ruby: String = "",
    
    // 2. 「単語帳の説明」モード用 (手動でXMLに追加可能)
    val guideJp: String = "",
    val guideKr: String = "",
    
    // 3. 「例文の発音」モード用
    val exJp: String = "",
    val exKr: String = "",

    val jpHide: Boolean = false,
    val krHide: Boolean = false
)
