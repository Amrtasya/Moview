package com.example.moviewapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.data.entity.DiaryEntity;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    List<DiaryEntity> diaryList;

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

        holder.txtMovieTitle.setText(diary.getTitle());
        holder.txtReview.setText(diary.getReview());
        holder.txtRating.setText(String.valueOf(diary.getRating()));

    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMovieTitle;
        TextView txtReview;
        TextView txtRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtReview = itemView.findViewById(R.id.txtReview);
            txtRating = itemView.findViewById(R.id.txtRating);

        }
    }

}