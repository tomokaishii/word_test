package com.example.test.data.repository

import android.content.Context
import android.content.res.Resources
import com.example.test.data.model.Word

/**
 * 【WordRepository】
 * アプリのデータソース（今回はXMLリソース）へのアクセスを一手に引き受けるクラスです。
 * 
 * ViewModel が「どこからデータを取るか」を知る必要をなくすことで、
 * 将来的にデータベースやAPIに変更する場合でも、ViewModel側の修正を最小限に抑えられます。
 */
class WordRepository(private val context: Context) {
    private val res: Resources = context.resources
    private val packageName: String = context.packageName

    /**
     * 指定されたレベル（N5〜N1）に対応するカテゴリー一覧をリソースから取得します。
     * 例: categories_n5 という名前の string-array を探します。
     */
    fun getCategories(level: String): List<String> {
        val catId = res.getIdentifier("categories_${level.lowercase()}", "array", packageName)
        return if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")
    }

    /**
     * 指定されたレベルとカテゴリーに対応する単語リストをリソースから取得します。
     * 日本語、韓国語、およびルビ（あれば）の配列を組み合わせて Word オブジェクトのリストを生成します。
     */
    fun getWords(level: String, category: String): List<Word> {
        val lv = level.lowercase()
        // リソース名を動的に解決 (例: n5_挨拶_jp)
        val jpId = res.getIdentifier("${lv}_${category}_jp", "array", packageName)
        val krId = res.getIdentifier("${lv}_${category}_kr", "array", packageName)
        val rubyId = res.getIdentifier("${lv}_${category}_ruby", "array", packageName)

        // 最低限、日本語と韓国語のデータが見つからない場合は空リストを返す
        if (jpId == 0 || krId == 0) return emptyList()

        val jpArr = res.getStringArray(jpId)
        val krArr = res.getStringArray(krId)
        // ルビは任意項目。存在しない場合は空文字の配列で補う
        val rubyArr = if (rubyId != 0) res.getStringArray(rubyId) else Array(jpArr.size) { "" }

        // インデックスを回してデータを結合
        return jpArr.indices.map { i ->
            Word(
                id = i + 1,
                jp = jpArr[i],
                ruby = rubyArr.getOrElse(i) { "" },
                kr = krArr.getOrElse(i) { "" }
            )
        }
    }
}
