package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity class for posting new post/comment on a particular conversation.
 *
 * @author Branko Mikusic
 */
public class EnterNewPostActivity extends AppCompatActivity {

    private EditText et;
    private TextInputLayout et_layout;
    private ConstraintLayout root;

    /**
     * Views handlers are fetched as well as Bundle object with data sent from calling fragment through
     * the intent, and also onClick listeners are created.
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_post);
        et = findViewById(R.id.enterpost_et_text);
        root = findViewById(R.id.frame_et_enterpost);
        et_layout = findViewById(R.id.enterpost_etlayout);
        ImageView iv_confirm = findViewById(R.id.enterpost_b_confirmpost);
        ImageView iv_cancel = findViewById(R.id.enterpost_b_cancelpost);

        Intent intent = getIntent();
        Bundle newPostBundle = intent.getBundleExtra(ConversationsViewAdapter.INTENT_EXTRA_KEY_BUNDLE);
        String documentId = newPostBundle.getString(ConversationsViewAdapter.BUNDLE_KEY_CONVERSATION_DOCUMENTID);

        iv_confirm.setOnClickListener((view)->{
            FirebaseHandler.createPostInFirestore(this,et.getText().toString(),documentId);
            finish();
        });
        iv_cancel.setOnClickListener((view)->{
            finish();
        });

    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume() {
        super.onResume();
        et.setText("");
        et_layout.requestFocus();
    }
}