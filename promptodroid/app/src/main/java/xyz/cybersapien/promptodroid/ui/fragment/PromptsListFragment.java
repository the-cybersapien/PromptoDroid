package xyz.cybersapien.promptodroid.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.DataStore;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.data.model.User;
import xyz.cybersapien.promptodroid.ui.adapter.PromptsAdapter;
import xyz.cybersapien.promptodroid.utils.Constants;
import xyz.cybersapien.recyclerele.RecyclerELEAdapter;

/**
 * A simple {@link Fragment} subclass to show a list of stories to the user.
 */
public class PromptsListFragment extends Fragment {

    @BindView(R.id.prompts_list_recycler_view)
    RecyclerView promptsListRecyclerView;

    @OnClick(R.id.fab_new_prompt)
    void addNew() {
        addNewPromptCallback.addNewPrompt();
    }

    private OnAddNewPrompt addNewPromptCallback;
    private FirebaseUser firebaseUser;
    private User user;
    private RecyclerELEAdapter recyclerAdapter;
    private ArrayList<PromptStory> storyArrayList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddNewPrompt) {
            addNewPromptCallback = (OnAddNewPrompt) context;
        } else {
            throw new IllegalStateException("Containing Activity must implement PromptsListFragment.OnAddNewPrompt");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prompts_list, container, false);
        ButterKnife.bind(this, v);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(firebaseUser);
        storyArrayList = new ArrayList<>();
        PromptsAdapter adapter = new PromptsAdapter(getContext(), storyArrayList);
        recyclerAdapter = new RecyclerELEAdapter(adapter, null, null, null);
        promptsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        promptsListRecyclerView.setAdapter(recyclerAdapter);

        DataStore.getInstance().getUserDataReference(user).addValueEventListener(databaseValueEventListener);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        addNewPromptCallback = null;
    }

    ValueEventListener databaseValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterator<DataSnapshot> snapshotIterator = dataSnapshot
                    .child(Constants.PROMPT_KEY).getChildren().iterator();
            ArrayList<PromptStory> stories = new ArrayList<>();
            for (Iterator<DataSnapshot> it = snapshotIterator; it.hasNext(); ) {
                DataSnapshot currentData = it.next();
                PromptStory story = currentData.getValue(PromptStory.class);
                if (story != null) {
                    stories.add(story);
                }
            }

            storyArrayList.clear();
            storyArrayList.addAll(stories);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            recyclerAdapter.setCurrentView(RecyclerELEAdapter.VIEW_ERROR);
        }
    };

    public interface OnAddNewPrompt {
        void addNewPrompt();
    }

}
