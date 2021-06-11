package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentArticlesListBinding;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentRecentBinding;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fragment class for showing list of invitations.
 *
 * @author Branko Mikusic
 */
public class RecentFragment extends Fragment {

    private Context context;
    //private InvitationsViewAdapter invitationsViewAdapter;

    public RecentFragment() {
        // Required empty public constructor
    }

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
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        context = getContext();
        //setupFirestoreRecyclerView(root);
        //setupListeners(root);
        //fragmentArticlesListBinding.rvArticles.setLayoutManager(new LinearLayoutManager(fragmentArticlesListBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));

        FragmentRecentBinding fragmentRecentBinding = FragmentRecentBinding.inflate(inflater,container,false);
        fragmentRecentBinding.recentRvArticles.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        fragmentRecentBinding.recentRvArticles.setAdapter(new ArticlesItemRecyclerViewAdapter(Article.ITEMS,getContext(),
                fragmentRecentBinding.flArticlesUpdating,3, R.layout.rv_recent_article_item ));
        fragmentRecentBinding.recentRvConversations.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        /*fragmentRecentBinding.rvArticles.setAdapter(new ArticlesItemRecyclerViewAdapter(Article.ITEMS,getContext(),
                fragmentArticlesListBinding.flArticlesUpdating));*/
        fragmentRecentBinding.recentRvInvitRecom.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        /*fragmentRecentBinding.rvArticles.setAdapter(new ArticlesItemRecyclerViewAdapter(Article.ITEMS,getContext(),
                fragmentArticlesListBinding.flArticlesUpdating));*/

        return fragmentRecentBinding.getRoot();
    }

    /**
     * Sets up listener on a Floating action button
     *
     * @param root root View reference
     */
    private void setupListeners(View root){

    }

    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    /*private void setupFirestoreRecyclerView(View root){
        Query query = FirebaseHandler.getInvitationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<InvitationObject> options = new FirestoreRecyclerOptions.Builder<InvitationObject>()
                .setQuery(query, InvitationObject.class)
                .build();
        invitationsViewAdapter = new InvitationsViewAdapter(options,getContext());
        RecyclerView rvInvitationList = root.findViewById(R.id.invitations_list_rv);
        rvInvitationList.setHasFixedSize(true);
        rvInvitationList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvitationList.setAdapter(invitationsViewAdapter);
    }*/

    /**
     * Every time onStart gets called FirestoreRecyclerAdapter have to start
     * listening to online database changes because each time onStop gets
     * called it stops listening.
     */
    /*@Override
    public void onStart() {
        super.onStart();
        invitationsViewAdapter.startListening();
    }*/

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    /*@Override
    public void onStop() {
        super.onStop();
        invitationsViewAdapter.stopListening();
    }*/
}