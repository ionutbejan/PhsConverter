package com.example.phsconverter.converter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.phsconverter.R;
import com.example.phsconverter.converter.textslider.RotateUpPageTransformation;
import com.example.phsconverter.converter.textslider.TextSlideViewPager;
import com.example.phsconverter.converter.wavefragment.CustomWaveformFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.phsconverter.converter.ConvertPresenter.SPLIT_CHARACTER;
import static com.example.phsconverter.utils.Constants.FILE_KEY;

public class ConvertActivity extends AppCompatActivity implements ConvertView {
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
    @BindView(R.id.vp_lyrics)
    ViewPager2 vpTextData;

    private CustomWaveformFragment convertFragment;
    private ConvertPresenter presenter;
    private String startPosition;
    private String endPosition;

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
        presenter = new ConvertPresenter(this);
        if (getIntent() != null) {
            convertFragment = CustomWaveformFragment.launch(((File) getIntent().getSerializableExtra(FILE_KEY)).getPath());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, convertFragment)
                    .commit();
        }

        setupViewPager();
    }

    private void setupViewPager() {
        vpTextData.setUserInputEnabled(false);
        vpTextData.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        vpTextData.setPageTransformer(new RotateUpPageTransformation());
    }

    @OnClick(R.id.tv_reset)
    public void onDeleteInputText() {
        if (etText.getText() != null && !etText.getText().toString().isEmpty()) {
            presenter.reset(true);
        }
    }

    @OnClick(R.id.tv_action)
    public void onAction() {
        presenter.onAction(etText);
    }

    @OnClick(R.id.tv_convert)
    public void onConvert() {
        presenter.onConvert();
    }

    @Override
    public void showProgress() {
        // TODO: 2/5/2020 show a loading progress
    }

    @Override
    public void hideProgress() {
        // TODO: 2/5/2020 hide loading progress
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateOnReset(boolean delete) {
        convertFragment.resetMarkers();
        tvCurrentWord.setText("");
        vpTextData.setVisibility(View.GONE);
        if (delete)
            etText.setText("");
        tvAction.setText(getString(R.string.start));
        etText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(tvAction, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void updateAfterStart(String[] data) {
        vpTextData.setAdapter(new TextSlideViewPager(this, data));
        vpTextData.setCurrentItem(0, true);
        tvAction.setText(getString(R.string.next));
        vpTextData.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean canUpdateFragmentUI() {
        this.startPosition = convertFragment.getStartMakerPos();
        this.endPosition = convertFragment.getEndMarkerPos();
        if (!convertFragment.updateMarkers()) {
            showMessage("Markers offset error");
            return false;
        }

        return true;
    }

    @Override
    public String getHeadsPositions() {
        return startPosition + SPLIT_CHARACTER + endPosition;
    }

    @Override
    public void updatePager(int position) {
        vpTextData.setCurrentItem(position, true);
    }

    @Override
    public void onWordsFinished() {
        tvAction.setText(getString(R.string.done));
    }
}
