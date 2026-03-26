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
 * AndroidViewModel を継承することで、リソースアクセスに必要な Context を保持します。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"
    
    /**
     * データ取得を担当するリポジトリ。
     * ViewModel自体がリソースID取得などの詳細なロジックを持たず、リポジトリに委譲します。
     */
    private val repository = WordRepository(application)

    // --- [UI状態（State）] ---
    // これらは Compose UI によって監視され、値が変更されると自動的に再描画が走ります。
    
    var currentLevel by mutableStateOf("N5") // 現在選択中のレベル
    var currentCategory by mutableStateOf("基本") // 現在選択中のカテゴリー
    var categories by mutableStateOf(listOf<String>()) // 現在のレベルで利用可能なカテゴリー一覧
    var isPlaying by mutableStateOf(false) // 音声が再生中かどうか
    var playbackSpeed by mutableFloatStateOf(1.0f) // 再生速度（0.8x, 1.0x, 1.2x）
    var currentPlayingIndex by mutableIntStateOf(0) // 現在の再生対象（または注目対象）の単語インデックス
    var selectedDescription by mutableStateOf("単語帳の説明") // 再生モード
    var fontSize by mutableStateOf(20) // 🌟 修正: 初期サイズを 20 (小) に変更

    /**
     * 表示対象の単語リスト。
     * mutableStateListOf を使用することで、リストの追加・削除・変更が即即にUIに反映されます。
     */
    val wordList = mutableStateListOf<Word>()
    
    /**
     * 派生状態（Derived State）。
     * 他の状態（wordList）から計算される状態で、効率的な再描画のために使用します。
     */
    val allJpHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } }
    val allKrHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } }

    private var mediaPlayer: MediaPlayer? = null

    /**
     * レベルを変更し、関連するカテゴリーと単語をリロードします。
     */
    fun updateLevel(level: String) {
        Log.d(TAG, "Level updated to: $level")
        currentLevel = level
        loadCategories()
        loadWords()
    }

    /**
     * カテゴリーを変更し、単語リストをリロードします。
     */
    fun updateCategory(category: String) {
        Log.d(TAG, "Category updated to: $category")
        currentCategory = category
        loadWords()
    }

    /**
     * リポジトリから現在のレベルに属するカテゴリー一覧を取得します。
     */
    private fun loadCategories() {
        categories = repository.getCategories(currentLevel)
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "基本"
        }
    }

    /**
     * リポジトリから単語リストを取得して更新します。
     */
    fun loadWords() {
        wordList.clear()
        wordList.addAll(repository.getWords(currentLevel, currentCategory))
        currentPlayingIndex = 0
    }

    /**
     * 選択中のモードとインデックスに基づいて音声を再生します。
     */
    fun playAudio() {
        mediaPlayer?.release()
        val res = getApplication<Application>().resources
        val packageName = getApplication<Application>().packageName

        // リソース名を動的に解決
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
        } else {
            Log.w(TAG, "Audio resource not found: $audioResName")
        }
    }

    /**
     * 再生/一時停止の状態をトグル（切り替え）します。
     */
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

    // ナビゲーション操作
    fun next() { if (currentPlayingIndex < wordList.size - 1) { currentPlayingIndex++; playAudio() } }
    fun prev() { if (currentPlayingIndex > 0) { currentPlayingIndex--; playAudio() } }

    /**
     * 単語リストをシャッフルします。
     */
    fun shuffle() {
        val shuffled = wordList.shuffled()
        wordList.clear()
        wordList.addAll(shuffled)
    }

    /**
     * 日本語列の表示/非表示を全行一括で切り替えます。
     */
    fun toggleAllJp() {
        val target = !allJpHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(jpHide = target) }
    }

    /**
     * 韓国語列の表示/非表示を全行一括で切り替えます。
     */
    fun toggleAllKr() {
        val target = !allKrHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(krHide = target) }
    }

    /**
     * 指定したインデックスの日本語表示を個別に切り替えます。
     */
    fun toggleWordJp(index: Int) {
        wordList[index] = wordList[index].copy(jpHide = !wordList[index].jpHide)
    }

    /**
     * 指定したインデックスの韓国語表示を個別に切り替えます。
     */
    fun toggleWordKr(index: Int) {
        wordList[index] = wordList[index].copy(krHide = !wordList[index].krHide)
    }

    /**
     * ViewModel 破棄時にプレイヤーを解放し、メモリリークを防止します。
     */
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
