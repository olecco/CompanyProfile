package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CompanyData {

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("divisionData")
    @Expose
    var divisions: Map<String, Division>? = null

    @SerializedName("treeData")
    @Expose
    var treeData: TreeData? = null

}