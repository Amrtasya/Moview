package com.example.moviewapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moviewapp.data.entity.DiaryEntity;

import java.util.List;

@Dao
public interface DiaryDao {

    // Create
    @Insert
    void insert(DiaryEntity diary);

    // Update
    @Update
    void update(DiaryEntity diary);

    // Delete
    @Delete
    void delete(DiaryEntity diary);

    // Semua diary berdasarkan user
    @Query("SELECT * FROM diary WHERE userId = :userId ORDER BY watchDate DESC")
    List<DiaryEntity> getDiaryByUser(int userId);

    // Ambil semua diary
    @Query("SELECT * FROM diary ORDER BY watchDate DESC")
    List<DiaryEntity> getAllDiary();

    // Detail diary berdasarkan id
    @Query("SELECT * FROM diary WHERE id = :id LIMIT 1")
    DiaryEntity getDiaryById(int id);

    // Watchlist
    @Query("SELECT * FROM diary WHERE userId = :userId AND watchStatus = 'WATCHLIST' ORDER BY watchDate DESC")
    List<DiaryEntity> getWatchlist(int userId);

    // Favorite
    @Query("SELECT * FROM diary WHERE userId = :userId AND isFavorite = 1 ORDER BY watchDate DESC")
    List<DiaryEntity> getFavoriteMovies(int userId);

    // Hapus berdasarkan id
    @Query("DELETE FROM diary WHERE id = :id")
    void deleteById(int id);
}