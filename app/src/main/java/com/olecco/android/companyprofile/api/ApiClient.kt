package com.olecco.android.companyprofile.api

import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.model.CompanyList
import retrofit2.Call
import retrofit2.http.GET

const val BASE_URL = "https://www.trefis.com/api/ameritrade"

interface ApiClient {

    @GET("/price/list")
    fun getCompanyList(): Call<CompanyList>

}