package xyz.cybersapien.promptodroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.DataStore
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.data.model.User
import xyz.cybersapien.promptodroid.ui.fragment.PromptEditFragment
import xyz.cybersapien.promptodroid.ui.fragment.PromptsListFragment
import xyz.cybersapien.promptodroid.utils.PROMPT_KEY

/**
 * Created by ogcybersapien on 24/1/18.
 */
class MainActivity : AppCompatActivity(), PromptsListFragment.InteractionListener, PromptEditFragment.InteractionListener {

    private var isTwoPane: Boolean = false
    private lateinit var promptEditFragment: PromptEditFragment
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val fireUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(fireUser)

        isTwoPane = story_detail_fragment_container != null

        if (savedInstanceState == null) {
            val promptsListFragment = PromptsListFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.stories_fragment_container, promptsListFragment)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.log_out -> {
                    val dialog = AlertDialog.Builder(this)
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.log_out_confirm)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                finish()
                            }.setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }.create()
                    dialog.show()
                    true
                }
                R.id.about -> {
                    val about = Intent(this, AboutActivity::class.java)
                    startActivity(about)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    fun setEditFragment(story: PromptStory?) {
        promptEditFragment = PromptEditFragment.getInstance(story)
        supportFragmentManager.beginTransaction()
                .replace(R.id.story_detail_fragment_container, promptEditFragment)
                .commit()
    }

    private fun launchMobileActivity(story: PromptStory?) {
        val intent = Intent(this, PromptEditActivity::class.java)
        intent.putExtra(PromptEditActivity.CURRENT_USER, currentUser)
        intent.putExtra(PromptEditActivity.SELECTED_PROMPT, story)
        startActivity(intent)
    }

    override fun addNewPrompt() =
            when (isTwoPane) {
                true -> setEditFragment(null)
                false -> launchMobileActivity(null)
            }

    override fun recyclerItemSelected(story: PromptStory) =
            when (isTwoPane) {
                true -> setEditFragment(story)
                false -> launchMobileActivity(story)
            }

    override fun getCurrentUser(): User = currentUser

    override fun saveNewPrompt(story: PromptStory) {
        story.userId = currentUser.uid
        updatePrompt(story)
    }

    override fun updatePrompt(story: PromptStory) {
        val dbRef = DataStore.getUserDataReference(currentUser)
                .child("$PROMPT_KEY/${story.date}")
        dbRef.setValue(story)
        supportFragmentManager.beginTransaction()
                .remove(promptEditFragment).commit()
    }

    override fun startTeleprompt(story: PromptStory) {
        val prompterIntent = Intent(this@MainActivity, PromptingActivity::class.java)
        prompterIntent.putExtra(PromptingActivity.SELECTED_STORY, story)
        startActivity(prompterIntent)
    }

}