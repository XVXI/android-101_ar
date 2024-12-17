package com.transcode.smartcity101p2

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.appbar_main2.view.*
import java.io.File

class VideoPlayerActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.leftBt.setOnClickListener { finish() }

        val outDir = Environment.getExternalStorageDirectory().absolutePath

        val outFile = File(outDir, intent?.extras?.getString("filename") ?: "video101.mp4")
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", outFile)
        viedoView.setVideoURI(uri)
        viedoView.start()

        var canprepared = false
        viedoView.setOnPreparedListener {
            canprepared = true
        }

        viedoView.setOnCompletionListener {
            if (canprepared) {
                viedoView.setVideoURI(uri)
                viedoView.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}