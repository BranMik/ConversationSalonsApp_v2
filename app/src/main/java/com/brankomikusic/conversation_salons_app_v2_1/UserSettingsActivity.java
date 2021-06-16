package com.brankomikusic.conversation_salons_app_v2_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityUserSettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * Activity class for user to change his user settings.
 *
 * @author Branko Mikusic
 */
public class UserSettingsActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 111;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserSettingsBinding activityUserSettingsBinding;

    /**
     * Methods for seting up user data, switches and listeners are initiated.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityUserSettingsBinding = ActivityUserSettingsBinding.inflate(getLayoutInflater());
        setContentView(activityUserSettingsBinding.getRoot());

        setupUserData();
        setupSwitches();
        setupListeners();
    }

    /**
     * Method to set up listeners to views.
     */
    private void setupListeners(){

        activityUserSettingsBinding.tvSettingsUserFullName.setOnClickListener((view)->{
            activityUserSettingsBinding.etSettingsUserFullName.setVisibility(View.VISIBLE);
            activityUserSettingsBinding.etSettingsUserFullName.setText(activityUserSettingsBinding.tvSettingsUserFullName.getText());
            activityUserSettingsBinding.tvSettingsUserFullName.setVisibility(View.INVISIBLE);
            activityUserSettingsBinding.etSettingsUserFullName.requestFocus();
        });

        activityUserSettingsBinding.etSettingsUserFullName.setOnEditorActionListener((view, actionId, event)->{
            if(actionId == EditorInfo.IME_ACTION_DONE){
                activityUserSettingsBinding.tvSettingsUserFullName.setVisibility(View.VISIBLE);
                activityUserSettingsBinding.tvSettingsUserFullName.setText(activityUserSettingsBinding.etSettingsUserFullName.getText());
                activityUserSettingsBinding.etSettingsUserFullName.setVisibility(View.INVISIBLE);
                UserObject.changeUserFullNameInFirestore(this, activityUserSettingsBinding.etSettingsUserFullName.getText().toString());
                return false;
            }
            return false;
        });

        activityUserSettingsBinding.bSignOut.setOnClickListener((view)->{
                FirebaseLoginWithGmailActivity.getGoogleSignInClient(this).signOut();
                FirebaseHandler.getFirebaseAuthInstance().signOut();
                Intent intent = new Intent(this,FirebaseLoginWithGmailActivity.class);
                startActivity(intent);
        });
        activityUserSettingsBinding.imgvSettingsProfilePic.setOnClickListener((view)->{
            selectImage();
        });
        activityUserSettingsBinding.imgvSettingsGoback.setOnClickListener((view)->{
            //finish();
            Intent intent = new Intent(UserSettingsActivity.this, MainActivity.class);
            startActivity(intent);
        });
        activityUserSettingsBinding.swNotifSettingsAllnewposts.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.ALLNEWPOSTS);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.ALLNEWPOSTS);
            }
        });
        activityUserSettingsBinding.swNotifSettingsRecommendations.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.NEW_RECOMMENDATION);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.NEW_RECOMMENDATION);
            }
        });
        activityUserSettingsBinding.swNotifSettingsInvitations.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.NEW_INVITATION);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.NEW_INVITATION);
            }
        });
        activityUserSettingsBinding.swNotifSettingsConversations.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.NEW_CONVERSATION_TOPIC);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.NEW_CONVERSATION_TOPIC);
            }
        });
        activityUserSettingsBinding.swNotifSettingsArticles.setOnCheckedChangeListener((view,state)->{

            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.NEW_ARTICLE);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.NEW_ARTICLE);
            }
        });
        activityUserSettingsBinding.swNotifSettingsAnnouncements.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.UserSettings.turnOnNotification(UserObject.UserSettings.NotificationTypes.NEW_ANNOUNCEMENT);
            }else{
                UserObject.UserSettings.turnOffNotification(UserObject.UserSettings.NotificationTypes.NEW_ANNOUNCEMENT);
            }
        });
        activityUserSettingsBinding.swAdminMode.setOnCheckedChangeListener((view,state)->{
            if(state){
                UserObject.getUserObjectInstance().setIsAdmin(true);
            }else{
                UserObject.getUserObjectInstance().setIsAdmin(false);
            }
        });
    }

    /**
     * Method that sets up user data shown on screen.
     */
    private void setupUserData(){
        if(UserObject.getUserObjectInstance().getFullName() != null && UserObject.getUserObjectInstance().getFullName().length()>1)
                activityUserSettingsBinding.tvSettingsUserFullName.setText(UserObject.getUserObjectInstance().getFullName());
        if(UserObject.getUserObjectInstance().getEmail() != null && UserObject.getUserObjectInstance().getEmail().length()>1)
            activityUserSettingsBinding.tvUseremailSettings.setText(UserObject.getUserObjectInstance().getEmail());
        if(UserObject.getUserObjectInstance().getIsAdmin() != null)
            activityUserSettingsBinding.swAdminMode.setChecked(UserObject.getUserObjectInstance().getIsAdmin());
        MyUtils.showImageFromCloudStorage(this,UserObject.getUserObjectInstance().getProfileImageLocationInCloudStorage(),
                activityUserSettingsBinding.imgvSettingsProfilePic, FirebaseHandler.getProfileImagesStorageReference(), R.drawable.profile_image_placeholder);
    }

    /**
     * Method for setting up switches in appropriate states according to the current state of user settings read from Shared Preferences.
     */
    private void setupSwitches(){
        SharedPreferences prefs = MainActivity.getSharedPreferences();
        activityUserSettingsBinding.swNotifSettingsAnnouncements.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.NEW_ANNOUNCEMENT.toString(),true));
        activityUserSettingsBinding.swNotifSettingsArticles.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.NEW_ARTICLE.toString(),true));
        activityUserSettingsBinding.swNotifSettingsConversations.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.NEW_CONVERSATION_TOPIC.toString(),true));
        activityUserSettingsBinding.swNotifSettingsInvitations.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.NEW_INVITATION.toString(),true));
        activityUserSettingsBinding.swNotifSettingsRecommendations.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.NEW_RECOMMENDATION.toString(),true));
        activityUserSettingsBinding.swNotifSettingsAllnewposts.setChecked(prefs.getBoolean(
                UserObject.UserSettings.NotificationTypes.ALLNEWPOSTS.toString(),true));
    }

    /**
     * Method that starts intent for device image selector.
     */
    private void selectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    /**
     * Method that receives results from called intents. Here the result is picked image.
     * @param requestCode variable holding the request code of returning intent result.
     * @param resultCode result code of returning intent result
     * @param data data from returning intent result
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            uploadProfileImageToCloudStorage(data.getData());
        }
    }

    /**
     * Code taken from https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
     * and modified to fit my implementation
     *
     * @param path Uri path to an image
     */
    private void uploadProfileImageToCloudStorage(Uri path){
        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final String imageCloudReference = UUID.randomUUID().toString();
        // Defining the child of storageReference
        StorageReference ref
                = FirebaseHandler.getProfileImagesStorageReference().child("/"  + imageCloudReference);

        // adding listeners on upload
        // or failure of image
        ref.putFile(path)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast.makeText(UserSettingsActivity.this,
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT).show();
                                FirebaseHandler.updateProfileImg(UserSettingsActivity.this,imageCloudReference, activityUserSettingsBinding.imgvSettingsProfilePic);
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(UserSettingsActivity.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {

                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(
                                "Uploaded "
                                        + (int)progress + "%");
                        }
                    }
                );
    }

}
