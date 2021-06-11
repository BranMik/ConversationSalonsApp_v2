package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.RvConversationsItemBinding;
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
    Random rnd ;

    /**
     * Creates a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions sent to parent constructor.
     * @param ctx Application context value is passed in.
     */
    public ConversationsViewAdapter(@NonNull FirestoreRecyclerOptions<ConversationObject> options, Context ctx) {
        super(options);
        this.context = ctx;
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
        viewHolder.binding.rvitemConversationlistTvDate.setText(datePosted);
        viewHolder.binding.rvitemConversationlistTvTitle.setText(conversationObject.getTitle());
        String documentId = getSnapshots().getSnapshot(position).getId();

        Animation an = AnimationUtils.loadAnimation(context,R.anim.anim_wobble);
        int rnd_dur = rnd.nextInt(5)*10+50;
        an.setDuration(rnd_dur);

        viewHolder.binding.getRoot().startAnimation(an);

        viewHolder.binding.cardView2.setOnClickListener((view)->{
            Intent intent = new Intent(context, ConversationDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_CONVERSATION_DOCUMENTID,documentId);
            bundle.putString(BUNDLE_KEY_CONVERSATION_TITLE,conversationObject.getTitle());
            bundle.putString(BUNDLE_KEY_CONVERSATION_INTRO,conversationObject.getText());
            intent.putExtra(INTENT_EXTRA_KEY_BUNDLE,bundle);
            context.startActivity(intent);
        });
        UserObject userInstance = UserObject.getUserObjectInstance();
        if(userInstance.getIsAdmin()){
            viewHolder.binding.delconversConverstemB.setVisibility(View.VISIBLE);
            viewHolder.binding.delconversConverstemB.setOnClickListener((view) -> {
                getSnapshots().getSnapshot(position).getReference().delete();
            });
        }else if(viewHolder.binding.delconversConverstemB.getVisibility() == View.VISIBLE) {
            viewHolder.binding.delconversConverstemB.setVisibility(View.INVISIBLE);
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
        return new ConversationViewHolder(RvConversationsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    /**
     * Class for creating ConversationViewHolder objects.
     */
    public class ConversationViewHolder extends RecyclerView.ViewHolder{
        RvConversationsItemBinding binding;

        ConversationViewHolder(@NonNull RvConversationsItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
