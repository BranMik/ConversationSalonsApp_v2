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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

/**
 * A fragment representing a list of Items.
 */
public class RecommendationsFragment extends Fragment {

    private Context context;
    private RecommendationsViewAdapter recommendationsViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecommendationsFragment() {
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
        View root = inflater.inflate(R.layout.fragment_recommendations_list, container, false);
        context = getContext();
        setupFirestoreRecyclerView(root);
        setupListeners(root);
        return root;
    }

    /**
     * Sets up listener on a Floating action button
     *
     * @param root root View reference
     */
    private void setupListeners(View root){
        FloatingActionButton fabNewRecommendation = root.findViewById(R.id.fab_newRecommendation);
        fabNewRecommendation.setOnClickListener((view)->{
            Intent newRecommendationIntent = new Intent(context, EnterNewRecommendationActivity.class);
            startActivity(newRecommendationIntent);
        });
    }
    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    private void setupFirestoreRecyclerView(View root){
        Query query = FirebaseHandler.getRecommendationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<RecommendationObject> options = new FirestoreRecyclerOptions.Builder<RecommendationObject>()
                .setQuery(query, RecommendationObject.class)
                .build();
        recommendationsViewAdapter = new RecommendationsViewAdapter(options,getContext());
        RecyclerView rvRecommendationsList = root.findViewById(R.id.recommendations_list_rv);
        rvRecommendationsList.setHasFixedSize(true);
        rvRecommendationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecommendationsList.setAdapter(recommendationsViewAdapter);
    }

    /**
     * Every time onStart gets called FirestoreRecyclerAdapter have to start
     * listening to online database changes because each time onStop gets
     * called it stops listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        recommendationsViewAdapter.startListening();
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        recommendationsViewAdapter.stopListening();
    }
}