package com.brankomikusic.conversationsalonsapp_v2;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashSet;

/**
 * POJO class for creating UserObject object and performing checks and actions in relation to them.
 *
 * @author: Branko Mikusic 2021
 */
public class UserObject {
    private static UserObject userObjectInstance = null;

    public static final String FIELD_UID = "userUID";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_FULL_NAME = "fullName";
    public static final String FIELD_PROFILE_IMAGE_LOC = "profileImageLocationInCloudStorage";
    public static final String FIELD_IS_MEMBERSHIP_CONFIRMED = "isMembershipConfirmed";
    public static final String FIELD_IS_ADMIN = "isAdmin";

    public static final String SHPREFS_KEY_EMAIL = "user_email";
    public static final String SHPREFS_KEY_FULLNAME = "user_full_name";
    public static final String SHPREFS_KEY_UID = "user_uid";
    public static final String SHPREFS_KEY_IMAGE_IN_CLOUD_STORAGE = "user_image_in_cloud_storage";
    public static final String SHPREFS_KEY_IS_ADMIN = "user_is_admin";
    public static final String SHPREFS_KEY_IS_CONFIRMED = "user_is_confirmed";

    // Fields holding book data.
    private String userUID;
    private String email;
    private String fullName;
    private String profileImageLocationInCloudStorage;
    private Boolean isMembershipConfirmed;
    private Boolean isAdmin;

    public static String userDocumentId_InMembersCollection;
    private static StorageReference profileImagesStorageRef;

    /**
     * Required empty constructor.
     */
    public UserObject(){
        //EmptyConstructor
    }

    /**
     * Constructor for creating user object with data from FirebaseUser object.
     *
     * @param user FirebaseUser object
     */
    public UserObject(FirebaseUser user){
        this.userUID = user.getUid();
        this.email = user.getEmail();
        this.fullName = user.getDisplayName();
        this.isMembershipConfirmed = false;
        this.isAdmin = false;
        this.profileImageLocationInCloudStorage = null;
    }

    /**
     * Constructor for creating user object with data from fetched from Firestore members collection.
     *
     * @param documentSnapshot DocumentSnapshot of member document from Firestore database
     */
    public UserObject(DocumentSnapshot documentSnapshot){
        this.userUID = (String)documentSnapshot.get(FIELD_UID);
        this.isAdmin = (Boolean)documentSnapshot.get(FIELD_IS_ADMIN);
        this.isMembershipConfirmed = (Boolean)documentSnapshot.get(FIELD_IS_MEMBERSHIP_CONFIRMED);
        this.fullName = (String)documentSnapshot.get(FIELD_FULL_NAME);
        this.email = (String)documentSnapshot.get(FIELD_EMAIL);
        this.profileImageLocationInCloudStorage = (String)documentSnapshot.get(FIELD_PROFILE_IMAGE_LOC);
        userDocumentId_InMembersCollection = documentSnapshot.getId();
        // MyUtils.parseImageLocalPathFromStorageLocation(this.profileImageLocationInFBStorage);
    }

    public String getProfileImageLocationInCloudStorage() {
        return profileImageLocationInCloudStorage;
    }


