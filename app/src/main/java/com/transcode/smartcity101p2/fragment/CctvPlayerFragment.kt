package com.transcode.smartcity101p2.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.fragment_cctvplayer.*

class CctvPlayerFragment : CoreFragment() {
    companion object {
        fun newInstance(stream_url: String): CctvPlayerFragment {
            return CctvPlayerFragment().apply {
                val bundle = Bundle()
                bundle.putString("stream_url", stream_url)
                arguments = bundle
            }
        }
    }

    private var player: SimpleExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var videoTrackSelectionFactory: TrackSelection.Factory? = null
    private var bandwidthMeter: DefaultBandwidthMeter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cctvplayer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        context?.let {
            bandwidthMeter = DefaultBandwidthMeter()
            videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            val loadControl = DefaultLoadControl()
            player = ExoPlayerFactory.newSimpleInstance(it, trackSelector, loadControl)
            player?.addListener(playerListener)
            playerView.player = player

            val stream_url = arguments?.getString("stream_url") ?: ""
            setDatasource(stream_url)
        }
    }

    private fun setDatasource(videourl: String) {
        player?.playWhenReady = true
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Sccitizen"), DefaultBandwidthMeter())
        val videoSource = HlsMediaSource(Uri.parse(videourl), dataSourceFactory, Handler(), null)
        player?.prepare(videoSource)
    }

    val playerListener = object : ExoPlayer.EventListener {

    }
}