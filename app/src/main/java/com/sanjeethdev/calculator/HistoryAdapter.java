package com.sanjeethdev.calculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {


    private Context context;
    private List<String> outputString;

    public HistoryAdapter(Context context, List<String> outputString) {
        this.context = context;
        this.outputString = outputString;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.textView.setText(outputString.get(position));
    }

    @Override
    public int getItemCount() {
        return outputString.size();
    }


    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.history_view_output);
        }
    }

}