    public String getUserUID() {
        return userUID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail(){
        return email;
    }

    public Boolean getIsMembershipConfirmed(){
        return isMembershipConfirmed;
    }
    
    public Boolean getIsAdmin(){ return isAdmin; }

    /**
     * If user instance does not exist new is created then returned, if it does exist that one
     * is returned
     * @return UserObject reference
     */
    public static UserObject getUserObjectInstance() {
        if(UserObject.userObjectInstance == null)
            UserObject.userObjectInstance = createUserObjectFromSharedPreferences();
        return UserObject.userObjectInstance;
    }

    public void setProfileImageLocationInCloudStorage(String location) {
        profileImageLocationInCloudStorage = location;
    }

    public void setUserUID(String uid) {
        userUID = uid;
    }

    public void setFullName(String fullName) {
        fullName = fullName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setIsMembershipConfirmed(boolean isConfirmed){
        isMembershipConfirmed = isConfirmed;
    }

    public void setIsAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    /**
     * Creates new user object instance from FirebaseUser object.
     * @param user FirebaseUser object
     * @return UserObject reference
     */
    public static UserObject createUserObjectInstanceForNewMember(FirebaseUser user){
        UserObject.userObjectInstance = new UserObject(user);
        storeUserObjectDataToSharedPreferences(UserObject.userObjectInstance);
        return UserObject.userObjectInstance;
    }

    /**
     * Creates new user object instance from DocumentSnapshot object.
     * @param documentSnapshot DocumentSnapshot object
     * @return UserObject reference
     */
    public static UserObject createUserObjectInstanceForExistingMember(DocumentSnapshot documentSnapshot){
        UserObject.userObjectInstance = new UserObject(documentSnapshot);
        storeUserObjectDataToSharedPreferences(UserObject.userObjectInstance);
        return UserObject.userObjectInstance;
    }

    /**
     * Creates UserObject from data stored in Shared Preferences.
     * @return UserObject reference
     */
    private static UserObject createUserObjectFromSharedPreferences(){
        SharedPreferences shpref = MainActivity.getSharedPreferences();
        UserObject userObject = new UserObject();

        userObject.setEmail(shpref.getString(UserObject.SHPREFS_KEY_EMAIL,""));
        userObject.setFullName(shpref.getString(UserObject.SHPREFS_KEY_FULLNAME,""));
        userObject.setProfileImageLocationInCloudStorage(shpref.getString(UserObject.SHPREFS_KEY_IMAGE_IN_CLOUD_STORAGE,""));
        userObject.setUserUID(shpref.getString(UserObject.SHPREFS_KEY_UID,""));
        userObject.setIsMembershipConfirmed(shpref.getBoolean(UserObject.SHPREFS_KEY_IS_CONFIRMED,false));
        userObject.setIsAdmin(shpref.getBoolean(UserObject.SHPREFS_KEY_IS_ADMIN,false));

        return userObject;
    }

    /**
     * Stores UserObject data in Shared Preferences.
     * @param userObject UserObject reference
     */
    private static void storeUserObjectDataToSharedPreferences(UserObject userObject){
        SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
        editor.putString(UserObject.SHPREFS_KEY_EMAIL, userObject.email);
        editor.putString(UserObject.SHPREFS_KEY_FULLNAME, userObject.fullName);
        editor.putString(UserObject.SHPREFS_KEY_IMAGE_IN_CLOUD_STORAGE, userObject.profileImageLocationInCloudStorage);
        editor.putString(UserObject.SHPREFS_KEY_UID, userObject.userUID);
        editor.putBoolean(UserObject.SHPREFS_KEY_IS_ADMIN, userObject.isAdmin);
        editor.putBoolean(UserObject.SHPREFS_KEY_IS_CONFIRMED, userObject.isMembershipConfirmed);
        editor.apply();
    }

    /**
     * Returns StorageReference in Cloud Storage for user profile image.
     * @return
     */
    public static StorageReference getProfileImagesStorageRef(){
        if(profileImagesStorageRef == null){
            profileImagesStorageRef = FirebaseStorage.getInstance().getReference().child("profileImages");
        }
        return profileImagesStorageRef;
    }

    /**
     * Utility Class for performing actions in relation to user settings.
     *
     * @author Branko Mikusic
     */
    static class UserSettings{
        private static UserSettings userSettings;

        private Boolean notifyToSubscribed;
        private HashSet<String> listOfSubscribedTopics = new HashSet<>();

        /**
         * Types of notification subscriptions
         */
        public enum NotificationTypes{
            NEW_ANNOUNCEMENT,NEW_ARTICLE,POSTS_FROM_SUBSCRIBED_TOPICS_JUST_SH_PREFS,
            NEW_CONVERSATION_TOPIC,NEW_INVITATION,NEW_RECOMMENDATION,
            ALLNEWPOSTS, JUST_SH_PREFS_SUBSCRIPTIONLIST
        }

        /**
         * Notification subscription keys are stored in an array
         */
        public final static String[] subscriptionKeys= {NotificationTypes.NEW_ANNOUNCEMENT.toString(),
                NotificationTypes.NEW_ARTICLE.toString(),
                NotificationTypes.NEW_CONVERSATION_TOPIC.toString(),
                NotificationTypes.NEW_INVITATION.toString(),
                NotificationTypes.NEW_RECOMMENDATION.toString(),
                NotificationTypes.POSTS_FROM_SUBSCRIBED_TOPICS_JUST_SH_PREFS.toString(),
                NotificationTypes.NEW_CONVERSATION_TOPIC.toString()};

        public static UserSettings getUserSettings(){
            if(userSettings == null){
                userSettings = new UserSettings();
            }
            return userSettings;
        }

        public Boolean getNotifyToSubscribed() {
            return notifyToSubscribed;
        }

        public void setNotifyToSubscribed(Boolean notifyToSubscribed) {
            this.notifyToSubscribed = notifyToSubscribed;
        }

        /**
         * Subscribe user to a particular conversation topic
         * @param topic Subscription keyword for the topic
         */
        public static void subscribeToConversationTopic(String topic){
            getUserSettings().listOfSubscribedTopics.add(topic);
            if(getUserSettings().notifyToSubscribed){
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
            }
            MainActivity.getSharedPreferences().edit().putStringSet(
                    NotificationTypes.JUST_SH_PREFS_SUBSCRIPTIONLIST.toString(),
                    getUserSettings().listOfSubscribedTopics).apply();
        }

        /**
         * Unsubscribe user to a particular conversation topic
         * @param topic Subscription keyword for the topic
         */
        public static void unsubscribeFromConversationTopic(String topic){
            getUserSettings().listOfSubscribedTopics.remove(topic);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
            MainActivity.getSharedPreferences().edit().putStringSet(
                    NotificationTypes.JUST_SH_PREFS_SUBSCRIPTIONLIST.toString(),
                    getUserSettings().listOfSubscribedTopics).apply();
        }

        /**
         * Turns on particular type of notification
         * @param notificationType Enum value that is subscription keyword for certain notification type
         */
        public static void turnOnNotification(NotificationTypes notificationType){
            FirebaseMessaging.getInstance().subscribeToTopic(notificationType.toString());
            MainActivity.getSharedPreferences().edit().putBoolean(notificationType.toString(), true).apply();
        }

        /**
         * Turns off particular type of notification
         * @param notificationType Enum value that is subscription keyword for certain notification type
         */
        public static void turnOffNotification(NotificationTypes notificationType){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(notificationType.toString());
            MainActivity.getSharedPreferences().edit().putBoolean(notificationType.toString(), false).apply();
        }

        /**
         * User subscriptions are initialized on app start with this method. If the subscription key does not exist in
         * Shared Preferences, that means its a new user and should be subscribed to default notification types. If the
         * key exists no action is performed since all further subscribing and unsubscribing is done through UserSettings
         * activity.
         */
        public static void initUserSubscriptions(){
            UserSettings locUserSettings = getUserSettings();
            if(MainActivity.getSharedPreferences() == null) return;
            SharedPreferences.Editor editor = null;
            FirebaseMessaging messagingInstance = null;
            boolean written = false;

            for(String key:subscriptionKeys) {
                if (!MainActivity.getSharedPreferences().contains(key)) {
                    if (editor == null) editor = MainActivity.getSharedPreferences().edit();
                        editor.putBoolean(key, true);
                    if(key.equals(NotificationTypes.POSTS_FROM_SUBSCRIBED_TOPICS_JUST_SH_PREFS.toString())){
                        locUserSettings.notifyToSubscribed = true;
                    }else {
                        written = true;
                        if (messagingInstance == null)
                            messagingInstance = FirebaseMessaging.getInstance();
                        messagingInstance.subscribeToTopic(key);
                    }
                }else if(key.equals(NotificationTypes.POSTS_FROM_SUBSCRIBED_TOPICS_JUST_SH_PREFS.toString())){
                    locUserSettings.notifyToSubscribed = MainActivity.getSharedPreferences().getBoolean(key,true);
                }
            }
            if(!MainActivity.getSharedPreferences().contains(NotificationTypes.ALLNEWPOSTS.toString())){
                editor.putBoolean(NotificationTypes.ALLNEWPOSTS.toString(),false);
            }
            locUserSettings.listOfSubscribedTopics = (HashSet<String>)MainActivity.getSharedPreferences().getStringSet(
                    NotificationTypes.JUST_SH_PREFS_SUBSCRIPTIONLIST.toString(),new HashSet<String> ());
            if(written) editor.apply();
        }
    }
}
