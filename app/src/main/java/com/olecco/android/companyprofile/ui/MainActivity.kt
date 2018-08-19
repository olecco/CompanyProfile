package com.olecco.android.companyprofile.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.olecco.android.companyprofile.CompanyProfileApplication
import com.olecco.android.companyprofile.R
import com.olecco.android.companyprofile.api.ApiResponse
import com.olecco.android.companyprofile.api.ApiResponseState
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.model.CompanyList
import com.olecco.android.companyprofile.model.DivisionData
import com.olecco.android.companyprofile.ui.piechart.PieChartAdapter
import com.olecco.android.companyprofile.ui.piechart.PieChartClickListener
import com.olecco.android.companyprofile.ui.piechart.PieChartView
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModel
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModelFactory

class MainActivity : AppCompatActivity() {

    val profileApplication: CompanyProfileApplication
        get() = application as CompanyProfileApplication

    val profilesRepository: ProfilesRepository
        get() = profileApplication.profilesRepository

    private lateinit var profilesViewModel: ProfilesViewModel
    private lateinit var companyListAdapter: CompanyListAdapter

    private lateinit var companySpinnerView: Spinner
    private lateinit var pieChartView: PieChartView
    private lateinit var progressView: View

    private lateinit var pieChartAdapter: DivisionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        pieChartAdapter = DivisionListAdapter(resources.getIntArray(R.array.profile_colors))

        profilesViewModel = ViewModelProviders.of(this,
                ProfilesViewModelFactory(profilesRepository)).get(ProfilesViewModel::class.java)

        profilesViewModel.companyList.observe(this, Observer {
            handleCompanyListResponse(it)
        })

        profilesViewModel.divisionsData.observe(this, Observer {
            handleDivisionListResponse(it)
        })
    }

    private fun bindViews() {
        companyListAdapter = CompanyListAdapter(this)
        companyListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        companySpinnerView = findViewById(R.id.company_list)
        companySpinnerView.adapter = companyListAdapter
        companySpinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                val company = companyListAdapter.getCompany(position)
                profilesViewModel.selectCompany(company.ticker ?: "")

            }
        }

        pieChartView = findViewById(R.id.pie_chart)
        progressView = findViewById(R.id.progress)

        pieChartView.pieChartClickListener = object : PieChartClickListener {
            override fun onSegmentClick(segmentIndex: Int) {
                Log.d("111", "index=" + segmentIndex)
            }

        }
    }

    private fun handleCompanyListResponse(companyListResponse: ApiResponse<CompanyList>?) {
        when(companyListResponse?.state) {
            ApiResponseState.LOADING -> {
                companySpinnerView.hide()
                pieChartView.hide()
                progressView.show()
            }
            ApiResponseState.SUCCESS -> {
                val companies = companyListResponse.data?.companies
                if (companies != null) {
                    companyListAdapter.data = companies
                }
                companySpinnerView.show()
                progressView.hide()
            }
            ApiResponseState.ERROR -> {
                // todo show error
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun handleDivisionListResponse(divisionListResponse: ApiResponse<List<DivisionData>>?) {
        when(divisionListResponse?.state) {
            ApiResponseState.LOADING -> {
                pieChartView.hide()
                progressView.show()
            }
            ApiResponseState.SUCCESS -> {
                pieChartAdapter.chartNameString = profilesViewModel.selectedCompany
                pieChartAdapter.divisions = divisionListResponse.data ?: listOf()
                pieChartView.adapter = pieChartAdapter

                pieChartView.show()
                progressView.hide()
            }
            ApiResponseState.ERROR -> {
                // todo show error
            }
            else -> {
                // do nothing
            }
        }
    }


    private class CompanyListAdapter(context: Context): ArrayAdapter<String>(context,
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

    private class DivisionListAdapter(val colors: IntArray) : PieChartAdapter {

        var divisions: List<DivisionData> = listOf()
        var chartNameString: String = ""

        override fun getChartName(): String {
            return chartNameString
        }

        override fun getSegmentCount(): Int {
            return divisions.size
        }

        override fun getSegmentValue(index: Int): Double {
            return divisions[index].value ?: 0.0
        }

        override fun getSegmentColor(index: Int): Int {
            if (index in 0 until colors.size) {
                return colors[index]
            }
            return Color.BLACK
        }

        override fun getSegmentName(index: Int): String {
            return divisions[index].name ?: ""
        }

    }

}
