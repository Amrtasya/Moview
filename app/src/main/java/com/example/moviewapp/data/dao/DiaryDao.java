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

    @Insert
    void insert(DiaryEntity diary);

    @Update
    void update(DiaryEntity diary);

    @Delete
    void delete(DiaryEntity diary);

    @Query("SELECT * FROM diary")
    List<DiaryEntity> getAllDiary();

    @Query("SELECT * FROM diary WHERE userId = :userId")
    List<DiaryEntity> getDiaryByUser(int userId);
}