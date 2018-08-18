package com.olecco.android.companyprofile.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import com.olecco.android.companyprofile.model.DivisionData

class ProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

    private var _companyList: LiveData<CompanyList>? = null
    val companyList: LiveData<CompanyList>
        get() {
            if (_companyList == null) {
                _companyList = repository.getCompanyList()
            }
            return _companyList!!
        }

    private val selectedCompanyData: MutableLiveData<String> = MutableLiveData()

    val divisionsData: LiveData<List<DivisionData>> =
            Transformations.switchMap(selectedCompanyData) {
                Transformations.map(repository.getCompanyData(it)) {
                    getDivisionDataList(it)
                }
            }

    fun selectCompany(companySymbol: String) {
        selectedCompanyData.value = companySymbol
    }

    private fun getDivisionDataList(companyData: CompanyData): List<DivisionData> {
        return companyData.treeData?.companyRoot?.divisionDataList ?: listOf()
    }

}