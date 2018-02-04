package xyz.cybersapien.promptodroid.ui

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import xyz.cybersapien.promptodroid.R

class AutoChangingTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), Runnable {

    var numberOfWords: Int = DEFAULT_WORDS
    var speed: Int = 1
    var running: Boolean = false
        set(value) {
            field = value
            if (value) {
                if (runningThread == null) {
                    runningThread = Thread(this)
                    runningThread!!.start()
                } else {
                    runningThread!!.interrupt()
                    runningThread = null
                    running = true
                }
            } else {
                if (runningThread?.isAlive == true) {
                    runningThread?.interrupt()
                }
            }
        }
    var wordsArray: Array<String>? = null
    var currentDisplay: String? = null
    private var currentWordIndex = 0
    @PromptType
    private var promptType: Int = PROMPT_TYPE_LINE
    private var runningThread: Thread? = null
    var completeText: String? = null
        set(value) {
            var str = value
            if (value != null) {
                field = value
                if (promptType == PROMPT_TYPE_WORDS) {
                    wordsArray = value.split(" ").toTypedArray()
                }
                str = getWordsToDisplay()
            }
            text = str
        }

    var finishedListener: OnFinishedListener? = null

    init {
        attrs?.let { setupAttributes(context, it) }
    }

    private fun setupAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.AutoChangingTextView, 0, 0)
        numberOfWords = typedArray.getInt(R.styleable.AutoChangingTextView_numberOfWords, DEFAULT_WORDS)
        speed = typedArray.getInt(R.styleable.AutoChangingTextView_speed, 1)
        promptType = typedArray.getInt(R.styleable.AutoChangingTextView_promptType, PROMPT_TYPE_LINE)
        completeText = typedArray.getNonResourceString(R.styleable.AutoChangingTextView_customText)
        val runner = typedArray.getBoolean(R.styleable.AutoChangingTextView_running, false)
        typedArray.recycle()
        running = runner
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.numWords = this.numberOfWords
        savedState.speed = this.speed
        savedState.running = this.running
        savedState.currentWordIndex = this.currentWordIndex
        savedState.promptType = this.promptType
        savedState.completeData = this.completeText ?: ""
        savedState.wordsArray = this.wordsArray
        savedState.currentText = this.currentDisplay ?: ""
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            this.numberOfWords = state.numWords
            this.speed = state.speed
            this.currentWordIndex = state.currentWordIndex
            this.promptType = state.promptType
            this.completeText = state.completeData
            this.wordsArray = state.wordsArray
            this.currentDisplay = state.currentText
            this.text = currentDisplay
            this.running = state.running
        }
    }

    override fun run() {
        while (running) {
            try {
                Thread.sleep(speed * 1000L)
            } catch (e: InterruptedException) {
                // No harm no foul
                // Print the stacktrace anyway
                e.printStackTrace()
            }
            if (currentWordIndex == -1) {
                running = false
                activateListener()
                break
            }
            val displayWords = getWordsToDisplay()
            post { text = displayWords }
            if (currentWordIndex == -1) {
                running = false
                activateListener()
            }
        }
    }


    private fun activateListener() {
        finishedListener?.onTextChangeFinished()
    }

    private fun getWordsToDisplay(): String =
            if (promptType == PROMPT_TYPE_LINE)
                getWordsByLine()
            else
                getWordsByNumber()

    private fun getWordsByLine(): String {
        val idx = completeText?.indexOf(".", currentWordIndex)!!
        val returnString: String
        if (idx != -1) {
            returnString = completeText!!.substring(currentWordIndex, idx.plus(1))
            currentWordIndex = idx + 1
        } else {
            returnString = completeText!!.substring(currentWordIndex)
            currentWordIndex = -1
        }
        return returnString.trim()
    }

    private fun getWordsByNumber(): String {
        val builder = StringBuilder()
        val totalWords =
                if (currentWordIndex + numberOfWords < wordsArray!!.size)
                    numberOfWords
                else
                    wordsArray!!.size - currentWordIndex
        for (i in 0..totalWords step 1) {
            builder.append(wordsArray!![i + currentWordIndex])
            builder.append(" ")
        }
        return builder.toString()
    }

    companion object {

        private const val LOG_TAG = "AutoChangingTextView"
        @JvmStatic
        private val DEFAULT_WORDS = 20
        const val PROMPT_TYPE_LINE = 0
        const val PROMPT_TYPE_WORDS = 1

        @IntDef(PROMPT_TYPE_LINE.toLong(), PROMPT_TYPE_WORDS.toLong())
        @Retention(AnnotationRetention.SOURCE)
        annotation class PromptType
    }

    internal class SavedState : View.BaseSavedState {

        var numWords: Int = 0
        var speed: Int = 0
        var running: Boolean = false
        var currentWordIndex: Int = 0
        var promptType: Int = 0
        var completeData: String = ""
        var wordsArray: Array<String>? = null
        var currentText: String = ""

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            this.numWords = source.readInt()
            this.speed = source.readInt()
            this.running = source.readByte().toInt() != 0
            this.currentWordIndex = source.readInt()
            this.promptType = source.readInt()
            this.completeData = source.readString()
            source.readStringArray(this.wordsArray)
            this.currentText = source.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(numWords)
            out.writeInt(speed)
            out.writeByte((if (running) 1 else 0).toByte())
            out.writeInt(currentWordIndex)
            out.writeInt(promptType)
            out.writeString(completeData)
            out.writeStringArray(wordsArray)
            out.writeString(currentText)
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    interface OnFinishedListener {
        fun onTextChangeFinished()
    }
}