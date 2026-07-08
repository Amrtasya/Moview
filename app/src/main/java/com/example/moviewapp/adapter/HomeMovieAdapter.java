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

public class HomeMovieAdapter extends RecyclerView.Adapter<HomeMovieAdapter.ViewHolder> {

    private final List<DiaryEntity> movieList;
    public HomeMovieAdapter(List<DiaryEntity> movieList) {
        this.movieList = movieList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_movie, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DiaryEntity movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());

        holder.tvRating.setText(
                "★ " + String.format("%.1f", movie.getRating())
        );

        holder.tvYearGenre.setText(movie.getGenre());

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w342" + movie.getPosterPath())
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPoster;
        TextView tvTitle;
        TextView tvRating;
        TextView tvYearGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvYearGenre = itemView.findViewById(R.id.tvYearGenre);
        }
    }
}