package com.example.moviewapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.moviewapp.data.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(FavoriteEntity movie);

    @Delete
    void delete(FavoriteEntity movie);

    @Query("SELECT * FROM favorite")
    List<FavoriteEntity> getAllFavorites();

    @Query("SELECT * FROM favorite WHERE userId = :userId")
    List<FavoriteEntity> getFavoritesByUser(int userId);
}