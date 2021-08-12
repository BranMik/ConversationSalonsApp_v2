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
 * InvitationsViewAdapter for RecyclerView showing list of invitations loaded from Firestore database.It extends
 * specialized FirestoreRecyclerAdapter class which loads data from database automatically, given
 * appropriate Model object and a ViewHolder.
 *
 * @author Branko Mikusic
 */
class InvitationsViewAdapter extends FirestoreRecyclerAdapter<InvitationObject, InvitationsViewAdapter.InvitationViewHolder> {
    private Context context;
    private int itemViewRes;
    private boolean isRecent;

    public InvitationsViewAdapter(@NonNull FirestoreRecyclerOptions<InvitationObject> options, Context ctx, int itemViewResourceNum,
                                  boolean isRecent){
        super(options);
        this.itemViewRes = itemViewResourceNum;
        this.isRecent = isRecent;
        context = ctx;
    }

    /**
     * OnBind method for each ViewHolder. Fills the ViewHolder's views with data.
     *
     * @param viewHolder A reference to the viewHolder being bounded.
     * @param position Position in a list of the item that is being bounded to a ViewHolder.
     * @param invitationObject Object with invitation data that is automatically populated from Firestore database fields.
     */
    @Override
    protected void onBindViewHolder(@NonNull InvitationViewHolder viewHolder, final int position, @NonNull final InvitationObject invitationObject) {
        String datePosted = invitationObject.getCreationTime().toDate().toString().substring(0,10)+" ,"+invitationObject.getCreationTime().toDate().toString().substring(29);
        viewHolder.tv_date.setText(datePosted);
        viewHolder.tv_text.setText(invitationObject.getTitle());
        FirebaseHandler.fillViewWithOtherMemberFullnameUsingUserUID(invitationObject.getAuthorUID(), viewHolder.tv_author_name);
        FirebaseHandler.fillViewOtherMemberProfilePic(context, invitationObject.getAuthorUID(),viewHolder.iv_author_pic);
        UserObject userInstance = UserObject.getUserObjectInstance();
        if(!isRecent) {
            if (userInstance.getIsAdmin() || invitationObject.getAuthorUID().equals(userInstance.getUserUID())) {
                viewHolder.iv_del.setVisibility(View.VISIBLE);
                viewHolder.iv_del.setOnClickListener((view) -> {
                    getSnapshots().getSnapshot(position).getReference().delete();
                });
            } else if (viewHolder.iv_del.getVisibility() == View.VISIBLE) {
                viewHolder.iv_del.setVisibility(View.INVISIBLE);
            }
        }else{
            viewHolder.cv_container.setOnClickListener((view)->{
                Navigation.findNavController(view).navigate(R.id.action_nav_recent_to_nav_invitations);
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
    public InvitationsViewAdapter.InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemViewRes, parent, false);
        return new InvitationsViewAdapter.InvitationViewHolder(view);
    }

    /**
     * Class for creating Invitation ViewHolder objects.
     */
    public class InvitationViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date;
        TextView tv_text;
        TextView tv_author_name;
        ImageView iv_author_pic;
        ImageView iv_del;
        CardView cv_container;

        InvitationViewHolder(@NonNull View itemView){
            super(itemView);
            tv_date = itemView.findViewById(R.id.invitations_item_tv_date);
            tv_text = itemView.findViewById(R.id.invitations_item_tv_text);
            tv_author_name = itemView.findViewById(R.id.invitations_item_tv_fullname);
            iv_author_pic = itemView.findViewById(R.id.invitations_item_imgv);
            cv_container = itemView.findViewById(R.id.invitations_item_cv);
            if(!isRecent) iv_del = itemView.findViewById(R.id.delinvitat_invitatitem_b);
        }
    }
}
