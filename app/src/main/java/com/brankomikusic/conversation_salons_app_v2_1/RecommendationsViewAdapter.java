package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
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

    private final Context context;
    private final boolean isRecent;
    private final int itemViewRes;

    public RecommendationsViewAdapter(@NonNull FirestoreRecyclerOptions<RecommendationObject> options, Context ctx, int itemViewResourceNum,
                                      boolean isRecent){
        super(options);
        context = ctx;
        this.isRecent = isRecent;
        this.itemViewRes = itemViewResourceNum;
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
        viewHolder.tv_date.setText(datePosted);
        viewHolder.tv_text.setText(recommendationObject.getTitle());
        FirebaseHandler.fillViewWithOtherMemberFullnameUsingUserUID(recommendationObject.getAuthorUID(), viewHolder.tv_authorName);
        FirebaseHandler.fillViewOtherMemberProfilePic(context, recommendationObject.getAuthorUID(),viewHolder.iv_authorImg);
        UserObject userInstance = UserObject.getUserObjectInstance();
        if(!isRecent) {
            if (userInstance.getIsAdmin() || recommendationObject.getAuthorUID().equals(userInstance.getUserUID())) {
                viewHolder.iv_del.setVisibility(View.VISIBLE);
                viewHolder.iv_del.setOnClickListener((view) -> {
                    getSnapshots().getSnapshot(position).getReference().delete();
                });
            } else if (viewHolder.iv_del.getVisibility() == View.VISIBLE) {
                viewHolder.iv_del.setVisibility(View.INVISIBLE);
            }
        }else{
            viewHolder.cv_container.setOnClickListener((view)->{
                Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_recommendations);
            });
        }
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
        View view = LayoutInflater.from(parent.getContext()).inflate(itemViewRes, parent, false);
        return new RecommendationsViewAdapter.RecommendationViewHolder(view);
        //return new RecommendationsViewAdapter.RecommendationViewHolder(RvRecommendationItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    /**
     * Class for creating Recommendation ViewHolder objects.
     */
    public class RecommendationViewHolder extends RecyclerView.ViewHolder{
        TextView tv_authorName;
        TextView tv_text;
        TextView tv_date;
        ImageView iv_authorImg;
        ImageView iv_del;
        CardView cv_container;

        RecommendationViewHolder(@NonNull View itemView){
            super(itemView);
            tv_authorName = itemView.findViewById(R.id.recommendations_item_tv_fullname);
            tv_date = itemView.findViewById(R.id.recommendation_item_tv_date);
            tv_text  = itemView.findViewById(R.id.recommendation_item_tv_text);
            iv_authorImg  = itemView.findViewById(R.id.recommendation_item_imgv);
            cv_container = itemView.findViewById(R.id.recommendation_item_cv);
            if(!isRecent) iv_del = itemView.findViewById(R.id.delrecomm_recommitem_b);
        }
    }
}