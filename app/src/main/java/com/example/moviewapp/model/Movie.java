package com.example.moviewapp.model;

import java.util.List;

public class Movie {

    private int id;
    private String title;
    private String poster_path;

    // Rating dari TMDB
    private double vote_average;

    // Tanggal rilis
    private String release_date;

    // Genre
    private List<Integer> genre_ids;
    private List<Genre> genres;
    private String overview;

    private int runtime;

    private String backdrop_path;
    private String original_language;

    public static class Genre {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // =====================
    // GETTER
    // =====================

    public List<Genre> getGenres() {
        return genres;
    }

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

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public String getOverview() {
        return overview;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getBackdropUrl() {
        return "https://image.tmdb.org/t/p/w780" + backdrop_path;
    }
    public String getOriginal_language() {
        return original_language;
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

    public String getGenreName() {

        // Kalau dari endpoint Detail Movie
        if (genres != null && !genres.isEmpty()) {
            return genres.get(0).getName();
        }

        // Kalau dari Search Movie
        if (genre_ids == null || genre_ids.isEmpty()) {
            return "Movie";
        }

        switch (genre_ids.get(0)) {

            case 28:
                return "Action";

            case 12:
                return "Adventure";

            case 16:
                return "Animation";

            case 35:
                return "Comedy";

            case 80:
                return "Crime";

            case 18:
                return "Drama";

            case 14:
                return "Fantasy";

            case 27:
                return "Horror";

            case 9648:
                return "Mystery";

            case 10749:
                return "Romance";

            case 878:
                return "Science Fiction";

            case 53:
                return "Thriller";

            default:
                return "Movie";
        }
    }
}