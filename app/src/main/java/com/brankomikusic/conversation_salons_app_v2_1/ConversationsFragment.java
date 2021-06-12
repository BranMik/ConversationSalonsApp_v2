package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

/**
 * Fragment class for showing list of conversation topics.
 *
 * @author Branko Mikusic
 */
public class ConversationsFragment extends Fragment {

    public ConversationsFragment(){

    }

    private ConversationsViewAdapter conversationsViewAdapter;
    private static Context context;

    /**
     * Root view of the layout is inflated.
     *
     * @param inflater Inflater object.
     * @param container Root ViewGroup object of the fragment's layout.
     * @param savedInstanceState Bundle that may contain saved values/states (or could be null).
     * @return an inflated View that belongs to this fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        context = getContext();
        setupAdminOptions(root);
        setupFirestoreRecyclerView(root);
        return root;
    }

    /**
     * Updating Articles list for ArticlesRecycler view is initiated from here so we are sure that source list
     * for Recycler does not get invalidated while it is showing data.
     */
    @Override
    public void onResume(){
        super.onResume();
        if(UpdateArticlesInDbFromBlog.articles_reinitialize_flag){
            Article.initArticleList(context);
        }else{
            Log.d(MainActivity.LOG_BR_INFO,"Articles don't need to be reinitialized");
        }
    }

    /**
     * Check is made if loged-in user is admin, if true then Floating Action Button for adding new Conversations
     * is shown and onClick action added, otherwise button stays invisible.
     * @param root Reference to the root View of the layout
     */
    private void setupAdminOptions(View root){
        if(UserObject.getUserObjectInstance().getIsAdmin()){
            FloatingActionButton fabNewConversation = root.findViewById(R.id.fab_newConversation);
            fabNewConversation.setVisibility(View.VISIBLE);
            fabNewConversation.setOnClickListener((view)->{
                Intent newConversationIntent = new Intent(context, EnterNewConversationActivity.class);
                startActivity(newConversationIntent);
            });
        }
    }

    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    private void setupFirestoreRecyclerView(View root){
        Query query = FirebaseHandler.getConversationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ConversationObject> options = new FirestoreRecyclerOptions.Builder<ConversationObject>()
                .setQuery(query,ConversationObject.class)
                .build();
        Log.d(MainActivity.LOG_BR_INFO,"Size of query : " + String.valueOf(options.getSnapshots().size()));
        conversationsViewAdapter = new ConversationsViewAdapter(options,getContext(),R.layout.rv_conversations_item, false);
        RecyclerView rvConversationsList = root.findViewById(R.id.rv_conversationslist);
        rvConversationsList.setHasFixedSize(true);
        rvConversationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversationsList.setAdapter(conversationsViewAdapter);
    }

    /**
     * Every time onStart gets called FirestoreRecyclerAdapter have to start
     * listening to online database changes because each time onStop gets
     * called it stops listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        conversationsViewAdapter.startListening();
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        conversationsViewAdapter.stopListening();
    }

}