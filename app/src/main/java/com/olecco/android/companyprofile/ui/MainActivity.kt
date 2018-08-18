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
import com.olecco.android.companyprofile.api.ApiResponseState
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.model.CompanyData
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
            when(it?.state) {
                ApiResponseState.LOADING -> {
                    companySpinnerView.hide()
                    pieChartView.hide()
                    progressView.show()
                }
                ApiResponseState.SUCCESS -> {

                }
                else -> {

                }
            }

            companyListAdapter.data = it?.companies!!
        })

        profilesViewModel.divisionsData.observe(this, Observer {

            pieChartAdapter.chartNameString = profilesViewModel.selectedCompany
            pieChartAdapter.divisions = it ?: listOf()
            pieChartView.adapter = pieChartAdapter

            Log.d("111", "size=${it?.size}")


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

//        val adapter: PieChartAdapter = object : PieChartAdapter {
//            override fun getSegmentName(index: Int): String {
//                if (index == 0) return "000000000000000"
//                if (index == 1) return "1111111"
//                if (index == 2) return "22222222222222222222222222"
//                return "333333333333333"
//            }
//
//            override fun getChartName(): String {
//                return "AAPL"
//            }
//
//            override fun getSegmentColor(index: Int): Int {
//                if (index == 0) return Color.RED
//                if (index == 1) return Color.GREEN
//                if (index == 2) return Color.BLUE
//                return Color.WHITE
//            }
//
//            override fun getSegmentValue(index: Int): Double {
//                if (index == 0) return 5.0
//                if (index == 1) return 3.0
//                if (index == 2) return 3.0
//                return 1.0
//            }
//
//            override fun getSegmentCount(): Int {
//                return 4
//            }
//        }
//
//        pieChartView.adapter = adapter

        pieChartView.pieChartClickListener = object : PieChartClickListener {
            override fun onSegmentClick(segmentIndex: Int) {
                Log.d("111", "index=" + segmentIndex)
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
