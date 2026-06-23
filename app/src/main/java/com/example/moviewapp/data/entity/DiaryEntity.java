package com.example.moviewapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary")
public class DiaryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private int tmdbId;
    private String title;
    private String posterPath;
    private float rating;
    private String review;
    private String watchStatus;
    private String watchDate;
    private String createdAt;
    private String updatedAt;

    public DiaryEntity(int userId, int tmdbId, String title,
                       String posterPath, float rating, String review,
                       String watchStatus, String watchDate,
                       String createdAt, String updatedAt) {
        this.userId = userId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.review = review;
        this.watchStatus = watchStatus;
        this.watchDate = watchDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public int getTmdbId() { return tmdbId; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public float getRating() { return rating; }
    public String getReview() { return review; }
    public String getWatchStatus() { return watchStatus; }
    public String getWatchDate() { return watchDate; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}