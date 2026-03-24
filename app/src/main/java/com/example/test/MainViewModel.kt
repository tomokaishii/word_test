package com.example.test

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel

/**
 * MainViewModel: アプリの「状態」と「ロジック」を管理するクラス
 * AndroidViewModelを継承することで、Context（アプリのリソース情報など）にアクセスできます。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel" // ログ出力用のタグ（デバッグ時に便利）
    private val res = application.resources // strings.xmlやarrays.xmlにアクセスするための道具
    private val packageName = application.packageName // アプリのパッケージ名（リソースID取得に必要）

    // --- [画面の状態管理] ComposeのUIがこれらを監視して、値が変わると自動で再描画されます ---
    var currentLevel by mutableStateOf("N5") // 現在選ばれているレベル（例：N5, N4...）
    var currentCategory by mutableStateOf("基本") // 現在選ばれているカテゴリー（例：挨拶, 食べ物...）
    var categories by mutableStateOf(listOf<String>()) // 現在のレベルで選択可能なカテゴリーのリスト
    var isPlaying by mutableStateOf(false) // 音声が再生中かどうか
    var playbackSpeed by mutableFloatStateOf(1.0f) // 再生速度（0.8x, 1.0x, 1.2xなど）
    var currentPlayingIndex by mutableIntStateOf(0) // 現在再生（注目）している単語の番号
    var selectedDescription by mutableStateOf("単語帳の説明") // 音声再生のモード選択
    var fontSize by mutableStateOf(25) // 画面に表示する文字サイズ

    // 実際に画面に並ぶ単語のリスト。中身が変わると画面のリストも動的に更新されます。
    val wordList = mutableStateListOf<Word>()

    // 全ての日本語/韓国語が隠れているかを判定する計算型プロパティ
    val allJpHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.jpHide } }
    val allKrHidden by derivedStateOf { wordList.isNotEmpty() && wordList.all { it.krHide } }

    // 音声再生を担当するAndroid標準のプレイヤー
    private var mediaPlayer: MediaPlayer? = null

    /**
     * レベル（N5〜N1）が変更された時に呼ばれる関数
     */
    fun updateLevel(level: String) {
        Log.d(TAG, "Level updated to: $level")
        currentLevel = level
        loadCategories() // 新しいレベルに合わせたカテゴリー一覧を読み直す
        loadWords()      // 新しいレベルに合わせた単語データを読み直す
    }

    /**
     * カテゴリー（「基本」など）が変更された時に呼ばれる関数
     */
    fun updateCategory(category: String) {
        Log.d(TAG, "Category updated to: $category")
        currentCategory = category
        loadWords() // そのカテゴリーの単語データを読み直す
    }

    /**
     * レベルに応じたカテゴリー一覧を XML (arrays.xml) から取得する
     */
    private fun loadCategories() {
        // XML内の名前（例：categories_n5）を動的に作成してIDを取得
        val catId = res.getIdentifier("categories_${currentLevel.lowercase()}", "array", packageName)

        // IDが見つかれば中身を読み込み、なければ「基本」だけ入れる
        categories = if (catId != 0) res.getStringArray(catId).toList() else listOf("基本")

        Log.d(TAG, "Loaded categories: $categories")

        // 今選んでいるカテゴリーが新しいリストに無ければ、先頭のカテゴリーにリセット
        if (currentCategory !in categories) {
            currentCategory = categories.firstOrNull() ?: "基本"
        }
    }

    /**
     * 選択中のレベルとカテゴリーに基づき、実際の単語データをXMLから読み込む
     */
    fun loadWords() {
        try {
            val lv = currentLevel.lowercase() // 検索用に小文字化（n5, n4...）
            val ct = currentCategory         // カテゴリー名

            // リソース名を動的に生成してIDを取得（例：n5_基本_jp）
            // これにより、大量のwhen文を書かずに済みます
            val jpId = res.getIdentifier("${lv}_${ct}_jp", "array", packageName)
            val krId = res.getIdentifier("${lv}_${ct}_kr", "array", packageName)
            val rubyId = res.getIdentifier("${lv}_${ct}_ruby", "array", packageName)

            // 最低限、日本語と韓国語のデータがあれば処理開始
            if (jpId != 0 && krId != 0) {
                val jpArr = res.getStringArray(jpId) // 日本語の文字列配列を取得
                val krArr = res.getStringArray(krId) // 韓国語の文字列配列を取得

                // ルビ（ふりがな）用配列。存在しない場合は空文字で埋める
                val rubyArr = if (rubyId != 0) {
                    res.getStringArray(rubyId)
                } else {
                    Array(jpArr.size) { "" }
                }

                wordList.clear() // 古いリストを一度空にする

                // ループを回して一つずつ Word オブジェクトに変換してリストへ追加
                // loadWords() 内の wordList.add 部分
                jpArr.indices.forEach { i ->
                    val rawRuby = rubyArr.getOrElse(i) { "" }
                    // カンマで分割してリスト化しておく（空文字なら空リスト）
                    val rubyList = if (rawRuby.isBlank()) emptyList() else rawRuby.split(",")

                    wordList.add(
                        Word(
                            id = i + 1,
                            jp = jpArr[i],
                            ruby = rawRuby, // 既存のString型
                            // 必要に応じて Word クラスに rubyList: List<String> を追加すると楽です
                            kr = krArr.getOrElse(i) { "" }
                        )
                    )
                }

                currentPlayingIndex = 0 // 単語が変わったので再生位置を先頭に戻す
                Log.i(TAG, "Successfully loaded ${wordList.size} words for $lv/$ct")
            } else {
                Log.w(TAG, "Resources not found for $lv/$ct")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading words", e)
            wordList.clear()
        }
    }

    /**
     * 音声を再生するメインの関数
     */
    fun playAudio() {
        mediaPlayer?.release() // 以前再生していた音があれば破棄する

        // 再生するファイル名を決定（例：n5_基本_word_1）
        val audioResName = when (selectedDescription) {
            "単語帳の説明" -> "audio_description"
            "単語の発音" -> "${currentLevel.lowercase()}_${currentCategory}_word_${currentPlayingIndex + 1}"
            "例文の発音" -> "${currentLevel.lowercase()}_${currentCategory}_example_${currentPlayingIndex + 1}"
            else -> "audio_description"
        }

        // res/raw フォルダ内の音声ファイルIDを取得
        val audioId = res.getIdentifier(audioResName, "raw", packageName)
        Log.d(TAG, "Attempting to play audio: $audioResName (ID: $audioId)")

        if (audioId != 0) {
            // MediaPlayerを作成して設定
            mediaPlayer = MediaPlayer.create(getApplication(), audioId).apply {
                try {
                    // 再生速度の設定（Androidのバージョンや設定により失敗する場合があるためtry-catch）
                    playbackParams = playbackParams.setSpeed(playbackSpeed)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to set playback speed", e)
                }

                // 再生が終わった時の処理
                setOnCompletionListener {
                    this@MainViewModel.isPlaying = false // 再生中フラグを下ろす
                    Log.d(TAG, "Playback completed")
                }
                start() // 再生開始！
                this@MainViewModel.isPlaying = true
            }
        } else {
            Log.w(TAG, "Audio resource not found: $audioResName")
        }
    }

    /**
     * 再生・一時停止ボタンの切り替え
     */
    fun togglePlay() {
        if (isPlaying) {
            mediaPlayer?.pause() // 再生中なら止める
            isPlaying = false
        } else {
            if (mediaPlayer == null) playAudio() else {
                mediaPlayer?.start() // 停止中なら再開する
                isPlaying = true
            }
        }
    }

    // 次の単語へ
    fun next() {
        if (currentPlayingIndex < wordList.size - 1) {
            currentPlayingIndex++
            playAudio()
        }
    }

    // 前の単語へ
    fun prev() {
        if (currentPlayingIndex > 0) {
            currentPlayingIndex--
            playAudio()
        }
    }

    /**
     * 単語リストをシャッフルする（ランダム並び替え）
     */
    fun shuffle() {
        val shuffled = wordList.shuffled()
        wordList.clear()
        wordList.addAll(shuffled)
    }

    /**
     * 全ての日本語表示/非表示を一括で切り替える
     */
    fun toggleAllJp() {
        val target = !allJpHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(jpHide = target) }
    }

    /**
     * 全ての韓国語表示/非表示を一括で切り替える
     */
    fun toggleAllKr() {
        val target = !allKrHidden
        wordList.indices.forEach { wordList[it] = wordList[it].copy(krHide = target) }
    }

    /**
     * 特定の行の日本語表示を切り替える
     */
    fun toggleWordJp(index: Int) {
        wordList[index] = wordList[index].copy(jpHide = !wordList[index].jpHide)
    }

    /**
     * 特定の行の韓国語表示を切り替える
     */
    fun toggleWordKr(index: Int) {
        wordList[index] = wordList[index].copy(krHide = !wordList[index].krHide)
    }

    /**
     * ViewModelが破棄される時の後処理
     * メモリ漏れを防ぐためにMediaPlayerを確実に解放します
     */
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}