package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CompanyRoot {

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("value")
    @Expose
    var value: Long? = null

}

class TreeData {

    @SerializedName("root")
    @Expose
    var companyRoot: CompanyRoot? = null

}