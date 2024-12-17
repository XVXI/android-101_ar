package com.transcode.smartcity101p2

import android.os.Bundle
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.YoutubePlayListFragment
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse

class YoutubeListActivity : CoreActivity() {

    lateinit var fragment: YoutubePlayListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_list)

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val youtube_list_id = login.authority_info?.YoutubeList.toString()
        fragment = YoutubePlayListFragment.newInstance("YouTube", youtube_list_id)
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}