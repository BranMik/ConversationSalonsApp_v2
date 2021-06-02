package com.brankomikusic.conversationsalonsapp;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

/**
 * Helper class with methods for communicating with Firebase.
 *
 * @author Branko Mikusic
 */
public abstract class FirebaseHandler {

    private static FirebaseAuth mAuth;
    private static FirebaseFirestore firestore;
    private static CollectionReference membersCollectionReference;
    private static CollectionReference conversationsCollectionReference;
    private static CollectionReference announcementsCollectionReference;
    private static CollectionReference invitationsCollectionReference;
    private static CollectionReference recommendationsCollectionReference;


    /**
     * Getter for FirebaseAuth instance.
     * @return FirebaseAuth instance
     */
    public static FirebaseAuth getFirebaseAuthInstance(){
        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    /**
     * Getter for Firebase Firestore instance.
     * @return Firebase Firestore instance
     */
    public static FirebaseFirestore getFirebaseFirestoreInstance(){
        if(firestore == null){
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

    /**
     * Getter for invitations CollectionReference.
     * @return invitations CollectionReference
     */
    public static CollectionReference getInvitationsCollectionReference(){
        if(invitationsCollectionReference == null){
            invitationsCollectionReference = FirebaseHandler.getFirebaseFirestoreInstance().collection("invitations");
        }
        return invitationsCollectionReference;
    }

    /**
     * Getter for recommendations CollectionReference.
     * @return invitations CollectionReference
     */
    public static CollectionReference getRecommendationsCollectionReference(){
        if(recommendationsCollectionReference == null){
            recommendationsCollectionReference = FirebaseHandler.getFirebaseFirestoreInstance().collection("recommendations");
        }
        return recommendationsCollectionReference;
    }

    /**
     * Getter for members CollectionReference.
     * @return members CollectionReference
     */
    public static CollectionReference getMembersCollectionReference(){
        if(membersCollectionReference == null){
            membersCollectionReference = FirebaseHandler.getFirebaseFirestoreInstance().collection("members");
        }
        return membersCollectionReference;
    }

    /**
     * Getter for conversations CollectionReference.
     * @return conversations CollectionReference
     */
    public static CollectionReference getConversationsCollectionReference(){
        if(conversationsCollectionReference == null){
            conversationsCollectionReference = FirebaseHandler.getFirebaseFirestoreInstance().collection("conversations");
        }
        return conversationsCollectionReference;
    }

    /**
     * Getter for announcements CollectionReference.
     * @return announcements CollectionReference
     */
    public static CollectionReference getAnnouncementsCollectionReference(){
        if(announcementsCollectionReference == null){
            announcementsCollectionReference = FirebaseHandler.getFirebaseFirestoreInstance().collection("announcements");
        }
        return announcementsCollectionReference;
    }

    /**
     * Getter for posts CollectionReference related to particular conversation.
     *
     * @param parentDocumentId Document id of conversation document to which the posts are related.
     * @return posts CollectionReference
     */
    public static CollectionReference getPostCollectionReference(String parentDocumentId){
            return FirebaseHandler.getConversationsCollectionReference().document(parentDocumentId).collection("posts");
    }

    /**
     * Method to create new member entry/document in Firestore database.
     *
     * @param user FirebaseUser object with data about user for whom member entry will be created in Firestore.
     * @param context Context reference
     */
    public static void createNewMemberEntry(FirebaseUser user, Context context){
        getMembersCollectionReference()
                .add(UserObject.createUserObjectInstanceForNewMember(user))
                .addOnCompleteListener((task)->{
                    if(task.isSuccessful()){
                        Toast.makeText(context,context.getString(R.string.message_createuser_success,user.getDisplayName()),Toast.LENGTH_LONG).show();
                        task.getResult().get().addOnCompleteListener((documentSnapshot)->{
                            UserObject.createUserObjectInstanceForExistingMember(documentSnapshot.getResult());
                        });
                    }else{
                        Toast.makeText(context,context.getString(R.string.message_createuser_fail,user.getDisplayName()),Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Method for creating post in Firestore database.
     * @param context Context reference.
     * @param postText String variable holding text of the post.
     * @param conversationDocumentId String variable holding document id for conversation document inside which post will be created.
     */
    public static void createPostInFirestore(Context context, String postText, final String conversationDocumentId){
        Calendar currentTime = Calendar.getInstance();

        PostObject postObject = new PostObject();
        postObject.setCreationTime(new Timestamp(currentTime.getTime()));
        postObject.setAuthorUID(UserObject.getUserObjectInstance().getUserUID());
        postObject.setText(postText);

        FirebaseHandler.getConversationsCollectionReference().document(conversationDocumentId)
                .collection("posts")
                .add(postObject)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Update in database successful
                        Toast.makeText(context,context.getText(R.string.comment_posted),Toast.LENGTH_LONG).show();
                    }
                    // Database update failed so displaySummary View is correspondingly updated and additionally internet access is checked.
                    else {
                        Toast.makeText(context,context.getText(R.string.comment__not_posted),Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Method for creating invitations entry in Firestore database.
     * @param context Context reference.
     * @param invitationText String variable holding text of the invitation.
     */
    public static void createInvitationInFirestore(Context context, String invitationText){
        InvitationObject invitationObject= new InvitationObject();
        invitationObject.setCreationTime(new Timestamp(Calendar.getInstance().getTime()));
        invitationObject.setAuthorUID(UserObject.getUserObjectInstance().getUserUID());
        invitationObject.setTitle(invitationText);

        FirebaseHandler.getInvitationsCollectionReference()
                .add(invitationObject)
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        Toast.makeText(context,context.getText(R.string.invitation_posted),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context,context.getText(R.string.invitation__not_posted),Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Method for creating recommendation entry in Firestore database.
     * @param context Context reference.
     * @param recommendationText String variable holding text of the recommendation.
     */
    public static void createRecommendationInFirestore(Context context, String recommendationText){
        RecommendationObject recommendationObject= new RecommendationObject();
        recommendationObject.setCreationTime(new Timestamp(Calendar.getInstance().getTime()));
        recommendationObject.setAuthorUID(UserObject.getUserObjectInstance().getUserUID());
        recommendationObject.setTitle(recommendationText);

        FirebaseHandler.getRecommendationsCollectionReference()
            .add(recommendationObject)
            .addOnCompleteListener(task ->{
                if(task.isSuccessful()){
                    Toast.makeText(context,context.getText(R.string.recommendation_posted),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context,context.getText(R.string.recommendation__not_posted),Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Method to create new conversation in Firestore database.
     *
     * @param context Context reference
     * @param title Title of the conversation being created.
     * @param bodyText Body of text for the conversation being created.
     */
    public static void createConversationInFirestore(Context context, String title, String bodyText){
        Calendar currentTime = Calendar.getInstance();

        ConversationObject conversationObject = new ConversationObject();
        conversationObject.setCreationTime(new Timestamp(currentTime.getTime()));
        conversationObject.setAuthorUID(UserObject.getUserObjectInstance().getUserUID());
        conversationObject.setText(bodyText);
        conversationObject.setTitle(title);

        FirebaseHandler.getConversationsCollectionReference()
                .add(conversationObject)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // Update in database successful
                        Toast.makeText(context," Your conversation was posted !",Toast.LENGTH_LONG).show();
                    }
                    // Database update failed so displaySummary View is correspondingly updated and additionally internet access is checked.
                    else {
                        Toast.makeText(context," Comment posting was not succesfull ! Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Method that fills passed in View with the profile picture of some other member.
     * @param context Context reference
     * @param userUID userUID of a user/member whose profile picture will be filled in ImageView
     * @param imgv Reference to ImageView that will be filled with fetched picture.
     */
    public static void fillViewOtherMemberProfilePic(Context context, String userUID, ImageView imgv){
        getMembersCollectionReference()
                .whereEqualTo("userUID", userUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       try {
                                                           MyUtils.showImageFromCloudStorage(context,document.get(UserObject.FIELD_PROFILE_IMAGE_LOC).toString(),
                                                                   imgv,R.drawable.image_empty);
                                                       }catch(NullPointerException ex){
                                                           Log.d(MainActivity.LOG_BR_ERROR, "Error getting full name : " + ex.getMessage());
                                                       }
                                                   }
                                               } else {
                                                   Log.d(MainActivity.LOG_BR_ERROR, "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );
    }

    /**
     * TextView passed in is filled with Full name of some member.
     *
     * @param userUID userUID of a member whose full name will be filled in the TextView
     * @param tv Reference of TextView that will be filled with name
     */
    public static void fillViewWithOtherMemberFullnameUsingUserUID(String userUID, TextView tv){
        getMembersCollectionReference()
                .whereEqualTo("userUID", userUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       try {
                                                           tv.setText(document.get(UserObject.FIELD_FULL_NAME).toString());
                                                       }catch(NullPointerException ex){
                                                           Log.d(MainActivity.LOG_BR_ERROR, "Error getting full name : " + ex.getMessage());
                                                       }
                                                       Log.d(MainActivity.LOG_BR_INFO, document.getId() + " => " + document.getData());
                                                   }
                                               } else {
                                                   Log.d(MainActivity.LOG_BR_ERROR, "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }
                );
    }

    /**
     * Method for updating profile image field in Firestore database with reference to new image. At the end
     * the image is filled in into the passed in view.
     *
     * @param context Context reference
     * @param ref String path to the image in the Cloud Storage
     * @param imgviewToUpdate Reference of ImageView that is to be filled with new profile picture.
     */
    public static void updateProfileImg(Context context, String ref, ImageView imgviewToUpdate){
        getMembersCollectionReference().document(UserObject.userDocumentId_InMembersCollection)
                .update(UserObject.FIELD_PROFILE_IMAGE_LOC,ref)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Profile Image succesfully updated.",Toast.LENGTH_LONG).show();
                        UserObject.getUserObjectInstance().setProfileImageLocationInCloudStorage(ref);
                        MyUtils.showImageFromCloudStorage(context,UserObject.getUserObjectInstance().getProfileImageLocationInCloudStorage(),
                                imgviewToUpdate, R.drawable.profile_image_placeholder);
                    }else{
                        Toast.makeText(context, "Updating profile image failed.",Toast.LENGTH_LONG).show();
                    }
                });

    }
}
