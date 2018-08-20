package com.olecco.android.companyprofile.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.olecco.android.companyprofile.api.ApiResponse
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import com.olecco.android.companyprofile.model.DivisionData

class ProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

    private var _companyList: LiveData<ApiResponse<CompanyList>>? = null
    val companyList: LiveData<ApiResponse<CompanyList>>
        get() {
            if (_companyList == null) {
                _companyList = repository.getCompanyList()
            }
            return _companyList ?: MutableLiveData()
        }

    private val selectedCompanyData: MutableLiveData<String> = MutableLiveData()
    val selectedCompany: String
        get() = selectedCompanyData.value ?: ""

    val divisionsData: LiveData<ApiResponse<List<DivisionData>>> =
            Transformations.switchMap(selectedCompanyData) {
                Transformations.map(repository.getCompanyData(it)) {
                    getDivisionDataList(it)
                }
            }

    fun selectCompany(companySymbol: String) {
        selectedCompanyData.value = companySymbol
    }

    private fun getDivisionDataList(companyDataResponse: ApiResponse<CompanyData>): ApiResponse<List<DivisionData>> {
        val divisionDataResponse: ApiResponse<List<DivisionData>> = ApiResponse()
        divisionDataResponse.state = companyDataResponse.state
        divisionDataResponse.errorMessage = companyDataResponse.errorMessage
        val companyData: CompanyData? = companyDataResponse.data
        divisionDataResponse.data =
                companyData?.treeData?.companyRoot?.divisionDataList ?: listOf()
        return divisionDataResponse
    }

}