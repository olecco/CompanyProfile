package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DivisionData {

    @SerializedName("text")
    @Expose
    var name: String? = null

    @SerializedName("identifier")
    @Expose
    var identifier: String? = null

    @SerializedName("value")
    @Expose
    var value: Long? = null

    @SerializedName("percent")
    @Expose
    var percent: Double? = null

}
