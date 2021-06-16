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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentArticlesListBinding;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentRecentBinding;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

/**
 * Fragment class for showing list of invitations.
 *
 * @author Branko Mikusic
 */
public class RecentFragment extends Fragment {

    private Context context;
    private FragmentRecentBinding fragmentRecentBinding;
    private ConversationsViewAdapter conversationsViewAdapter;
    private InvitationsViewAdapter invitationsViewAdapter;
    private RecommendationsViewAdapter recommendationsViewAdapter;

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
        context = getContext();
        fragmentRecentBinding = FragmentRecentBinding.inflate(inflater,container,false);

        setupAnnouncement();

        fragmentRecentBinding.recentRvArticles.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        fragmentRecentBinding.recentRvArticles.setAdapter(new ArticlesItemRecyclerViewAdapter(Article.ITEMS,getContext(),
                fragmentRecentBinding.flArticlesUpdating,5, R.layout.rv_recent_article_item ));

        fragmentRecentBinding.recentRvConversations.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        setupConversationsAdapter();

        fragmentRecentBinding.recentRvInvitations.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        setupInvitationsAdapter();

        fragmentRecentBinding.recentRvRecommendations.setLayoutManager(new LinearLayoutManager(fragmentRecentBinding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecommendationsAdapter();

        setupListeners();

        return fragmentRecentBinding.getRoot();
    }


    private void setupAnnouncement(){
        FirebaseHandler.populateAnnouncementFromFirestore(context, fragmentRecentBinding.recentAnnouncementTvTitle, null,
                fragmentRecentBinding.recentAnnouncementTvDate,fragmentRecentBinding.recentAnnouncementIv);

        fragmentRecentBinding.recentCvAnnouncement.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_announcement);
        });
    }

    private void setupConversationsAdapter(){
        Query query = FirebaseHandler.getConversationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING).limit(6);
        FirestoreRecyclerOptions<ConversationObject> options = new FirestoreRecyclerOptions.Builder<ConversationObject>()
                .setQuery(query,ConversationObject.class)
                .build();
        conversationsViewAdapter = new ConversationsViewAdapter(options,getContext(),R.layout.rv_recent_conversation_item, true);
        fragmentRecentBinding.recentRvConversations.setAdapter(conversationsViewAdapter);
     }

     private void setupInvitationsAdapter(){
         Query query = FirebaseHandler.getInvitationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING).limit(5);
         FirestoreRecyclerOptions<InvitationObject> options = new FirestoreRecyclerOptions.Builder<InvitationObject>()
                 .setQuery(query, InvitationObject.class)
                 .build();
         invitationsViewAdapter = new InvitationsViewAdapter(options,getContext(), R.layout.rv_recent_invitation_item, true);
         fragmentRecentBinding.recentRvInvitations.setAdapter(invitationsViewAdapter);
     }

     private void setupRecommendationsAdapter(){
         Query query = FirebaseHandler.getRecommendationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING).limit(5);
         FirestoreRecyclerOptions<RecommendationObject> options = new FirestoreRecyclerOptions.Builder<RecommendationObject>()
                 .setQuery(query, RecommendationObject.class)
                 .build();
         recommendationsViewAdapter = new RecommendationsViewAdapter(options, getContext(), R.layout.rv_recent_recommendation_item, true);
         fragmentRecentBinding.recentRvRecommendations.setAdapter(recommendationsViewAdapter);
     }

    /**
     * Set up listeners for icons representing sections.
     *
     */
    private void setupListeners(){
        fragmentRecentBinding.recentIvAnnouncement.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_announcement);
        });
        fragmentRecentBinding.recentIvArticles.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_articles);
        });
        fragmentRecentBinding.recentIvConversations.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_conversations);
        });
        fragmentRecentBinding.recentIvInvitations.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_invitations);
        });
        fragmentRecentBinding.recentIvRecommendations.setOnClickListener((view)->{
            Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_recommendations);
        });
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
        invitationsViewAdapter.startListening();
        recommendationsViewAdapter.startListening();
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        conversationsViewAdapter.stopListening();
        invitationsViewAdapter.stopListening();
        recommendationsViewAdapter.stopListening();
    }
}