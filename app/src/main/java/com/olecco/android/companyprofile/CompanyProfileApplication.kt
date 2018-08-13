package com.olecco.android.companyprofile

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.olecco.android.companyprofile.api.ApiClient
import com.olecco.android.companyprofile.api.BASE_URL
import com.olecco.android.companyprofile.api.ProfilesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanyProfileApplication : Application() {

    lateinit var profilesRepository: ProfilesRepository

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        profilesRepository = ProfilesRepository(retrofit.create(ApiClient::class.java))

    }

}