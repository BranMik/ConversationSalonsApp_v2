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

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentAnnouncementBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;

public class AnnouncementFragment extends Fragment {
    private FragmentAnnouncementBinding fragmentAnnouncementBinding;
    private Context context;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AnnouncementFragment() {

    }

    /**
     *  Recycler View is initialized here.
     *
     * @param inflater Inflater object.
     * @param container Root ViewGroup object of the fragment.
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     * @return Root View of the fragments layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentAnnouncementBinding = FragmentAnnouncementBinding.inflate(inflater,container,false);
        this.context = getContext();
        setupListeners();
        getData();
        return fragmentAnnouncementBinding.getRoot();
    }

    private void getData(){
        FirebaseHandler.populateAnnouncementFromFirestore(context, fragmentAnnouncementBinding.announcementTvTitle, fragmentAnnouncementBinding.announcementTvBodytext,
                fragmentAnnouncementBinding.announcementTvDate,fragmentAnnouncementBinding.announcementIvImage);

    }
    private void setupListeners(){
        fragmentAnnouncementBinding.announcementFabNewAnnouncement.setOnClickListener((view)->{
            Intent intent_createNewAnnouncement = new Intent(context,CreateNewAnnouncementActivity.class);
            startActivity(intent_createNewAnnouncement);
        });
    }
}
