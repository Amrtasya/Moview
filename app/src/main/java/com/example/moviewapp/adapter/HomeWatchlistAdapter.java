package com.example.moviewapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviewapp.R;
import com.example.moviewapp.data.entity.WatchlistEntity;

import java.util.List;

public class HomeWatchlistAdapter extends RecyclerView.Adapter<HomeWatchlistAdapter.ViewHolder> {

    private final Context context;
    private final List<WatchlistEntity> movieList;

    public HomeWatchlistAdapter(Context context, List<WatchlistEntity> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_home_watchlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WatchlistEntity movie = movieList.get(position);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster = itemView.findViewById(R.id.imgPoster);
        }
    }
}