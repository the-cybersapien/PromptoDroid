package xyz.cybersapien.promptodroid.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromptEditFragment extends Fragment {

    private static final String LOG_TAG = PromptEditFragment.class.getSimpleName();
    public static final String SELECTED_PROMPT = "selected_prompt";
    public static final String IS_NEW_PROMPT = "new_prompt";
    public static final String IS_EDITING_EXTRA = "extra";

    private boolean newPrompt;
    private boolean isEditMode;
    private PromptStory story;
    private InteractionListener interactionListener;

    @BindView(R.id.title_field_boxes)
    TextFieldBoxes titleFieldBoxes;
    @BindView(R.id.title_extended_edit_text)
    ExtendedEditText titleEditText;
    @BindView(R.id.story_text_field_box)
    TextFieldBoxes storyFieldBoxes;
    @BindView(R.id.story_extended_edit_text)
    ExtendedEditText storyEditText;
    @BindView(R.id.prompt_date_view)
    TextView promptDateTextView;
    @BindView(R.id.prompt_word_count_view)
    TextView wordCountTextView;
    @BindView(R.id.fab_edit_prompt)
    FloatingActionButton actionButton;

    public static PromptEditFragment getInstance(@Nullable PromptStory story) {
        PromptEditFragment editFragment = new PromptEditFragment();
        if (story != null) {
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putParcelable(SELECTED_PROMPT, story);
            editFragment.setArguments(fragmentBundle);
        }
        return editFragment;
    }

    public PromptEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            interactionListener = (InteractionListener) context;
        } else {
            throw new ClassCastException(getActivity().toString() + " must implement `PromptEditFragment.InteractionListener`");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            newPrompt = args == null;
            if (!newPrompt) {
                story = args.getParcelable(SELECTED_PROMPT);
            }
        } else {
            newPrompt = savedInstanceState.getBoolean(IS_NEW_PROMPT);
            story = savedInstanceState.getParcelable(SELECTED_PROMPT);
            isEditMode = savedInstanceState.getBoolean(IS_EDITING_EXTRA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prompt_edit, container, false);
        ButterKnife.bind(this, rootView);

        if (newPrompt) {
            initNewPrompt();
        } else {
            initStoryDetail();
            if (isEditMode) {
                initEditor();
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_NEW_PROMPT, newPrompt);
        outState.putParcelable(SELECTED_PROMPT, story);
        outState.putBoolean(IS_EDITING_EXTRA, isEditMode);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_edit) {
            initEditor();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    private void initEditor() {
        titleFieldBoxes.setEnabled(true);
        storyFieldBoxes.setEnabled(true);
        actionButton.setImageResource(R.drawable.ic_check);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });
        isEditMode = true;
    }

    private void initNewPrompt() {
        promptDateTextView.setVisibility(View.GONE);
        wordCountTextView.setVisibility(View.GONE);
        initEditor();
    }

    private void initStoryDetail() {
        setHasOptionsMenu(true);
        titleEditText.setText(story.getStoryTitle());
        storyEditText.setText(story.getStoryDetail());
        String date = Utilities.getFormattedDate(story.getDate());
        promptDateTextView.setText(date);
        int wordCount = story.getStoryDetail().split(" ").length;
        wordCountTextView.setText(getString(R.string.words_string, wordCount));
        promptDateTextView.setVisibility(View.VISIBLE);
        wordCountTextView.setVisibility(View.VISIBLE);
        actionButton.setImageResource(R.drawable.ic_play);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.startTeleprompt(story);
            }
        });
    }

    private void saveDetails() {
        String storyTitle = titleEditText.getText().toString();
        String storyDetail = storyEditText.getText().toString();
        if (newPrompt) {
            interactionListener.saveNewPrompt(new PromptStory(storyTitle, storyDetail));
        } else {
            story.setStoryTitle(storyTitle);
            story.setStoryDetail(storyDetail);
            interactionListener.updatePrompt(story);
        }
    }

    public interface InteractionListener {
        void saveNewPrompt(PromptStory story);

        void updatePrompt(PromptStory story);

        void startTeleprompt(PromptStory story);
    }
}
