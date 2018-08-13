package com.olecco.android.companyprofile.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.olecco.android.companyprofile.CompanyProfileApplication
import com.olecco.android.companyprofile.R
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModel
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModelFactory

class MainActivity : AppCompatActivity() {

    val profileApplication: CompanyProfileApplication
        get() = application as CompanyProfileApplication

    val profilesRepository: ProfilesRepository
        get() = profileApplication.profilesRepository

    lateinit var profilesViewModel: ProfilesViewModel

    lateinit var companyListAdapter: CompanyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        profilesViewModel = ViewModelProviders.of(this,
                ProfilesViewModelFactory(profilesRepository)).get(ProfilesViewModel::class.java)

        profilesViewModel.companyList.observe(this, Observer {
            companyListAdapter.data = it?.companies!!
        })
    }

    private fun bindViews() {
        companyListAdapter = CompanyListAdapter(this)
        companyListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val companyListSpinner: Spinner = findViewById(R.id.company_list)
        companyListSpinner.adapter = companyListAdapter
        companyListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                val company = companyListAdapter.getCompany(position)

                Toast.makeText(this@MainActivity, company.ticker, Toast.LENGTH_LONG).show()

            }
        }
    }

    class CompanyListAdapter(context: Context): ArrayAdapter<String>(context,
            android.R.layout.simple_spinner_item) {

        var data: List<Company> = ArrayList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItem(position: Int): String {
            return data[position].name?:""
        }

        override fun getCount(): Int {
            return data.size
        }

        fun getCompany(position: Int): Company {
            return data[position]
        }

    }



}
