package com.transcode.smartcity101p2.fragment

import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.AllCityAdapter
import com.transcode.smartcity101p2.contract.SelectPlaceFragmentContract
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.CityResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.presenter.SelectPlaceFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_select_place.*


class SelectPlaceFragment : CoreFragment(), SelectPlaceFragmentContract.View, AllCityAdapter.ClickItem {

    companion object {
        fun newInstance(): SelectPlaceFragment {
            return SelectPlaceFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: SelectPlaceFragmentPresenter
    lateinit var adapter: AllCityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("เลือกเมือง")
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }
        appBar.leftBt.load("", R.mipmap.market_back_icon)

        presenter = SelectPlaceFragmentPresenter(this)
        context?.let {
            adapter = AllCityAdapter(it)
            adapter.setRecyclerView(all_city)
            adapter.setClickListener(this)
        }

//        grantLocationPermission()

        onclickItem(CityResponse().apply {
            Cityname = "เทศบาลเมืองร้อยเอ็ด"
            Cityid = "6"
        })
    }

    private fun grantLocationPermission() {
        activity?.let {
            if (!isGrantedPermission()) {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else {
                //show nearby
                //get all place with location
                presenter.getAllCity("lat", "lang")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (isGrantedPermission()) {
//                    Log.e("isGrantedPermission", "true")
                    //show nearby
                    //get all place with location
                    presenter.getAllCity("lat", "lang")
                } else {
//                    not show nearby
                    //get all place without location
//                    Log.e("isGrantedPermission", "false")
                    presenter.getAllCity()
                }
            }
            else -> {
            }
        }
    }

    override fun updateCityList(list: ArrayList<CityResponse>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()

        for (res in list) {
            if (res.Cityid == "6") {
                onclickItem(res)
                break
            }
        }
    }

    override fun onclickItem(res: CityResponse) {
        Hawk.put(Const.KEY_CITY, res)
        fragmentManager?.popBackStack()
        FragmentHelper.remove(fragmentManager, this)
    }
}