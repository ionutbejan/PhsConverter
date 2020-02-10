package com.example.phsconverter.main.converted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phsconverter.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConvertedListAdapter extends RecyclerView.Adapter<ConvertedListAdapter.ViewHolder> {
    private ArrayList<File> files;

    ConvertedListAdapter() {
        this.files = new ArrayList<>();
    }

    void setFilesData(ArrayList<File> filesData) {
        this.files = filesData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.converted_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return files != null ? files.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_file_name)
        AppCompatTextView tvFileName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            tvFileName.setText(files.get(getAdapterPosition()).getName());
        }
    }
}
