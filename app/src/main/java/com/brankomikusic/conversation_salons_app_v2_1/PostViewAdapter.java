package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.RvPostItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * ConversationsViewAdapter for RecyclerView showing list of conversations loaded from Firestore database.It extends
 * specialized FirestoreRecyclerAdapter class which loads data from database automatically, given
 * appropriate Model object and a ViewHolder.
 *
 * @author Branko Mikusic
 */
public class PostViewAdapter extends FirestoreRecyclerAdapter<PostObject, PostViewAdapter.PostViewHolder> {

    private Context context;

    /**
     * Creates a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions sent to parent constructor.
     * @param ctx Application context value is passed in.
     */
    public PostViewAdapter(@NonNull FirestoreRecyclerOptions<PostObject> options, Context ctx){
        super(options);
        context = ctx;
    }

    /**
     * OnBind method for each ViewHolder. Fills the ViewHolder's views with data from the post object, and also
     * fetches poster name and profile image from the Firestore database.
     *
     * @param holder A reference to the viewHolder being bounded.
     * @param position Position in a list of the item that is being bounded to a ViewHolder.
     * @param post Object with post data that is automatically populated from Firestore database fields.
     */
    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostObject post) {
        String date = post.getCreationTime().toDate().toString().substring(0,10)+" , "+
                post.getCreationTime().toDate().toString().substring(29);
        holder.binding.rvitemPostTvContent.setText(post.getText());
        holder.binding.rvitemPostTvDate.setText(date);
        FirebaseHandler.fillViewWithOtherMemberFullnameUsingUserUID(post.getAuthorUID(), holder.binding.rvitemPostTvAuthor);
        FirebaseHandler.fillViewOtherMemberProfilePic(context, post.getAuthorUID(),holder.binding.rvPostItemImgvUserProfilePic);
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
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(RvPostItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    /**
     * Class for creating PostViewHolder objects.
     */
    public class PostViewHolder extends RecyclerView.ViewHolder{

        RvPostItemBinding binding;

        PostViewHolder(@NonNull RvPostItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
