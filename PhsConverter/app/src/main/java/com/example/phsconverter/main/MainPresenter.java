package com.example.phsconverter.main;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phsconverter.main.media.Media;
import com.example.phsconverter.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.phsconverter.main.MainActivity.RequestPermissionCode;

class MainPresenter {
    private MainView view;
    private String newFilePath;
    private MainActivity activity;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording;
    private int playingPosition = -1;

    MainPresenter(MainView view) {
        activity = (MainActivity) view.getContext();
        this.view = view;
    }

    void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> onStopPlaying(playingPosition));
        view.onCreate();
    }

    void onRecording() {
        if (checkPermission()) {
            File pshDir = new File(FileUtils.getApplicationDataPath(activity));
            boolean pshDirAvailable = true;
            if (!pshDir.exists()) {
                pshDirAvailable = pshDir.mkdirs();
            }
            if (pshDirAvailable) {
                newFilePath = pshDir + "/phs_" +
                        System.currentTimeMillis() + ".3gp";
                prepareMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    view.onRecording();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    view.message(e.getMessage(), false);
                }
            } else {
                view.message("Could not access *.phs files", false);
            }
        } else {
            requestPermission();
        }
    }

    void onStopRecording() {
        mediaRecorder.stop();
        view.onStopRecording(newFilePath);
        view.message("Recording Completed !", true);
    }

    void onPlaying(String newFilePath, int playingPosition) {
        this.playingPosition = playingPosition;
        view.onPlaying();
        try {
            mediaPlayer.setDataSource(newFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            view.message(e.getMessage(), false);
            e.printStackTrace();
        }
    }

    void onStopPlaying(int position) {
        if (position != -1) {
            view.onStopPlaying(position);
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer.reset();
                prepareMediaRecorder();
            }
        }
    }

    void loadMedia() {
        if (checkPermission())
            view.onMediaLoaded(from(new File(FileUtils.getApplicationDataPath(activity))));
        else
            view.onPermissionDenied();
    }

    void delete(File mediaFile) {
        view.onMediaDeleted(mediaFile.delete());
    }

    private ArrayList<Media> from(File phsDir) {
        ArrayList<Media> inFiles = new ArrayList<>();
        if (phsDir.exists()) {
            File[] files = phsDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(from(file));
                } else {
                    if (file.getName().endsWith(".3gp") && file.getName().contains("phs")) {
                        inFiles.add(new Media(file));
                    }
                }
            }
        }
        Collections.reverse(inFiles);
        return inFiles;
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(activity, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_PHONE_STATE}, RequestPermissionCode);
    }

    boolean checkPermission() {
        int storage = ContextCompat.checkSelfPermission(activity,
                WRITE_EXTERNAL_STORAGE);
        int record = ContextCompat.checkSelfPermission(activity,
                RECORD_AUDIO);
        int phone = ContextCompat.checkSelfPermission(activity,
                READ_PHONE_STATE);
        return storage == PackageManager.PERMISSION_GRANTED &&
                record == PackageManager.PERMISSION_GRANTED &&
                phone == PackageManager.PERMISSION_GRANTED;
    }

    private void prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(newFilePath);
    }

    boolean isRecording() {
        return isRecording;
    }

    void setRecording(boolean recording) {
        isRecording = recording;
    }
}
