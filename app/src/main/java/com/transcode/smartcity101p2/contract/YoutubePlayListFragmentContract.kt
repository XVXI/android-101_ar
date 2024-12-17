package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.YoutubePlayListResponse

interface YoutubePlayListFragmentContract {
    interface View {
        fun updateList(res: YoutubePlayListResponse)
        fun requestFail(message: String)
    }

    interface Presenter {
        fun requestYoutubePlayList(part: String, maxResults: String, playlistId: String, key: String)
    }
}