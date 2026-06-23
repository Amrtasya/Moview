package com.example.moviewapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.moviewapp.data.entity.WatchlistEntity;

import java.util.List;

@Dao
public interface WatchlistDao {

    @Insert
    void insert(WatchlistEntity movie);

    @Delete
    void delete(WatchlistEntity movie);

    @Query("SELECT * FROM watchlist")
    List<WatchlistEntity> getAllWatchlist();

    @Query("SELECT * FROM watchlist WHERE userId = :userId")
    List<WatchlistEntity> getWatchlistByUser(int userId);
}