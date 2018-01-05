package xyz.cybersapien.promptodroid.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class PromptsListFragment extends Fragment implements PromptsAdapter.OnClickListener {

    private static final String LOG_TAG = PromptsListFragment.class.getSimpleName();

    @BindView(R.id.prompts_list_recycler_view)
    RecyclerView promptsListRecyclerView;

    @OnClick(R.id.fab_new_prompt)
    void addNew() {
        interactionListener.addNewPrompt();
    }

    private InteractionListener interactionListener;
    private User user;
    private RecyclerELEAdapter recyclerAdapter;
    private ArrayList<PromptStory> storyArrayList;

    private View emptyView;
    private View errorView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            interactionListener = (InteractionListener) context;
        } else {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement PromptsListFragment.InteractionListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prompts_list, container, false);
        ButterKnife.bind(this, v);
        user = interactionListener.getCurrentUser();
        promptsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyView = inflater.inflate(R.layout.view_empty, promptsListRecyclerView, false);
        errorView = inflater.inflate(R.layout.view_error, promptsListRecyclerView, false);

        storyArrayList = new ArrayList<>();
        PromptsAdapter adapter = new PromptsAdapter(getContext(), storyArrayList, this);
        recyclerAdapter = new RecyclerELEAdapter(adapter, emptyView, null, errorView);

        promptsListRecyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setCurrentView(RecyclerELEAdapter.VIEW_EMPTY);

        DataStore.getInstance().getUserDataReference(user).addValueEventListener(databaseValueEventListener);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    ValueEventListener databaseValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> snapshotIterator = dataSnapshot
                    .child(Constants.PROMPT_KEY).getChildren().iterator();
            ArrayList<PromptStory> stories = new ArrayList<>();
            for (; snapshotIterator.hasNext(); ) {
                DataSnapshot currentData = snapshotIterator.next();
                PromptStory story = currentData.getValue(PromptStory.class);
                if (story != null) {
                    stories.add(story);
                    Log.d(LOG_TAG, "onDataChange: " + story);
                }
            }

            storyArrayList.clear();
            storyArrayList.addAll(stories);
            recyclerAdapter.notifyDataSetChanged();
            recyclerAdapter.setCurrentView(RecyclerELEAdapter.VIEW_NORMAL);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            recyclerAdapter.setCurrentView(RecyclerELEAdapter.VIEW_ERROR);
        }
    };

    @Override
    public void onItemClick(int i) {
        interactionListener.recyclerItemSelected(storyArrayList.get(i));
    }

    public interface InteractionListener {
        void addNewPrompt();

        void recyclerItemSelected(PromptStory story);

        User getCurrentUser();
    }

}
