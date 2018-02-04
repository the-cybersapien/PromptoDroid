package xyz.cybersapien.promptodroid.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_prompts_list.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.DataStore
import xyz.cybersapien.promptodroid.data.model.PromptStory
import xyz.cybersapien.promptodroid.data.model.User
import xyz.cybersapien.promptodroid.ui.adapter.PromptsAdapter
import xyz.cybersapien.promptodroid.utils.PROMPT_KEY
import xyz.cybersapien.recyclerele.RecyclerELEAdapter

class PromptsListFragment : Fragment(), PromptsAdapter.OnClickListener {

    private val LOG_TAG = PromptsListFragment::class.java.simpleName

    private lateinit var interactionListener: InteractionListener
    private val storyArrayList: ArrayList<PromptStory> = ArrayList()
    private lateinit var recyclerAdapter: RecyclerELEAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            interactionListener = context
        } else {
            throw ClassCastException(activity!!.javaClass.name + " must implement PromptsListFragment.InteractionListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_prompts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = interactionListener.getCurrentUser()
        prompts_list_recycler_view.layoutManager = LinearLayoutManager(context)

        val emptyView = layoutInflater.inflate(R.layout.view_empty, prompts_list_recycler_view, false)
        val errorView = layoutInflater.inflate(R.layout.view_error, prompts_list_recycler_view, false)
        val promptsAdapter = PromptsAdapter(context!!, storyArrayList, this)
        recyclerAdapter = RecyclerELEAdapter(promptsAdapter, emptyView, null, errorView)

        prompts_list_recycler_view.adapter = recyclerAdapter
        recyclerAdapter.currentView = RecyclerELEAdapter.VIEW_EMPTY
        DataStore.getUserDataReference(user).addValueEventListener(databaseValueListener)
        fab_new_prompt.setOnClickListener { interactionListener.addNewPrompt() }
    }

    private val databaseValueListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val snapIterator = dataSnapshot.child(PROMPT_KEY)
                    .children.iterator()
            // TODO: Start from Line 96
            val stories = ArrayList<PromptStory>()
            while (snapIterator.hasNext()) {
                val currentData = snapIterator.next()
                val story = currentData.getValue(PromptStory::class.java)
                if (story != null) {
                    stories.add(story)
                }
            }

            storyArrayList.clear()
            storyArrayList.addAll(stories)
            recyclerAdapter.notifyDataSetChanged()
            recyclerAdapter.currentView = RecyclerELEAdapter.VIEW_NORMAL
        }

        override fun onCancelled(p0: DatabaseError?) {
            recyclerAdapter.currentView = RecyclerELEAdapter.VIEW_ERROR
        }
    }

    override fun onItemClick(index: Int) {
        interactionListener.recyclerItemSelected(storyArrayList[index])
    }

    interface InteractionListener {
        fun addNewPrompt()

        fun recyclerItemSelected(story: PromptStory)

        fun getCurrentUser(): User
    }
}