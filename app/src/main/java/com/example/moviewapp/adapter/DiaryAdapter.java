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
import com.example.moviewapp.data.entity.DiaryEntity;
import com.example.moviewapp.ui.diary.EditReviewActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private final List<DiaryEntity> diaryList;

    public DiaryAdapter(List<DiaryEntity> diaryList) {
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntity diary = diaryList.get(position);

        // 1. Set Teks Dasar
        holder.txtMovieTitle.setText(diary.getTitle());
        holder.txtReview.setText(diary.getReview() != null ? diary.getReview() : "");
        holder.txtGenre.setText(diary.getWatchStatus() != null ? diary.getWatchStatus() : "WATCHED");
        holder.txtRating.setText(String.format(Locale.getDefault(), "⭐ %.1f", diary.getRating()));

        // 2. Logic Parsing Tanggal (Format database: yyyy-MM-dd)
        if (diary.getWatchDate() != null && !diary.getWatchDate().isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(diary.getWatchDate());

                if (date != null) {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

                    holder.txtDay.setText(dayFormat.format(date));
                    holder.txtMonth.setText(monthFormat.format(date).toUpperCase());
                }
            } catch (Exception e) {
                holder.txtDay.setText("-");
                holder.txtMonth.setText("-");
            }
        }

        // 3. Logic Loading Poster dengan Glide
        if (diary.getPosterPath() != null && !diary.getPosterPath().isEmpty()) {
            String posterUrl = "https://image.tmdb.org/t/p/w342" + diary.getPosterPath();
            Glide.with(holder.itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Pastikan drawable ini ada
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_launcher_background);
        }

        // 4. Click Listener untuk Edit
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditReviewActivity.class);
            intent.putExtra("DIARY_ID", diary.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDay, txtMonth, txtMovieTitle, txtGenre, txtReview, txtRating;
        ImageView imgPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtMonth = itemView.findViewById(R.id.txtMonth);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtGenre = itemView.findViewById(R.id.txtGenre);
            txtReview = itemView.findViewById(R.id.txtReview);
            txtRating = itemView.findViewById(R.id.txtRating);
        }
    }
}