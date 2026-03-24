package com.example.test

/**
 * 単語データモデル
 *
 * @param id 一意識別子
 * @param jp 日本語表記（漢字・かな混じり）
 * @param ruby ルビ情報。特定の文字の上に振る場合は、親文字とルビをパイプとカンマで指定可能。
 *             例: "お父さん" に対して "父|とう"
 *             例: "美味しい" に対して "美|お,味|い"
 * @param kr 韓国語表記
 * @param jpHide 日本語の非表示フラグ
 * @param krHide 韓国語の非表示フラグ
 */
data class Word(
    val id: Int,
    val jp: String,
    val ruby: String = "",
    val kr: String,
    val jpHide: Boolean = false,
    val krHide: Boolean = false
)
