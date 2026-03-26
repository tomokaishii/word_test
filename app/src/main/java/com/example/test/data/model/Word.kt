package com.example.test.data.model

/**
 * 単語データモデル
 */
data class Word(
    val id: Int,
    val jp: String,
    val ruby: String = "",
    val kr: String,
    val jpHide: Boolean = false,
    val krHide: Boolean = false
)