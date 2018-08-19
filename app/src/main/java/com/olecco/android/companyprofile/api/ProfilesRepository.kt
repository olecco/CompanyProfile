package com.olecco.android.companyprofile.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import kotlinx.coroutines.experimental.launch

class ProfilesRepository(private val apiClient: ApiClient) {

    fun getCompanyList(): LiveData<ApiResponse<CompanyList>> {

        val result = MutableLiveData<ApiResponse<CompanyList>>()

        val apiResponse: ApiResponse<CompanyList> = ApiResponse()
        apiResponse.state = ApiResponseState.LOADING
        result.postValue(apiResponse)

        launch {
            try {
                val request = apiClient.getCompanyList()

                val data = request.await()
                data.companies = data.companies?.sortedBy {it.name}

                apiResponse.data = data
                apiResponse.state = ApiResponseState.SUCCESS
            }
            catch (e: Exception) {
                handleError(apiResponse, e)
            }
            result.postValue(apiResponse)
        }
        return result
    }

    fun getCompanyData(symbol: String): LiveData<ApiResponse<CompanyData>> {
        val result = MutableLiveData<ApiResponse<CompanyData>>()

        val apiResponse: ApiResponse<CompanyData> = ApiResponse()
        apiResponse.state = ApiResponseState.LOADING
        result.postValue(apiResponse)

        launch {
            try {
                val request = apiClient.getCompanyData(symbol)

                apiResponse.data = request.await()
                apiResponse.state = ApiResponseState.SUCCESS
            }
            catch (e: Exception) {
                handleError(apiResponse, e)
            }
            result.postValue(apiResponse)
        }
        return result
    }

    private fun handleError(apiResponse: ApiResponse<out Any>, e: Exception) {
        apiResponse.state = ApiResponseState.ERROR
        apiResponse.errorMessage = e.message ?: ""
    }

}