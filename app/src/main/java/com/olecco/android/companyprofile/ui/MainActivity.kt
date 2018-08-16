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
import com.olecco.android.companyprofile.api.ProfilesRepository
import com.olecco.android.companyprofile.model.Company
import com.olecco.android.companyprofile.model.CompanyData
import com.olecco.android.companyprofile.ui.PieChartView.PieChartAdapter
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModel
import com.olecco.android.companyprofile.ui.viewmodel.ProfilesViewModelFactory

class MainActivity : AppCompatActivity() {

    val profileApplication: CompanyProfileApplication
        get() = application as CompanyProfileApplication

    val profilesRepository: ProfilesRepository
        get() = profileApplication.profilesRepository

    lateinit var profilesViewModel: ProfilesViewModel
    lateinit var companyListAdapter: CompanyListAdapter
    lateinit var pieChartView: PieChartView

    private val companyDataObserver: Observer<CompanyData> = Observer {

        //Toast.makeText(this@MainActivity, it?.symbol + " : " + it?.divisions?.size, Toast.LENGTH_LONG).show()


        Log.d("111", "size=${it?.treeData?.companyRoot?.divisionDataList?.size}");


        profilesViewModel.companyData?.removeObservers(this@MainActivity)

    }

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

                profilesViewModel.selectedCompany = company.ticker!!


                profilesViewModel.companyData?.observe(this@MainActivity, companyDataObserver)


                //Toast.makeText(this@MainActivity, company.ticker, Toast.LENGTH_LONG).show()

            }
        }

        pieChartView = findViewById(R.id.pie_chart)

        val adapter: PieChartAdapter = object : PieChartAdapter {
            override fun getSegmentColor(index: Int): Int {
                if (index == 0) return Color.RED
                if (index == 1) return Color.GREEN
                if (index == 2) return Color.BLUE
                return Color.WHITE
            }

            override fun getSegmentValue(index: Int): Double {
                if (index == 0) return 5.0
                if (index == 1) return 3.0
                if (index == 2) return 3.0
                return 1.0
            }

            override fun getSegmentCount(): Int {
                return 4
            }
        }

        pieChartView.adapter = adapter


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
