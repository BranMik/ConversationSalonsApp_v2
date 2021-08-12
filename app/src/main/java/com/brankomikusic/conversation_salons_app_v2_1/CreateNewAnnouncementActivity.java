package com.brankomikusic.conversation_salons_app_v2_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityCreateNewAnnouncementBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateNewAnnouncementActivity extends AppCompatActivity {


    private ActivityCreateNewAnnouncementBinding layoutBinding;
    private final int PICK_IMAGE_REQUEST = 111;

    /**
     * Views handlers are fetched and onClick listeners are created.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutBinding = ActivityCreateNewAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());
        Intent received_intent = getIntent();
        layoutBinding.createannouncementBConfirm.setOnClickListener((view)->{
            if(layoutBinding.createannouncementEtTitle.getText() != null && layoutBinding.createannouncementEtTitle.getText().toString().length()>0 &&
                    layoutBinding.createannouncementEtText.getText() != null && layoutBinding.createannouncementEtText.getText().toString().length()>0){
                FirebaseHandler.createAnnouncementInFirestore(received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_TITLE),
                        received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_TEXT), received_intent.getStringExtra(AnnouncementFragment.KEY_EXTRA_ANNOUNCEMENT_DATE),
                        layoutBinding.createannouncementEtTitle.getText().toString(), layoutBinding.createannouncementEtText.getText().toString());
                // Store current announcement in firestore archive, store entered text as current
                finish();
            }else{
                Toast.makeText(this,getText(R.string.no_empty_fields),Toast.LENGTH_LONG).show();
            }
        });

        layoutBinding.createannouncementBCancel.setOnClickListener((view)->{
            finish();
        });

        layoutBinding.createannouncementBChangePicture.setOnClickListener((view)->{
            selectImage();
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            uploadNewAnnouncementImageToCloudStorage(data.getData());
        }
    }

    /**
     * Code taken from https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
     * and modified to fit my implementation
     *
     * @param path Uri path to an image
     */
    private void uploadNewAnnouncementImageToCloudStorage(Uri path){
        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        //final String imageCloudReference = UUID.randomUUID().toString();
        // Defining the child of storageReference
        StorageReference ref = FirebaseHandler.getStorageInstance().getReference().child("generalImages/announcement_current_img");
        //= FirebaseHandler.getProfileImagesStorageReference().child("/"  + imageCloudReference);

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
                                Toast.makeText(CreateNewAnnouncementActivity.this,
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT).show();
                               // MyUtils.showImageFromCloudStorage(CreateNewAnnouncementActivity.this,"announcement_current_img", fragmentAnnouncementBinding.announcementIvImage,
                                 //       FirebaseHandler.getStorageInstance().getReference().child("generalImages"), R.drawable.consal_announcements_2, false);
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(CreateNewAnnouncementActivity.this,
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

    /**
     * Views are initialized after the Activity is created, when they become accessible .
     */
    @Override
    protected void onResume(){
        super.onResume();
        layoutBinding.createannouncementEtText.setText("");
        layoutBinding.createannouncementEtTitle.setText("");
        layoutBinding.createannouncementEtlayoutText.requestFocus();
    }
}
