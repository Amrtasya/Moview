package com.example.moviewapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.DiaryAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.DiaryEntity;

import java.util.List;

public class DiaryLogsActivity extends AppCompatActivity {

    RecyclerView rvDiary;
    TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_logs);

        rvDiary = findViewById(R.id.rvDiary);
        txtEmpty = findViewById(R.id.txtEmpty);

        rvDiary.setLayoutManager(new LinearLayoutManager(this));

        List<DiaryEntity> diaries = DatabaseClient.getInstance(this)
                .diaryDao()
                .getAllDiary();

        DiaryAdapter adapter = new DiaryAdapter(diaries);
        rvDiary.setAdapter(adapter);

        if (diaries.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            rvDiary.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvDiary.setVisibility(View.VISIBLE);
        }
    }
}