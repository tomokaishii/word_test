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
     * レベルやカテゴリが変更された際に、リポジトリを通じてXMLからデータを読み込みます。
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
        // XMLの categories_n5 等からカテゴリ名一覧を取得
        categories = repository.getCategories(currentLevel)
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "基本"
        }
    }

    fun loadWords() {
        // XMLの n5_基本_jp 等から単語リストを構築
        wordList.clear()
        wordList.addAll(repository.getWords(currentLevel, currentCategory))
        currentPlayingIndex = 0
    }

    /**
     * [音声リソース名の解決ルール]
     * 🌟 ここを修正することで、res/raw 内のファイル名との紐付けを手動で変更できます。
     */
    private fun resolveAudioResourceName(): String {
        val lv = currentLevel.lowercase()      // 例: "n5"
        val cat = currentCategory              // 例: "基本"
        val idx = currentPlayingIndex + 1      // 1から始まる番号

        return when (selectedDescription) {
            "単語帳の説明" -> "audio_description"
            "単語の発音" -> "${lv}_${cat}_word_${idx}"    // 例: n5_基本_word_1
            "例文の発音" -> "${lv}_${cat}_example_${idx}" // 例: n5_基本_example_1
            else -> "audio_description"
        }
    }

    /**
     * [音声再生ロジック]
     * 解決されたリソース名に基づいて MediaPlayer で再生します。
     */
    fun playAudio() {
        mediaPlayer?.release()
        val res = getApplication<Application>().resources
        val packageName = getApplication<Application>().packageName

        val audioResName = resolveAudioResourceName()
        Log.d(TAG, "Playing audio: $audioResName")
        
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        if (audioId != 0) {
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                try {
                    playbackParams = playbackParams.setSpeed(playbackSpeed)
                } catch (e: Exception) {
                    Log.e(TAG, "Speed setting error", e)
                }
                // 🌟 MediaPlayer.isPlaying と MainViewModel.isPlaying の名前衝突を避けるため this@MainViewModel を指定
                setOnCompletionListener { this@MainViewModel.isPlaying = false }
                start()
                this@MainViewModel.isPlaying = true
            }
        } else {
            Log.w(TAG, "Audio not found: $audioResName (Check res/raw folder)")
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
