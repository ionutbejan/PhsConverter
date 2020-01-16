package com.example.phsconverter.main.media;

import java.io.File;

public class Media {
    private File mediaFile;
    private boolean isPlaying;

    public Media(File mediaFile) {
        this.mediaFile = mediaFile;
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
    }
}
