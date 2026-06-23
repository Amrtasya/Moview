package com.example.moviewapp.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.moviewapp.data.dao.DiaryDao;
import com.example.moviewapp.data.dao.FavoriteDao;
import com.example.moviewapp.data.dao.UserDao;
import com.example.moviewapp.data.dao.WatchlistDao;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.data.entity.FavoriteEntity;
import com.example.moviewapp.data.entity.UserEntity;
import com.example.moviewapp.data.entity.WatchlistEntity;

@Database(
        entities = {
                UserEntity.class,
                DiaryEntity.class,
                WatchlistEntity.class,
                FavoriteEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract DiaryDao diaryDao();

    public abstract WatchlistDao watchlistDao();

    public abstract FavoriteDao favoriteDao();
}