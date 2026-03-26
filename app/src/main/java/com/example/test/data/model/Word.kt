package com.example.test.data.model

/**
 * 【Word: 単語データモデル】
 * アプリ内で扱う「単語」の最小単位を定義するデータクラスです。
 * 
 * @param id 単語を識別するための一意の番号。LazyColumnのkeyとしても使用されます。
 * @param jp 日本語の表記。漢字・かな混じりの文字列です。
 * @param ruby 振仮名（ルビ）情報。特定の漢字の上に振るための特殊記法もサポートします。
 * @param kr 韓国語の表記。対訳として表示されます。
 * @param jpHide 日本語部分をマスク（隠す）するかどうかのフラグ。
 * @param krHide 韓国語部分をマスク（隠す）するかどうかのフラグ。
 */
data class Word(
    val id: Int,
    val jp: String,
    val ruby: String = "",
    val kr: String,
    val jpHide: Boolean = false,
    val krHide: Boolean = false
)
