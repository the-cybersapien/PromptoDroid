package xyz.cybersapien.promptodroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import xyz.cybersapien.promptodroid.R;

/**
 * Created by ogcybersapien on 31/12/17.
 */

public class AutoChangingTextView extends android.support.v7.widget.AppCompatTextView implements Runnable {


    private static final String LOG_TAG = AutoChangingTextView.class.getSimpleName();

    private static final int DEFAULT_WORDS = 20;
    public static final int PROMPT_TYPE_LINE = 0;
    public static final int PROMPT_TYPE_WORDS = 1;

    @IntDef({PROMPT_TYPE_LINE, PROMPT_TYPE_WORDS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PromptType {
    }

    private int numberOfWords = DEFAULT_WORDS;
    private int speed = 1;
    private boolean running = false;
    private String completeText;
    private String[] wordsArray;
    private String currentDisplay;
    private int currentWordIndex = 0;
    @PromptType
    private int promptType = PROMPT_TYPE_LINE;
    private Thread runningThread;

    private OnFinishedListener finishedListener;

    public AutoChangingTextView(Context context) {
        super(context);
        running = false;
    }

    public AutoChangingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(context, attrs);
    }

    public AutoChangingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(context, attrs);
    }

    public void setupAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AutoChangingTextView, 0, 0);
        numberOfWords = array.getInt(R.styleable.AutoChangingTextView_numberOfWords, DEFAULT_WORDS);
        speed = array.getInt(R.styleable.AutoChangingTextView_speed, 1);
        boolean running = array.getBoolean(R.styleable.AutoChangingTextView_running, false);
        promptType = array.getInteger(R.styleable.AutoChangingTextView_promptType, PROMPT_TYPE_LINE);
        setCustomText(array.getNonResourceString(R.styleable.AutoChangingTextView_customText));
        array.recycle();
        setRunning(running);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.numWords = this.numberOfWords;
        state.speed = this.speed;
        state.running = this.running;
        state.currentWordIndex = this.currentWordIndex;
        state.promptType = this.promptType;
        state.completeData = this.completeText;
        state.wordsArray = this.wordsArray;
        state.currentText = this.currentDisplay;
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof SavedState) {
            SavedState stateData = (SavedState) state;
            this.numberOfWords = stateData.numWords;
            this.speed = stateData.speed;
            this.running = stateData.running;
            this.currentWordIndex = stateData.currentWordIndex;
            this.promptType = stateData.promptType;
            this.completeText = stateData.completeData;
            this.wordsArray = stateData.wordsArray;
            this.currentDisplay = stateData.currentText;
        }
        this.setText(currentDisplay);
        if (running) {
            setRunning(true);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(speed * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (currentWordIndex == -1) {
                running = false;
                encoreListener();
                break;
            }
            final String displayWords = getWordsToDisplay();
            Log.i(LOG_TAG, "run: " + displayWords);
            post(new Runnable() {
                @Override
                public void run() {
                    setText(displayWords);
                }
            });
            if (currentWordIndex == -1) {
                running = false;
                encoreListener();
            }
        }
    }

    private void encoreListener() {
        if (finishedListener != null) {
            finishedListener.onFinished();
        }
    }

    private String getWordsToDisplay() {
        if (promptType == PROMPT_TYPE_LINE) {
            return getWordsByLine();
        } else {
            return getWordsByNumber();
        }
    }

    private String getWordsByLine() {
        int idx = completeText.indexOf(".", currentWordIndex);
        String returnString;
        if (idx != -1) {
            returnString = completeText.substring(currentWordIndex, idx + 1);
            currentWordIndex = idx + 1;
        } else {
            returnString = completeText.substring(currentWordIndex);
            currentWordIndex = -1;
        }
        return returnString.trim();
    }

    private String getWordsByNumber() {
        StringBuilder builder = new StringBuilder();
        int totalWords = currentWordIndex + numberOfWords < wordsArray.length ? numberOfWords : wordsArray.length - currentWordIndex;
        for (int i = 0; i < totalWords; i++) {
            builder.append(wordsArray[i + currentWordIndex]);
            builder.append(" ");
        }
        return builder.toString();
    }

    public int getNumberOfWords() {
        return numberOfWords;
    }

    public void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if (running) {
            if (runningThread == null) {
                runningThread = new Thread(this);
                runningThread.start();
            } else {
                runningThread.interrupt();
                runningThread = null;
                setRunning(true);
            }
        } else {
            if (runningThread != null && runningThread.isAlive()) {
                runningThread.interrupt();
            }
        }
    }

    public void toggleRunning() {
        setRunning(!isRunning());
    }

    public void setCustomText(String text) {
        if (text != null) {
            completeText = text;
            if (promptType == PROMPT_TYPE_WORDS) {
                wordsArray = text.split(" ");
            }
            text = getWordsToDisplay();
        }
        super.setText(text);
    }

    public String getCustomText() {
        return completeText;
    }

    public void setPromptType(@PromptType int promptType) {
        this.promptType = promptType;
    }

    @PromptType
    public int getPromptType() {
        return promptType;
    }

    public void setFinishedListener(OnFinishedListener finishedListener) {
        this.finishedListener = finishedListener;
    }

    static class SavedState extends BaseSavedState {

        int numWords;
        int speed;
        boolean running;
        int currentWordIndex;
        int promptType;
        String completeData;
        String[] wordsArray;
        String currentText;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            this.numWords = source.readInt();
            this.speed = source.readInt();
            this.running = source.readByte() != 0;
            this.currentWordIndex = source.readInt();
            this.promptType = source.readInt();
            this.completeData = source.readString();
            source.readStringArray(this.wordsArray);
            this.currentText = source.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(numWords);
            out.writeInt(speed);
            out.writeByte((byte) (running ? 1 : 0));
            out.writeInt(currentWordIndex);
            out.writeInt(promptType);
            out.writeString(completeData);
            out.writeStringArray(wordsArray);
            out.writeString(currentText);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnFinishedListener {
        void onFinished();
    }
}
