package com.olecco.android.companyprofile.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.olecco.android.companyprofile.api.ProfilesRepository

@Suppress("UNCHECKED_CAST")
class ProfilesViewModelFactory(private val profilesRepository: ProfilesRepository):
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfilesViewModel(profilesRepository) as T
    }

}