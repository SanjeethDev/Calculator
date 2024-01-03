package com.sanjeethdev.calculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sanjeethdev.calculator.databinding.ActivityHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private static ActivityHistoryBinding binding;
    private List<String> outputStrings;
    private HistoryAdapter adapter;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        View viewBinding = binding.getRoot();
        setContentView(viewBinding);

        dbHelper = new DBHelper(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        binding.historyRecyclerview.setLayoutManager(linearLayoutManager);
        outputStrings = new ArrayList<>();

        showHistory();
        adapter = new HistoryAdapter(HistoryActivity.this, outputStrings);
        binding.historyRecyclerview.setAdapter(adapter);

        binding.historyBack.setOnClickListener(view -> {
            dbHelper.close();
            finish();
        });

        binding.historyClear.setOnClickListener(view -> {
            dbHelper.deleteAllEquation();
            binding.historyRecyclerview.notifyAll();

        });

    }

    private void showHistory() {
        Cursor cursor = dbHelper.getHistory();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No History", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                outputStrings.add(cursor.getString(1) + "\n = " + cursor.getString(2));
            }
        }
    }
}