package com.yohan.esg_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap


class TestActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        var gson= GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder().baseUrl("http://192.168.1.103:8080/")
//            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();
        val service = retrofit.create(RetrofitService::class.java);

        //이미지 가져오기
        val imagePath = ""
        val image = BitmapFactory.decodeFile(imagePath)

        var imageString:String=""
        if (image != null) {
            imageString=BitmapConverter().bitmapToString(image)
        }

        Log.d("abcdefg",imageString)

        //end of 이미지 가져오기

        var param:HashMap<String,String> = java.util.HashMap<String,String>()

        param.put("image",imageString)


        val call=service.sendFile(CSVReader.innerUrl,param)

        call?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성고된 경우
                    var result: String? = response.body()

                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("YMC", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("YMC", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}

