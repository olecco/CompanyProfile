package com.olecco.android.companyprofile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Company {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("ticker")
    @Expose
    var ticker: String? = null
    @SerializedName("private")
    @Expose
    var private: Boolean? = null
    @SerializedName("marketPrice")
    @Expose
    var marketPrice: String? = null
    @SerializedName("updated")
    @Expose
    var updated: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null

}
