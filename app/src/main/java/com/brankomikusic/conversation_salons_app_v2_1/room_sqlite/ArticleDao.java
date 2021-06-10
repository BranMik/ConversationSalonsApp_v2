package com.brankomikusic.conversation_salons_app_v2_1.room_sqlite;

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

    @Query("SELECT * FROM Article order by date_published DESC")
    List<Article> getAll();

    @Query("SELECT * FROM Article where title LIKE  :titleVar")
    Article findByName(String titleVar);

    @Query("SELECT COUNT(*) from Article")
    int countArticles();

    @Query("SELECT * from Article order by date_published DESC LIMIT :howMany")
    List<Article> getRecentArticles(int howMany);

    @Query("SELECT link FROM Article where link LIKE  :linkVar")
    String findByLink(String linkVar);

    @Insert
    void insertAll(Article... articles);

    @Delete
    void delete(Article article);
}

