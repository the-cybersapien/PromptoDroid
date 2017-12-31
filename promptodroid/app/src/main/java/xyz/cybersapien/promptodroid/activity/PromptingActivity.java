package xyz.cybersapien.promptodroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.ui.AutoChangingTextView;

public class PromptingActivity extends AppCompatActivity {

    private static final String LOG_TAG = PromptingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompting);
        final AutoChangingTextView scrollingTextView = findViewById(R.id.scrollingTextView);
        scrollingTextView.setCustomText(getString(R.string.lorem_ipsum));

        findViewById(R.id.toggle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollingTextView.toggleRunning();
            }
        });
    }
}
