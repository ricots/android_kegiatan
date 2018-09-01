package com.android.laporan.fragment;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.laporan.R;
import com.android.laporan.adp.adp_kegiatan;
import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.koneksi.SharedPrefManager;

public class home extends Fragment {
    private RecyclerView recyclerView;
    private RestManager restManager;
    private adp_kegiatan bungaAdapter;
    ProgressDialog loading;
    SharedPrefManager sharedPrefManager;
    apidata mApiService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);

        return v;
    }
}
