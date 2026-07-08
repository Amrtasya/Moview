package com.example.moviewapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite")
public class FavoriteEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private int tmdbId;
    private String title;
    private String posterPath;
    private String addedDate;
    private String genre;     // TAMBAHAN
    private double rating;    // TAMBAHAN

    public FavoriteEntity(int userId, int tmdbId, String title, String posterPath,
                          String addedDate, String genre, double rating) {
        this.userId = userId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.addedDate = addedDate;
        this.genre = genre;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public int getTmdbId() { return tmdbId; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public String getAddedDate() { return addedDate; }
    public String getGenre() { return genre; }       // Tambahkan getter
    public double getRating() { return rating; }    // Tambahkan getter
}