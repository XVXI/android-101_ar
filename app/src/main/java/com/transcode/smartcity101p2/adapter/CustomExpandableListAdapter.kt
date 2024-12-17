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
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.QueueSlotResponse
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_select_time.view.*
import java.util.ArrayList
import java.util.HashMap

class CustomExpandableListAdapter(var context: Context) : BaseExpandableListAdapter() {

    var expandableListTitle = arrayListOf<HashMap<String, Any>>()
    var expandableListDetail = HashMap<String, ArrayList<QueueSlotResponse.QueueSlotData>>()
    var listView: ExpandableListView? = null
    var listener: ChildClickListener? = null

    fun setView(listView: ExpandableListView) {
        this.listView = listView
        this.listView?.setAdapter(this)
    }

    fun setChildListener(listener: ChildClickListener) {
        this.listener = listener
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
        val date = this.expandableListTitle[listPosition]["date"] as Long
        return this.expandableListDetail[date.toString()]?.get(expandedListPosition)
    }

    fun setData(list: ArrayList<HashMap<String, Any>>) {
        expandableListTitle = list
    }

    fun getAllChild(groupPosition: Int): ArrayList<QueueSlotResponse.QueueSlotData> {
        val date = this.expandableListTitle[groupPosition]["date"] as Long
        return expandableListDetail[date.toString()] ?: arrayListOf()
    }

    fun updateChild(groupPosition: Int, list: ArrayList<QueueSlotResponse.QueueSlotData>) {
        val date = this.expandableListTitle[groupPosition]["date"] as Long
        expandableListDetail.remove(date.toString())
        expandableListDetail[date.toString()] = list

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
            convertView = layoutInflater.inflate(R.layout.item_select_time, null)
        }
        val expandedListTextView = convertView?.findViewById<View>(R.id.title) as TextView
        val data = getChild(listPosition, expandedListPosition) as QueueSlotResponse.QueueSlotData

        var stime = data.QueueFromTime ?: "00:00:00"
        stime = stime.substring(0, stime.length - 3)
        if (stime.startsWith("0")) stime = stime.substring(1, stime.length)

        var etime = data.QueueToTime ?: "00:00:00"
        etime = etime.substring(0, etime.length - 3)
        if (etime.startsWith("0")) etime = etime.substring(1, etime.length)

        val texts = "$stime - $etime"

        expandedListTextView.text = texts

        val limit = Integer.parseInt(data.QueueLimit ?: "0")
        val checkIn = Integer.parseInt(data.CountCheckin ?: "0")

        if (data.selected) {
            convertView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_select_time_bg_blue_tran)
            convertView.mark.visibility = View.VISIBLE
        } else {
            convertView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_select_time_bg_gray_tran)
            convertView.mark.visibility = View.INVISIBLE
        }

        if (checkIn >= limit) {
            convertView.fulltext.visibility = View.VISIBLE
            convertView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_select_time_bg_gray_tran)
            convertView.setOnClickListener({})
        } else {
            convertView.fulltext.visibility = View.INVISIBLE
            convertView.setOnClickListener({

                for (i in 0 until groupCount) {
                    val d = getAllChild(i)
                    for (j in d) {
                        j.selected = false
                        if (j.QueueSlotId == data.QueueSlotId) {
                            j.selected = true
                        }
                    }
                }
                notifyDataSetChanged()
                listener?.onChildClick(listPosition, data)
            })
        }

        return convertView
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as HashMap<String, Any>
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_select_time, null)
        }
        val listTitleTextView = convertView?.findViewById<View>(R.id.title) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = AppUtils.getDateString("EEE " + AppUtils.formateDate3, listTitle["date"] as Long)

        val imageView = convertView?.findViewById<View>(R.id.item_image) as ImageView
        imageView.visibility = View.VISIBLE

        if (isExpanded) {
            imageView.load("", R.mipmap.icon_coll)
        } else {
            imageView.load("", R.mipmap.icon_exp)
        }

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        val date = this.expandableListTitle[listPosition]["date"] as Long
        return this.expandableListDetail[date.toString()]?.size ?: 0
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
        fun onChildClick(groupPosition: Int, data: QueueSlotResponse.QueueSlotData)
    }
}