package com.brankomikusic.conversationsalonsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

public class EnterNewRecommendationActivity extends AppCompatActivity {

    private EditText et;
    private TextInputLayout et_layout;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_recommendation);

        et = findViewById(R.id.enterrecommendation_et_text);
        et_layout = findViewById(R.id.enterrecommendation_etlayout);
        root = findViewById(R.id.enterrecommendation_root);

        setListeners();
    }

    /**
     * Listeners are set on buttons.
     */
    private void setListeners(){

        ImageView iv_confirm = findViewById(R.id.enterrecommendation_b_confirmrecommendation);
        ImageView iv_cancel = findViewById(R.id.enterrecommendation_b_cancelrecommendation);

        iv_confirm.setOnClickListener((c)-> {
            if (et.getText() != null && et.getText().length() > 0){
                FirebaseHandler.createRecommendationInFirestore(this, et.getText().toString());
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_field),Toast.LENGTH_LONG).show();
            }
        });
        iv_cancel.setOnClickListener((c)->{
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