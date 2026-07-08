package com.example.moviewapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.entity.WatchlistEntity;
import com.example.moviewapp.ui.diary.ReviewMovieActivity;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final Context context;
    private final List<WatchlistEntity> watchlistList;

    public WatchlistAdapter(Context context, List<WatchlistEntity> watchlistList) {
        this.context = context;
        this.watchlistList = watchlistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistEntity movie = watchlistList.get(position);

        // Mengatur judul dan genre
        holder.txtTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown Movie");
        holder.txtGenre.setText(movie.getGenre() != null ? movie.getGenre() : "No Genre");

        // Memuat poster film
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

        // Menambahkan navigasi ke halaman ReviewMovieActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewMovieActivity.class);
            // Mengirim data film agar ReviewMovieActivity tahu film apa yang dibuka
            intent.putExtra("TMDB_ID", movie.getTmdbId());
            intent.putExtra("MOVIE_TITLE", movie.getTitle());
            intent.putExtra("POSTER_PATH", movie.getPosterPath());
            intent.putExtra("GENRE", movie.getGenre());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return watchlistList != null ? watchlistList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPoster;
        TextView txtTitle;
        TextView txtGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtGenre = itemView.findViewById(R.id.txtGenre);
        }
    }
}