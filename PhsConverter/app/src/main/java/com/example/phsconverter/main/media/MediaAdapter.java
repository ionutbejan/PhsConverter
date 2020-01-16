package com.example.phsconverter.main.media;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phsconverter.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private ArrayList<Media> mediaFiles;
    private MediaClickListener listener;
    private Media playingMedia;

    public MediaAdapter(ArrayList<Media> files) {
        this.mediaFiles = files;
    }

    public void addMedia(ArrayList<Media> files) {
        this.mediaFiles = files;
        notifyDataSetChanged();
    }

    public void addMedia(String newRecording) {
        mediaFiles.add(0, new Media(new File(newRecording)));
        notifyItemInserted(0);
    }

    public void remove(Media media) {
        int pos = positionOf(media);
        mediaFiles.remove(media);
        notifyItemRemoved(pos);
    }

    private int positionOf(Media media) {
        for (int i = 0; i < mediaFiles.size(); i++) {
            if (media.getMediaFile().getName().equals(mediaFiles.get(i).getMediaFile().getName())) {
                return i;
            }
        }

        return -1;
    }

    public void onStopPlaying(int position) {
        Media media = mediaFiles.get(position);
        media.setPlaying(!media.isPlaying());
        notifyItemChanged(position);
        if (playingMedia != null && playingMedia != media) {
            playingMedia.setPlaying(false);
            int playingMediaPosition = positionOf(playingMedia);
            if (playingMediaPosition != -1)
                notifyItemChanged(playingMediaPosition);
        }
        playingMedia = media;
    }

    public void setMediaListener(MediaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mediaFiles != null ? mediaFiles.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ib_delete)
        ImageButton btnDelete;
        @BindView(R.id.ib_play)
        ImageButton btnPlay;
        @BindView(R.id.ib_convert)
        ImageButton btnConvert;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            setListeners();
        }

        void bind() {
            Media media = mediaFiles.get(getAdapterPosition());
            tvName.setText(itemView.getContext().getString(R.string.list_item_prefix, formatFrom(media.getMediaFile().getName())));
            btnPlay.setImageDrawable(itemView.getContext().getDrawable(media.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play));
        }

        private String formatFrom(String fileName) {
            Log.i(MediaAdapter.class.getSimpleName(), fileName);
            String timeStamp = fileName.split("_")[1].split("\\.")[0];
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            return formatter.format(new Date(Long.parseLong(timeStamp)));
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onMediaClicked(mediaFiles.get(getAdapterPosition()), getAdapterPosition());
            }
        }

        private void setListeners() {
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteMedia(mediaFiles.get(getAdapterPosition()));
                }
            });

            btnConvert.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConvertMedia(mediaFiles.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface MediaClickListener {
        void onMediaClicked(Media media, int position);

        void onDeleteMedia(Media media);

        void onConvertMedia(Media media);
    }
}
