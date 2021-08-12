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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentInvitationsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * Fragment class for showing list of invitations.
 *
 * @author Branko Mikusic
 */
public class InvitationsFragment extends Fragment {

    private Context context;
    private InvitationsViewAdapter invitationsViewAdapter;

    public InvitationsFragment() {
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
        FragmentInvitationsBinding fragmentInvitationsBinding = FragmentInvitationsBinding.inflate(inflater,container,false);
        setupFirestoreRecyclerView(fragmentInvitationsBinding);
        setupListeners(fragmentInvitationsBinding);
        return fragmentInvitationsBinding.getRoot();
    }

    /**
     * Sets up listener on a Floating action button
     *
     * @param fragmentInvitationsBinding  View binder reference
     */
    private void setupListeners(FragmentInvitationsBinding fragmentInvitationsBinding){
        fragmentInvitationsBinding.fabNewInvitation.setOnClickListener((view)->{
            Intent newInvitationIntent = new Intent(context, EnterNewInvitationActivity.class);
            startActivity(newInvitationIntent);
        });
    }

    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    private void setupFirestoreRecyclerView(FragmentInvitationsBinding fragmentInvitationsBinding){
        Query query = FirebaseHandler.getInvitationsCollectionReference().orderBy("creationTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<InvitationObject> options = new FirestoreRecyclerOptions.Builder<InvitationObject>()
                .setQuery(query, InvitationObject.class)
                .build();
        Log.d(MainActivity.LOG_BR_INFO,"Invitations size of query : " + String.valueOf(options.getSnapshots().size()));
        invitationsViewAdapter = new InvitationsViewAdapter(options,getContext(), R.layout.rv_invitation_item, false);
        fragmentInvitationsBinding.invitationsListRv.setHasFixedSize(true);
        fragmentInvitationsBinding.invitationsListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentInvitationsBinding.invitationsListRv.setAdapter(invitationsViewAdapter);
    }

    /**
     * Every time onStart gets called FirestoreRecyclerAdapter have to start
     * listening to online database changes because each time onStop gets
     * called it stops listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        invitationsViewAdapter.startListening();
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        invitationsViewAdapter.stopListening();
    }
}