package com.android.laporan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.koneksi.BaseApiService;
import com.android.laporan.koneksi.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {
    TextView login;
    EditText user,pass;
    Spinner spin;
    ProgressDialog loading;
    Context mContext;
//    BaseApiService mApiService;
    apidata mApiService;
    SharedPrefManager sharedPrefManager;
    String jabatan;
    private RestManager restManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        spin = (Spinner) findViewById(R.id.spin);
        String[] ITEMS = {"Pilih","pegawai", "kepala bidang", "kepala dinas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin = (Spinner) findViewById(R.id.spin);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spin.getSelectedItemPosition() == 0){
                    Toast.makeText(getApplicationContext(),"harap pilih jabatan",Toast.LENGTH_LONG).show();
                } else  if (spin.getSelectedItemPosition() == 1){
                jabatan = "jbt1";
            } else  if (spin.getSelectedItemPosition() == 2){
                jabatan = "jbt2";
                } else  if (spin.getSelectedItemPosition() == 3){
                    jabatan = "jbt3";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        login = (TextView) findViewById(R.id.login);

        mContext = this;
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        sharedPrefManager = new SharedPrefManager(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestLogin();
            }
        });

        if (sharedPrefManager.getSPSudahLogin()){
            Intent in = new Intent(login.this, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();
        }
    }

    private void requestLogin(){
        mApiService.loginRequest(user.getText().toString(), pass.getText().toString(),jabatan)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                loading.dismiss();
                                Toast.makeText(mContext, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();
                                sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, user.getText().toString());
                                sharedPrefManager.saveSPString(SharedPrefManager.SP_jab, jabatan);
//                                sharedPrefManager.saveSPString(SharedPrefManager.SP_jab, jabatan);
                                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);
                                Intent in = new Intent(login.this, MainActivity.class);
                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                finish();
                                Log.d("hasilnya ", response.message().toString());
                            } else if (response.code() == 400) {
                                    loading.dismiss();
                                    response.message().toString();
                                    Log.d("hasilnyacode ", response.message().toString());
                                Toast.makeText(mContext, "GAGAL LOGIN PERIKSA USERNAME DAN PASSWORD", Toast.LENGTH_SHORT).show();
                                }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                        Log.d("hasilnya ",t.getMessage().toString());
                    }
                });
    }
}
