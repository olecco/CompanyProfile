package com.olecco.android.companyprofile.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList

class ProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

    private var _companyList: LiveData<CompanyList>? = null
    val companyList: LiveData<CompanyList>
        get() {
            if (_companyList == null) {
                _companyList = repository.getCompanyList()
            }
            return _companyList!!
        }

    var companyData: LiveData<CompanyData>? = null
    var selectedCompany: String = ""
        set(value) {
            if (field != value) {
                field = value
                companyData = repository.getCompanyData(value)
            }

        }

}