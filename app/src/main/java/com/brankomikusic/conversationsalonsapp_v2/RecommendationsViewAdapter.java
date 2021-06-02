package com.brankomikusic.conversationsalonsapp_v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brankomikusic.conversationsalonsapp_v2.databinding.FragmentRecommendationItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * RecommendationsViewAdapter for RecyclerView showing list of recommendations loaded from Firestore database.It extends
 * specialized FirestoreRecyclerAdapter class which loads data from database automatically, given
 * appropriate Model object and a ViewHolder.
 *
 * @author Branko Mikusic
 */
public class RecommendationsViewAdapter extends FirestoreRecyclerAdapter<RecommendationObject, RecommendationsViewAdapter.RecommendationViewHolder> {

    private Context context;

    public RecommendationsViewAdapter(@NonNull FirestoreRecyclerOptions<RecommendationObject> options, Context ctx){
        super(options);
        context = ctx;
    }

    /**
     * OnBind method for each ViewHolder. Fills the ViewHolder's views with data.
     *
     * @param viewHolder A reference to the viewHolder being bounded.
     * @param position Position in a list of the item that is being bounded to a ViewHolder.
     * @param recommendationObject Object with invitation data that is automatically populated from Firestore database fields.
     */
    @Override
    protected void onBindViewHolder(@NonNull RecommendationViewHolder viewHolder, final int position, @NonNull final RecommendationObject recommendationObject) {
        String datePosted = recommendationObject.getCreationTime().toDate().toString().substring(0,10)+" ,"+recommendationObject.getCreationTime().toDate().toString().substring(29);
        viewHolder.binding.recommendationItemTvDate.setText(datePosted);
        viewHolder.binding.recommendationItemTvText.setText(recommendationObject.getTitle());
        FirebaseHandler.fillViewWithOtherMemberFullnameUsingUserUID(recommendationObject.getAuthorUID(), viewHolder.binding.recommendationsItemTvFullname);
        FirebaseHandler.fillViewOtherMemberProfilePic(context, recommendationObject.getAuthorUID(),viewHolder.binding.recommendationItemImgv);
    }

    /**
     * View is inflated and returned.
     *
     * @param parent Reference to the root view.
     * @param viewType Type of the View being created.
     * @return inflated layout.
     */
    @NonNull
    @Override
    public RecommendationsViewAdapter.RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendationsViewAdapter.RecommendationViewHolder(FragmentRecommendationItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    /**
     * Class for creating Recommendation ViewHolder objects.
     */
    public class RecommendationViewHolder extends RecyclerView.ViewHolder{
        FragmentRecommendationItemBinding binding;

        RecommendationViewHolder(@NonNull FragmentRecommendationItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}