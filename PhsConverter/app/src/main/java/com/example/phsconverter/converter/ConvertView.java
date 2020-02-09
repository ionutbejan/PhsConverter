package com.example.phsconverter.converter;

import android.content.Context;

import java.io.File;

public interface ConvertView {
    void showProgress();

    void hideProgress();

    Context getContext();

    void updateOnReset(boolean delete);

    void showMessage(String message);

    void updateAfterStart(String[] data);

    boolean canUpdateFragmentUI();

    String getHeadsPositions();

    void updatePager(int position);

    void onWordsFinished();

    File getCurrentPlayingFile();
}
