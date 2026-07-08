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

    // Mengambil satu detail diary spesifik berdasarkan ID
    @Query("SELECT * FROM diary WHERE id = :id LIMIT 1")
    DiaryEntity getDiaryById(int id);

    // [TAMBAHAN] Untuk cek apakah film sudah ada di diary (untuk logika Update/Insert)
    @Query("SELECT * FROM diary WHERE userId = :userId AND tmdbId = :tmdbId LIMIT 1")
    DiaryEntity getDiaryByTmdbId(int userId, int tmdbId);

    // [TAMBAHAN] Untuk memfilter status (WATCHED atau WATCHLIST)
    @Query("SELECT * FROM diary WHERE userId = :userId AND watchStatus = :status ORDER BY watchDate DESC")
    List<DiaryEntity> getDiaryByStatus(int userId, String status);

    // Mengambil semua catatan diary milik user tertentu
    @Query("SELECT * FROM diary WHERE userId = :userId ORDER BY watchDate DESC")
    List<DiaryEntity> getDiaryByUser(int userId);

    // Mengambil semua diary tanpa filter user
    @Query("SELECT * FROM diary ORDER BY watchDate DESC")
    List<DiaryEntity> getAllDiary();

    // Mengambil data untuk halaman Favorite
    @Query("SELECT * FROM diary WHERE userId = :userId AND isFavorite = 1 ORDER BY watchDate DESC")
    List<DiaryEntity> getFavoriteMovies(int userId);

    // Tambahkan ini ke dalam DiaryDao.java
    @Query("SELECT * FROM diary WHERE userId = :userId AND watchStatus = 'WATCHLIST' ORDER BY watchDate DESC")
    List<DiaryEntity> getWatchlist(int userId);
}