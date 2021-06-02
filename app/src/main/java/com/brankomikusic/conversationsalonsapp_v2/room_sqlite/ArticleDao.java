package com.brankomikusic.conversationsalonsapp_v2.room_sqlite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Dao interface for interacting with the Article table
 */
@Dao
public interface ArticleDao {

    @Query("SELECT * FROM Article")
    List<Article> getAll();

    @Query("SELECT * FROM Article where title LIKE  :titleVar")
    Article findByName(String titleVar);

    @Query("SELECT link FROM Article where articleId ==  (SELECT max(articleId) FROM Article)")
    String mostRecentArticleLink();

    @Query("SELECT COUNT(*) from Article")
    int countArticles();

    @Query("SELECT max(articleOrder) from Article")
    int maxOrderNum();

    @Query("SELECT link FROM Article where link LIKE  :linkVar")
    String findByLink(String linkVar);

    @Insert
    void insertAll(Article... articles);

    @Delete
    void delete(Article country);
}

