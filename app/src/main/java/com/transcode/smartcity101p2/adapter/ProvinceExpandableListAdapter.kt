package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.ProvinceResponse
import com.transcode.smartcity101p2.view.KanitTextView
import java.util.ArrayList
import java.util.HashMap

class ProvinceExpandableListAdapter(var context: Context) : BaseExpandableListAdapter() {

    var expandableListTitle = arrayListOf<HashMap<String, String>>()
    var expandableListDetail = HashMap<String, ArrayList<ProvinceResponse.ProvinceResponseData>>()
    var listView: ExpandableListView? = null
    var listener: PhoneBookListAdapterListener? = null

    fun setView(listView: ExpandableListView) {
        this.listView = listView
        this.listView?.setAdapter(this)
    }

    fun setChildListener(listener: PhoneBookListAdapterListener) {
        this.listener = listener
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
        val title = this.expandableListTitle[listPosition]["title"].toString()
        return this.expandableListDetail[title]?.get(expandedListPosition)
    }

    fun setData(list: ArrayList<HashMap<String, String>>) {
        expandableListTitle = list
    }

    fun setChildData(child: HashMap<String, ArrayList<ProvinceResponse.ProvinceResponseData>>) {
        expandableListDetail = child
    }

    fun getAllChild(groupPosition: Int): ArrayList<ProvinceResponse.ProvinceResponseData> {
        val title = this.expandableListTitle[groupPosition]["title"].toString()
        return expandableListDetail[title] ?: arrayListOf()
    }

    fun updateChild(groupPosition: Int, list: ArrayList<ProvinceResponse.ProvinceResponseData>) {
        val title = this.expandableListTitle[groupPosition]["title"].toString()
        expandableListDetail.remove(title)
        expandableListDetail[title] = list

        notifyDataSetChanged()

        listView?.postDelayed({ listView?.setSelectedGroup(groupPosition) }, 250)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater = this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_stamp_province, null)
        }
        val root = convertView?.findViewById<View>(R.id.root)
        val expandedListTextView = convertView?.findViewById<View>(R.id.title) as TextView
        val data = getChild(listPosition, expandedListPosition) as ProvinceResponse.ProvinceResponseData

        expandedListTextView.text = data.province_name

        convertView.setOnClickListener {
            notifyDataSetChanged()
            listener?.onPhoneBookListClick(data)
        }

        return convertView
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as HashMap<String, Any>
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_exp_province_group, null)
        }
        val listTitleTextView = convertView?.findViewById<View>(R.id.row_phonebook_group_name) as KanitTextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.setFont()
        listTitleTextView.text = listTitle["title"].toString()

        val imageView = convertView?.findViewById<View>(R.id.row_phonebook_group_exp) as ImageView

//        if (isExpanded) {
//            imageView.load("", R.drawable.icon_29_33)
//        } else {
//            imageView.load("", R.drawable.icon_29_34)
//        }

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        val title = this.expandableListTitle[listPosition]["title"].toString()
        return this.expandableListDetail[title]?.size ?: 0
    }

    override fun getGroup(listPosition: Int): Any {
        return this.expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    interface ChildClickListener {
        fun onChildClick(groupPosition: Int, data: ProvinceResponse.ProvinceResponseData)
    }

    interface PhoneBookListAdapterListener {

        fun onPhoneBookListClick(phonebookItem: ProvinceResponse.ProvinceResponseData)
    }
}