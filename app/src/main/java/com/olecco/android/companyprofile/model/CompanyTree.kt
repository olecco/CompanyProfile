package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TreeData {

    @SerializedName("root")
    @Expose
    var companyRoot: CompanyRoot? = null

}

class CompanyRoot {

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("value")
    @Expose
    var value: Double? = null

    @SerializedName("child")
    @Expose
    var divisionDataList: List<DivisionData>? = null

}