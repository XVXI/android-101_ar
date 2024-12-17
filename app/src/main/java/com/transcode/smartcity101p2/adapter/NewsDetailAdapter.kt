package com.transcode.smartcity101p2.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.LocationActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.KanitTextView
import kotlinx.android.synthetic.main.item_news_comment.view.*
import kotlinx.android.synthetic.main.item_news_detail.view.*
import kotlinx.android.synthetic.main.item_queue_header.view.*


class NewsDetailAdapter(var context: Context, var fragmentManager: FragmentManager?) : AppBaseAdapter() {
    var list = arrayListOf<Any>()
    private var listener: ClickItem? = null

    companion object {
        val TYPE_NEWS_DETAIL = 0
        val TYPE_HEADER = 1
        val TYPE_COMMENT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = when (viewType) {
            TYPE_NEWS_DETAIL -> inflater.inflate(R.layout.item_news_detail, parent, false)
            TYPE_HEADER -> inflater.inflate(R.layout.item_queue_header, parent, false)
            else -> inflater.inflate(R.layout.item_news_comment, parent, false)
        }
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NEWS_DETAIL -> {
                val hashMap = list[position] as HashMap<String, String>
                holder.itemView.titles.text = hashMap["title"]
                val reg = Regex("<[^>]*>|\\\\n|&nbsp;")
                holder.itemView.detail.text = reg.replace(hashMap["data"].toString(), "")
//                val imageGetter = PicassoImageGetter(holder.itemView.detail, context)
//                holder.itemView.detail.text = Html.fromHtml(hashMap["data"].toString(), Html.FROM_HTML_MODE_COMPACT, imageGetter, null)
                holder.itemView.ratingBar.rating = (hashMap["rating"] ?: "0.0").toFloat()
                holder.itemView.ratingText.text = holder.itemView.ratingBar.rating.toString()
                holder.itemView.ratingBar.isEnabled = false
                holder.itemView.layout_file.visibility = View.GONE
                holder.itemView.layout_location.visibility = View.GONE
                when {
                    holder.itemView.ratingBar.rating <= 0 -> holder.itemView.frame_rating.visibility = View.GONE
                    else -> holder.itemView.frame_rating.visibility = View.VISIBLE
                }

//                val date = AppUtils.dateStringToMillis(hashMap["date"].toString(), arrayOf(AppUtils.formateDate0))
//                holder.itemView.text_date.text = AppUtils.getDateString(AppUtils.formateDate5, date)
                holder.itemView.text_date.text = hashMap["Day"] + " " + hashMap["Time"]
                holder.itemView.text_gene.text = hashMap["cat"]

                if (!hashMap["lat"].isNullOrEmpty()) {
                    val lat = hashMap["lat"] ?: "0"
                    val lng = hashMap["lng"] ?: "0"
                    holder.itemView.layout_location.visibility = View.VISIBLE
                    holder.itemView.layout_location.setOnClickListener {
                        //                        fragmentManager?.let {
//                            FragmentHelper.replace(it,
//                                    LocationFragment.newInstance(lat.toDouble(), lng.toDouble()),
//                                    R.id.content_home_frame)
//                        }
                        val intent = Intent(context, LocationActivity::class.java)
                        intent.putExtra("lat", lat.toDouble())
                        intent.putExtra("lng", lng.toDouble())
                        context.startActivity(intent)
                    }
                }

                if (!hashMap["files"].isNullOrEmpty()) {
                    val typetoken = object : TypeToken<ArrayList<NewsFileResponse.NewsFileResponseData>>() {}
                    val gson = Gson()
                    val res = gson.fromJson<ArrayList<NewsFileResponse.NewsFileResponseData>>(hashMap["files"], typetoken.type)
                    if (res.size > 0) {
                        holder.itemView.layout_file.visibility = View.VISIBLE
                        holder.itemView.layout_file.removeAllViews()
                        for (o in res) {
                            val v = KanitTextView(context)
                            holder.itemView.layout_file.addView(v)
                            var filename = o.FileUrl ?: ""
                            if (filename.contains('/')) {
                                filename = filename.substring(filename.lastIndexOf('/') + 1)
                            }

                            if (filename.contains('=')) {
                                filename = filename.substring(filename.lastIndexOf('=') + 1)
                            }

                            v.text = filename
                            val pl = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            pl.setMargins(20, 0, 20, 0)
                            v.layoutParams = pl
                            v.setOnClickListener {
                                val url = if (o.FileUrl.toString().contains(".png", true) || o.FileUrl.toString().contains(".jpg", true)) {
                                    AppUtils.getImageUrl(o.FileUrl.toString())
                                } else {
                                    o.FileUrl.toString()
                                }
                                val target = Intent(Intent.ACTION_VIEW)
                                if (url.contains(".png", true) || url.contains(".jpg", true)) {
                                    target.setData(Uri.parse(url))
                                } else {
                                    target.setDataAndType(Uri.parse(url), "application/pdf")
                                }

                                target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                                val intent = Intent.createChooser(target, "Open File")
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    // Instruct the user to install a PDF reader here, or something
                                }

                            }
                        }
                    }
                }
            }
            TYPE_HEADER -> {
                holder.itemView.title.text = context.getString(R.string.all_comment_text)
            }
            TYPE_COMMENT -> {
                val data = list[position] as AllNewsCommentResponse.AllNewsCommentResponseItem
                holder.itemView.message.text = data.Comment
                holder.itemView.name.text = data.FName + " " + data.LName
                holder.itemView.date.text = data.Day + " " + data.Time
                holder.itemView.ratings.text = data.Rate

                val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                val type = when {
                    login.authority_info?.IsFb == "1" -> "facebook"
                    login.authority_info?.IsFb == "2" -> "guest"
                    login.authority_info?.IsFb == "3" -> "google"
                    else -> "email"
                }

                if (login.authority_info?.CitizenId == data.CitizenId) {
                    when (type) {
                        "email" -> {
                            val image = Hawk.get<String>("user_image") ?: ""
                            if (image != "" || image != "null") {
                                holder.itemView.icon.load(image, R.mipmap.man)
                            }
                        }
                        "facebook" -> if (fb_image.isNullOrEmpty()) {
                            val accessToken = AccessToken.getCurrentAccessToken()
                            val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                                // Application code
                                fb_image = try {
                                    json.getJSONObject("picture").getJSONObject("data").getString("url")
                                } catch (exception: Exception) {
                                    ""
                                }
                                holder.itemView.icon.load(fb_image, R.mipmap.man)
                            }
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                            request.parameters = parameters
                            request.executeAsync()
                        } else {
                            holder.itemView.icon.load(fb_image, R.mipmap.man)
                        }
                        "google" -> {
                            val acct = Hawk.get<GAccount>("G_ACCOUNT")
                            if (acct != null) {
                                holder.itemView.icon.load(acct.photoUrl, R.mipmap.man)
                            } else {
                                holder.itemView.icon.load("", R.mipmap.man)
                            }
                        }
                    }
                } else {
                    holder.itemView.icon.load(AppUtils.getImageUrl(data.CitizenImg), R.mipmap.man)
                }
            }
        }
    }

    var fb_image = ""

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<Any>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: Any)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                TYPE_NEWS_DETAIL
            }
            1 -> {
                TYPE_HEADER
            }
            else -> TYPE_COMMENT
        }
    }
}