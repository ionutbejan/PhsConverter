package com.example.phsconverter.converter;

import androidx.annotation.NonNull;

public class Data {
    private String text;
    private String startStamp;
    private String endStamp;

    Data(String text, String startStamp, String endStamp) {
        this.text = text;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStartStamp() {
        return startStamp;
    }

    public void setStartStamp(String startStamp) {
        this.startStamp = startStamp;
    }

    public String getEndStamp() {
        return endStamp;
    }

    public void setEndStamp(String endStamp) {
        this.endStamp = endStamp;
    }

    @NonNull
    @Override
    public String toString() {
        return startStamp + "   " + endStamp + "   " + text + "\n";
    }
}
