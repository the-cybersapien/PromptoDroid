package xyz.cybersapien.promptodroid.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class PromptHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.prompts_title_text_view)
    public TextView promptTitleTextView;
    @BindView(R.id.words_count_text_view)
    public TextView wordsCountTextView;
    @BindView(R.id.prompts_date_text_view)
    public TextView promptsDateTextView;

    public PromptHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
