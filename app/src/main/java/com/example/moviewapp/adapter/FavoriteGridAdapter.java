package com.example.moviewapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.data.entity.FavoriteEntity;
import com.example.moviewapp.ui.diary.EditReviewActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteGridAdapter extends RecyclerView.Adapter<FavoriteGridAdapter.ViewHolder> {

    private final List<FavoriteEntity> favoriteList;

    public FavoriteGridAdapter(List<FavoriteEntity> favoriteList) {
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteEntity movie = favoriteList.get(position);
        holder.txtMovieTitle.setText(movie.getTitle());

        int stars = (int) (movie.getRating() / 2);
        StringBuilder starStr = new StringBuilder();
        for(int i = 0; i < stars; i++) {
            starStr.append("⭐");
        }
        holder.txtRatingStars.setText(starStr.toString());

        String posterUrl = "https://image.tmdb.org/t/p/w342" + movie.getPosterPath();
        Glide.with(holder.itemView.getContext())
                .load(posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imgPoster);

        // Perbaikan: Mencari Diary ID di Background agar tidak error
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                // Mencari record di tabel diary berdasarkan userId dan tmdbId
                DiaryEntity diary = DatabaseClient.getInstance(context)
                        .diaryDao().getDiaryByTmdbId(movie.getUserId(), movie.getTmdbId());

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (diary != null) {
                        Intent intent = new Intent(context, EditReviewActivity.class);
                        intent.putExtra("DIARY_ID", diary.getId());
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Data review tidak ditemukan untuk film ini", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            executor.shutdown();
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtMovieTitle, txtRatingStars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtRatingStars = itemView.findViewById(R.id.txtRatingStars);
        }
    }
}