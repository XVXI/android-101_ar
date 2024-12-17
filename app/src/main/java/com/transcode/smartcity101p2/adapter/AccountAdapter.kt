package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.fragment.AccountFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper


class AccountAdapter(var context: Context ,var fragmentManager: FragmentManager?) : AppBaseAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.blank_frame, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fragmentManager?.let {
            FragmentHelper.replaceWithoutAddingToBackStack(it , AccountFragment.newInstance() , R.id.sub_menu_content)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context , LinearLayoutManager.HORIZONTAL , false)
        rv.adapter = this
    }
}