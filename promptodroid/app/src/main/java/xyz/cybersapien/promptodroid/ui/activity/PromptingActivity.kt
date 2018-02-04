package xyz.cybersapien.promptodroid.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prompting.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.ui.AutoChangingTextView

/**
 * Created by ogcybersapien on 24/1/18.
 */
class PromptingActivity : AppCompatActivity(), AutoChangingTextView.OnFinishedListener {

    private lateinit var story: PromptStory
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompting)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            story = intent.getParcelableExtra(SELECTED_STORY)
            changingPromptTextView.completeText = story.storyDetail
            isRunning = false
        } else {
            story = savedInstanceState.getParcelable(SELECTED_STORY)
            isRunning = savedInstanceState.getBoolean(PROMPT_RUNNING)
        }

        changingPromptTextView.finishedListener = this

        title = story.storyTitle
        fab_toggle.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) {
                startPrompts()
            } else {
                stopPrompts()
            }
        }

        if (isRunning) {
            startPrompts()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SELECTED_STORY, story)
        outState.putBoolean(PROMPT_RUNNING, isRunning)
    }

    private fun startPrompts() {
        fab_toggle.setImageResource(R.drawable.ic_pause)
        fab_toggle.contentDescription = getString(R.string.stop_prompt)
        changingPromptTextView.running = true
    }

    private fun stopPrompts() {
        fab_toggle.setImageResource(R.drawable.ic_play)
        fab_toggle.contentDescription = getString(R.string.start_prompt)
        changingPromptTextView.running = false
    }

    override fun onTextChangeFinished() {
        runOnUiThread {
            fab_toggle.setImageResource(R.drawable.ic_refresh)
            fab_toggle.contentDescription = getString(R.string.restart_prompt)
        }
    }


    companion object {
        const val SELECTED_STORY = "selectedStory"
        const val PROMPT_RUNNING = "running"
        const val LOG_TAG = "PromptingActivity"
    }
}