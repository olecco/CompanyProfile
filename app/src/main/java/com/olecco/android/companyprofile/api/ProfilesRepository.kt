package com.olecco.android.companyprofile.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import kotlinx.coroutines.experimental.launch

class ProfilesRepository(private val apiClient: ApiClient) {

    fun getCompanyList(): LiveData<CompanyList> {

        val result = MutableLiveData<CompanyList>()

        launch {
            val request = apiClient.getCompanyList()

            val response = request.await()


            result.postValue(response)


        }


//        launch {
//
//            val resp: Response<CompanyList> = req()
//
//
//            Log.d("111", "size=${resp.body()?.companies?.size}")
//        }



        return result

    }


//    fun req(): Response<CompanyList> {
//        val request = apiClient.getCompanyList()
//
//
//        return request.execute()
//    }

    fun getCompanyData(symbol: String): LiveData<CompanyData> {
        val result = MutableLiveData<CompanyData>()

        launch {
            val request = apiClient.getCompanyData(symbol)

            val response = request.await()


            result.postValue(response)


        }

        return result

    }



}