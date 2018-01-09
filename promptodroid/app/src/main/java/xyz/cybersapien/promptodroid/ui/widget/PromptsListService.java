package xyz.cybersapien.promptodroid.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.DataStore;
import xyz.cybersapien.promptodroid.data.model.PromptStory;
import xyz.cybersapien.promptodroid.data.model.User;
import xyz.cybersapien.promptodroid.utils.ConstantsKt;
import xyz.cybersapien.promptodroid.utils.UtilitiesKt;


/**
 * Created by ogcybersapien on 5/1/18.
 */

public class PromptsListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PromptsListViewFactory(getApplicationContext(), intent);
    }

    static class PromptsListViewFactory implements RemoteViewsFactory {

        private Context context;
        private ArrayList<PromptStory> promptStories;
        private User currentUser;
        private CountDownLatch countDownLatch;


        public PromptsListViewFactory(Context context, Intent intent) {
            this.context = context;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUser = new User(user);
            promptStories = new ArrayList<>();
        }

        @Override
        public void onCreate() {
        }

        @Override
        public int getCount() {
            return (promptStories == null) ? 0 : promptStories.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_item_prompt_story);
            PromptStory story = promptStories.get(position);
            view.setTextViewText(R.id.prompts_title_text_view, story.getStoryTitle());
            view.setTextViewText(R.id.prompts_date_text_view,
                    UtilitiesKt.getFormattedDate(story.getDate())
            );
            view.setTextViewText(R.id.words_count_text_view,
                    UtilitiesKt.getWordCount(context, story.getStoryDetail())
            );
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public void onDataSetChanged() {
            // Do Nothing
            countDownLatch = new CountDownLatch(1);
            getData();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void getData() {
            DataStore.getInstance().getUserDataReference(currentUser)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> snapshotIterator = dataSnapshot
                                    .child(ConstantsKt.PROMPT_KEY).getChildren().iterator();
                            ArrayList<PromptStory> stories = new ArrayList<>();
                            for (; snapshotIterator.hasNext(); ) {
                                DataSnapshot currentData = snapshotIterator.next();
                                PromptStory story = currentData.getValue(PromptStory.class);
                                if (story != null) {
                                    stories.add(story);
                                }
                            }
                            if (countDownLatch.getCount() != 0) {
                                promptStories.clear();
                                promptStories.addAll(stories);
                                countDownLatch.countDown();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Do Nothing
                        }
                    });
        }

        @Override
        public void onDestroy() {
            // Do Nothing
        }
    }
}
