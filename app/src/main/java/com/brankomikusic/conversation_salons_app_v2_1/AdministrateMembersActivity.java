package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityMembersListBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class AdministrateMembersActivity extends AppCompatActivity {

    AdministrateMembersViewAdapter administrateMembersViewAdapter;

    /**
     * Method that overrides method from the inherited parent class and is called when Activity is being
     * created. Toolbar is set up here with home (back) button enabled. Values are fetched from bundle object
     * sent through the intent. Layout views are set-up with those values.
     *
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMembersListBinding binding = ActivityMembersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imgvMemberslistGoback.setOnClickListener((view)->{
            finish();
        });
        setupFirestoreRecyclerView(binding.getRoot());
    }

    /**
     * Sets up RecyclerView with FirestoreRecyclerAdapter.
     */
    private void setupFirestoreRecyclerView(View root){
        Query query = FirebaseHandler.getMembersCollectionReference().orderBy("fullName");
        FirestoreRecyclerOptions<UserObject> options = new FirestoreRecyclerOptions.Builder<UserObject>()
                .setQuery(query,UserObject.class)
                .build();
        Log.d(MainActivity.LOG_BR_INFO,"Members size of query : " + String.valueOf(options.getSnapshots().size()));
        administrateMembersViewAdapter = new AdministrateMembersViewAdapter(options,this);
        RecyclerView rvMembersList = root.findViewById(R.id.rv_members_list);
        rvMembersList.setHasFixedSize(true);
        rvMembersList.setLayoutManager(new LinearLayoutManager(this));
        rvMembersList.setAdapter(administrateMembersViewAdapter);
    }

    /**
     * Every time onStart gets called FirestoreRecyclerAdapter have to start
     * listening to online database changes because each time onStop gets
     * called it stops listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        administrateMembersViewAdapter.startListening();
    }

    /**
     * In onStop FirestoreRecyclerAdapter stops listening to online
     * database changes so resources are not unnecessarily wasted.
     */
    @Override
    public void onStop() {
        super.onStop();
        administrateMembersViewAdapter.stopListening();
    }

}
