package xyz.cybersapien.promptodroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.DataStore;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.data.model.User;
import xyz.cybersapien.promptodroid.ui.fragment.PromptEditFragment;
import xyz.cybersapien.promptodroid.utils.ConstantsKt;

public class PromptEditActivity extends AppCompatActivity implements PromptEditFragment.InteractionListener {

    private static final String LOG_TAG = PromptEditActivity.class.getSimpleName();
    public static final String CURRENT_USER = "current_user";
    public static final String SELECTED_PROMPT = PromptEditFragment.SELECTED_PROMPT;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.story_detail_fragment_container)
    FrameLayout storyDetailFragmentContainer;

    User currentUser;
    PromptStory story;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            currentUser = getIntent().getParcelableExtra(CURRENT_USER);
            story = getIntent().getParcelableExtra(SELECTED_PROMPT);
            setEditFragment(story);
        } else {
            currentUser = savedInstanceState.getParcelable(CURRENT_USER);
            story = savedInstanceState.getParcelable(SELECTED_PROMPT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_USER, currentUser);
        outState.putParcelable(SELECTED_PROMPT, story);
    }

    private void setEditFragment(@Nullable PromptStory story) {
        Fragment promptEditFragment = PromptEditFragment.getInstance(story);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.story_detail_fragment_container, promptEditFragment)
                .commit();
    }

    @Override
    public void saveNewPrompt(PromptStory story) {
        story.setUserId(currentUser.getUid());
        updatePrompt(story);
    }

    @Override
    public void updatePrompt(PromptStory story) {
        DatabaseReference dbRef = DataStore.getInstance()
                .getUserDataReference(currentUser).child(ConstantsKt.PROMPT_KEY + "/" + story.getDate());
        dbRef.setValue(story);
        finish();
    }

    @Override
    public void startTeleprompt(PromptStory story) {
        Intent prompterIntent = new Intent(PromptEditActivity.this, PromptingActivity.class);
        prompterIntent.putExtra(PromptingActivity.SELECTED_STORY, story);
        startActivity(prompterIntent);
    }
}
