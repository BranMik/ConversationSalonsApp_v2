package com.brankomikusic.conversationsalonsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity class for posting new conversation topics.
 *
 * @author Branko Mikusic
 */
public class EnterNewConversationActivity extends AppCompatActivity {

    private EditText et_title;
    private EditText et_intro;
    private TextInputLayout etlayout_intro;
    private ConstraintLayout root;

    /**
     * Views handlers are fetched and onClick listeners are created.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_conversation);

        et_intro = findViewById(R.id.enterconversation_et_intro);
        et_title = findViewById(R.id.enterconversation_et_title);
        etlayout_intro = findViewById(R.id.enterconversation_etlayout_intro);
        ImageView iv_confirm = findViewById(R.id.enterconversation_b_confirm);
        ImageView iv_cancel = findViewById(R.id.enterconversation_b_cancel);
        root = findViewById(R.id.enterconversation_root);

        iv_confirm.setOnClickListener((view)->{
            if(et_title.getText() != null && et_title.getText().toString().length()>0 &&
            et_intro.getText() != null && et_intro.getText().toString().length()>0){
                FirebaseHandler.createConversationInFirestore(this, et_title.getText().toString(), et_intro.getText().toString());
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_fields),Toast.LENGTH_LONG).show();
            }
        });

        iv_cancel.setOnClickListener((view)->{
            finish();
        });
    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume(){
        super.onResume();
        et_intro.setText("");
        et_title.setText("");
        etlayout_intro.requestFocus();
    }
}