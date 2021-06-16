package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityCreateNewAnnouncementBinding;

public class CreateNewAnnouncementActivity extends AppCompatActivity {


    private ActivityCreateNewAnnouncementBinding layoutBinding;

    /**
     * Views handlers are fetched and onClick listeners are created.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutBinding = ActivityCreateNewAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());
        Intent received_intent = getIntent();
        layoutBinding.createannouncementBConfirm.setOnClickListener((view)->{
            if(layoutBinding.createannouncementEtTitle.getText() != null && layoutBinding.createannouncementEtTitle.getText().toString().length()>0 &&
                    layoutBinding.createannouncementEtText.getText() != null && layoutBinding.createannouncementEtText.getText().toString().length()>0){
                FirebaseHandler.createAnnouncementInFirestore(received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_TITLE),
                        received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_TEXT), received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_DATE),
                        layoutBinding.createannouncementEtTitle.getText().toString(), layoutBinding.createannouncementEtText.getText().toString());
                // Store current announcement in firestore archive, store entered text as current
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_fields),Toast.LENGTH_LONG).show();
            }
        });

        layoutBinding.createannouncementBCancel.setOnClickListener((view)->{
            finish();
        });
    }

    /**
     * Views are initialized after the Activity is created, when they become accessible .
     */
    @Override
    protected void onResume(){
        super.onResume();
        layoutBinding.createannouncementEtText.setText("");
        layoutBinding.createannouncementEtTitle.setText("");
        layoutBinding.createannouncementEtlayoutText.requestFocus();
    }
}
