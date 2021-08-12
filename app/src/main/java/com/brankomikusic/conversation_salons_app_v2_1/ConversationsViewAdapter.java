package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Random;

/**
 * ConversationsViewAdapter for RecyclerView showing list of conversations loaded from Firestore database.It extends
 * specialized FirestoreRecyclerAdapter class which loads data from database automatically, given
 * appropriate Model object and a ViewHolder.
 *
 * @author Branko Mikusic
 */
public class ConversationsViewAdapter extends FirestoreRecyclerAdapter<ConversationObject, ConversationsViewAdapter.ConversationViewHolder> {

    public static String BUNDLE_KEY_CONVERSATION_DOCUMENTID = "DOCUMENTID";
    public static String BUNDLE_KEY_CONVERSATION_TITLE = "TITLE";
    public static String BUNDLE_KEY_CONVERSATION_INTRO = "INTRO";
    public static String INTENT_EXTRA_KEY_BUNDLE = "BUNDLE";
    private Context context;
    private int itemViewResource;
    private boolean isRecents;
    private Random rnd ;

    /**
     * Creates a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions sent to parent constructor.
     * @param ctx Application context value is passed in.
     */
    public ConversationsViewAdapter(@NonNull FirestoreRecyclerOptions<ConversationObject> options, Context ctx, int itemViewResourceNum
    , boolean isRecents) {
        super(options);
        this.itemViewResource = itemViewResourceNum;
        this.context = ctx;
        this.isRecents = isRecents;
        rnd = new Random();
    }

    /**
     * OnBind method for each ViewHolder. Fills the ViewHolder's views with data and add onclick action which starts
     * ConversationDetailActivity
     *
     * @param viewHolder A reference to the viewHolder being bounded.
     * @param position Position in a list of the item that is being bounded to a ViewHolder.
     * @param conversationObject Object with conversation data that is automatically populated from Firestore database fields.
     */
    @Override
    protected void onBindViewHolder(@NonNull ConversationViewHolder viewHolder, final int position, @NonNull final ConversationObject conversationObject) {
        String datePosted = conversationObject.getCreationTime().toDate().toString().substring(0,10)+" ,"+conversationObject.getCreationTime().toDate().toString().substring(29);
        viewHolder.tv_date.setText(datePosted);
        viewHolder.tv_title.setText(conversationObject.getTitle());
        viewHolder.tv_body.setText(conversationObject.getText());
        String documentId = getSnapshots().getSnapshot(position).getId();

        Animation an = AnimationUtils.loadAnimation(context,R.anim.anim_wobble);
        int rnd_dur = rnd.nextInt(5)*10+50;
        an.setDuration(rnd_dur);

        viewHolder.root.startAnimation(an);

        viewHolder.cardView.setOnClickListener((view)->{
            Intent intent = new Intent(context, ConversationDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_CONVERSATION_DOCUMENTID,documentId);
            bundle.putString(BUNDLE_KEY_CONVERSATION_TITLE,conversationObject.getTitle());
            bundle.putString(BUNDLE_KEY_CONVERSATION_INTRO,conversationObject.getText());
            intent.putExtra(INTENT_EXTRA_KEY_BUNDLE,bundle);
            context.startActivity(intent);
        });
        if(!isRecents) {
            UserObject userInstance = UserObject.getUserObjectInstance();
            if (userInstance.getIsAdmin()) {
                viewHolder.b_del.setVisibility(View.VISIBLE);
                viewHolder.b_del.setOnClickListener((view) -> {
                    getSnapshots().getSnapshot(position).getReference().delete();
                });
            } else if (viewHolder.b_del.getVisibility() == View.VISIBLE) {
                viewHolder.b_del.setVisibility(View.INVISIBLE);
            }
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
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemViewResource, parent, false);
        return new ConversationViewHolder(view);
    }

    /**
     * Class for creating ConversationViewHolder objects.
     */
    public class ConversationViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout root;
        CardView cardView;
        TextView tv_title;
        TextView tv_body;
        TextView tv_date;
        ImageView b_del;

        ConversationViewHolder(@NonNull View itemView){
            super(itemView);
            this.root = itemView.findViewById(R.id.rvitem_conversationlist_cl_root);
            this.cardView = itemView.findViewById(R.id.rvitem_conversationlist_cardView);
            this.tv_title = itemView.findViewById(R.id.rvitem_conversationlist_tv_title);
            this.tv_body = itemView.findViewById(R.id.rvitem_conversationlist_tv_body);
            this.tv_date = itemView.findViewById(R.id.rvitem_conversationlist_tv_date);
            if(!isRecents)this.b_del = itemView.findViewById(R.id.delconvers_conversitem_b);
        }

    }
}
