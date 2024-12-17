package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetShopResponse
import kotlinx.android.synthetic.main.item_product_type_list.view.*

class ProductTypeListAdapter(var context: Context) : AppBaseAdapter() {
    var list = arrayListOf<ArrayList<GetProductResponse.GetProductResponseData>>()

    val key_best = 0
    val key_pop = 1
    val key_new = 2

    private var listener: ClickProductItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_product_type_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val view = holder.itemView
        when (position) {
            key_best -> {
                view.text_title.text = context.resources.getString(R.string.store_string_sell_week)
            }
            key_pop -> {
                view.text_title.text = context.resources.getString(R.string.store_string_pop_week)
            }
            key_new -> {
                view.text_title.text = context.resources.getString(R.string.store_string_last)
            }
        }
        val adapter = ProductListAdapter(context)
        adapter.setRecyclerView(view.recycler_product)
        adapter.setData(data)
        adapter.notifyDataSetChanged()

        adapter.setClickListener(object : ProductListAdapter.ClickProductItem {
            override fun onClickProductItem(shop: GetShopResponse.GetShopResponseData, res: GetProductResponse.GetProductResponseData) {
                listener?.onClickProductItem(shop, res)
            }
        })
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setMockup() {
        this.list.add(arrayListOf())
        this.list.add(arrayListOf())
        this.list.add(arrayListOf())
    }

    fun setPopular(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        this.list.removeAt(key_pop)
        this.list.add(key_pop, list)
    }

    fun setBestSeller(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        this.list.removeAt(key_best)
        this.list.add(key_best, list)
    }

    fun setNewProduct(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        this.list.removeAt(key_new)
        this.list.add(key_new, list)
    }

    fun setClickListener(listener: ClickProductItem) {
        this.listener = listener
    }

    interface ClickProductItem {
        fun onClickProductItem(shop: GetShopResponse.GetShopResponseData, res: GetProductResponse.GetProductResponseData)
    }
}