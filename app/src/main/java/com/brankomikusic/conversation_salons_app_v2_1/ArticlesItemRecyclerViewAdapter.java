package com.brankomikusic.conversation_salons_app_v2_1;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import java.util.List;
import java.util.Random;

/**
 * {@link RecyclerView.Adapter} Adapter for RecyclerView customized to show list of articles scraped from the Conversation Salons
 * blog website.
 *
 *  @author Branko Mikusic 2021
 */
public class ArticlesItemRecyclerViewAdapter extends RecyclerView.Adapter<ArticlesItemRecyclerViewAdapter.ArticleViewHolder> {

    public final static String BUNDLE_KEY_AUTHOR= "AUTHOR";
    public final static String BUNDLE_KEY_TITLE= "TITLE";
    public final static String BUNDLE_KEY_IMGPATH = "IMGPATH";
    public final static String BUNDLE_KEY_CONTENT = "CONTENT";
    public final static String BUNDLE_KEY_DATE = "DATE";
    public final static String INTENT_EXTRA_KEY_BUNDLE = "BUNDLE";

    private final List<Article> mValues;
    private final int itemViewResource;
    public static Animator currentAnimator;
    private Context context;
    private Random rnd;

    /**
     * Constructor sets class variables and shows appropriate View if there are not articles yet to be shown.
     *
     * @param items List of Article items to be shown in Recycler View.
     * @param context Context variable.
     * @param frameLayout_update Reference to the layout that is to be shown if items list is empty and there are no
     *                           articles to be shown.
     */
    public ArticlesItemRecyclerViewAdapter(List<Article> items, Context context, FrameLayout frameLayout_update,int limit,
                                           int item_view_resource) {
        this.context = context;
        this.itemViewResource = item_view_resource;
        if(limit>0 && limit<items.size()) mValues = items.subList(0,limit) ;
        else mValues = items;
        if(mValues.size()<=1){
            frameLayout_update.setVisibility(View.VISIBLE);
        }else{
            frameLayout_update.setVisibility(View.GONE);
        }
        rnd = new Random();
    }

    /**
     * Layout of article item is inflated and and returned as a placeholder for article data.
     * @param parent Reference to the parent ViewGroup.
     * @param viewType Type of the View being created.
     * @return inflated layout for holding article data.
     */
    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemViewResource, parent, false);
        return new ArticleViewHolder(view);
    }

    /**
     *  OnBind method for each ViewHolder which is used to fill the ViewHolder with data taken from the article in a certain position
     *  in article items list.
     * @param holder   A reference to the ViewHolder being bounded.
     * @param position Position of an article object in a list of items being shown in RecyclerView..
     */
    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, int position) {
        holder.tv_author.setText(mValues.get(position).getAuthor());
        holder.tv_title.setText(mValues.get(position).getTitle());
        holder.tv_date.setText(mValues.get(position).getDate_formated());
        MyUtils.showImageFromUri(context,mValues.get(position).getImg_path(),holder.image);

        Animation an = AnimationUtils.loadAnimation(context,R.anim.anim_wobble);
        int rnd_dur = rnd.nextInt(5)*10+50;
        an.setDuration(rnd_dur);
        holder.card.startAnimation(an);

        holder.layout.setOnClickListener((view)->{
            Intent intent = new Intent(holder.itemView.getContext(),ArticleDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_AUTHOR,mValues.get(position).getAuthor());
            bundle.putString(BUNDLE_KEY_DATE,mValues.get(position).getDate_formated());
            bundle.putString(BUNDLE_KEY_TITLE,mValues.get(position).getTitle());
            bundle.putString(BUNDLE_KEY_IMGPATH,mValues.get(position).getImg_path());
            bundle.putString(BUNDLE_KEY_CONTENT,mValues.get(position).getBody());
            intent.putExtra(INTENT_EXTRA_KEY_BUNDLE,bundle);
            context.startActivity(intent);
        });
    }

    /**
     *
     * @return number of items in a list.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * ViewHolder class for creating ViewHolder objects for items to be shown RecyclerView.
     */
    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tv_title;
        TextView tv_author;
        TextView tv_date;
        LinearLayout layout;
        CardView card;

        /**
         * Views for ArticleViewHolder are being initialized/
         * @param itemView Reference to RecyclerList item object(View).
         */
        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.rvitem_articles_iv_image);
            tv_author = itemView.findViewById(R.id.rvitem_articles_tv_author);
            tv_title = itemView.findViewById(R.id.rvitem_articles_tv_title);
            layout = itemView.findViewById(R.id.rvitem_articles_innerLinLayout);
            tv_date = itemView.findViewById(R.id.rvitem_articles_tv_date);
            card = itemView.findViewById(R.id.rvitem_articles_card);
        }

    }
}