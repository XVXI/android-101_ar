package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TravelPlaceDetailActivity
import com.transcode.smartcity101p2.adapter.MyStampListAdapter
import com.transcode.smartcity101p2.adapter.ProvinceExpandableListAdapter
import com.transcode.smartcity101p2.adapter.StampListAdapter
import com.transcode.smartcity101p2.contract.TravelStampFragmentContract
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.presenter.TravelStampFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_travel_stamp.*

class TravelStampFragment : CoreFragment(), TravelStampFragmentContract.View, ProvinceExpandableListAdapter.PhoneBookListAdapterListener, StampListAdapter.StampClickItem, MyStampListAdapter.MyStampClickItem {
    override fun onMyStampClickItem(res: MyStampResponse.MyStampResponseData) {
        val intent = Intent(context, TravelPlaceDetailActivity::class.java)
        intent.putExtra("title", res.place_name)
        intent.putExtra("place_id", res.place_id)
        startActivity(intent)
    }

    override fun onStampClickItem(res: StampResponse.StampResponseData) {
        val intent = Intent(context, TravelPlaceDetailActivity::class.java)
        intent.putExtra("title", res.place_name)
        intent.putExtra("place_id", res.place_id)
        startActivity(intent)
    }

    override fun onPhoneBookListClick(item: ProvinceResponse.ProvinceResponseData) {
        showStampPlaceByID(item.province_id ?: "")
    }

    override fun updateStamp(list: ArrayList<StampResponse.StampResponseData>) {
        adapter.setData(list)
        adapter.setStamp(myStamp)
        adapter.notifyDataSetChanged()
    }

    override fun updateMyStamp(list: ArrayList<MyStampResponse.MyStampResponseData>, place_id: String) {
        myStamp = list

        myadapter.setData(list)
        myadapter.notifyDataSetChanged()

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getStampByPlaceID(login.authority_info?.CitizenId
                ?: "", if (place_id.isNullOrEmpty()) {
            login.authority_info?.CityId ?: ""
        } else {
            place_id
        })
    }

    override fun updateProvince(list: ArrayList<ProvinceResponse.ProvinceResponseData>) {
        val listHeader = arrayListOf<String>()
        val allHeader = arrayListOf<HashMap<String, String>>()
        val allChild = hashMapOf<String, ArrayList<ProvinceResponse.ProvinceResponseData>>()

        for (data in list) {
            var inList = false
            for (charTitle in listHeader) {
                if ((data.province_name ?: "").startsWith(charTitle)) {
                    inList = true
                    break
                }
            }
            if (!inList) {
                val name = data.province_name ?: "" as String
                val s = if(name.length>0){
                    name[0].toString()
                }else{
                    ""
                }
                listHeader.add(s)

                val h = hashMapOf<String, String>()
                h["title"] = s
                allHeader.add(h)
            }
        }

        allHeader.sortBy {
            it["title"]
        }

        for (charTitle in listHeader) {
            val childList = arrayListOf<ProvinceResponse.ProvinceResponseData>()
            for (data in list) {
                if ((data.province_name ?: "").startsWith(charTitle)) {
                    childList.add(data)
                }
            }
            allChild[charTitle] = childList
        }

        expAdapter.setChildData(allChild)
        expAdapter.setData(allHeader)
        expAdapter.notifyDataSetChanged()
        expAdapter.setChildListener(this)

        for (i in 0 until expAdapter.groupCount) {
            recyclerView_select.expandGroup(i)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): TravelStampFragment {
            return TravelStampFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var expAdapter: ProvinceExpandableListAdapter
    lateinit var presenter: TravelStampFragmentPresenter
    lateinit var adapter: StampListAdapter
    lateinit var myadapter: MyStampListAdapter
    var myStamp = arrayListOf<MyStampResponse.MyStampResponseData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_travel_stamp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = TravelStampFragmentPresenter(this)

        context?.let {
            adapter = StampListAdapter(it)
            adapter.setRecyclerView(recyclerView_current)
            adapter.setClickItemListener(this)

            myadapter = MyStampListAdapter(it)
            myadapter.setRecyclerView(recyclerView_my)
            myadapter.setClickItemListener(this)

            expAdapter = ProvinceExpandableListAdapter(it)
            expAdapter.setView(recyclerView_select)
        }

        bt_other.setOnClickListener {
            layout_bottom_menu.visibility = View.VISIBLE
            val appBar = appbar as CustomAppBarLayout2
            appBar.setTitle("พิชิตแลนด์มาร์คทั่วไทย")
            appBar.setTitleColor(resources.getColor(R.color.dark_purple))
            appBar.rightBt.visibility = View.INVISIBLE

            layout_bottom_menu.visibility = View.VISIBLE
            layout_current.visibility = View.GONE
            layout_select.visibility = View.VISIBLE
            layout_my_stamp.visibility = View.GONE
        }



        bt_my_stamp.setOnClickListener {
            layout_bottom_menu.visibility = View.VISIBLE
            val appBar = appbar as CustomAppBarLayout2
            appBar.setTitle("My Stamp")
            appBar.setTitleColor(resources.getColor(R.color.dark_purple))
            appBar.rightBt.visibility = View.INVISIBLE

            layout_bottom_menu.visibility = View.VISIBLE
            layout_current.visibility = View.GONE
            layout_select.visibility = View.GONE
            layout_my_stamp.visibility = View.VISIBLE
        }
    }

    fun showStampPlaceByID(place_id: String) {
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var used_place_id = if (place_id.isNullOrEmpty()) {
            login.authority_info?.CityId ?: ""
        } else {
            place_id
        }

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(arguments?.getString("TITLE") ?: "")
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            if (!layout_current.isShown) {
                showStampPlaceByID("")
            } else {
                activity?.apply {
                    finish()
                }
            }
        }
        appBar.rightBt.visibility = View.INVISIBLE

        layout_bottom_menu.visibility = View.VISIBLE
        layout_current.visibility = View.VISIBLE
        layout_select.visibility = View.GONE
        layout_my_stamp.visibility = View.GONE

//        get my stamp first
        presenter.getMyStamp(login.authority_info?.CitizenId ?: "", place_id)
//        then get by stamp id
        presenter.getProvince()
    }
}