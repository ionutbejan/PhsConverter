package com.example.phsconverter.main;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phsconverter.main.media.Media;

import java.util.ArrayList;

public interface MainView {
    void onCreate();

    AppCompatActivity getContext();

    void onRecording();

    void onPlaying();

    void message(String message, boolean success);

    void onStopRecording(String newRecording);

    void onStopPlaying(int position);

    void onMediaLoaded(ArrayList<Media> mediaFiles);

    void onPermissionDenied();

    void onMediaDeleted(boolean success);
}
