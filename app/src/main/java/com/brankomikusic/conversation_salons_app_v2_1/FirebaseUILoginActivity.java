package com.brankomikusic.conversation_salons_app_v2_1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityLoginSplashscreenBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

/**
 * Demonstrate authentication using the FirebaseUI-Android library. This fragment demonstrates
 * using FirebaseUI for basic email/password sign in.
 *
 * For more information, visit https://github.com/firebase/firebaseui-android
 */
public class FirebaseUILoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityLoginSplashscreenBinding.inflate(getLayoutInflater()).getRoot());
        UpdateArticlesInDbFromBlog.initiateArticleUpdateFromBlogToLocalDb(FirebaseUILoginActivity.this);
        MainActivity.setSharedPreferencesInstance(getSharedPreferences(MainActivity.SHARED_PREFERENCES_IDENTIFIER_NAME,MODE_PRIVATE));


        //!! check whether a user is already signed in from a previous session _new
        if (FirebaseHandler.getFirebaseAuthInstance().getCurrentUser() != null) {
            Log.d(MainActivity.LOG_BR_INFO,"IN");
            Toast.makeText(this,"SIGNED IN",Toast.LENGTH_LONG);
            //TODO If email is not confirmed then message
            checkIfMemberProfileExists(FirebaseHandler.getFirebaseAuthInstance().getCurrentUser());
            //TODO Message name which is loged in
        } else {
            // TODO not signed in
            Log.d(MainActivity.LOG_BR_INFO,"OUT");
            Toast.makeText(this,"NOT SIGNED IN",Toast.LENGTH_LONG);
            signInSequence();
        }
    }

    private void signInSequence(){
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_firebase_login_ui)
                .setGoogleButtonId(R.id.firebaseLoginScreen_b_signInGoogle)
                .setEmailButtonId(R.id.firebaseLoginScreen_b_signIn)
                .setTosAndPrivacyPolicyId(R.id.firebaseloginui_tv_terms)
                .build();

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                //.setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.LoginTheme) //TODO edit the theme to Brand ??
                .setTosAndPrivacyPolicyUrls("https://drive.google.com/file/d/1qvw9QTk0bXiPedkpkY5mjhj4CepUIEV7/view?usp=sharing",
                        "https://drive.google.com/file/d/1djLNlCg5lVZoaeIKARqC5EhzJ_r8IZKW/view?usp=sharing")
                .build();
        startActivityForResult(intent,RC_SIGN_IN);
        Log.d(MainActivity.LOG_BR_INFO,"New SignIn Sequence");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MainActivity.LOG_BR_INFO,"In onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Log.d(MainActivity.LOG_BR_INFO,"requestCode == RC_SIGN_IN");
            if (resultCode == Activity.RESULT_OK) {
                // Sign in succeeded
                Log.d(MainActivity.LOG_BR_INFO,"resultCode == OK");
                updateUI(FirebaseHandler.getFirebaseAuthInstance().getCurrentUser());
            } else {
                // Sign in failed
                Log.d(MainActivity.LOG_BR_INFO,"resultCode NOT OK !!!");
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();

                updateUI(null);
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        Log.d(MainActivity.LOG_BR_INFO,"In updateUI");
        if (user != null) {
            // Signed in
            checkIfMemberProfileExists(user);
        } else {
            // Signed out
            finish();
            Log.d(MainActivity.LOG_BR_INFO,"FINIST IN UPDATEUI - null user");
        }
    }

    /**
     * Check is made is user signed into the Firebase first time user of the application, if yes then
     * new entry/document is created for the user in members collection of the Firestore database.
     *
     * @param user FirebaseUser object representing signed in user
     */
    private void checkIfMemberProfileExists(FirebaseUser user){
        FirebaseHandler.getMembersCollectionReference()
                .whereEqualTo(UserObject.FIELD_EMAIL, user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                            Log.d(MainActivity.LOG_BR_INFO,String.valueOf(user.isEmailVerified()));
                            if(user.isEmailVerified()) {
                                Log.d(MainActivity.LOG_BR_INFO,"email is verified");
                                if(!((Boolean)task.getResult().getDocuments().get(0).get(UserObject.FIELD_BLOCKED))) {
                                    UserObject.createUserObjectInstanceForExistingMember(task.getResult().getDocuments().get(0));
                                    MyUtils.showAlertMessage(FirebaseUILoginActivity.this, getString(R.string.message_signed_in, user.getEmail(), task.getResult().getDocuments().get(0).get(UserObject.FIELD_FULL_NAME))
                                            , MyUtils.AlertType.POSITIVE, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startMainApplicationUI();
                                                }
                                            });
                                }else{
                                    finish();
                                }
                            }else{
                                Log.d(MainActivity.LOG_BR_INFO,"email is not verified");
                                MyUtils.showAlertMessage(FirebaseUILoginActivity.this, getString(R.string.message_email_not_verified),
                                        MyUtils.AlertType.INFO, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.d(MainActivity.LOG_BR_INFO,"int i = " + i);
                                                if(i == -3){
                                                    FirebaseHandler.sendEmailVerification(user,FirebaseUILoginActivity.this);
                                                }else
                                                    FirebaseHandler.signOut(FirebaseUILoginActivity.this);
                                            }
                                        });
                            }
                        }else{
                            // User record doesn't exist, so it should be created, with confirmed value false.
                            Log.d(MainActivity.LOG_BR_INFO,"Create User");
                            FirebaseHandler.createNewMemberEntry(user,FirebaseUILoginActivity.this);

                            //startMainApplicationUI();
                        }
                    }
                });
    }

    /**
     * Main activity is now started after user is signed in and checked.
     */
    private void startMainApplicationUI(){
        Intent startMainIntent = new Intent(this, MainActivity.class);
        startActivity(startMainIntent);
    }

}
