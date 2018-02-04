package xyz.cybersapien.promptodroid.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_prompt_edit.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.utils.getFormattedDate
import xyz.cybersapien.promptodroid.utils.getWordCount

class PromptEditFragment : Fragment() {

    private var newPrompt: Boolean = false
    private var isEditMode: Boolean = false
    private var story: PromptStory? = null
    private var interactionListener: InteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            interactionListener = context
        } else {
            throw ClassCastException("$activity must implement `PromptEditFragment.InteractionListener`")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            newPrompt = arguments == null
            if (!newPrompt) {
                story = arguments!!.getParcelable(SELECTED_PROMPT)
            }
        } else {
            newPrompt = savedInstanceState.getBoolean(IS_NEW_PROMPT)
            story = savedInstanceState.getParcelable(SELECTED_PROMPT)
            isEditMode = savedInstanceState.getBoolean(IS_EDITING_EXTRA)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_prompt_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (newPrompt) {
            initNewPrompt()
        } else {
            initStoryDetail()
            if (isEditMode) {
                initEditor()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_NEW_PROMPT, newPrompt)
        outState.putParcelable(SELECTED_PROMPT, story)
        outState.putBoolean(IS_EDITING_EXTRA, isEditMode)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_edit_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_item_edit) {
            initEditor()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        interactionListener = null
    }

    private fun initEditor() {
        title_text_layout.isEnabled = true
        story_text_layout.isEnabled = true
        fab_play_prompt.setImageResource(R.drawable.ic_check)
        fab_play_prompt.setOnClickListener { saveDetails() }
        isEditMode = true
        enableEditText(title_edit_text)
        enableEditText(story_edit_text)
    }

    private fun initNewPrompt() {
        prompt_date_view.visibility = View.GONE
        prompt_word_count_view.visibility = View.GONE
        initEditor()
    }

    private fun initStoryDetail() {
        setHasOptionsMenu(true)
        disableEditText(title_edit_text)
        disableEditText(story_edit_text)
        title_edit_text.setText(story?.storyTitle)
        story_edit_text.setText(story?.storyDetail)
        val date = getFormattedDate(story!!.date)
        prompt_date_view.text = date
        val wordsCount = getWordCount(context!!, story!!.storyDetail)
        prompt_word_count_view.text = wordsCount
        prompt_date_view.visibility = View.VISIBLE
        prompt_word_count_view.visibility = View.VISIBLE
        fab_play_prompt.setImageResource(R.drawable.ic_play)
        fab_play_prompt.setOnClickListener {
            interactionListener?.startTeleprompt(story!!)
        }
    }

    private fun saveDetails() {
        val storyTitle = title_edit_text.text.toString()
        val storyDetail = story_edit_text.text.toString()
        if (newPrompt) {
            interactionListener?.saveNewPrompt(PromptStory(storyTitle, storyDetail))
        } else {
            story?.storyTitle = storyTitle
            story?.storyDetail = storyDetail
            interactionListener?.updatePrompt(story!!)
        }
    }

    private fun disableEditText(editText: TextInputEditText) {
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        @Suppress("DEPRECATION")
        val backgroundColor: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(R.color.grey850, null)
        else
            resources.getColor(R.color.grey850)
        changeBackgroundColor(backgroundColor)
    }

    private fun enableEditText(editText: TextInputEditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.keyListener = TextInputEditText(context).keyListener
        @Suppress("DEPRECATION")
        val backgroundColor: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(R.color.cardview_dark_background, null)
        else
            resources.getColor(R.color.cardview_dark_background)
        changeBackgroundColor(backgroundColor)
    }

    private fun changeBackgroundColor(backgroundColor: Int) {
        title_text_layout.setBackgroundColor(backgroundColor)
        story_text_layout.setBackgroundColor(backgroundColor)
    }

    companion object {

        const val LOG_TAG: String = "PromptEditFragment"
        const val SELECTED_PROMPT = "selected_prompt"
        const val IS_NEW_PROMPT = "new_prompt"
        const val IS_EDITING_EXTRA = "extra"

        @JvmStatic
        fun getInstance(story: PromptStory?): PromptEditFragment {
            val editFragment = PromptEditFragment()
            if (story != null) {
                val fragBundle = Bundle()
                fragBundle.putParcelable(SELECTED_PROMPT, story)
                editFragment.arguments = fragBundle
            }
            return editFragment
        }
    }

    interface InteractionListener {
        fun saveNewPrompt(story: PromptStory)

        fun updatePrompt(story: PromptStory)

        fun startTeleprompt(story: PromptStory)
    }
}