package com.example.phsconverter.converter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;

import com.example.phsconverter.R;
import com.example.phsconverter.utils.Constants;
import com.example.phsconverter.utils.FileUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.phsconverter.utils.Constants.FILE_KEY;

public class ConvertActivity extends AppCompatActivity {
    private final static String SPACE_REGEX = "\\s+";
    @BindView(R.id.tv_reset)
    AppCompatTextView tvDelete;
    @BindView(R.id.et_text)
    AppCompatEditText etText;
    @BindView(R.id.tv_current_word)
    AppCompatTextView tvCurrentWord;
    @BindView(R.id.tv_convert)
    AppCompatTextView tvConvert;
    @BindView(R.id.tv_action)
    AppCompatTextView tvAction;
    private boolean started = false;
    private boolean finished = false;
    private ArrayList<Data> dataToConvert;
    private String[] textData;
    private int currentWordPosition = 0;
    private CustomWaveformFragment convertFragment;

    public static void launch(Context context, File file) {
        Intent intent = new Intent(context, ConvertActivity.class);
        intent.putExtra(FILE_KEY, file);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convert_activity);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            convertFragment = CustomWaveformFragment.launch(((File) getIntent().getSerializableExtra(FILE_KEY)).getPath());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, convertFragment)
                    .commit();
        }
    }

    @OnClick(R.id.tv_reset)
    public void onDeleteInputText() {
        if (etText.getText() != null && !etText.getText().toString().isEmpty()) {
            reset(true);
        }
    }

    private void reset(boolean delete) {
        started = false;
        finished = false;
        textData = null;
        dataToConvert = new ArrayList<>();
        tvAction.setText(getString(R.string.start));
        currentWordPosition = 0;
        convertFragment.resetMarkers();
        tvCurrentWord.setText("");
        if (delete)
            etText.setText("");
    }

    @OnClick(R.id.tv_action)
    public void onAction() {
        if (finished) {
            for (Data data : dataToConvert)
                Log.i(ConvertActivity.class.getSimpleName(), data.toString() + "");
            return;
        }

        if (!started) {
            if (etText.getText() != null && !etText.getText().toString().isEmpty()) {
                started = true;
                tvAction.setText(getString(R.string.next));
                textData = etText.getText().toString().split(SPACE_REGEX);
                dataToConvert = new ArrayList<>();
                highlightWord(textData[0]);
            } else {
                Snackbar.make(tvAction, "No text data available", Snackbar.LENGTH_SHORT).show();
                reset(false);
            }
        } else {
            highlight(currentWordPosition);
        }
    }

    private void highlight(int wordPosition) {
        highlightWord(textData[wordPosition]);
        if (wordPosition <= textData.length - 1) {
            String startPosition = convertFragment.getStartMakerPos();
            String endPosition = convertFragment.getEndMarkerPos();
            if (!convertFragment.updateMarkers()) {
                Snackbar.make(tvAction, "Markers offset error", Snackbar.LENGTH_SHORT).show();
                return;
            }
            dataToConvert.add(new Data(textData[wordPosition], startPosition, endPosition));
            Log.i(Constants.TAG, "Added " + textData[wordPosition] + " from position " + currentWordPosition);

            currentWordPosition++;
            if (currentWordPosition == textData.length) {
                onWordsFinished();
            }
        } else {
            onWordsFinished();
        }
    }

    private void onWordsFinished() {
        tvAction.setText(getString(R.string.done));
        finished = true;
    }

    @OnClick(R.id.tv_convert)
    public void onConvert() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification_channel_id");
        builder.setContentTitle("File Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download);
        if (manager != null) {
            new Thread(() -> {
                String data = Objects.requireNonNull(etText.getText()).toString();
                FileUtils.writeToFile(data, this, false);
                for (int increment = 0; increment < 100; increment++) {
                    builder.setProgress(100, increment, false);
                    manager.notify(1, builder.build());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.i(Constants.TAG, e.getMessage());
                    }
                }

                builder.setContentText("Upload Completed")
                        .setProgress(0, 0, false);

                manager.notify(1, builder.build());
            }).start();
        }
    }

    private void highlightWord(String word) {
        tvCurrentWord.setText(getString(R.string.current_word, word));
    }
}
