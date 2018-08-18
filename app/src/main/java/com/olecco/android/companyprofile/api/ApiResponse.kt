package com.olecco.android.companyprofile.api

enum class ApiResponseState { NONE, LOADING, SUCCESS, ERROR }

class ApiResponse<T> {

    var data: T? = null

    var state: ApiResponseState = ApiResponseState.NONE

    var errorMessage: String = ""

}