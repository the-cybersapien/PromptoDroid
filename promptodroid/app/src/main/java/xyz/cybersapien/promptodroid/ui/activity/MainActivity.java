package xyz.cybersapien.promptodroid.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.DataStore;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.data.model.User;
import xyz.cybersapien.promptodroid.ui.fragment.PromptEditFragment;
import xyz.cybersapien.promptodroid.ui.fragment.PromptsListFragment;
import xyz.cybersapien.promptodroid.utils.Constants;

public class MainActivity extends AppCompatActivity implements PromptsListFragment.InteractionListener,
        PromptEditFragment.InteractionListener {

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
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = new User(user);

        isTwoPane = storyDetailFragmentContainer != null;

        if (savedInstanceState == null) {
            promptsListFragment = new PromptsListFragment();
            // Initialize and attach the List Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stories_fragment_container, promptsListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEditFragment(@Nullable PromptStory story) {
        promptEditFragment = PromptEditFragment.getInstance(story);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.story_detail_fragment_container, promptEditFragment)
                .commit();
    }

    @Override
    public void addNewPrompt() {
        if (isTwoPane) {
            setEditFragment(null);
        } else {
            launchMobileActivity(null);
        }
    }

    @Override
    public void recyclerItemSelected(PromptStory story) {
        if (isTwoPane) {
            setEditFragment(story);
        } else {
            launchMobileActivity(story);
        }
    }

    private void launchMobileActivity(PromptStory story) {
        Intent intent = new Intent(MainActivity.this, PromptEditActivity.class);
        intent.putExtra(PromptEditActivity.CURRENT_USER, currentUser);
        intent.putExtra(PromptEditActivity.SELECTED_PROMPT, story);
        startActivity(intent);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void saveNewPrompt(PromptStory story) {
        story.setUserId(currentUser.getUid());
        updatePrompt(story);
    }

    @Override
    public void updatePrompt(PromptStory story) {
        DatabaseReference dbRef = DataStore.getInstance().getUserDataReference(currentUser).child(Constants.PROMPT_KEY + "/" + story.getDate());
        dbRef.setValue(story);
        getSupportFragmentManager().beginTransaction()
                .remove(promptEditFragment).commit();
    }

    @Override
    public void startTeleprompt(PromptStory story) {
        Intent prompterIntent = new Intent(MainActivity.this, PromptingActivity.class);
        prompterIntent.putExtra(PromptingActivity.SELECTED_STORY, story);
        startActivity(prompterIntent);
    }
}
