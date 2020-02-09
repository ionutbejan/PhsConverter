package com.example.phsconverter.converter;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;

import static com.example.phsconverter.utils.Constants.TAG;

class ConvertPresenter {
    private final static String SPACE_REGEX = "\\s+";
    final static String SPLIT_CHARACTER = ":";
    private ConvertView view;
    private boolean started = false;
    private boolean finished = false;
    private ArrayList<Data> dataToConvert;
    private String[] textData;
    private int currentWordPosition = 0;

    ConvertPresenter(ConvertView view) {
        this.view = view;
    }

    void onAction(AppCompatEditText etText) {
        if (finished) {
            return;
        }

        if (!started) {
            if (etText.getText() != null && !etText.getText().toString().isEmpty()) {
                started = true;
                etText.setVisibility(View.INVISIBLE);
                textData = etText.getText().toString().split(SPACE_REGEX);
                dataToConvert = new ArrayList<>();
                view.updateAfterStart(textData);
            } else {
                view.showMessage("No text data available");
                reset(false);
            }
        } else {
            highlight(currentWordPosition);
        }
    }

    void reset(boolean delete) {
        started = false;
        finished = false;
        textData = null;
        dataToConvert = new ArrayList<>();
        currentWordPosition = 0;
        view.updateOnReset(delete);
    }

    private void highlight(int wordPosition) {
        if (wordPosition <= textData.length - 1) {
            if (view.canUpdateFragmentUI()) {
                String headsPositions = view.getHeadsPositions();
                String startPosition = headsPositions.split(SPLIT_CHARACTER)[0];
                String endPosition = headsPositions.split(SPLIT_CHARACTER)[1];
                dataToConvert.add(new Data(textData[wordPosition], startPosition, endPosition));
                currentWordPosition++;
                view.updatePager(currentWordPosition);
                if (currentWordPosition == textData.length) {
                    finished = true;
                    view.onWordsFinished();
                }
            }
        } else {
            finished = true;
            view.onWordsFinished();
        }
    }

    void onConvert() {
        Log.i(TAG, dataToConvert + "");
    }
}
