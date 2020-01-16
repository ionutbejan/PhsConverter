package com.example.phsconverter.converter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.phsconverter.R;
import com.semantive.waveformandroid.waveform.Segment;
import com.semantive.waveformandroid.waveform.WaveformFragment;

import java.util.Arrays;
import java.util.List;

import static com.example.phsconverter.utils.Constants.FILE_KEY;

public class CustomWaveformFragment extends WaveformFragment {
    private String filePath;

    static CustomWaveformFragment launch(String filePath) {
        CustomWaveformFragment fragment = new CustomWaveformFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILE_KEY, filePath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (getArguments() != null)
            filePath = getArguments().getString(FILE_KEY);
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.mRewindButton.setImageResource(R.drawable.ic_rewind);
        this.mFfwdButton.setImageResource(R.drawable.ic_forward);
        return view;
    }

    @Override
    protected String getFileName() {
        return filePath;
    }

    @Override
    protected List<Segment> getSegments() {
        return Arrays.asList(
                new Segment(55.2, 55.8, Color.rgb(238, 23, 104)),
                new Segment(56.2, 56.6, Color.rgb(238, 23, 104)),
                new Segment(58.4, 59.9, Color.rgb(184, 92, 184)));
    }

    @Override
    protected void enableDisableButtons() {
        if (this.mIsPlaying) {
            this.mPlayButton.setImageResource(R.drawable.ic_pause);
            this.mPlayButton.setContentDescription(this.getResources().getText(R.string.stop));
        } else {
            this.mPlayButton.setImageResource(R.drawable.ic_play);
            this.mPlayButton.setContentDescription(this.getResources().getText(R.string.play));
        }
    }

    boolean updateMarkers() {
        int aux = this.mEndPos - this.mStartPos;
        if (aux <= 0)
            return false;
        this.mStartPos = this.mEndPos + aux > this.mMaxPos ? this.mMaxPos - 1 : this.mEndPos;
        this.mEndPos = this.mEndPos + aux > this.mMaxPos ? this.mMaxPos : this.mEndPos + aux;
        this.updateDisplay();
        return true;
    }

    void resetMarkers() {
        this.mStartPos = 0;
        this.mEndPos = this.mMaxPos;
        this.updateDisplay();
    }

    String getEndMarkerPos() {
        return String.valueOf(mWaveformView.pixelsToMillisecs(this.mEndPos));
    }

    String getStartMakerPos() {
        return String.valueOf(mWaveformView.pixelsToMillisecs(this.mStartPos));
    }
}
