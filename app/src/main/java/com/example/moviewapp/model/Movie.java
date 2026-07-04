package com.example.moviewapp.model;

public class Movie {

    private int id;
    private String title;
    private String poster_path;

    // Rating dari TMDB
    private double vote_average;

    // Tanggal rilis
    private String release_date;

    // =====================
    // GETTER
    // =====================

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    // =====================
    // Helper
    // =====================

    public String getPosterUrl() {
        return "https://image.tmdb.org/t/p/w500" + poster_path;
    }

    public String getYear() {
        if (release_date != null && release_date.length() >= 4) {
            return release_date.substring(0, 4);
        }
        return "-";
    }
}