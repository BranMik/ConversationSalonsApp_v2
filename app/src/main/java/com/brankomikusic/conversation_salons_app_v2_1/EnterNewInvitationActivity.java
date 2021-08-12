package com.brankomikusic.conversation_salons_app_v2_1;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityEnterNewInvitationBinding;

public class EnterNewInvitationActivity extends AppCompatActivity {

    private ActivityEnterNewInvitationBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityEnterNewInvitationBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setListeners();
    }

    /**
     * Listeners are set on buttons.
     */
    private void setListeners(){

        viewBinding.enterinvitationBConfirminvitation.setOnClickListener((c)-> {
            if (viewBinding.enterinvitationEtText.getText().length() > 0){
                FirebaseHandler.createInvitationInFirestore(this, viewBinding.enterinvitationEtText.getText().toString());
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_field),Toast.LENGTH_LONG).show();
            }
        });
        viewBinding.enterinvitationBCancelinvitation.setOnClickListener((c)->{
            finish();
        });
    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume() {
        super.onResume();
        viewBinding.enterinvitationEtText.setText("");
        viewBinding.enterinvitationEtlayout.requestFocus();
    }

}