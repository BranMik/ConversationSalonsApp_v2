package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentAnnouncementBinding;

public class AnnouncementFragment extends Fragment {
    private FragmentAnnouncementBinding fragmentAnnouncementBinding;
    private Context context;
    public static final String KEY_EXTRA_ANNOUNCEMENT_TITLE = "announcement_extra_title";
    public static final String KEY_EXTRA_ANNOUNCEMENT_TEXT = "announcement_extra_text";
    public static final String KEY_EXTRA_ANNOUNCEMENT_DATE = "announcement_extra_date";

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
        return fragmentAnnouncementBinding.getRoot();
    }

    private void getData(){
        FirebaseHandler.populateAnnouncementFromFirestore(context, fragmentAnnouncementBinding.announcementTvTitle, fragmentAnnouncementBinding.announcementTvBodytext,
                fragmentAnnouncementBinding.announcementTvDate,fragmentAnnouncementBinding.announcementIvImage);
    }
    private void setupListeners(){
        if(UserObject.getUserObjectInstance().getIsAdmin()) {
            fragmentAnnouncementBinding.announcementFabNewAnnouncement.setOnClickListener((view) -> {
                Intent intent_createNewAnnouncement = new Intent(context, CreateNewAnnouncementActivity.class);
                intent_createNewAnnouncement.putExtra(KEY_EXTRA_ANNOUNCEMENT_TITLE, fragmentAnnouncementBinding.announcementTvTitle.getText());
                intent_createNewAnnouncement.putExtra(KEY_EXTRA_ANNOUNCEMENT_TEXT, fragmentAnnouncementBinding.announcementTvBodytext.getText());
                intent_createNewAnnouncement.putExtra(KEY_EXTRA_ANNOUNCEMENT_DATE, fragmentAnnouncementBinding.announcementTvDate.getText());
                startActivity(intent_createNewAnnouncement);
            });
        }else{
            fragmentAnnouncementBinding.announcementFabNewAnnouncement.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        getData();
        setupListeners();
    }
}
