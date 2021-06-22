package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Class holding various utility methods.
 *
 * @author Branko Mikusic
 */
public class MyUtils {

    /**
     * Enum determining my two standardized types of AlertDialogs that could be shown with utility method.
     *
     */
    public enum AlertType{
        POSITIVE,NEGATIVE
    }

    /**
     * Utility method that loads image from CloudStorage and shows it in the passed in ImageView view.
     * @param context Context reference
     * @param imgPath path in Cloudstorage to the image
     * @param imgView reference to ImageView that should be filled with fetched image
     * @param fallbackResource resource code of the image to be shown if image fetching is unsucessful
     *
     */
    public static void showImageFromCloudStorage(@NonNull final Context context, String imgPath, @NonNull final ImageView imgView,
                                                 StorageReference storageReference, final Integer fallbackResource, boolean isProfilePic){
        int radius = (isProfilePic)?70:40;
        int margin = (isProfilePic)?0:0;
        try {
            storageReference.child(imgPath).getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .transform(new RoundedCornersTransformation(radius, margin))
                        .into(imgView);
            }).addOnFailureListener(exception -> {
                // ImageView is set to default image if unsuccessful image loading from database.
                Log.e(MainActivity.LOG_BR_ERROR, exception.getMessage());
                if(fallbackResource != null)
                    //imgView.setImageResource(fallbackResource);
                    Glide.with(context)
                            .load(fallbackResource)
                            .transform(new RoundedCornersTransformation(radius, margin))
                            .into(imgView);
                else
                    //imgView.setImageResource(R.drawable.default_image);
                    Glide.with(context)
                            .load(R.drawable.default_image)
                            .transform(new RoundedCornersTransformation(radius, margin))
                            .into(imgView);
            });
        }
        // ImageView is set to default image if some error happened.
        catch(NullPointerException | IllegalArgumentException exception){
            if(fallbackResource != null)
                //imgView.setImageResource(fallbackResource);
                Glide.with(context)
                        .load(fallbackResource)
                        .transform(new RoundedCornersTransformation(radius, margin))
                        .into(imgView);
            else
                //imgView.setImageResource(R.drawable.default_image);
                Glide.with(context)
                        .load(R.drawable.default_image)
                        .transform(new RoundedCornersTransformation(radius, margin))
                        .into(imgView);
        }
    }

    /**
     * Utility method that loads image from Uri and shows it in the passed in ImageView view.
     * @param context Context reference
     * @param imgUri Uri address of the image
     * @param imageView reference to ImageView that should be filled with fetched image
     *
     */
    public static void showImageFromUri(@NonNull final Context context,String imgUri, @NonNull final ImageView imageView){
        try {
        Glide.with(context)
                .load(imgUri)
                .transform(new RoundedCornersTransformation(30, 0))
                .into(imageView);
        }
            // ImageView is set to default image if some error happened.
        catch(NullPointerException | IllegalArgumentException exception){
            //imageView.setImageResource(R.drawable.default_image);
            Glide.with(context)
                    .load(R.drawable.default_image)
                    .transform(new RoundedCornersTransformation(30,0))
                    .into(imageView);
        }
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Utility method that shows standardized custom AlertDialog.
     *
     * @param context Originating context reference.
     * @param alertMessage String holding message for the user that will be shown in AlertDialog.
     * @param alertType Variable signifying my AlertDialog type which determines its title and theme.
     * @param onClickEvent Passed in OnClickListener that will be set on AlertDialog confirmation button.
     */
    public static void showAlertMessage(@NonNull Context context, String alertMessage, @NonNull AlertType alertType,
                                        DialogInterface.OnClickListener onClickEvent){
        String alertTitle;
        int alertTheme;

        if(alertType == AlertType.POSITIVE){
            alertTitle = context.getString(R.string.good_notification_title);
            alertTheme = R.style.GoodAlertDialogStyle;
        }else{
            alertTitle = context.getString(R.string.alert_notification_title);
            alertTheme = R.style.AlertDialogStyle;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, alertTheme);
        builder.setTitle(alertTitle).setMessage(alertMessage).setPositiveButton("OK", onClickEvent)
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button b =dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(b != null){
            b.setTextSize(20);
            b.setTextColor(context.getColor(R.color.white));
        }
    }

}
