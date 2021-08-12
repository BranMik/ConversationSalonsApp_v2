package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityEnterNewPostBinding;

/**
 * Activity class for posting new post/comment on a particular conversation.
 *
 * @author Branko Mikusic
 */
public class EnterNewPostActivity extends AppCompatActivity {

    private ActivityEnterNewPostBinding viewBinding;

    /**
     * Views handlers are fetched as well as Bundle object with data sent from calling fragment through
     * the intent, and also onClick listeners are created.
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityEnterNewPostBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        Intent intent = getIntent();
        Bundle newPostBundle = intent.getBundleExtra(ConversationsViewAdapter.INTENT_EXTRA_KEY_BUNDLE);
        String documentId = newPostBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_DOCUMENTID);

        viewBinding.enterpostBConfirmpost.setOnClickListener((view)-> {
            if (viewBinding.enterpostEtText.getText().length() > 0){
                FirebaseHandler.createPostInFirestore(this, viewBinding.enterpostEtText.getText().toString(), documentId);
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_field),Toast.LENGTH_LONG).show();
            }
        });
        viewBinding.enterpostBCancelpost.setOnClickListener((view)->{
            finish();
        });

    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume() {
        super.onResume();
        viewBinding.enterpostEtText.setText("");
        viewBinding.enterpostEtlayout.requestFocus();
    }
}