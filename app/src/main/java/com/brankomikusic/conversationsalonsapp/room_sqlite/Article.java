package com.brankomikusic.conversationsalonsapp.room_sqlite;


import android.content.Context;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.brankomikusic.conversationsalonsapp.ArticlesFragment;
import com.brankomikusic.conversationsalonsapp.ArticlesItemRecyclerViewAdapter;
import com.brankomikusic.conversationsalonsapp.MainActivity;
import com.brankomikusic.conversationsalonsapp.backgroundtasks.UpdateArticlesInDbFromBlog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class describing Article table in the Room database articles-database
 *
 * @author Branko Mikusic
 */
@Entity(tableName = "Article")
public class Article {
    @PrimaryKey(autoGenerate = true)
    private int articleId;

    @ColumnInfo(name = "articleOrder")
    private int articleOrder;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "img_path")
    private String img_path;

    public final static List<Article> ITEMS = new ArrayList<>();

    /**
     * Constructor setting values to fields in the table
     *
     * @param title value for title field
     * @param author value for author field
     * @param img_path value for img_path field
     */
    public Article(String title, String author, String img_path){
        super();
        this.title = title;
        this.author = author;
        this.img_path = img_path;
    }

    public Article(){
        super();
    }

    /**
     * Non standard helper method used for creating Articles list that is source for
     * ArticlesRecyclerAdapter
     *
     * @param ctx Context reference
     */
    public static void initArticleList(Context ctx){
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        ITEMS.clear();
        Future<List<Article>>  listFromDb = executorService.submit(()->{
            ArticlesDatabase articlesDatabase = ArticlesDatabase.getAppDatabase(ctx);
            return articlesDatabase.articleDao().getAll();
        });
        try {
            ITEMS.addAll(listFromDb.get());
            UpdateArticlesInDbFromBlog.articles_reinitialize_flag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleOrder() {
        return articleOrder;
    }

    public void setArticleOrder(int articleOrder) {
        this.articleOrder = articleOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }
}
