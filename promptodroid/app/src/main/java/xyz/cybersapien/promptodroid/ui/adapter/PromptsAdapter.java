package xyz.cybersapien.promptodroid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.utils.Utilities;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class PromptsAdapter extends RecyclerView.Adapter<PromptHolder> {

    private static final String LOG_TAG = PromptsAdapter.class.getSimpleName();
    private Context context;
    private List<PromptStory> storyList;
    private OnClickListener interactionListener;

    public PromptsAdapter(Context context, List<PromptStory> storyList, OnClickListener clickListener) {
        this.context = context;
        interactionListener = clickListener;
        this.storyList = storyList;
        setHasStableIds(true);
    }

    @Override
    public PromptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prompt_story, parent, false);
        return new PromptHolder(view);
    }

    @Override
    public void onBindViewHolder(final PromptHolder holder, int position) {
        PromptStory currentStory = storyList.get(position);

        int words = currentStory.getStoryDetail().split(" ").length;

        holder.promptTitleTextView.setText(currentStory.getStoryTitle());
        String formattedDate = Utilities.getFormattedDate(currentStory.getDate());
        holder.promptsDateTextView.setText(formattedDate);
        holder.wordsCountTextView.setText(context.getString(R.string.words_string, words));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.onItemClick(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnClickListener {
        void onItemClick(int i);
    }
}
