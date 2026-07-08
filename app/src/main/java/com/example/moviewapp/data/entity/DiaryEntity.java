package com.example.moviewapp.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary")
public class DiaryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private int tmdbId;
    private String title;
    private String director;
    private String genre; // Added genre field
    private String posterPath;
    private float rating;
    private String review;
    private String watchStatus;
    private String watchDate;
    private boolean isFavorite;
    private String createdAt;
    private String updatedAt;

    // Constructor kosong untuk Room
    @Ignore
    public DiaryEntity() {
    }

    // Constructor lengkap
    public DiaryEntity(int userId, int tmdbId, String title, String director, String genre,
                       String posterPath, float rating, String review,
                       String watchStatus, String watchDate,
                       boolean isFavorite,
                       String createdAt, String updatedAt) {
        this.userId = userId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.posterPath = posterPath;
        this.rating = rating;
        this.review = review;
        this.watchStatus = watchStatus;
        this.watchDate = watchDate;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getter ---
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTmdbId() { return tmdbId; }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public String getGenre() { return genre; }
    public String getPosterPath() { return posterPath; }
    public float getRating() { return rating; }
    public String getReview() { return review; }
    public String getWatchStatus() { return watchStatus; }
    public String getWatchDate() { return watchDate; }
    public boolean isFavorite() { return isFavorite; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // --- Setter ---
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
    public void setTitle(String title) { this.title = title; }
    public void setDirector(String director) { this.director = director; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setRating(float rating) { this.rating = rating; }
    public void setReview(String review) { this.review = review; }
    public void setWatchStatus(String watchStatus) { this.watchStatus = watchStatus; }
    public void setWatchDate(String watchDate) { this.watchDate = watchDate; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}