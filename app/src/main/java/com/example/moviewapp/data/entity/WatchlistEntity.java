package com.example.moviewapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchlist")
public class WatchlistEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private int tmdbId;
    private String title;
    private String posterPath;
    private String genre;
    private String addedDate;

    public WatchlistEntity(int userId,
                           int tmdbId,
                           String title,
                           String posterPath,
                           String genre,
                           String addedDate) {

        this.userId = userId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.genre = genre;
        this.addedDate = addedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getGenre() {
        return genre;
    }

    public String getAddedDate() {
        return addedDate;
    }
}