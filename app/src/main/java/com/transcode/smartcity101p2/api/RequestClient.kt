package com.transcode.smartcity101p2.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestClient {
    private object Holder {
        val INSTANCE = RequestClient()
    }

    private var login_services: Services
    private var youtube_services: Services
    private var buy_services: Services
    private var cate_services: Services

    init {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit_base = Retrofit.Builder()
                .baseUrl(Services.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        login_services = retrofit_base.create(Services::class.java)

        val retrofit_youtube = Retrofit.Builder()
                .baseUrl(Services.YOUTUBE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        youtube_services = retrofit_youtube.create(Services::class.java)

        val retrofit_buy = Retrofit.Builder()
                .baseUrl(Services.BUY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        buy_services = retrofit_buy.create(Services::class.java)

        val retrofit_cate = Retrofit.Builder()
                .baseUrl(Services.CATE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        cate_services = retrofit_cate.create(Services::class.java)
    }

    companion object {
        val INSTANCE: RequestClient by lazy {
            Holder.INSTANCE
        }
    }

    fun getLoginService(): Services = login_services

    fun getYoutubeService(): Services = youtube_services

    fun getBuyService(): Services = buy_services

    fun getCateService(): Services = cate_services
}