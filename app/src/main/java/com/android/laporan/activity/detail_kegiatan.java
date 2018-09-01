package com.android.laporan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.laporan.R;
import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.oop.item;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class detail_kegiatan extends AppCompatActivity {
    TextView waktu,tgl_penanganan,waktu_update;
    EditText dtl_kegiatan, dtl_kendala,dtl_solusi;
    String statusnya,idnya;
    Button upd_kegiatan;
    apidata mApiService;
    private RestManager restManager;
    ProgressDialog loading;
    String getdata_klik;
    ImageView img_dtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kegiatan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("detail kegiatan");
        setSupportActionBar(toolbar);

        Bundle bundlekirim = getIntent().getExtras();
        getdata_klik = bundlekirim.getString("kirim");

        img_dtl = (ImageView) findViewById(R.id.img_dtl);
        waktu = (TextView) findViewById(R.id.waktu);
        tgl_penanganan = (TextView) findViewById(R.id.tgl_penanganan);
        waktu_update = (TextView) findViewById(R.id.waktu_update);
        dtl_kegiatan = (EditText) findViewById(R.id.dtl_kegiatan);
        dtl_kendala = (EditText) findViewById(R.id.dtl_kendala);
        dtl_solusi = (EditText) findViewById(R.id.dtl_solusi);
        upd_kegiatan = (Button) findViewById(R.id.upd_kegiatan);

        Intent intent = getIntent();
        if (getdata_klik.equalsIgnoreCase("kgt")) {
            item dtlnya = (item) intent.getSerializableExtra("kirim_detail");
            Picasso.with(getApplicationContext()).load("http://laphar.000webhostapp.com/gambar/" +
                    dtlnya.getId_kegiatan() + ".png").into(img_dtl);
            waktu.setText(dtlnya.getHari() + ", " + dtlnya.getJam() + ", " + dtlnya.getTanggal());
            dtl_kegiatan.setText(dtlnya.getNama_kegiatan());
            dtl_kendala.setText(dtlnya.getKendala());
            statusnya = dtlnya.getStatus();
            waktu_update.setText("last update data : "+ dtlnya.getLast_update());
            idnya = dtlnya.getId_kegiatan();
            dtl_solusi.setVisibility(View.GONE);
            tgl_penanganan.setVisibility(View.GONE);
            if (statusnya.equalsIgnoreCase("kadaluarsa")){
                upd_kegiatan.setEnabled(false);
            }else if (statusnya.equalsIgnoreCase("belum kadaluarsa")){
                upd_kegiatan.setEnabled(true);
            }
            upd_kegiatan.setVisibility(View.GONE);

//            restManager = new RestManager();
//            mApiService = restManager.ambil_data_kegiatan();
//
//            upd_kegiatan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loading = ProgressDialog.show(detail_kegiatan.this, null, "Harap Tunggu...", true, false);
//
//                    mApiService.update_kegiatan(idnya,
//                            dtl_kegiatan.getText().toString(), dtl_kendala.getText().toString())
//                            .enqueue(new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                    if (response.isSuccessful()){
//                                        loading.dismiss();
//                                        Toast.makeText(detail_kegiatan.this, "Berhasil update data", Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    } else {
//                                        loading.dismiss();
//                                        Toast.makeText(detail_kegiatan.this, "Gagal update data", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    loading.dismiss();
//                                    Toast.makeText(detail_kegiatan.this, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            });
        }else if (getdata_klik.equalsIgnoreCase("hst")) {
            item dtlnya = (item) intent.getSerializableExtra("kirim_hst");
            Picasso.with(getApplicationContext()).load("http://laphar.000webhostapp.com/gambar/" +
                    dtlnya.getId_kegiatan() + ".png").into(img_dtl);
            waktu.setText(dtlnya.getHari() + ", " + dtlnya.getJam() + ", " + dtlnya.getTanggal());
            dtl_kegiatan.setText(dtlnya.getNama_kegiatan());
            dtl_kendala.setText(dtlnya.getKendala());
            statusnya = dtlnya.getStatus();
            waktu_update.setText("last update data : "+ dtlnya.getLast_update());
            idnya = dtlnya.getId_kegiatan();
            tgl_penanganan.setText(dtlnya.getTanggal_penanganan());
            dtl_solusi.setText(dtlnya.getSolusi());
            upd_kegiatan.setVisibility(View.GONE);
        }else if (getdata_klik.equalsIgnoreCase("all")) {
            item dtlnya = (item) intent.getSerializableExtra("kirim_all");
            Picasso.with(getApplicationContext()).load("http://laphar.000webhostapp.com/gambar/" +
                    dtlnya.getId_kegiatan() + ".png").into(img_dtl);
            waktu.setText(dtlnya.getHari() + ", " + dtlnya.getJam() + ", " + dtlnya.getTanggal());
            dtl_kegiatan.setText(dtlnya.getNama_kegiatan());
            dtl_kendala.setText(dtlnya.getKendala());
            waktu_update.setText("last update data : "+ dtlnya.getLast_update());
            statusnya = dtlnya.getStatus();
            idnya = dtlnya.getId_kegiatan();
            Date ys=new Date();
            SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
            tgl_penanganan.setText(s.format(ys));

            restManager = new RestManager();
            mApiService = restManager.ambil_data_kegiatan();
            upd_kegiatan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loading = ProgressDialog.show(detail_kegiatan.this, null, "Harap Tunggu...", true, false);

                    mApiService.update_kendala(idnya,
                            tgl_penanganan.getText().toString(), dtl_solusi.getText().toString())
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()){
                                        loading.dismiss();
                                        Toast.makeText(detail_kegiatan.this, "Berhasil update data", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(add_kegiatan.this, ContentFragment.class)
//                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    } else {
                                        loading.dismiss();
                                        Toast.makeText(detail_kegiatan.this, "Gagal update data", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    loading.dismiss();
                                    Toast.makeText(detail_kegiatan.this, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
