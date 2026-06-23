package com.example.moviewapp.ui;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_logs);

        rvDiary = findViewById(R.id.rvDiary);

        List<DiaryEntity> diaries =
                DatabaseClient.getInstance(this)
                        .diaryDao()
                        .getAllDiary();

        DiaryAdapter adapter = new DiaryAdapter(diaries);

        rvDiary.setLayoutManager(new LinearLayoutManager(this));
        rvDiary.setAdapter(adapter);
    }
}