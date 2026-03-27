package com.example.test.data.repository

import android.content.Context
import android.content.res.Resources
import com.example.test.data.model.Word

/**
 * 【WordRepository】
 * XMLリソースからテキストデータと音声ファイル名のリストを一括取得し、Wordモデルへ変換します。
 */
class WordRepository(private val context: Context) {
    private val res: Resources = context.resources
    private val packageName: String = context.packageName

    fun getCategories(level: String): List<String> {
        val catId = res.getIdentifier("categories_${level.lowercase()}", "array", packageName)
        return if (catId != 0) res.getStringArray(catId).toList() else listOf("family")
    }

    /**
     * 指定されたレベルとカテゴリに基づき、単語・例文・説明の全データを取得
     */
    fun getWords(level: String, category: String): List<Word> {
        val lv = level.lowercase()
        val cat = category

        // 1. 単語モード用 (例: n5_family_word_jp)
        val jpWordId = res.getIdentifier("${lv}_${cat}_word_jp", "array", packageName)
        val krWordId = res.getIdentifier("${lv}_${cat}_word_kr", "array", packageName)
        val rubyWordId = res.getIdentifier("${lv}_${cat}_word_ruby", "array", packageName)
        val audioWordId = res.getIdentifier("${lv}_${cat}_word_audio", "array", packageName)

        // 2. 例文モード用 (例: n5_family_ex_jp)
        val jpExId = res.getIdentifier("${lv}_${cat}_ex_jp", "array", packageName)
        val krExId = res.getIdentifier("${lv}_${cat}_ex_kr", "array", packageName)
        val rubyExId = res.getIdentifier("${lv}_${cat}_ex_ruby", "array", packageName)
        val audioExId = res.getIdentifier("${lv}_${cat}_ex_audio", "array", packageName)

        // 3. 単語帳の説明モード用 (arrays_string_guide.xml: guide_jp 等)
        val jpGuideId = res.getIdentifier("guide_jp", "array", packageName)
        val krGuideId = res.getIdentifier("guide_kr", "array", packageName)
        val rubyGuideId = res.getIdentifier("guide_ruby", "array", packageName)
        val audioGuideId = res.getIdentifier("guide_audio", "array", packageName)

        if (jpWordId == 0 || krWordId == 0) return emptyList()

        val jpArr = res.getStringArray(jpWordId)
        val krArr = res.getStringArray(krWordId)
        val rubyArr = if (rubyWordId != 0) res.getStringArray(rubyWordId) else Array(jpArr.size) { "" }
        val audioWordArr = if (audioWordId != 0) res.getStringArray(audioWordId) else Array(jpArr.size) { "" }

        val exJpArr = if (jpExId != 0) res.getStringArray(jpExId) else Array(jpArr.size) { "" }
        val exKrArr = if (krExId != 0) res.getStringArray(krExId) else Array(jpArr.size) { "" }
        val exRubyArr = if (rubyExId != 0) res.getStringArray(rubyExId) else Array(jpArr.size) { "" }
        val audioExArr = if (audioExId != 0) res.getStringArray(audioExId) else Array(jpArr.size) { "" }

        val guideJpArr = if (jpGuideId != 0) res.getStringArray(jpGuideId) else Array(jpArr.size) { "" }
        val guideKrArr = if (krGuideId != 0) res.getStringArray(krGuideId) else Array(jpArr.size) { "" }
        val guideRubyArr = if (rubyGuideId != 0) res.getStringArray(rubyGuideId) else Array(jpArr.size) { "" }
        val audioGuideArr = if (audioGuideId != 0) res.getStringArray(audioGuideId) else Array(jpArr.size) { "" }

        // ユーティリティ: 拡張子除去
        fun String.clean(): String = this.substringBeforeLast(".")

        return jpArr.indices.map { i ->
            Word(
                id = i + 1,
                jp = jpArr[i],
                kr = krArr[i],
                ruby = rubyArr.getOrElse(i) { "" },
                audioWord = audioWordArr.getOrElse(i) { "" }.clean(),
                
                exJp = exJpArr.getOrElse(i) { "" },
                exKr = exKrArr.getOrElse(i) { "" },
                exRuby = exRubyArr.getOrElse(i) { "" },
                audioEx = audioExArr.getOrElse(i) { "" }.clean(),
                
                guideJp = guideJpArr.getOrElse(i) { "" },
                guideKr = guideKrArr.getOrElse(i) { "" },
                guideRuby = guideRubyArr.getOrElse(i) { "" },
                audioGuide = audioGuideArr.getOrElse(i) { "" }.clean()
            )
        }
    }
}
