package com.example.phsconverter.converter;

import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.phsconverter.utils.FileUtils;
import com.example.phsconverter.utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

class ConvertPresenter {
    private final static String SPACE_REGEX = "\\s+";
    private final static String SPECIAL_CHARACTERS_REGEX = "[^\\p{L}\\p{Z}]";
    final static String SPLIT_CHARACTER = ":";
    private ConvertView view;
    private boolean started = false;
    private boolean finished = false;
    private ArrayList<Data> dataToConvert;
    private String[] textData;
    private int currentWordPosition = 0;
    private String initialTextData;

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
                initialTextData = etText.getText().toString();
                etText.setVisibility(View.INVISIBLE);
                String data = etText.getText().toString();
                textData = data.replaceAll(SPECIAL_CHARACTERS_REGEX, " ").split(SPACE_REGEX);
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
        if (finished) {
            view.showProgress();
            long conversionTime = Calendar.getInstance().getTimeInMillis();
            String[] files = new String[3];
            files[0] = FileUtils.writeToFile(initialTextData, view.getContext(), FileUtils.LAB_EXTENSION, conversionTime);
            files[1] = FileUtils.writeToFile(TextUtils.toPlainText(dataToConvert), view.getContext(), FileUtils.PHS_EXTENSION, conversionTime);
            files[2] = view.getCurrentPlayingFile().getPath();

            if (FileUtils.zip(view.getContext(), files, conversionTime)) {
                File labFile = new File(files[0]);
                File phsFile = new File(files[1]);
                labFile.delete();
                phsFile.delete();
            }

            view.hideProgress();
        } else {
            view.showMessage("You cannot convert at this time.");
        }
    }
}
