package com.olecco.android.companyprofile

import android.app.Application
import com.olecco.android.companyprofile.api.ApiClient
import com.olecco.android.companyprofile.api.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanyProfileApplication : Application() {

    private lateinit var apiClient: ApiClient

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        apiClient = retrofit.create(ApiClient::class.java)

    }

}