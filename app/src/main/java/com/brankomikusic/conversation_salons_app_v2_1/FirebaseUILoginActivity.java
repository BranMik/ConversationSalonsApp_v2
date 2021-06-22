package com.brankomikusic.conversation_salons_app_v2_1;

import android.app.Activity;
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
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

/**
 * Demonstrate authentication using the FirebaseUI-Android library. This fragment demonstrates
 * using FirebaseUI for basic email/password sign in.
 *
 * For more information, visit https://github.com/firebase/firebaseui-android
 */
public class FirebaseUILoginActivity extends AppCompatActivity {

    private ActivityFirebaseLoginUiBinding binding;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_CREATE_ACCOUNT = 9002;
    private static final int RC_SIGN_IN_WITH_GOOGLE = 9003;
    private static GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UpdateArticlesInDbFromBlog.initiateArticleUpdateFromBlogToLocalDb(FirebaseUILoginActivity.this);
        MainActivity.setSharedPreferencesInstance(getSharedPreferences(MainActivity.SHARED_PREFERENCES_IDENTIFIER_NAME,MODE_PRIVATE));

        /*binding = ActivityFirebaseLoginUiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseHandler.getFirebaseAuthInstance();
        setupListeners(); _old*/

        //!! check whether a user is already signed in from a previous session _new
        if (FirebaseHandler.getFirebaseAuthInstance().getCurrentUser() != null) {
            // TODO already signed in
        } else {
            // TODO not signed in
        }
    }

    private void signInSequence_new(){
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setTheme(R.style.LoginTheme) //TODO edit the theme to Brand ??
                .build();
        Log.d(MainActivity.LOG_BR_INFO,"New SignIn Sequence");
    }

    public void setupListeners() {
        binding.firebaseLoginScreenBSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startManualSignIn();
            }
        });

        binding.firebaseLoginScreenBCreateAccount.setOnClickListener((v)->{
            createNewAccount();
        });

        binding.firebaseLoginScreenBSignInGoogle.setOnClickListener((view)->{
            signInWithGoogle();
        });
    }

    private void signInWithGoogle(){
        setupGoogleSignIn(this);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_WITH_GOOGLE);
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
                            Toast.makeText(FirebaseUILoginActivity.this,"Authentication process failed!! Try again or contact the administrator.",Toast.LENGTH_LONG).show();
                            mGoogleSignInClient.signOut();
                            FirebaseHandler.getFirebaseAuthInstance().signOut();
                            Log.w(MainActivity.LOG_BR_INFO, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MainActivity.LOG_BR_INFO,"In onActivityResult");
        if (requestCode == RC_SIGN_IN_WITH_GOOGLE) {
            Log.d(MainActivity.LOG_BR_INFO,"requestCode == RC_SIGN_IN_WITH_GOOGLE");
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(MainActivity.LOG_BR_ERROR, "Google sign in failed", e);
                }
            } else {
                // Sign in failed
                Log.d(MainActivity.LOG_BR_INFO,"resultCode NOT OK !!!");
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }else if (requestCode == RC_SIGN_IN) {
            Log.d(MainActivity.LOG_BR_INFO,"requestCode == RC_SIGN_IN");
            if (resultCode == Activity.RESULT_OK) {
                Log.d(MainActivity.LOG_BR_INFO,"resultCode == OK");
                // Sign in succeeded
                updateUI(FirebaseHandler.getFirebaseAuthInstance().getCurrentUser());
            } else {
                // Sign in failed
                Log.d(MainActivity.LOG_BR_INFO,"resultCode NOT OK !!!");
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();

                updateUI(null);
            }
        }else if (requestCode == RC_CREATE_ACCOUNT) {
            Log.d(MainActivity.LOG_BR_INFO,"requestCode == RC_CREATE_ACC");
            if (resultCode == Activity.RESULT_OK) {
                Log.d(MainActivity.LOG_BR_INFO,"resultCode == OK");
                // Data are valid, try to create user
                firebaseCreateUser(data.getStringExtra("email"),data.getStringExtra("name"),data.getStringExtra("pass"));
            } else {
                // Sign in failed
                Log.d(MainActivity.LOG_BR_INFO,"resultCode NOT OK !!!");
                Toast.makeText(this, "Account could not be created", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }
    private void startManualSignIn() {
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        Log.d(MainActivity.LOG_BR_INFO,"In sign in");
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setTheme(R.style.LoginTheme) //TODO edit the theme to Brand ??
                .build();
        Log.d(MainActivity.LOG_BR_INFO,"Prije startActivityForResult");
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void createNewAccount(){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivityForResult(intent, RC_CREATE_ACCOUNT);
    }

    private void firebaseCreateUser(String email, String name, String pass){
        FirebaseHandler.getFirebaseAuthInstance().addAuthStateListener(new MyCreateUserAuthStateListener(this));
        FirebaseHandler.getFirebaseAuthInstance().createUserWithEmailAndPassword(email,pass);
        //TODO Update name of user
    }

    class MyCreateUserAuthStateListener implements FirebaseAuth.AuthStateListener {
        Context context;

        public MyCreateUserAuthStateListener(Context ctx){
            super();
            context = ctx;
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
            if(auth.getCurrentUser()!=null){
                Toast.makeText(context ,"SUCCESS!! user is "+auth.getCurrentUser().getDisplayName(),Toast.LENGTH_LONG).show();
                FirebaseHandler.setFirebaseAuthInstance(auth);
                updateUI(auth.getCurrentUser());
            }else{
                Toast.makeText(context ,"SOMETHING WENT WRONG. USER IS NOT CREATED.",Toast.LENGTH_LONG).show();
            }
            FirebaseHandler.getFirebaseAuthInstance().removeAuthStateListener(this);
        }
    }

    private void updateUI(FirebaseUser user) {
        Log.d(MainActivity.LOG_BR_INFO,"In updateUI");
        if (user != null) {
            binding.firebaseLoginScreenBSignInGoogle.setVisibility(View.INVISIBLE);
            binding.firebaseLoginScreenBSignIn.setVisibility(View.INVISIBLE);
            binding.firebaseLoginScreenBCreateAccount.setVisibility(View.INVISIBLE);
            String appUserEmail=user.getEmail();
            // Signed in
            checkIfMemberProfileExists(user);
        } else {
            // Signed out
            binding.firebaseLoginScreenBSignInGoogle.setVisibility(View.VISIBLE);
            binding.firebaseLoginScreenBSignIn.setVisibility(View.VISIBLE);
            binding.firebaseLoginScreenBCreateAccount.setVisibility(View.VISIBLE);

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
                            startMainApplicationUI();
                        }else{
                            // User record doesn't exist, so it should be created, with confirmed value false.
                            Log.d(MainActivity.LOG_BR_INFO,"Create User");
                            FirebaseHandler.createNewMemberEntry(user,FirebaseUILoginActivity.this);
                            startMainApplicationUI();
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
