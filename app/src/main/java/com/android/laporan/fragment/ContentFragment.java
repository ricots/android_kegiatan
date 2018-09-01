package com.android.laporan.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.laporan.R;
import com.android.laporan.activity.add_kegiatan;
import com.android.laporan.activity.detail_kegiatan;
import com.android.laporan.adp.adp_kegiatan;
import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.koneksi.SharedPrefManager;
import com.android.laporan.oop.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 04-06-2015.
 */
public class ContentFragment extends Fragment implements adp_kegiatan.kegiatanKlik{
    Spinner spin_data;
    private RecyclerView recyclerView;
    private RestManager restManager;
    private adp_kegiatan adapter_kgt;
    ProgressDialog loading;
    SharedPrefManager sharedPrefManager;
    apidata mApiService;
    String param,s,jabatan,pilih_data;
    FloatingActionButton add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_fragment,container,false);

        sharedPrefManager = new SharedPrefManager(getActivity());
        param = sharedPrefManager.getSPNama();
        Toast.makeText(getActivity(),sharedPrefManager.getSPNama(),Toast.LENGTH_LONG).show();

        Bundle b = getArguments();
        s = b.getString("datakirim");
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();

        sharedPrefManager = new SharedPrefManager(getActivity());
        jabatan = sharedPrefManager.getSP_jab();
        Toast.makeText(getActivity(),sharedPrefManager.getSP_jab(),Toast.LENGTH_LONG).show();

        spin_data = (Spinner) v.findViewById(R.id.spin_data);
        recyclerView = (RecyclerView) v.findViewById(R.id.list_daftar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        adapter_kgt = new adp_kegiatan(this);
        recyclerView.setAdapter(adapter_kgt);

        add = (FloatingActionButton) v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(),add_kegiatan.class);
                startActivity(in);
            }
        });

        String[] ITEMS = {"Pilih","perminggu", "perbulan", "pertahun"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_data.setAdapter(adapter);

        //cari data
        spin_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spin_data.getSelectedItemPosition() ==0){
                    Toast.makeText(getActivity(), "harap pilih data", Toast.LENGTH_LONG).show();

                }else if ((spin_data.getSelectedItemPosition() ==1) && (s.equalsIgnoreCase("kegiatan"))){
                    pilih_data = "7";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_kegiatan();
                }else if ((spin_data.getSelectedItemPosition() ==2) && (s.equalsIgnoreCase("kegiatan"))){
                    pilih_data = "30";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_kegiatan();
                }else if ((spin_data.getSelectedItemPosition() ==3) && (s.equalsIgnoreCase("kegiatan"))){
                    pilih_data = "352";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_kegiatan();

                }else if ((spin_data.getSelectedItemPosition() ==1) && (s.equalsIgnoreCase("history"))){
                    pilih_data = "7";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_history();
                }else if ((spin_data.getSelectedItemPosition() ==2) && (s.equalsIgnoreCase("history"))){
                    pilih_data = "30";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_history();
                }else if ((spin_data.getSelectedItemPosition() ==3) && (s.equalsIgnoreCase("history"))){
                    pilih_data = "352";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_history();

                }else if ((spin_data.getSelectedItemPosition() ==1) && (s.equalsIgnoreCase("all"))){
                    pilih_data = "7";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_all_kendala();
                }else if ((spin_data.getSelectedItemPosition() ==2) && (s.equalsIgnoreCase("all"))){
                    pilih_data = "30";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_all_kendala();
                }else if ((spin_data.getSelectedItemPosition() ==3) && (s.equalsIgnoreCase("all"))){
                    pilih_data = "352";
                    adapter_kgt.clear();
                    adapter_kgt.notifyDataSetChanged();
                    cari_all_kendala();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (s.equalsIgnoreCase("kegiatan")){
//            recyclerView.setAdapter(adapter_kgt);
            kegiatan();
        }else if (s.equalsIgnoreCase("history")){
//            recyclerView.setAdapter(adapter_kgt);
            history();
            add.setVisibility(View.GONE);
        }else if (s.equalsIgnoreCase("all") && jabatan.equalsIgnoreCase("jbt2")
                || jabatan.equalsIgnoreCase("jbt3")){
//            recyclerView.setAdapter(adapter_kgt);
            adapter_kgt.notifyDataSetChanged();
            kendala_all();
        }
        return v;
    }

    public void kegiatan(){
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Map<String, String> data = new HashMap<>();
        data.put("nip", param);
        mApiService.getdata_kegiatan(param);
        Call<List<item>> listCall = mApiService.getdata_kegiatan(param);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {

                if (response.isSuccessful()) {
//                if(response.code() == 200) {
                    List<item> list_kegiatan = response.body();
//                    list_kegiatan.clear();
                        for (int i = 0; i < list_kegiatan.size(); i++) {
                            item datanya = list_kegiatan.get(i);
                            adapter_kgt.addBunga(datanya);
                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                            Log.d("hasilnya ", response.toString());
                            loading.dismiss();
                        }

                        }/*else if (response.code() == 400)*/{
                        loading.dismiss();
                    }
//                }else {
//                    loading.dismiss();
//                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.toString(),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "cek koneksi internet", Toast.LENGTH_SHORT).show();
                Log.d("hasilnya ", t.toString());
            }
        });
    }

    public void cari_kegiatan(){
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Map<String, String> data = new HashMap<>();
        data.put("nip", param);
        mApiService.getdata_kegiatan(param);
        Call<List<item>> listCall = mApiService.getcari_data_kegiatan(param,pilih_data);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {
                //if (response.isSuccessful()) {
                if(response.code() == 200) {
                    List<item> list_kegiatan = response.body();
                    for (int i = 0; i < list_kegiatan.size(); i++) {
                        item datanya = list_kegiatan.get(i);
                        adapter_kgt.addBunga(datanya);
                        Toast.makeText(getActivity(), "data di temukan", Toast.LENGTH_LONG).show();
                        Log.d("hasilnya ", response.toString());
                        loading.dismiss();
                    }

                }else if (response.code() == 400){
                    Toast.makeText(getActivity(), "data tidak di temukan", Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
//                }else {
//                    loading.dismiss();
//                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.toString(),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "cek koneksi internet", Toast.LENGTH_SHORT).show();
                Log.d("hasilnya ", t.toString());
            }
        });
    }

    public void history(){
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Map<String, String> data = new HashMap<>();
        data.put("nip", param);
        mApiService.getdata_kendala(param);
        Call<List<item>> listCall = mApiService.getdata_kendala(param);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {

                if (response.isSuccessful()) {
                    List<item> list_kegiatan = response.body();
                    for (int i = 0; i < list_kegiatan.size(); i++) {
                            item datanya = list_kegiatan.get(i);
                            adapter_kgt.addBunga(datanya);
                            Toast.makeText(getActivity(), "data di temukan", Toast.LENGTH_LONG).show();
                            Log.d("hasilnya ", response.message().toString());
                            loading.dismiss();
                    }
                }/*else if (response.code() == 400)*/{
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d("hasilnyaeror ", t.getMessage().toString());
            }
        });
    }

    public void cari_history(){
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Map<String, String> data = new HashMap<>();
        data.put("nip", param);
        mApiService.getdata_kendala(param);
        Call<List<item>> listCall = mApiService.getcari_data_kegiatan(param,pilih_data);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {

                if (response.code() == 200)/*if (response.isSuccessful())*/ {
                    List<item> list_kegiatan = response.body();
                    for (int i = 0; i < list_kegiatan.size(); i++) {
                        item datanya = list_kegiatan.get(i);
                        adapter_kgt.addBunga(datanya);
                        Toast.makeText(getActivity(), "data di temukan", Toast.LENGTH_LONG).show();
                        Log.d("hasilnya ", response.message().toString());
                        loading.dismiss();
                    }
                }else if (response.code() == 400){
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d("hasilnyaeror ", t.getMessage().toString());
            }
        });
    }

    public void kendala_all(){

        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Call<List<item>> listCall = mApiService.getSemua_detail();
//        Map<String, String> data = new HashMap<>();
//        data.put("nip", param);
//        mApiService.getdata_kendala(param);
//        Call<List<item>> listCall = mApiService.getdata_kendala(param);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {

                if (response.isSuccessful()) {
                    List<item> list_kegiatan = response.body();
                    for (int i = 0; i < list_kegiatan.size(); i++) {
                        item datanya = list_kegiatan.get(i);
                        adapter_kgt.addBunga(datanya);
                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        Log.d("hasilnya ", response.message().toString());
                        loading.dismiss();
                    }
                }else /*if (response.equals("[]"))*/{
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d("hasilnyaeror ", t.getMessage().toString());
            }
        });
    }

    public void cari_all_kendala(){
//        recyclerView = (RecyclerView) getActivity().findViewById(R.id.list_daftar);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
//        adapter_kgt = new adp_kegiatan(this);
//        recyclerView.setAdapter(adapter_kgt);

        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
        loading = ProgressDialog.show(getActivity(), null, "Harap Tunggu...", true, false);
        Map<String, String> data = new HashMap<>();
        data.put("nip", param);
        mApiService.getcari_detail_kendala_all(param,pilih_data);
        Call<List<item>> listCall = mApiService.getcari_detail_kendala_all(param,pilih_data);
        listCall.enqueue(new Callback<List<item>>() {
            @Override
            public void onResponse(Call<List<item>> call, Response<List<item>> response) {

                if (response.code() == 200) /*if (response.isSuccessful())*/ {
                    List<item> list_kegiatan = response.body();
                    for (int i = 0; i < list_kegiatan.size(); i++) {
                        item datanya = list_kegiatan.get(i);
                        adapter_kgt.addBunga(datanya);
                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        Log.d("hasilnya ", response.message().toString());
                        loading.dismiss();
                    }
                }else if (response.code() == 400){
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<item>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d("hasilnyaeror ", t.getMessage().toString());
            }
        });
    }

    @Override
    public void onClick(int position) {
        if (s.equalsIgnoreCase("kegiatan")){
            item pilih_kegiatan = adapter_kgt.getAmbilBunga(position);
            Intent intent = new Intent(getActivity(), detail_kegiatan.class);
            intent.putExtra("kirim_detail", pilih_kegiatan);
            Bundle bundle = new Bundle();
            bundle.putString("kirim","kgt");
            intent.putExtras(bundle);
            startActivity(intent);
        }else if (s.equalsIgnoreCase("history")){
            item pilih_kegiatan = adapter_kgt.getAmbilBunga(position);
            Intent intent = new Intent(getActivity(), detail_kegiatan.class);
            intent.putExtra("kirim_hst", pilih_kegiatan);
            Bundle bundle = new Bundle();
            bundle.putString("kirim","hst");
            intent.putExtras(bundle);
            startActivity(intent);
        }else if (s.equalsIgnoreCase("all")){
            item pilih_kegiatan = adapter_kgt.getAmbilBunga(position);
            Intent intent = new Intent(getActivity(), detail_kegiatan.class);
            intent.putExtra("kirim_all", pilih_kegiatan);
            Bundle bundle = new Bundle();
            bundle.putString("kirim","all");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
