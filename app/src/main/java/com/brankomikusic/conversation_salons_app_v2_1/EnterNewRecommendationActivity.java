package com.brankomikusic.conversation_salons_app_v2_1;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityEnterNewRecommendationBinding;

public class EnterNewRecommendationActivity extends AppCompatActivity {

    private ActivityEnterNewRecommendationBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityEnterNewRecommendationBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setListeners();
    }

    /**
     * Listeners are set on buttons.
     */
    private void setListeners(){

        viewBinding.enterrecommendationBConfirmrecommendation.setOnClickListener((c)-> {
            if (viewBinding.enterrecommendationEtText.getText().length() > 0){
                FirebaseHandler.createRecommendationInFirestore(this, viewBinding.enterrecommendationEtText.getText().toString());
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_field),Toast.LENGTH_LONG).show();
            }
        });
        viewBinding.enterrecommendationBCancelrecommendation.setOnClickListener((c)->{
            finish();
        });
    }

    /**
     * After the Activity is created so views are accessible they are initialized.
     */
    @Override
    protected void onResume() {
        super.onResume();
        viewBinding.enterrecommendationEtText.setText("");
        viewBinding.enterrecommendationEtlayout.requestFocus();
    }

}