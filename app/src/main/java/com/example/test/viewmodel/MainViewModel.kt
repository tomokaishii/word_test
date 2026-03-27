package com.example.test.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.example.test.data.model.Word
import com.example.test.data.repository.WordRepository

/**
 * 【MainViewModel】
 * アプリの表示状態と、音声・テキストの取得ロジックを管理します。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"
    private val repository = WordRepository(application)

    // --- [UI状態（State）] ---
    var currentLevel by mutableStateOf("N5") 
    var currentCategory by mutableStateOf("family") 
    var categories by mutableStateOf(listOf<String>()) 
    var isPlaying by mutableStateOf(false) 
    var playbackSpeed by mutableFloatStateOf(1.0f) 
    var currentPlayingIndex by mutableIntStateOf(0) 
    var selectedDescription by mutableStateOf("単語帳の説明")
    var fontSize by mutableStateOf(20) 

    val wordList = mutableStateListOf<Word>()
    
    val allJpHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } }
    val allKrHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } }

    private var mediaPlayer: MediaPlayer? = null

    /**
     * [カテゴリー翻訳マップ]
     */
    private val categoryMap = mapOf(
        "family"   to "家族",
        "house"    to "家の中",
        "daily"    to "日常",
        "city"     to "街",
        "greeting" to "挨拶",
        "basic"    to "基本"
    )

    private fun toEnglishCategory(jpName: String): String = 
        categoryMap.entries.find { it.value == jpName }?.key ?: "family"

    private fun toJapaneseCategory(enName: String): String = 
        categoryMap[enName] ?: enName

    fun updateLevel(level: String) {
        currentLevel = level
        loadCategories()
    }

    fun updateCategory(category: String) {
        currentCategory = category
        loadWords()
    }

    private fun loadCategories() {
        val rawIds = repository.getCategories(currentLevel)
        categories = rawIds // ここは英語名のまま保持して、UI側で変換、またはここを変換済みリストにする
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "family"
        }
        loadWords()
    }

    fun loadWords() {
        wordList.clear()
        wordList.addAll(repository.getWords(currentLevel, currentCategory))
        currentPlayingIndex = 0
    }

    /**
     * [1. 表示用テキストの解決ルール]
     * セレクトボックスのモードに応じて、音声ガイドエリアに出す「日本語」「韓国語」「ルビ」を決定します。
     */
    fun getCurrentDisplayText(): Triple<String, String, String> {
        val word = wordList.getOrNull(currentPlayingIndex) ?: return Triple("準備中", "---", "")
        
        return when (selectedDescription) {
            "単語帳の説明" -> {
                Triple(word.guideJp, word.guideKr, word.guideRuby)
            }
            "単語の発音" -> {
                Triple(word.jp, word.kr, word.ruby)
            }
            "例文の発音" -> {
                Triple(word.exJp, word.exKr, word.exRuby)
            }
            else -> Triple("---", "---", "")
        }
    }

    /**
     * [2. 音声リソース名の解決ルール]
     */
    private fun resolveAudioResourceName(): String {
        val word = wordList.getOrNull(currentPlayingIndex) ?: return ""
        
        return when (selectedDescription) {
            "単語帳の説明" -> word.audioGuide
            "単語の発音" -> word.audioWord
            "例文の発音" -> word.audioEx
            else -> ""
        }
    }

    /**
     * [3. 音声再生メインロジック]
     */
    fun playAudio() {
        mediaPlayer?.release()
        val res = getApplication<Application>().resources
        val packageName = getApplication<Application>().packageName

        val audioResName = resolveAudioResourceName()
        if (audioResName.isBlank()) {
            this@MainViewModel.isPlaying = false
            return
        }

        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                try { playbackParams = playbackParams.setSpeed(playbackSpeed) } catch (e: Exception) {}
                
                setOnCompletionListener {
                    if (currentPlayingIndex < wordList.size - 1) {
                        currentPlayingIndex++
                        playAudio()
                    } else {
                        // 全ての再生が終わったら停止し、インデックスを最初に戻す
                        this@MainViewModel.isPlaying = false
                        this@MainViewModel.currentPlayingIndex = 0
                    }
                }
                
                start()
                this@MainViewModel.isPlaying = true
            }
        } else {
            this@MainViewModel.isPlaying = false
        }
    }

    fun togglePlay() {
        if (isPlaying) { 
            mediaPlayer?.pause()
            isPlaying = false 
        } else { 
            playAudio()
        }
    }

    fun next() { 
        if (currentPlayingIndex < wordList.size - 1) { 
            currentPlayingIndex++
            if (isPlaying) playAudio() 
        } 
    }
    
    fun prev() { 
        if (currentPlayingIndex > 0) { 
            currentPlayingIndex--
            if (isPlaying) playAudio() 
        } 
    }

    fun shuffle() {
        val shuffled = wordList.shuffled()
        wordList.clear()
        wordList.addAll(shuffled)
    }

    fun toggleAllJp() {
        val target = !allJpHidden
        val updated = wordList.map { it.copy(jpHide = target, krHide = false) }
        wordList.clear()
        wordList.addAll(updated)
    }

    fun toggleAllKr() {
        val target = !allKrHidden
        val updated = wordList.map { it.copy(krHide = target, jpHide = false) }
        wordList.clear()
        wordList.addAll(updated)
    }

    fun toggleWordJp(index: Int) {
        val word = wordList[index]
        wordList[index] = if (word.krHide) word.copy(jpHide = true, krHide = false) else word.copy(jpHide = !word.jpHide)
    }

    fun toggleWordKr(index: Int) {
        val word = wordList[index]
        wordList[index] = if (word.jpHide) word.copy(jpHide = false, krHide = true) else word.copy(krHide = !word.krHide)
    }

    override fun onCleared() { super.onCleared(); mediaPlayer?.release() }
}
