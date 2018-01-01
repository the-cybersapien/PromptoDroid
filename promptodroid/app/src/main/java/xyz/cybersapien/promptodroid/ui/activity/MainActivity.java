package xyz.cybersapien.promptodroid.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.ui.fragment.PromptEditFragment;
import xyz.cybersapien.promptodroid.ui.fragment.PromptsListFragment;
import xyz.cybersapien.promptodroid.utils.Constants;

public class MainActivity extends AppCompatActivity implements PromptsListFragment.OnAddNewPrompt {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.stories_fragment_container)
    FrameLayout StoryListFragmentContainer;
    @BindView(R.id.story_detail_fragment_container)
    @Nullable
    FrameLayout storyDetailFragmentContainer;

    private boolean isTwoPane;
    private PromptsListFragment promptsListFragment;
    private PromptEditFragment promptEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        isTwoPane = storyDetailFragmentContainer != null;

        if (savedInstanceState == null) {
            promptsListFragment = new PromptsListFragment();
            // Initialize and attach the List Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stories_fragment_container, promptsListFragment)
                    .commit();

        }

        if (isTwoPane) {
            setEditFragment(null);
        }
    }

    private void setEditFragment(@Nullable PromptStory story) {
        if (promptEditFragment == null) {
            promptEditFragment = new PromptEditFragment();
        }
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable(Constants.PROMPT_PARCELABLE_KEY, story);
        promptEditFragment.setArguments(fragmentBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.story_detail_fragment_container, promptEditFragment)
                .commit();
    }

    @Override
    public void addNewPrompt() {
        // TODO: New Fragment activity or New Fragment in Container
    }
}
