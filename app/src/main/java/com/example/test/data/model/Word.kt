package com.example.test.data.model

/**
 * 【Word: 単語データモデル】
 * 3つのモード（単語・例文・説明）のテキストと音声情報を一括管理します。
 */
data class Word(
    val id: Int,
    
    // 1. 単語モード用
    val jp: String,
    val kr: String,
    val ruby: String = "",
    val audioWord: String = "",
    
    // 2. 例文モード用
    val exJp: String = "",
    val exKr: String = "",
    val exRuby: String = "",
    val audioEx: String = "",
    
    // 3. 説明モード用 (XML: guide_jp 等から取得)
    val guideJp: String = "",
    val guideKr: String = "",
    val guideRuby: String = "",
    val audioGuide: String = "",

    val jpHide: Boolean = false,
    val krHide: Boolean = false
)
