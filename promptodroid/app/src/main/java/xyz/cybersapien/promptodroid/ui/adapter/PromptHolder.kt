package xyz.cybersapien.promptodroid.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_prompt_story.view.*

/**
 * Created by ogcybersapien on 15/1/18.
 */
class PromptHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val promptTitleTextView: TextView = itemView.prompts_title_text_view
    val wordsCountTextView: TextView = itemView.words_count_text_view
    val promptsDateTextView: TextView = itemView.prompts_date_text_view
}