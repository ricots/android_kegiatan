package com.android.laporan.adp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.laporan.R;
import com.android.laporan.activity.detail_kegiatan;
import com.android.laporan.fragment.ContentFragment;
import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.oop.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dafidzeko on 5/11/2016.
 */
public class adp_kegiatan extends RecyclerView.Adapter<adp_kegiatan.Holder> {

    private static final String TAG = adp_kegiatan.class.getSimpleName();
    private List<item> kgt = new ArrayList<>();
    private final kegiatanKlik mListen;
    apidata mApiService;
    private RestManager restManager;
    ProgressDialog loading;
    private Context context;

    public adp_kegiatan(kegiatanKlik listener) {
        kgt = new ArrayList<>();
        mListen = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kegiatan, parent, false);
        context = parent.getContext();
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        item bunga = kgt.get(position);
        holder.id_kegiatan.setText(bunga.getId_kegiatan());
        holder.tanggal.setText(bunga.getTanggal());
        holder.hari.setText(bunga.getHari());
        holder.jam.setText(bunga.getJam());

         }

    @Override
    public int getItemCount() {
        return kgt.size();
    }


    public void addBunga(item bunga) {
        //Log.d(TAG,bunga.getFoto());
        //   Log.d(TAG,bunga.getFoto());
        kgt.add(bunga);
        notifyDataSetChanged();
    }

    public void clear() {
        kgt.clear();
        notifyDataSetChanged();
    }

    public item getAmbilBunga(int position) {
        return kgt.get(position);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView namaBunga, hargaBunga;
        TextView id_kegiatan,hari,jam,tanggal;

        public Holder(View itemView) {
            super(itemView);
            id_kegiatan = (TextView) itemView.findViewById(R.id.id_kegiatan);
            hari = (TextView) itemView.findViewById(R.id.hari_kegiatan);
            jam = (TextView) itemView.findViewById(R.id.jam_kegiatan);
            tanggal = (TextView) itemView.findViewById(R.id.tgl_kegiatan);
            itemView.setOnClickListener(this);
            restManager = new RestManager();
            mApiService = restManager.ambil_data_kegiatan();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
//                    Toast.makeText(v.getContext(),"tes",Toast.LENGTH_LONG).show();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setMessage("apakah anda yakin ingin menghapus ?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String idnya = id_kegiatan.getText().toString();
                                    hapus(idnya, getPosition());
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return false;
                }
            });
        }

        public void hapus(String idnya, final int position) {
            final String id = idnya;
            loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);

            mApiService.delete_kegiatan(id)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                Log.d("hasilRetro", response.message().toString());
                                loading.dismiss();
                                Toast.makeText(context, "Berhasil delete data", Toast.LENGTH_SHORT).show();
//                                kgt.remove(position);
//                                notifyItemRemoved(position);
//                                notifyItemRangeChanged(position, kgt.size());

                            } else {
                                loading.dismiss();
                                Toast.makeText(context, "Gagal delete data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loading.dismiss();
                            Toast.makeText(context, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
                        }
                    });

        }


        @Override
        public void onClick(View v) {
            mListen.onClick(getLayoutPosition());
        }
    }

    public interface kegiatanKlik {
        void onClick(int position);
    }

}
