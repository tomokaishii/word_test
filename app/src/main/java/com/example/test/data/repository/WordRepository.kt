package com.example.test.data.repository

import android.content.Context
import android.content.res.Resources
import com.example.test.data.model.Word

/**
 * 【WordRepository】
 * アプリのデータソース（今回はXMLリソース）へのアクセスを一手に引き受けるクラスです。
 */
class WordRepository(private val context: Context) {
    private val res: Resources = context.resources
    private val packageName: String = context.packageName

    /**
     * 指定されたレベル（N5〜N1）に対応するカテゴリー一覧をリソースから取得します。
     */
    fun getCategories(level: String): List<String> {
        val catId = res.getIdentifier("categories_${level.lowercase()}", "array", packageName)
        return if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")
    }

    /**
     * 指定されたレベルとカテゴリーに対応する単語リストをリソースから取得します。
     * 「単語」「例文」「説明」の3つのモードに必要なテキストを全て取得して結合します。
     */
    fun getWords(level: String, category: String): List<Word> {
        val lv = level.lowercase()

        // 1. 「単語の発音」用のリソースID取得
        val jpWordId = res.getIdentifier("${lv}_${category}_jp", "array", packageName)
        val krWordId = res.getIdentifier("${lv}_${category}_kr", "array", packageName)
        val rubyWordId = res.getIdentifier("${lv}_${category}_ruby", "array", packageName)

        // 2. 「例文の発音」用のリソースID取得 (例: n5_基本_ex_jp)
        val jpExampleId = res.getIdentifier("${lv}_${category}_ex_jp", "array", packageName)
        val krExampleId = res.getIdentifier("${lv}_${category}_ex_kr", "array", packageName)

        // 3. 「単語帳の説明」用のリソースID取得 (例: n5_基本_guide_jp)
        val jpGuideId = res.getIdentifier("${lv}_${category}_guide_jp", "array", packageName)
        val krGuideId = res.getIdentifier("${lv}_${category}_guide_kr", "array", packageName)

        // 最低限、単語の日本語と韓国語のデータが見つからない場合は空リストを返す
        if (jpWordId == 0 || krWordId == 0) return emptyList()

        val jpArr = res.getStringArray(jpWordId)
        val krArr = res.getStringArray(krWordId)
        
        // 各配列の読み込み。存在しない場合は空文字の配列で補う
        val rubyArr = if (rubyWordId != 0) res.getStringArray(rubyWordId) else Array(jpArr.size) { "" }
        val exJpArr = if (jpExampleId != 0) res.getStringArray(jpExampleId) else Array(jpArr.size) { "" }
        val exKrArr = if (krExampleId != 0) res.getStringArray(krExampleId) else Array(jpArr.size) { "" }
        val guideJpArr = if (jpGuideId != 0) res.getStringArray(jpGuideId) else Array(jpArr.size) { "" }
        val guideKrArr = if (krGuideId != 0) res.getStringArray(krGuideId) else Array(jpArr.size) { "" }

        // インデックスを回して全てのモードのデータを1つの Word オブジェクトに結合
        return jpArr.indices.map { i ->
            Word(
                id = i + 1,
                jp = jpArr[i],
                kr = krArr[i],
                ruby = rubyArr.getOrElse(i) { "" },
                exJp = exJpArr.getOrElse(i) { "" },
                exKr = exKrArr.getOrElse(i) { "" },
                guideJp = guideJpArr.getOrElse(i) { "" },
                guideKr = guideKrArr.getOrElse(i) { "" }
            )
        }
    }
}
