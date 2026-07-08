package com.example.moviewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.entity.DiaryEntity;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<DiaryEntity> historyList;

    public HistoryAdapter(List<DiaryEntity> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntity movie = historyList.get(position);

        holder.txtTitle.setText(movie.getTitle());

        // Menggunakan data yang tersedia di DiaryEntity
        // Menampilkan Director sebagai ganti Genre/Year agar tidak error
        String subTitle = (movie.getDirector() != null && !movie.getDirector().isEmpty())
                ? movie.getDirector() : "No Director Info";
        holder.txtGenreYear.setText(subTitle);

        holder.txtDate.setText(String.format(Locale.getDefault(), "📅 %s", movie.getWatchDate()));

        // Menampilkan bintang sesuai rating
        int stars = (int) (movie.getRating() / 2);
        String ratingDisplay = String.format(Locale.getDefault(), "%.1f", movie.getRating());

        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            starStr.append("⭐");
        }
        holder.txtRating.setText(String.format("%s %s", starStr.toString(), ratingDisplay));

        String posterUrl = "https://image.tmdb.org/t/p/w342" + movie.getPosterPath();
        Glide.with(holder.itemView.getContext())
                .load(posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitle, txtGenreYear, txtDate, txtRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtGenreYear = itemView.findViewById(R.id.txtGenreYear);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtRating = itemView.findViewById(R.id.txtRating);
        }
    }
}