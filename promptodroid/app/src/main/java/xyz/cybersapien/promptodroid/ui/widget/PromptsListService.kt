package xyz.cybersapien.promptodroid.ui.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.DataStore
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.data.model.User
import xyz.cybersapien.promptodroid.utils.PROMPT_KEY
import xyz.cybersapien.promptodroid.utils.getFormattedDate
import xyz.cybersapien.promptodroid.utils.getWordCount
import java.util.concurrent.CountDownLatch

/**
 * Created by ogcybersapien on 16/1/18.
 */

class PromptsListService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return PromptsListViewFactory(applicationContext)
    }

    internal class PromptsListViewFactory(
            private val context: Context
    ) : RemoteViewsFactory {

        private var promptStories = ArrayList<PromptStory>()
        private var currentUser: User
        private lateinit var countDownLatch: CountDownLatch

        init {
            val fireUser = FirebaseAuth.getInstance().currentUser!!
            currentUser = User(fireUser)
        }

        override fun onCreate() {

        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getItemId(position: Int): Long = position.toLong()

        override fun onDataSetChanged() {
            countDownLatch = CountDownLatch(1)
            getData()
            try {
                countDownLatch.await()
            } catch (error: InterruptedException) {
                error.printStackTrace()
            }
        }

        private fun getData() {
            DataStore.getUserDataReference(currentUser)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {}

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val snapShotIterator =
                                    dataSnapshot.child(PROMPT_KEY).children.iterator()
                            val stories = ArrayList<PromptStory>()
                            while (snapShotIterator.hasNext()) {
                                val currentData = snapShotIterator.next()
                                val story = currentData.getValue(PromptStory::class.java)
                                if (story != null) {
                                    stories.add(story)
                                }
                            }
                            if (countDownLatch.count != 0L) {
                                promptStories.clear()
                                promptStories.addAll(stories)
                                countDownLatch.countDown()
                            }
                        }
                    })
        }

        override fun hasStableIds() = false

        override fun getViewAt(position: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_item_prompt_story)
            val story = promptStories[position]
            views.setTextViewText(R.id.prompts_title_text_view, story.storyTitle)
            views.setTextViewText(R.id.prompts_date_text_view, getFormattedDate(story.date))
            views.setTextViewText(R.id.words_count_text_view, getWordCount(context, story.storyDetail))
            return views
        }

        override fun getCount(): Int = promptStories.size

        override fun getViewTypeCount(): Int = 1

        override fun onDestroy() = Unit

    }
}