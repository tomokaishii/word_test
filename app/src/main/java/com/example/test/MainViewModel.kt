package com.example.test

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel

/**
 * MainViewModel: アプリの状態とロジックを管理
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val res = application.resources
    private val packageName = application.packageName

    // 状態管理
    var currentLevel by mutableStateOf("N5")
    var currentCategory by mutableStateOf("基本")
    var categories by mutableStateOf(listOf<String>())
    var isPlaying by mutableStateOf(false)
    var playbackSpeed by mutableFloatStateOf(1.0f)
    var currentPlayingIndex by mutableIntStateOf(0)
    var selectedDescription by mutableStateOf("単語帳の説明")
    var fontSize by mutableStateOf(25)

    val wordList = mutableStateListOf<Word>()
    
    val allJpHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } }
    val allKrHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } }

    private var mediaPlayer: MediaPlayer? = null

    /**
     * レベル変更時にカテゴリーと単語を再ロード
     */
    fun updateLevel(level: String) {
        currentLevel = level
        loadCategories()
        loadWords()
    }

    /**
     * カテゴリー変更時に単語を再ロード
     */
    fun updateCategory(category: String) {
        currentCategory = category
        loadWords()
    }

    private fun loadCategories() {
        val catId = res.getIdentifier("categories_${currentLevel.lowercase()}", "array", packageName)
        categories = if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "基本"
        }
    }

    /**
     * リソースから単語リストをロード
     */
    fun loadWords() {
        try {
            val lv = currentLevel.lowercase()
            val ct = currentCategory
            val jpId = res.getIdentifier("${lv}_${ct}_jp", "array", packageName)
            val krId = res.getIdentifier("${lv}_${ct}_kr", "array", packageName)
            val rubyId = res.getIdentifier("${lv}_${ct}_ruby", "array", packageName)

            if (jpId != 0 && krId != 0) {
                val jpArr = res.getStringArray(jpId)
                val krArr = res.getStringArray(krId)
                val rubyArr = if (rubyId != 0) res.getStringArray(rubyId) else Array(jpArr.size) { "" }
                
                wordList.clear()
                jpArr.indices.forEach { i ->
                    wordList.add(Word(
                        id = i + 1, 
                        jp = jpArr[i], 
                        ruby = rubyArr.getOrElse(i) { "" }, 
                        kr = krArr.getOrElse(i) { "" }
                    ))
                }
                currentPlayingIndex = 0
            }
        } catch (e: Exception) {
            wordList.clear()
        }
    }

    /**
     * 音声再生（ shadowingを避けるため this@MainViewModel を使用）
     */
    fun playAudio() {
        mediaPlayer?.release()
        val audioResName = when (selectedDescription) {
            "単語帳の説明" -> "audio_description"
            "単語の発音" -> "${currentLevel.lowercase()}_${currentCategory}_word_${currentPlayingIndex + 1}"
            "例文の発音" -> "${currentLevel.lowercase()}_${currentCategory}_example_${currentPlayingIndex + 1}"
            else -> "audio_description"
        }
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                // 再生速度設定
                try { playbackParams = playbackParams.setSpeed(playbackSpeed) } catch (e: Exception) {}
                
                setOnCompletionListener { this@MainViewModel.isPlaying = false }
                start()
                this@MainViewModel.isPlaying = true
            }
        }
    }

    fun togglePlay() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            if (mediaPlayer == null) playAudio() else {
                mediaPlayer?.start()
                isPlaying = true
            }
        }
    }

    fun next() { if (currentPlayingIndex < wordList.size - 1) { currentPlayingIndex++; playAudio() } }
    fun prev() { if (currentPlayingIndex > 0) { currentPlayingIndex--; playAudio() } }

    fun shuffle() {
        val shuffled = wordList.shuffled()
        wordList.clear()
        wordList.addAll(shuffled)
    }

    fun toggleAllJp() {
        val target = !allJpHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(jpHide = target) }
    }

    fun toggleAllKr() {
        val target = !allKrHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(krHide = target) }
    }

    fun toggleWordJp(index: Int) {
        wordList[index] = wordList[index].copy(jpHide = !wordList[index].jpHide)
    }

    fun toggleWordKr(index: Int) {
        wordList[index] = wordList[index].copy(krHide = !wordList[index].krHide)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
