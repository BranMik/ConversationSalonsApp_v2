package com.brankomikusic.conversation_salons_app_v2_1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brankomikusic.conversation_salons_app_v2_1.databinding.FragmentArticlesListBinding;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;

/**
 * Fragment showing a list of Articles scraped previously from the blog section of Conversation
 * Salons web site.
 *
 * @author Branko Mikusic 2021
 */
public class ArticlesFragment extends Fragment {


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesFragment() {

    }

    /**
     *  Recycler View is initialized here.
     *
     * @param inflater Inflater object.
     * @param container Root ViewGroup object of the fragment.
     * @param savedInstanceState Bundle variable that may contain saved values/states (or could be null).
     * @return Root View of the fragments layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentArticlesListBinding fragmentArticlesListBinding = FragmentArticlesListBinding.inflate(inflater,container,false);
        fragmentArticlesListBinding.rvArticles.setLayoutManager(new LinearLayoutManager(fragmentArticlesListBinding.getRoot().getContext()));
        fragmentArticlesListBinding.rvArticles.setAdapter(new ArticlesItemRecyclerViewAdapter(Article.ITEMS,getContext(),
                fragmentArticlesListBinding.flArticlesUpdating, -1, R.layout.rv_articles_item));

        return fragmentArticlesListBinding.getRoot();
    }
}