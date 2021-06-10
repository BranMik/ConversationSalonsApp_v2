package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityFirebaseLoginUiBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Class for signing into the Firebase with through Google auth provider. Taken from
 * https://firebase.google.com/docs/auth/android/google-signin?authuser=0 and modified
 * to a certain degree.
 */
public class FirebaseLoginWithGmailActivity extends AppCompatActivity {
    private ActivityFirebaseLoginUiBinding binding;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_CREATE_ACCOUNT = 9002;
    private static GoogleSignInClient mGoogleSignInClient;

    /**
     * Sequence for scraping articles from Conversation Salons web page, and filling them into the local database is initiated
     * here. Shared preferences are fetched and reference is stored in a static variable in MainActivity. Also Google sign in action
     * is set up.
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdateArticlesInDbFromBlog.initiateArticleUpdateFromBlogToLocalDb(FirebaseLoginWithGmailActivity.this);
        MainActivity.setSharedPreferencesInstance(getSharedPreferences(MainActivity.SHARED_PREFERENCES_IDENTIFIER_NAME,MODE_PRIVATE));
        binding = ActivityFirebaseLoginUiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupGoogleSignIn(this);
        setupListeners();
    }

    /**
     * Listener is set on sign in buton.
     */
    private void setupListeners() {
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    /**
     * Check if user is signed in (non-null) and update UI accordingly.
     */
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseHandler.getFirebaseAuthInstance().getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Method for seting up Google sign in action.
     */
    private static void setupGoogleSignIn(Context context){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.server_client_id_for_google_signin))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /**
     * Method for starting Google sign in action.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * After the Google sign in intent returns further checks and appropriate actions are
     * performed.
     *
     * @param requestCode Request code of returning intent.
     * @param resultCode Result code of returning intent.
     * @param data Data returned by intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(MainActivity.LOG_BR_ERROR, "Google sign in failed", e);
                }
            }else if(resultCode == RESULT_CANCELED){
                if(data != null && data.getDataString() != null)
                    Log.d(MainActivity.LOG_BR_INFO, data.getDataString());
            }else{
                if(data != null && data.getDataString() != null)
                    Log.d(MainActivity.LOG_BR_INFO, data.getDataString());
            }
        }
    }

    /**
     * After Google sign in returns idToken, it is used for signing into the Firebase.
     * @param idToken The string representing Google sign in idToken
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseHandler.getFirebaseAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseHandler.getFirebaseAuthInstance().getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FirebaseLoginWithGmailActivity.this,"Authentication process failed!! Try again or contact the administrator.",Toast.LENGTH_LONG).show();
                            mGoogleSignInClient.signOut();
                            FirebaseHandler.getFirebaseAuthInstance().signOut();
                            Log.w(MainActivity.LOG_BR_INFO, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * UI is updated accordingly depending on result of sign in action.
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Signed in
            binding.signInButton.setVisibility(View.INVISIBLE);
            checkIfMemberProfileExists(user);
        } else {
            // Signed out
            //Toast.makeText(this,"Authentication process failed!! Try again or contact the administrator.",Toast.LENGTH_LONG).show();
            binding.signInButton.setVisibility(View.VISIBLE);
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
                            UserObject.createUserObjectInstanceForExistingMember(task.getResult().getDocuments().get(0));
                            startActivityForConfirmed();
                        }else{
                            // User record doesn't exist, so it should be created, with confirmed value false.
                            Log.d(MainActivity.LOG_BR_INFO,"Create User");
                            FirebaseHandler.createNewMemberEntry(user,FirebaseLoginWithGmailActivity.this);
                            startActivityForConfirmed();
                        }
                    }
                });
    }

    /**
     * Main activity is now started after user is signed in and checked.
     */
    private void startActivityForConfirmed(){
        Intent startMainIntent = new Intent(this, MainActivity.class);
        startActivity(startMainIntent);
    }

    /**
     * Getter for the GoogleSignInClient object
     * @return GoogleSignInClient object.
     */
    public static GoogleSignInClient getGoogleSignInClient(Context context){
        if(mGoogleSignInClient == null){
            setupGoogleSignIn(context);
        }
        return mGoogleSignInClient;
    }
}
