package com.brankomikusic.conversation_salons_app_v2_1.room_sqlite;


import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;

import java.util.ArrayList;
import java.util.List;
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

    @ColumnInfo(name = "date_published")
    private String date_published;

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

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }

    public String getDate_published() {
        return date_published;
    }

    public String getDate_formated() {
        String date_formated = date_published.substring(6,8)+"/"+date_published.substring(4,6)+"/"+date_published.substring(0,4);
        return date_formated;
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
