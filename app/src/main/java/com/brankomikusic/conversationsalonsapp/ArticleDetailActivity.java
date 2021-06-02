package com.brankomikusic.conversationsalonsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_article_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show back button


        Intent intent = getIntent();
        Bundle articleBundle = intent.getBundleExtra(ArticlesItemRecyclerViewAdapter.INTENT_EXTRA_KEY_BUNDLE);
        ImageView imageView = findViewById(R.id.article_detail_imgv_image);
        TextView tv_title = findViewById(R.id.article_detail_tv_title);
        TextView tv_author = findViewById(R.id.article_detail_tv_author);
        TextView tv_content = findViewById(R.id.article_detail_tv_content);
        ScrollView scr_view = findViewById(R.id.article_detail_scrv_content);

        tv_title.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_TITLE,""));
        tv_author.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_AUTHOR,""));
        tv_content.setText(articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_CONTENT,""));
        MyUtils.showImageFromUri(this,articleBundle.getString(ArticlesItemRecyclerViewAdapter.BUNDLE_KEY_IMGPATH,""),imageView);
        scr_view.fullScroll(View.FOCUS_UP);

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