package com.example.phsconverter.converter.textslider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phsconverter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextSlideViewPager extends RecyclerView.Adapter<TextSlideViewPager.ViewHolder> {

    private String[] mData;
    private LayoutInflater layoutInflater;

    public TextSlideViewPager(Context context, String[] data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_viewpager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_current_word)
        AppCompatTextView tvCurrentWord;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            tvCurrentWord.setText(mData[getAdapterPosition()]);
        }
    }

}
