package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

/**
 * Activity that is responsible  for showing the content of an conversation topic that was chosen from the list
 * of conversations shown in ConversationsFragment fragment.
 *
 * @author Branko Mikusic 2021
 */
public class ConversationDetailActivity extends AppCompatActivity {

    private PostViewAdapter postViewAdapter;
    private RecyclerView recyclerView;

    /**
     * Toolbar is set up here with home (back) button enabled. Values are fetched from bundle object
     * sent through the intent. This layout views are set-up with those values. OnClick listener is set up on
     * Floating Action Button for creating new post.
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_conversation_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button

        Intent intent = getIntent();
        Bundle conversationBundle = intent.getBundleExtra(ConversationsViewAdapter.INTENT_EXTRA_KEY_BUNDLE);
        TextView tv_title = findViewById(R.id.conversation_detail_tv_title);
        TextView tv_intro = findViewById(R.id.conversation_detail_tv_intro);
        ScrollView scr_view = findViewById(R.id.conversation_detail_scrollview);
        FloatingActionButton fabNewPost = findViewById(R.id.conversation_detail_fab_newpost);
        recyclerView = findViewById(R.id.conversation_detail_rv_postslist);

        tv_title.setText(conversationBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_TITLE,""));
        tv_intro.setText(conversationBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_INTRO,""));
        scr_view.fullScroll(View.FOCUS_UP);

        fabNewPost.setOnClickListener((view)-> {
            Intent intentPost = new Intent(this, EnterNewPostActivity.class);
            Bundle postEntryBundle = new Bundle();
            postEntryBundle.putString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_DOCUMENTID,
                    conversationBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_DOCUMENTID));
            intentPost.putExtra(ConversationsViewAdapter.INTENT_EXTRA_KEY_BUNDLE,postEntryBundle);
            startActivity(intentPost);
        });

        setupRecycler(conversationBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_DOCUMENTID));
    }

    /**
     * FirestoreRecyclerView is set up here.
     *
     * @param conversationDocumentId
     */
    private void setupRecycler(String conversationDocumentId){
        Query query = FirebaseHandler.getPostCollectionReference(conversationDocumentId).orderBy("creationTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostObject> options = new FirestoreRecyclerOptions.Builder<PostObject>()
                .setQuery(query, PostObject.class)
                .build();
        postViewAdapter = new PostViewAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postViewAdapter);
    }

    /**
     *  When onStart gets called FirestoreRecyclerAdapter have to start
     *  listening to online database changes because each time onStop gets
     *  called it stops listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        postViewAdapter.startListening();
        recyclerView.scrollToPosition(0);
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        postViewAdapter.stopListening();
    }

    /**
     * Overrides the supertype method to add action that facilitate functionality of the "back" button in the
     * toolbar. The action is to close the Activity.
     *
     * @param item MenuItem reference, here it refers to "home" button in toolbar.
     * @return true if item was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}