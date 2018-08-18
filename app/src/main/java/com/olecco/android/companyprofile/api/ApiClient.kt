package com.olecco.android.companyprofile.api

import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://www.trefis.com/api/ameritrade/"

interface ApiClient {

    @GET("price/list")
    fun getCompanyList(): Deferred<CompanyList>

    @GET("modeldata/{symbol}")
    fun getCompanyData(@Path("symbol") symbol: String): Deferred<CompanyData>

}