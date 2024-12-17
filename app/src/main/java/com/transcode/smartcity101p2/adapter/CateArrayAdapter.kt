package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.transcode.smartcity101p2.model.buyapi.response.CateListResponse

class CateArrayAdapter(var mContext: Context, var resource: Int, var list: ArrayList<CateListResponse>) : ArrayAdapter<CateListResponse>(mContext, resource, list) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(resource, parent, false) as TextView
        view.text = list[position].pd_online_cat_name
        return view
    }
}