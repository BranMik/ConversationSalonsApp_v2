package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityArticleDetailBinding;

/**
 * Activity that is responsible  for showing the content of an article that was chosen from the list
 * of articles shown in ArticlesFragment fragment.
 *
 * @author Branko Mikusic 2021
 */
public class ArticleDetailActivity extends AppCompatActivity {

    /**
     * Method that overrides method from the inherited parent class and is called when Activity is being
     * created. Toolbar is set up here with home (back) button enabled. Values are fetched from bundle object
     * sent through the intent. Layout views are set-up with those values.
     *
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityArticleDetailBinding activityArticleDetailBinding = ActivityArticleDetailBinding.inflate(getLayoutInflater());

        setContentView(activityArticleDetailBinding.getRoot());

        setSupportActionBar(activityArticleDetailBinding.toolbarArticleDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button


        Intent intent = getIntent();
        Bundle articleBundle = intent.getBundleExtra(ArticlesItemRecyclerViewAdapter.INTENT_EXTRA_KEY_BUNDLE);

        activityArticleDetailBinding.articleDetailTvTitle.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_TITLE,""));
        activityArticleDetailBinding.articleDetailTvAuthor.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_AUTHOR,""));
        activityArticleDetailBinding.articleDetailTvContent.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_CONTENT,""));
        activityArticleDetailBinding.articleDetailTvDate.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_DATE,""));
        MyUtils.showImageFromUri(this,articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_IMGPATH,""),activityArticleDetailBinding.articleDetailImgvImage);
        activityArticleDetailBinding.articleDetailScrvContent.fullScroll(View.FOCUS_UP);

    }

    /**
     * Overrides the supertype method to add action that facilitate functionality of the "back" button in the
     * toolbar. The action is to close the Activity.
     *
     * @param item MenuItem reference, here it refers to "home" button in toolbar.
     * @return true if item was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}