package xyz.cybersapien.promptodroid.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.utils.getFormattedDate
import xyz.cybersapien.promptodroid.utils.getWordCount
import xyz.cybersapien.promptodroid.utils.layoutInflator

/**
 * Created by ogcybersapien on 15/1/18.
 */
class PromptsAdapter(
        val context: Context,
        val storyList: List<PromptStory>,
        val clickListener: OnClickListener
) : RecyclerView.Adapter<PromptHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PromptHolder {
        val view = context.layoutInflator()
                .inflate(R.layout.item_prompt_story, parent, false)
        return PromptHolder(view)
    }

    override fun onBindViewHolder(holder: PromptHolder, position: Int) {
        val currentStory = storyList[position]
        holder.promptTitleTextView.text = currentStory.storyTitle
        val formattedDate = getFormattedDate(currentStory.date)
        holder.promptsDateTextView.text = formattedDate
        holder.wordsCountTextView.text = getWordCount(context, currentStory.storyDetail)
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(holder.layoutPosition)
        }
    }

    override fun getItemCount(): Int = storyList.size

    override fun getItemId(position: Int): Long = position.toLong()

    interface OnClickListener {
        fun onItemClick(index: Int)
    }
}