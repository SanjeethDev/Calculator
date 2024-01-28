package com.sanjeethdev.calculator;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sanjeethdev.calculator.databinding.ActivityHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    DBHelper dbHelper;
    private List<String> outputStrings;
    private HistoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHistoryBinding binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        View viewBinding = binding.getRoot();
        setContentView(viewBinding);

        dbHelper = new DBHelper(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        binding.historyRecyclerview.setLayoutManager(linearLayoutManager);
        outputStrings = new ArrayList<>();

        showHistory();
        adapter = new HistoryAdapter(outputStrings);
        binding.historyRecyclerview.setAdapter(adapter);

        binding.historyBack.setOnClickListener(view -> {
            dbHelper.close();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        binding.historyClear.setOnClickListener(view -> clearHistory());


    }

    private void showHistory() {
        Cursor cursor = dbHelper.getHistory();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Empty History", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                outputStrings.add(cursor.getString(1) + "\n = " + cursor.getString(2));
            }
        }
    }

    private void clearHistory() {
        int size = adapter.getItemCount();
        outputStrings.clear();
        adapter.notifyItemRangeRemoved(0, size);
        dbHelper.deleteAllEquation();
    }
}