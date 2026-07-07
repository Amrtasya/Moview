package com.example.moviewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.model.Movie;
import com.bumptech.glide.Glide;
import com.example.moviewapp.ui.movie.MovieDetailActivity;

import java.util.List;
import android.util.Log;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;

    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Movie movie = movieList.get(position);

        holder.tvTitle.setText(movie.getTitle());

        // Genre sementara
        holder.tvGenre.setText(
                movie.getGenreName() + " • " + movie.getYear()
        );

        // Belum ada rating user
        holder.tvRating.setText("☆☆☆☆☆");
        holder.tvStatus.setText("Not Rated Yet");

        String imageUrl =
                "https://image.tmdb.org/t/p/w500"
                        + movie.getPoster_path();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(holder.itemView.getContext(),
                            MovieDetailActivity.class);

            intent.putExtra("movie_id", movie.getId());

            holder.itemView.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvGenre;
        TextView tvRating;
        TextView tvStatus;
        ImageView imgPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            imgPoster = itemView.findViewById(R.id.imgPoster);
        }
    }
}