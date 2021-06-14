package com.brankomikusic.conversation_salons_app_v2_1;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
                                                 StorageReference storageReference, final Integer fallbackResource){
        try {
            storageReference.child(imgPath).getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .transform(new RoundedCornersTransformation(20, 10))
                        .into(imgView);
            }).addOnFailureListener(exception -> {
                // ImageView is set to default image if unsuccessful image loading from database.
                Log.e(MainActivity.LOG_BR_ERROR, exception.getMessage());
                if(fallbackResource != null)
                    imgView.setImageResource(fallbackResource);
                else
                    imgView.setImageResource(R.drawable.articles_updating);
            });
        }
        // ImageView is set to default image if some error happened.
        catch(NullPointerException | IllegalArgumentException exception){
            if(fallbackResource != null)
                imgView.setImageResource(fallbackResource);
            else
             imgView.setImageResource(R.drawable.articles_updating);
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
            imageView.setImageResource(R.drawable.articles_updating);
        }
    }

}
