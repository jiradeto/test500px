package com.jiradeto.liveat500px.manager.http;

import com.jiradeto.liveat500px.dao.PhotoItemCollectionDao;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by 515895 on 2/24/2016.
 */
public interface ApiService {
    @POST("list")
    Call<PhotoItemCollectionDao> LoadPhotoList();

    @POST("list/after/{id}")
    Call<PhotoItemCollectionDao> LoadPhotoListAfterId(@Path("id") int id);

    @POST("list/before/{id}")
    Call<PhotoItemCollectionDao> LoadPhotoListBeforeId(@Path("id") int id);

}
