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
import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentConversationsListBinding;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
        context = getContext();
        FragmentConversationsListBinding fragmentConversationsListBinding = FragmentConversationsListBinding.inflate(inflater,
                container,false);
        setupAdminOptions(fragmentConversationsListBinding);
        setupFirestoreRecyclerView(fragmentConversationsListBinding);
        return fragmentConversationsListBinding.getRoot();
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
     * @param fragmentConversationsListBinding Reference to the View binder
     */
    private void setupAdminOptions(FragmentConversationsListBinding fragmentConversationsListBinding){
        if(UserObject.getUserObjectInstance().getIsAdmin()){
            fragmentConversationsListBinding.fabNewConversation.setVisibility(View.VISIBLE);
            fragmentConversationsListBinding.fabNewConversation.setOnClickListener((view)->{
                Intent newConversationIntent = new Intent(context, EnterNewConversationActivity.class);
                startActivity(newConversationIntent);
            });
        }
    }

    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    private void setupFirestoreRecyclerView(FragmentConversationsListBinding fragmentConversationsListBinding){
        Query query = FirebaseHandler.getConversationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ConversationObject> options = new FirestoreRecyclerOptions.Builder<ConversationObject>()
                .setQuery(query,ConversationObject.class)
                .build();
        conversationsViewAdapter = new ConversationsViewAdapter(options,getContext(),R.layout.rv_conversations_item, false);
        fragmentConversationsListBinding.rvConversationslist.setHasFixedSize(true);
        fragmentConversationsListBinding.rvConversationslist.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentConversationsListBinding.rvConversationslist.setAdapter(conversationsViewAdapter);
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