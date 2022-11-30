package com.yohan.esg_app

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

//    //GET 예제
//    @GET("posts/1")
//    fun getUser(): Call<User>
//
//    @GET("/")
//    fun getUserPage(): Call<String>

    @POST("{path}")//우리가 사용할 함수!
    fun sendFile(@Path("path") path:String,
        @Body param: HashMap<String,String>// @Body 태그는 HTTP 메세지 Body에 실어서 보내겠다는 의미.
    ): Call<String>


    @Multipart
    @POST("{path}")
    fun sendImage(@Path("path") path:String,
        @Part  file : MultipartBody.Part): Call<User>


//    @GET("posts/1")
//    fun getStudent(@Query("school_id") schoolId: Int,
//                   @Query("grade") grade: Int,
//                   @Query("classroom") classroom: Int): Call<ExampleResponse>
//
//
//    //POST 예제
//    @FormUrlEncoded
//    @POST("posts")
//    fun getContactsObject(@Field("idx") idx: String): Call<JsonObject>
}