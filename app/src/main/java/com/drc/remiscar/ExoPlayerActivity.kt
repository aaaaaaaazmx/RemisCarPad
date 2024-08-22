package com.drc.remiscar

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView


class ExoPlayerActivity : Activity() {
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo)

        player = SimpleExoPlayer.Builder(this).build()
        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.player = player


// 本地视频
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.aed)


        // 或网络视频
        val mediaItem = MediaItem.fromUri(uri)
        // val mediaItem = MediaItem.fromUri(Uri.parse("https://v3.huoshanvod.com/c482b3aa612e6bf511a5a2ed22867430/66c6c627/video/tos/cn/tos-cn-ve-0015c800/oEjVxAZeENAtLDRklX8bABgflLtcq4g99qnmAB/?a=1112&ch=0&cr=0&dr=3&cd=0%7C0%7C0%7C0&br=386&bt=386&cs=0&ds=3&ft=k7j3HVVywfyRF38Pmo~pK7pswApxK3YwvrKnZwocdo0g3cI&mime_type=video_mp4&qs=0&rc=NDc6N2k1MztoNWk7Nzg3OEBpM3NlcDw6ZjM3azMzNGkzM0BhXzFeYS1hNTYxL2FgNF5iYSNjYDNrcjRfaWJgLS1kLWFzcw%3D%3D&btag=80000e00028000&dy_q=1724299062&feature_id=f0150a16a324336cda5d6dd0b69ed299&l=20240822115742F5D84040F20C5A9B507C"))

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()

        findViewById<ImageButton>(R.id.iv_back).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player!!.release()
        }
    }
}