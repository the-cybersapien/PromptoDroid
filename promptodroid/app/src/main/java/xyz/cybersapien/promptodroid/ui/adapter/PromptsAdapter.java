package xyz.cybersapien.promptodroid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.model.PromptStory;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class PromptsAdapter extends RecyclerView.Adapter<PromptHolder> {

    private Context context;
    private List<PromptStory> storyList;

    public PromptsAdapter(Context context, List<PromptStory> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @Override
    public PromptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prompt_story, parent, false);
        return new PromptHolder(view);
    }

    @Override
    public void onBindViewHolder(PromptHolder holder, int position) {
        PromptStory currentStory = storyList.get(position);

        int words = currentStory.getStoryDetail().split(" ").length;

        holder.promptTitleTextView.setText(currentStory.getStoryTitle());
        holder.promptsDateTextView.setText(getFormattedDate(currentStory.getDate()));
        holder.wordsCountTextView.setText(context.getString(R.string.words_string, words));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    private static String getFormattedDate(long date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        return dateFormat.format(new Date(date));
    }

}
