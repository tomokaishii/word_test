package com.example.test

// 🌟 ルビ対応版
data class Word(
    val id: Int,
    val jp: String,
    val ruby: String = "",
    val kr: String,
    val jpHide: Boolean = false,
    val krHide: Boolean = false
)