package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityEnterNewConversationBinding;

/**
 * Activity class for posting new conversation topics.
 *
 * @author Branko Mikusic
 */
public class EnterNewConversationActivity extends AppCompatActivity {

    ActivityEnterNewConversationBinding viewBinding;

    /**
     * Views handlers are fetched and onClick listeners are created.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityEnterNewConversationBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        viewBinding.enterconversationBConfirm.setOnClickListener((view)->{
            if(viewBinding.enterconversationEtTitle.getText().toString().length()>0 && viewBinding.enterconversationEtIntro.getText().toString().length()>0){
                FirebaseHandler.createConversationInFirestore(this, viewBinding.enterconversationEtTitle.getText().toString(), viewBinding.enterconversationEtIntro.getText().toString());
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_fields),Toast.LENGTH_LONG).show();
            }
        });

        viewBinding.enterconversationBCancel.setOnClickListener((view)->{
            finish();
        });
    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume(){
        super.onResume();
        viewBinding.enterconversationEtIntro.setText("");
        viewBinding.enterconversationEtTitle.setText("");
        viewBinding.enterconversationEtlayoutIntro.requestFocus();
    }
}