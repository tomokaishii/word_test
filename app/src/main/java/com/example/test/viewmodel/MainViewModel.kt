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
 * アプリケーションのビジネスロジックとUI状態を管理する中心的なクラスです。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"
    private val repository = WordRepository(application)

    // --- [UI状態（State）] ---
    var currentLevel by mutableStateOf("N5") 
    var currentCategory by mutableStateOf("基本") 
    var categories by mutableStateOf(listOf<String>()) 
    var isPlaying by mutableStateOf(false) 
    var playbackSpeed by mutableFloatStateOf(1.0f) 
    var currentPlayingIndex by mutableIntStateOf(0) 
    var selectedDescription by mutableStateOf("単語帳の説明") 
    var fontSize by mutableStateOf(20) 

    /**
     * 表示対象の単語リスト。
     */
    val wordList = mutableStateListOf<Word>()
    
    val allJpHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } }
    val allKrHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } }

    private var mediaPlayer: MediaPlayer? = null

    fun updateLevel(level: String) {
        currentLevel = level
        loadCategories()
        loadWords()
    }

    fun updateCategory(category: String) {
        currentCategory = category
        loadWords()
    }

    private fun loadCategories() {
        categories = repository.getCategories(currentLevel)
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "基本"
        }
    }

    fun loadWords() {
        wordList.clear()
        wordList.addAll(repository.getWords(currentLevel, currentCategory))
        currentPlayingIndex = 0
    }

    fun playAudio() {
        mediaPlayer?.release()
        val res = getApplication<Application>().resources
        val packageName = getApplication<Application>().packageName

        val audioResName = when (selectedDescription) {
            "単語帳の説明" -> "audio_description"
            "単語の発音" -> "${currentLevel.lowercase()}_${currentCategory}_word_${currentPlayingIndex + 1}"
            "例文の発音" -> "${currentLevel.lowercase()}_${currentCategory}_example_${currentPlayingIndex + 1}"
            else -> "audio_description"
        }
        
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                try {
                    playbackParams = playbackParams.setSpeed(playbackSpeed)
                } catch (e: Exception) {
                    Log.e(TAG, "Playback speed setting failed", e)
                }
                setOnCompletionListener { 
                    this@MainViewModel.isPlaying = false 
                }
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

    /**
     * 日本語列の表示/非表示を全行一括で切り替えます。
     * 🌟 高速化: 1要素ずつ更新せず、リストを一括で差し替えます。
     */
    fun toggleAllJp() {
        val currentAllKrHidden = allKrHidden
        val currentAllJpHidden = allJpHidden
        
        val updatedList = wordList.map { word ->
            if (currentAllKrHidden) {
                word.copy(jpHide = true, krHide = false)
            } else {
                word.copy(jpHide = !currentAllJpHidden)
            }
        }
        
        wordList.clear()
        wordList.addAll(updatedList)
    }

    /**
     * 韓国語列の表示/非表示を全行一括で切り替えます。
     * 🌟 高速化: 1要素ずつ更新せず、リストを一括で差し替えます。
     */
    fun toggleAllKr() {
        val currentAllJpHidden = allJpHidden
        val currentAllKrHidden = allKrHidden
        
        val updatedList = wordList.map { word ->
            if (currentAllJpHidden) {
                word.copy(jpHide = false, krHide = true)
            } else {
                word.copy(krHide = !currentAllKrHidden)
            }
        }
        
        wordList.clear()
        wordList.addAll(updatedList)
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
        mediaPlayer = null
    }
}
