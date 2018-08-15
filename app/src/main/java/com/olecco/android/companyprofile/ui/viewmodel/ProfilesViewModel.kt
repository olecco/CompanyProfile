package com.olecco.android.companyprofile.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.model.CompanyList
import com.olecco.android.companyprofile.model.Division

class ProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

    private var _companyList: LiveData<CompanyList>? = null
    val companyList: LiveData<CompanyList>
        get() {
            if (_companyList == null) {
                _companyList = repository.getCompanyList()
            }
            return _companyList!!
        }

    var divisionList: LiveData<List<Division>>? = null

    //var companyData: LiveData<CompanyData>? = null
    var selectedCompany: String = ""
        set(value) {
            if (field != value) {
                field = value

                divisionList = Transformations.map(repository.getCompanyData(value)) {
                    val result: MutableList<Division> = mutableListOf()
                    val divisions = it?.divisions
                    if (divisions != null) {
                        var sum: Long = 0
                        for ((_, division) in divisions) {
                            result.add(division)
                            sum += division.value!!
                        }
                        val total: Long = it.treeData?.companyRoot?.value!!
                        val netCashDivision = Division()
                        netCashDivision.value = total - sum
                        netCashDivision.name = "Net Cash Balance"
                        netCashDivision.description = ""
                        result.add(netCashDivision)
                    }


                    result
                }

                //companyData = repository.getCompanyData(value)



            }

        }

}