package xyz.cybersapien.promptodroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prompt_edit.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.DataStore
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.data.model.User
import xyz.cybersapien.promptodroid.ui.fragment.PromptEditFragment
import xyz.cybersapien.promptodroid.utils.PROMPT_KEY

/**
 * Created by ogcybersapien on 24/1/18.
 */
class PromptEditActivity : AppCompatActivity(), PromptEditFragment.InteractionListener {

    private lateinit var currentUser: User
    private var story: PromptStory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt_edit)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            currentUser = intent.getParcelableExtra(CURRENT_USER)
            story = intent.getParcelableExtra(SELECTED_PROMPT)
            setEditFragment(story)
        } else {
            currentUser = savedInstanceState[CURRENT_USER] as User
            story = savedInstanceState[SELECTED_PROMPT] as PromptStory?
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CURRENT_USER, currentUser)
        outState.putParcelable(SELECTED_PROMPT, story)
    }

    private fun setEditFragment(story: PromptStory?) {
        val promptEditFragment = PromptEditFragment.getInstance(story)
        supportFragmentManager.beginTransaction()
                .replace(R.id.story_detail_fragment_container, promptEditFragment)
                .commit()
    }

    override fun saveNewPrompt(story: PromptStory) {
        story.userId = currentUser.uid
        updatePrompt(story)
    }

    override fun updatePrompt(story: PromptStory) {
        val dbRef = DataStore.getUserDataReference(currentUser)
                .child("$PROMPT_KEY/${story.date}")
        dbRef.setValue(story)
        finish()
    }

    override fun startTeleprompt(story: PromptStory) {
        val prompterIntent = Intent(this, PromptingActivity::class.java)
        prompterIntent.putExtra(PromptingActivity.SELECTED_STORY, story)
        startActivity(prompterIntent)
    }

    companion object {
        const val LOG_TAG = "PromptEditActivity"
        const val CURRENT_USER = "current_user"
        const val SELECTED_PROMPT = PromptEditFragment.SELECTED_PROMPT
    }
}