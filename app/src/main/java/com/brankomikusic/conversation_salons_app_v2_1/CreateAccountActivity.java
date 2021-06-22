package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityCreateAccountBinding;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateAccountBinding binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners(binding);
    }

    private void setListeners(ActivityCreateAccountBinding binding){
        binding.etPass.setOnFocusChangeListener((v,isFocus)->{
            if(!isFocus){
                if(binding.etPass.getText() == null || binding.etPass.getText().length() < 6){
                    binding.tvPassErr.setVisibility(View.VISIBLE);
                }else{
                    binding.tvPassErr.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.bConfirm.setOnClickListener((v)->{
            String name = (binding.etName.getText().length() > 0)?binding.etName.getText().toString():"";
            String email = (binding.etEmail.getText().length() > 0)?binding.etEmail.getText().toString():"";
            String pass = (binding.etPass.getText().length() > 0)?binding.etPass.getText().toString():"";

            if(enteredDataValidityCheck(email,name,pass)) {
                Intent intent = new Intent();
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("pass",pass);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean enteredDataValidityCheck(String email, String name, String password){
        boolean isValid = false;


        // Check if email is filled in with non valid value.
        if(email.length() > 0 && !MyUtils.isEmailValid(email)){
            MyUtils.showAlertMessage(this,getString(R.string.alert_createacc_emailerr), MyUtils.AlertType.NEGATIVE,null);
        }
        // Check if name too short.
        else if(name.length()<2) {
            MyUtils.showAlertMessage(this,getString(R.string.alert_createacc_nameerr), MyUtils.AlertType.NEGATIVE,null);
        }
        // Check if
        else if(password.length()<6) {
            MyUtils.showAlertMessage(this,getString(R.string.alert_createacc_passerr), MyUtils.AlertType.NEGATIVE,null);
        }
        // If previous checks passed, values entered are valid.
        else{
            isValid = true;
        }

        return isValid;
    }
}
