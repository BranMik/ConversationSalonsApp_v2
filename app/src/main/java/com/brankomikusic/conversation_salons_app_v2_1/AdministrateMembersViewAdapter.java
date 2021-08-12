package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Random;

/**
 * ViewAdapter for RecyclerView showing list of members and their status, loaded from Firestore database.It extends
 * specialized FirestoreRecyclerAdapter class which loads data from database automatically, given
 * appropriate Model object and a ViewHolder.
 *
 * @author Branko Mikusic
 */
public class AdministrateMembersViewAdapter extends FirestoreRecyclerAdapter<UserObject, AdministrateMembersViewAdapter.AdministratedMemberViewHolder> {

    private Context context;
    private Random rnd;
    /**
     * Creates a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions sent to parent constructor.
     * @param ctx Application context value is passed in.
     */
    public AdministrateMembersViewAdapter(@NonNull FirestoreRecyclerOptions<UserObject> options, Context ctx) {
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
     * @param userObject Object with member data that is automatically populated from Firestore database fields.
     */
    @Override
    protected void onBindViewHolder(@NonNull AdministratedMemberViewHolder viewHolder, final int position, @NonNull final UserObject userObject) {
        viewHolder.tv_memberName.setText(userObject.getFullName());
        viewHolder.tv_email.setText(userObject.getEmail());
        viewHolder.tb_activateBlockMember.setChecked(userObject.getIsBlocked());
        viewHolder.tb_activateBlockMember.setBackgroundColor((userObject.getIsBlocked())? ContextCompat.getColor(context,R.color.colorAccent2):
                                                                                    ContextCompat.getColor(context,R.color.brand_color_2_whitish));

        Animation an = AnimationUtils.loadAnimation(context,R.anim.anim_wobble_more);
        int rnd_dur = rnd.nextInt(10)*10+50;
        an.setDuration(rnd_dur);
        viewHolder.tb_activateBlockMember.startAnimation(an);

        String documentId = getSnapshots().getSnapshot(position).getId();
        FirebaseHandler.fillViewOtherMemberProfilePic(context, userObject.getUserUID(),viewHolder.iv_memberPic);
        viewHolder.tb_activateBlockMember.setOnCheckedChangeListener((view, state)->{
            FirebaseHandler.changeIsMemberBlockedStatus(context,documentId,state);
        });
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
    public AdministratedMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_member_item, parent, false);
        return new AdministratedMemberViewHolder(view);
    }

    /**
     * Class for creating ConversationViewHolder objects.
     */
    public class AdministratedMemberViewHolder extends RecyclerView.ViewHolder{
        TextView tv_memberName;
        TextView tv_email;
        ImageView iv_memberPic;
        ToggleButton tb_activateBlockMember;

        AdministratedMemberViewHolder(@NonNull View itemView){
            super(itemView);
            this.tv_memberName = itemView.findViewById(R.id.rvitem_member_tv_name);
            this.iv_memberPic = itemView.findViewById(R.id.rvitem_member_imgv_userProfilePic);
            this.tb_activateBlockMember = itemView.findViewById(R.id.rvitem_member_tb_blocked);
            this.tv_email = itemView.findViewById(R.id.rvitem_member_tv_email);
        }

    }
}
