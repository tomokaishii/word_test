package com.example.test.data.repository

import android.content.Context
import android.content.res.Resources
import com.example.test.data.model.Word

/**
 * 単語データの取得を担当するリポジトリ
 */
class WordRepository(private val context: Context) {
    private val res: Resources = context.resources
    private val packageName: String = context.packageName

    fun getCategories(level: String): List<String> {
        val catId = res.getIdentifier("categories_${level.lowercase()}", "array", packageName)
        return if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")
    }

    fun getWords(level: String, category: String): List<Word> {
        val lv = level.lowercase()
        val jpId = res.getIdentifier("${lv}_${category}_jp", "array", packageName)
        val krId = res.getIdentifier("${lv}_${category}_kr", "array", packageName)
        val rubyId = res.getIdentifier("${lv}_${category}_ruby", "array", packageName)

        if (jpId == 0 || krId == 0) return emptyList()

        val jpArr = res.getStringArray(jpId)
        val krArr = res.getStringArray(krId)
        val rubyArr = if (rubyId != 0) res.getStringArray(rubyId) else Array(jpArr.size) { "" }

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
