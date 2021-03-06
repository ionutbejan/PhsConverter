package com.example.phsconverter.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phsconverter.R;
import com.example.phsconverter.converter.ConvertActivity;
import com.example.phsconverter.main.converted.ConvertedListFragment;
import com.example.phsconverter.main.media.Media;
import com.example.phsconverter.main.media.MediaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView {
    public static final int RequestPermissionCode = 101;

    @BindView(R.id.btnRecord)
    FloatingActionButton btnRecord;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.rv_playlist)
    RecyclerView rvPlaylist;
    @BindView(R.id.tv_permission)
    TextView tvPermissions;
    @BindView(R.id.tv_no_media)
    TextView tvNoMedia;
    @BindView(R.id.btnSwitch)
    FloatingActionButton btnSwitch;
    @BindView(R.id.iv_recording)
    AppCompatImageView ivRecording;
    @BindView(R.id.recording_layout)
    FrameLayout flRecording;

    private MainPresenter presenter;
    private MediaAdapter adapter;
    private int playingPosition = -1;
    private boolean showingFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean storagePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean recordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean phoneStatePermission = grantResults[2] ==
                        PackageManager.PERMISSION_GRANTED;

                if (storagePermission && recordPermission && phoneStatePermission) {
                    tvPermissions.setVisibility(View.GONE);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnSwitch.setVisibility(View.VISIBLE);
                    btnRecord.setEnabled(true);
                    btnSwitch.setEnabled(true);
                    rvPlaylist.setVisibility(View.VISIBLE);
                    presenter.loadMedia();
                } else {
                    showSnack("Permission Denied!");
                    tvPermissions.setVisibility(View.VISIBLE);
                    btnRecord.setVisibility(View.INVISIBLE);
                    rvPlaylist.setVisibility(View.INVISIBLE);
                    btnRecord.setEnabled(false);
                    btnSwitch.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);
        presenter.onCreate();
    }

    @Override
    public void onCreate() {
        ButterKnife.bind(this);
        boolean permission = presenter.checkPermission();
        tvPermissions.setVisibility(permission ? View.GONE : View.VISIBLE);
        btnRecord.setVisibility(permission ? View.VISIBLE : View.INVISIBLE);
        btnRecord.setEnabled(permission);
        rvPlaylist.setVisibility(permission ? View.VISIBLE : View.INVISIBLE);
        setupRecycler();
        setupRecordingAnimation();
    }

    private void setupRecordingAnimation() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                ivRecording,
                PropertyValuesHolder.ofFloat("scaleX", 1.3f),
                PropertyValuesHolder.ofFloat("scaleY", 1.3f));
        scaleDown.setDuration(660);
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    private void setupRecycler() {
        adapter = new MediaAdapter(new ArrayList<>());
        setAdapterListener();
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPlaylist.setAdapter(adapter);
        presenter.loadMedia();
    }

    private void setAdapterListener() {
        adapter.setMediaListener(new MediaAdapter.MediaClickListener() {
            @Override
            public void onMediaClicked(Media media, int position) {
                if (!media.isPlaying()) {
                    presenter.onStopPlaying(position);
                    presenter.onPlaying(media.getMediaFile().getPath(), position);
                    playingPosition = position;
                } else {
                    presenter.onStopPlaying(position);
                    playingPosition = -1;
                }
            }

            @Override
            public void onDeleteMedia(Media media) {
                if (media.isPlaying()) {
                    showSnack("Can't delete playing media.");
                    return;
                }
                adapter.remove(media);
                presenter.delete(media.getMediaFile());
                if (adapter.getItemCount() == 0) {
                    tvNoMedia.setVisibility(View.VISIBLE);
                    rvPlaylist.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onConvertMedia(Media media) {
                if (playingPosition != -1)
                    presenter.onStopPlaying(playingPosition);
                ConvertActivity.launch(MainActivity.this, media.getMediaFile());
            }
        });
    }

    @Override
    public AppCompatActivity getContext() {
        return this;
    }

    @Override
    public void onRecording() {
        flRecording.setVisibility(View.VISIBLE);
        btnSwitch.setEnabled(false);
        showSnack("Recording Started");
    }

    @Override
    public void onPlaying() {
        btnRecord.setEnabled(false);
    }

    @Override
    public void onStopRecording(String newRecordPath) {
        flRecording.setVisibility(View.GONE);
        btnSwitch.setEnabled(true);
        if (adapter.getItemCount() == 0) {
            tvNoMedia.setVisibility(View.GONE);
            rvPlaylist.setVisibility(View.VISIBLE);
        }
        adapter.addMedia(newRecordPath);
    }

    @Override
    public void onStopPlaying(int position) {
        btnRecord.setEnabled(true);
        adapter.onStopPlaying(position);
    }

    @Override
    public void onMediaLoaded(ArrayList<Media> mediaFiles) {
        adapter.addMedia(mediaFiles);
        tvNoMedia.setVisibility(mediaFiles == null || mediaFiles.isEmpty() ? View.VISIBLE : View.GONE);
        rvPlaylist.setVisibility(mediaFiles == null || mediaFiles.isEmpty() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onPermissionDenied() {
        showSnack("Permission Denied!");
        tvPermissions.setVisibility(View.VISIBLE);
        btnRecord.setVisibility(View.INVISIBLE);
        rvPlaylist.setVisibility(View.INVISIBLE);
        btnRecord.setEnabled(false);
        btnSwitch.setVisibility(View.INVISIBLE);
        btnSwitch.setEnabled(false);
    }

    @Override
    public void onMediaDeleted(boolean success) {
        showSnack(success ? "Media record deleted" : "Couldn't delete the file");
    }

    @Override
    public void message(String message, boolean success) {
        showSnack(message);
    }

    @OnClick(R.id.btnRecord)
    public void onRecord() {
        if (!presenter.isRecording()) {
            presenter.onRecording();
        } else {
            presenter.onStopRecording();
        }
        presenter.setRecording(!presenter.isRecording());
        btnRecord.setImageResource(presenter.isRecording() ? R.drawable.ic_recording : R.drawable.ic_record);
    }

    @OnClick(R.id.btnSwitch)
    public void onSwitchFragment() {
        if (showingFragment) {
            getSupportFragmentManager().popBackStack();
            showingFragment = false;
            return;
        }
        showingFragment = true;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, new ConvertedListFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.tv_permission)
    public void onRequestPermission() {
        presenter.requestPermission();
    }

    private void showSnack(String message) {
        Snackbar.make(btnRecord, message, Snackbar.LENGTH_SHORT).show();
    }
}