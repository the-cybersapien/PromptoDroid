package xyz.cybersapien.promptodroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
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
    private int currentWordIndex = 0;
    @PromptType
    private int promptType = PROMPT_TYPE_LINE;
    private Thread runningThread;

    public AutoChangingTextView(Context context) {
        super(context);
        running = false;
        runningThread = new Thread(this);
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
        runningThread = new Thread(this);
        setCustomText(array.getNonResourceString(R.styleable.AutoChangingTextView_customText));
        array.recycle();
        setRunning(running);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(speed * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final String displayWords = getWordsToDisplay();
            Log.i(LOG_TAG, "run: " + displayWords);
            post(new Runnable() {
                @Override
                public void run() {
                    setText(displayWords);
                }
            });
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
        String returnString = completeText.substring(currentWordIndex, idx + 1);
        currentWordIndex = idx + 1;
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
            runningThread.start();
        } else {
            if (runningThread.isAlive()) {
                runningThread.interrupt();
                runningThread = new Thread(this);
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
}
