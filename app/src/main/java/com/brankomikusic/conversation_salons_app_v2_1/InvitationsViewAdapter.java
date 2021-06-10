package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentInvitationItemBinding;
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

    public InvitationsViewAdapter(@NonNull FirestoreRecyclerOptions<InvitationObject> options, Context ctx){
        super(options);
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
        viewHolder.binding.invitationsItemTvDate.setText(datePosted);
        viewHolder.binding.invitationsItemTvText.setText(invitationObject.getTitle());
        FirebaseHandler.fillViewWithOtherMemberFullnameUsingUserUID(invitationObject.getAuthorUID(), viewHolder.binding.invitationsItemTvFullname);
        FirebaseHandler.fillViewOtherMemberProfilePic(context, invitationObject.getAuthorUID(),viewHolder.binding.invitationsItemImgv);
        UserObject userInstance = UserObject.getUserObjectInstance();
        if(userInstance.getIsAdmin() || invitationObject.getAuthorUID().equals(userInstance.getUserUID())){
            viewHolder.binding.delinvitatInvitatitemB.setVisibility(View.VISIBLE);
            viewHolder.binding.delinvitatInvitatitemB.setOnClickListener((view) -> {
                getSnapshots().getSnapshot(position).getReference().delete();
            });
        }else if(viewHolder.binding.delinvitatInvitatitemB.getVisibility() == View.VISIBLE) {
            viewHolder.binding.delinvitatInvitatitemB.setVisibility(View.INVISIBLE);
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
        return new InvitationViewHolder(FragmentInvitationItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    /**
     * Class for creating Invitation ViewHolder objects.
     */
    public class InvitationViewHolder extends RecyclerView.ViewHolder{
        FragmentInvitationItemBinding binding;

        InvitationViewHolder(@NonNull FragmentInvitationItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
