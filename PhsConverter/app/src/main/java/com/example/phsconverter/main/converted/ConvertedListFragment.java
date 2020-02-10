package com.example.phsconverter.main.converted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phsconverter.R;
import com.example.phsconverter.utils.FileUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConvertedListFragment extends Fragment {
    @BindView(R.id.rv_converted_files)
    RecyclerView rvZips;
    @BindView(R.id.tv_no_zips)
    TextView tvNoZips;
    private ConvertedListAdapter zipsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.converted_list_fragment, container, false);
        ButterKnife.bind(this, view);
        zipsAdapter = new ConvertedListAdapter();
        setupRecycler();
        return view;
    }

    private void setupRecycler() {
        rvZips.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
        rvZips.setAdapter(zipsAdapter);
        zipsAdapter.setFilesData(FileUtils.getConvertedFiles(getContext()));
    }
}
