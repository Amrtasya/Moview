package com.example.moviewapp.ui.diary;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewapp.R;
import com.example.moviewapp.adapter.WatchlistAdapter;
import com.example.moviewapp.data.database.DatabaseClient;
import com.example.moviewapp.data.entity.WatchlistEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    private RecyclerView rvWatchlist;
    private ImageView btnBack;
    private FloatingActionButton fabAdd;

    private WatchlistAdapter adapter;
    private List<WatchlistEntity> watchlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        initView();
        loadWatchlist();

        btnBack.setOnClickListener(v -> finish());

        fabAdd.setOnClickListener(v -> {
            // TODO: Buka Search Movie atau Review Movie
        });
    }

    private void initView() {
        rvWatchlist = findViewById(R.id.rvWatchlist);
        btnBack = findViewById(R.id.btnBack);
        fabAdd = findViewById(R.id.fabAdd);

        rvWatchlist.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadWatchlist() {
        watchlistList = DatabaseClient
                .getInstance(this)
                .watchlistDao()
                .getAllWatchlist();

        adapter = new WatchlistAdapter(this, watchlistList);
        rvWatchlist.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlist();
    }
}