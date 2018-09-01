package com.android.laporan.helper;

//import android.telecom.Call;

import com.android.laporan.oop.item;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dafidzeko on 5/11/2016.
 */
public interface apidata {


    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("nip") String nip,
                                    @Field("password") String password, @Field("id_jabatan") String id_jabatan);

    @GET("list_kegiatan.php")
    Call<List<item>> getdata_kegiatan(@Query("nip") String nip);
//    Call<List<item>> getdata_kegiatan();

    @GET("cari_kegiatan.php")
    Call<List<item>> getcari_data_kegiatan(@Query("nip") String nip, @Query("data") String data);

    @GET("list_detail_kendala.php")
    Call<List<item>> getdata_kendala(@Query("nip") String nip);

    @GET("cari_detail_kendala.php")
    Call<List<item>> getcari_detail_kendala(@Query("nip") String nip, @Query("data") String data);

    @GET("list_detail_kendala_all.php")
    Call<List<item>> getSemua_detail();

    @GET("cari_detail_kendala_all.php")
    Call<List<item>> getcari_detail_kendala_all(@Query("nip") String nip, @Query("data") String data);

    @FormUrlEncoded
    @POST("add_kegiatan.php")
    Call<ResponseBody> simpan_kegiatan(@Field("nip") String nip,
                                           @Field("hari") String hari,
                                           @Field("jam") String jam,
                                           @Field("tanggal") String tanggal,
                                           @Field("nama_kegiatan") String nama_kegiatan,
                                           @Field("kendala") String kendala,
                                       @Field("gambar") String gambar,
                                       @Field("last_update") String last_update);

    @FormUrlEncoded
    @POST("update_kegiatan.php")
    Call<ResponseBody> update_kegiatan(@Field("id_kegiatan") String id_kegiatan,
                                       @Field("nama_kegiatan") String nama_kegiatan,
                                       @Field("kendala") String kendala);

    @FormUrlEncoded
    @POST("update_kendala.php")
    Call<ResponseBody> update_kendala(@Field("id_kegiatan") String id_kegiatan,
                                       @Field("tanggal_penanganan") String tanggal_penanganan,
                                       @Field("solusi") String solusi);

    @FormUrlEncoded
    @POST("update_lokasi.php")
    Call<ResponseBody> update_lokasi(@Field("nip") String nip,
                                       @Field("lat") String lat,
                                       @Field("lng") String lng);

    @FormUrlEncoded
    @POST("delete_kegiatan.php/{id_kegiatan}")
    Call<ResponseBody> delete_kegiatan(@Field("id_kegiatan") String id_kegiatan);
}
