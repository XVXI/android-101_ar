package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.NewsResponse
import com.transcode.smartcity101p2.utils.CustomFont
import com.transcode.smartcity101p2.utils.FontManager
import kotlinx.android.synthetic.main.item_news.view.*
import kotlinx.android.synthetic.main.item_news_list.view.*

class AllNewsAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<NewsResponse>()
    var total_news = 0
    private var listener: ClickItem? = null

    val VIEW_TYPE_UPDATED_NEWS = 2

    val handler = Handler()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_UPDATED_NEWS) {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_news, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        } else {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_news_list, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (getItemViewType(position) == VIEW_TYPE_UPDATED_NEWS) {
            holder.itemView.title.text = data.NewsTitle
            holder.itemView.title.typeface = FontManager.getTypeFace(context, CustomFont.KANIT_REGULAR)
            holder.itemView.setOnClickListener {
                listener?.onclickItem(list[position])
            }

//            val date = AppUtils.dateStringToMillis(data.CreateDateTime.toString(), arrayOf(AppUtils.formateDate0))
//            holder.itemView.text_date_header.text = AppUtils.getDateString(AppUtils.formateDate5, date)
            holder.itemView.text_date_header.text = data.Day + " " + data.Time
            holder.itemView.text_gene_header.text = data.newsCatName

            val photoAdapter = PhotosAdapter(context)
            photoAdapter.setClickItemListener(object : PhotosAdapter.ClickItem {
                override fun onClickItem() {
                    listener?.onclickItem(list[position])
                }
            })
            holder.itemView.photos_viewpager.adapter = photoAdapter
            data.imgs?.let {
                val list = arrayListOf<String>()
                for (n in it) {
                    n?.let {
                        if (it.startsWith("http")) list.add(n)
                    }
                }
                if (list.size <= 0) {
                    holder.itemView.frameImage.visibility = View.GONE
                    holder.itemView.no_image.visibility = View.VISIBLE
                } else {
                    holder.itemView.frameImage.visibility = View.VISIBLE
                    holder.itemView.no_image.visibility = View.GONE
                }
                photoAdapter.setData(list)
                photoAdapter.notifyDataSetChanged()
                holder.itemView.tab_layout.setupWithViewPager(holder.itemView.photos_viewpager, true)
                holder.itemView.photos_viewpager.setCurrentItem(0, false)

                if (list.size > 1) {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        holder.itemView.photos_viewpager?.setCurrentItem(1, true)
                    }, 3500)
                }
            } ?: kotlin.run {
                holder.itemView.frameImage.visibility = View.GONE
                holder.itemView.no_image.visibility = View.VISIBLE
            }

            for (i in 0 until photoAdapter.count step 1) {
                val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab2, null)
                tab.setBackgroundResource(R.drawable.tab_selector)
                holder.itemView.tab_layout.getTabAt(i)?.customView = tab
                holder.itemView.tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
            }
            holder.itemView.photos_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    if (position >= photoAdapter.count) {
                        holder.itemView.photos_viewpager?.setCurrentItem(0, false)
                    } else {
                        holder.itemView.photos_viewpager?.setCurrentItem(position, true)
                    }

                    handler.removeCallbacksAndMessages(null)
                    holder.itemView.photos_viewpager?.let {
                        handler.postDelayed({
                            val nextItem = if (it.currentItem + 1 < it.adapter?.count ?: 0) {
                                it.currentItem + 1
                            } else {
                                0
                            }
                            it.setCurrentItem(nextItem, true)
                        }, 3500)
                    }
                }

            })
        } else {
            holder.itemView.list_title.text = data.NewsTitle
            holder.itemView.list_title.typeface = FontManager.getTypeFace(context, CustomFont.KANIT_REGULAR)
//            val date = AppUtils.dateStringToMillis(data.CreateDateTime.toString(), arrayOf(AppUtils.formateDate0))
//            holder.itemView.text_date.text = AppUtils.getDateString(AppUtils.formateDate5, date)
            holder.itemView.text_date.text = data.Day + " " + data.Time
            holder.itemView.text_gene.text = data.newsCatName
            holder.itemView.setOnClickListener {
                listener?.onclickItem(list[position])
            }

            data.imgs?.let {
                var link = ""
                for (i in it) {
                    if (i != null) {
                        if (i.startsWith("http")) {
                            link = i
                            break
                        }
                    }
                }
                holder.itemView.list_image.load(link, R.mipmap.placeholder_image)
            }?:kotlin.run {
                holder.itemView.list_image.load("", R.mipmap.placeholder_image)
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<NewsResponse>) {
        if (this.list.size == 0) {
            this.list = list
        } else {
            this.list.addAll(list)
        }

        if (this.list.size > 0) {
            val data = this.list[0]
            total_news = if (data.Counts != null) {
                data.Counts
            } else {
                0
            }
        }
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: NewsResponse)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_UPDATED_NEWS
        } else {
            super.getItemViewType(position)
        }
    }
}