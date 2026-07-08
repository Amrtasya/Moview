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

    // --- Operasi Dasar (CRUD) ---
    @Insert
    void insert(DiaryEntity diary);

    @Update
    void update(DiaryEntity diary);

    @Delete
    void delete(DiaryEntity diary);

    @Query("DELETE FROM diary WHERE id = :id")
    void deleteById(int id);

    // --- Query Data untuk UI ---

    @Query("SELECT * FROM diary WHERE id = :id LIMIT 1")
    DiaryEntity getDiaryById(int id);

    @Query("SELECT * FROM diary WHERE userId = :userId AND tmdbId = :tmdbId LIMIT 1")
    DiaryEntity getDiaryByTmdbId(int userId, int tmdbId);

    @Query("SELECT * FROM diary WHERE userId = :userId AND watchStatus = :status ORDER BY watchDate DESC")
    List<DiaryEntity> getDiaryByStatus(int userId, String status);

    @Query("SELECT * FROM diary WHERE userId = :userId ORDER BY watchDate DESC")
    List<DiaryEntity> getDiaryByUser(int userId);

    @Query("SELECT * FROM diary ORDER BY watchDate DESC")
    List<DiaryEntity> getAllDiary();

    @Query("SELECT * FROM diary WHERE userId = :userId AND isFavorite = 1 ORDER BY watchDate DESC")
    List<DiaryEntity> getFavoriteMovies(int userId);

    @Query("SELECT * FROM diary WHERE userId = :userId AND watchStatus = 'WATCHLIST' ORDER BY watchDate DESC")
    List<DiaryEntity> getWatchlist(int userId);

    // ===== TAMBAHAN: 3 query untuk stats di ProfileActivity =====

    // Total film yang sudah ditonton → angka "MOVIES"
    @Query("SELECT COUNT(*) FROM diary WHERE userId = :userId AND watchStatus = 'WATCHED'")
    int getTotalWatchedMovies(int userId);

    // Film yang ditonton tahun ini → angka "THIS YEAR"
    // watchDate format String "yyyy-MM-dd", strftime mengambil tahun dari string tsb
    @Query("SELECT COUNT(*) FROM diary WHERE userId = :userId AND watchStatus = 'WATCHED' AND strftime('%Y', watchDate) = strftime('%Y', 'now')")
    int getWatchedThisYear(int userId);

    // Rata-rata rating semua review user → angka "AVG. RATING"
    @Query("SELECT COALESCE(AVG(rating), 0.0) FROM diary WHERE userId = :userId AND rating > 0")
    float getAvgRating(int userId);
}