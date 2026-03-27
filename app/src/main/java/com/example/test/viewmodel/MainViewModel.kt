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
    var currentCategory by mutableStateOf("基本") 
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
     * [テキスト取得ロジック]
     * レベルやカテゴリが変更された際にリポジトリを通じてXMLからデータを読み込みます。
     */
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

    /**
     * [1. 表示用テキストの解決ルール]
     * セレクトボックス（selectedDescription）の切り替えに応じて、
     * 音声ガイドエリアに表示する日本語と韓国語を決定します。
     * 🌟 手動で表示文言を変えたい場合は、この when 文の中身を編集してください。
     */
    fun getCurrentDisplayText(): Pair<String, String> {
        // 現在注目している単語データを取得
        val word = wordList.getOrNull(currentPlayingIndex) ?: return "準備中" to "---"
        
        return when (selectedDescription) {
            // モード：単語帳の説明
            "単語帳の説明" -> {
                // guideJp が空なら「準備中」を表示
                if (word.guideJp.isBlank()) "準備中" to "---"
                else word.guideJp to word.guideKr
            }
            // モード：単語の発音
            "単語の発音" -> {
                // jp が空なら「準備中」を表示
                if (word.jp.isBlank()) "準備中" to "---"
                else word.jp to word.kr
            }
            // モード：例文の発音
            "例文の発音" -> {
                // exJp が空なら「準備中」を表示
                if (word.exJp.isBlank()) "準備中" to "---"
                else word.exJp to word.exKr
            }
            else -> "---" to "---"
        }
    }

    /**
     * [2. 音声リソース名の解決ルール]
     * res/raw 内のファイル名との紐付けルールです。
     * 🌟 ファイル名の形式（アンダースコア等）を手動で変えたい場合はここを修正してください。
     */
    private fun resolveAudioResourceName(): String {
        val lv = currentLevel.lowercase()      // "n5", "n4" ...
        val cat = currentCategory              // "基本", "家族" ...
        val idx = currentPlayingIndex + 1      // 1, 2, 3 ... (1から始まる番号)

        return when (selectedDescription) {
            // 説明モードの固定音声
            "単語帳の説明" -> "audio_guide_desc"
            
            // 単語モード：例 n5_家族_word_1
            "単語の発音" -> "${lv}_${cat}_word_${idx}"
            
            // 例文モード：例 n5_家族_example_1
            "例文の発音" -> "${lv}_${cat}_example_${idx}"
            
            else -> "audio_guide_desc"
        }
    }

    /**
     * [3. 音声再生メインロジック]
     * 解決されたリソース名に基づいて MediaPlayer で再生します。
     */
    fun playAudio() {
        mediaPlayer?.release()
        val res = getApplication<Application>().resources
        val packageName = getApplication<Application>().packageName

        // ルールに従ってファイル名を特定
        val audioResName = resolveAudioResourceName()
        Log.d(TAG, "Playing audio resource: $audioResName")
        
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                try {
                    playbackParams = playbackParams.setSpeed(playbackSpeed)
                } catch (e: Exception) {
                    Log.e(TAG, "Playback speed setting failed", e)
                }
                // 再生終了時に状態を更新 (this@MainViewModel で名前衝突回避)
                setOnCompletionListener { this@MainViewModel.isPlaying = false }
                start()
                this@MainViewModel.isPlaying = true
            }
        } else {
            Log.w(TAG, "Audio not found: $audioResName (res/rawフォルダを確認してください)")
            this@MainViewModel.isPlaying = false
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
        val updatedList = wordList.map { it.copy(jpHide = target, krHide = false) }
        wordList.clear()
        wordList.addAll(updatedList)
    }

    fun toggleAllKr() {
        val target = !allKrHidden
        val updatedList = wordList.map { it.copy(krHide = target, jpHide = false) }
        wordList.clear()
        wordList.addAll(updatedList)
    }

    fun toggleWordJp(index: Int) {
        val word = wordList[index]
        wordList[index] = if (word.krHide) word.copy(jpHide = true, krHide = false) 
                          else word.copy(jpHide = !word.jpHide)
    }

    fun toggleWordKr(index: Int) {
        val word = wordList[index]
        wordList[index] = if (word.jpHide) word.copy(jpHide = false, krHide = true) 
                          else word.copy(krHide = !word.krHide)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
