package xyz.cybersapien.promptodroid.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.ui.AutoChangingTextView;

public class PromptingActivity extends AppCompatActivity implements AutoChangingTextView.OnFinishedListener {

    private static final String LOG_TAG = PromptingActivity.class.getSimpleName();
    public static final String SELECTED_STORY = "selectedStory";
    public static final String PROMPT_RUNNING = "running";

    @BindView(R.id.changingPromptTextView)
    AutoChangingTextView promptingTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_toggle)
    FloatingActionButton toggleButton;

    private PromptStory story;
    private boolean isRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompting);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            story = getIntent().getParcelableExtra(SELECTED_STORY);
            promptingTextView.setCustomText(story.getStoryDetail());
            isRunning = false;
        } else {
            story = savedInstanceState.getParcelable(SELECTED_STORY);
            isRunning = savedInstanceState.getBoolean(PROMPT_RUNNING);
            if (isRunning) {
                startPrompting();
            }
        }

        setTitle(story.getStoryTitle());
        toggleButton.setOnClickListener(v -> {
            isRunning = !isRunning;
            if (isRunning) {
                startPrompting();
            } else {
                stopPrompting();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_STORY, story);
        outState.putBoolean(PROMPT_RUNNING, isRunning);
    }

    private void startPrompting() {
        toggleButton.setImageResource(R.drawable.ic_pause);
        promptingTextView.setRunning(true);
    }

    private void stopPrompting() {
        toggleButton.setImageResource(R.drawable.ic_play);
        promptingTextView.setRunning(false);
    }

    @Override
    public void onFinished() {
        toggleButton.setImageResource(R.drawable.ic_refresh);
    }
}
