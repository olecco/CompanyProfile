package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CompanyList {

    @SerializedName("companies")
    @Expose
    var companies: List<Company>? = null

}