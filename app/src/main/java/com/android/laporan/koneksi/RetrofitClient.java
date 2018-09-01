package com.android.laporan.koneksi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fariz ramadhan.
 * website : www.farizdotid.com
 * github : https://github.com/farizdotid
 */
public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(String baseUrl){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

//    public BaseApiService ambil_data(){
//        if(retrofit==null){
//
//            Retrofit retrofit =new Retrofit.Builder()
//                    .baseUrl(UtilsApi.BASE_URL_API)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//
//            retrofit = retrofit.create(BaseApiService.class);
//
//        }
//        return retrofit;
//    }
}
