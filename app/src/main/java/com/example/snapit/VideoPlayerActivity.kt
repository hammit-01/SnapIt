package com.example.snapit

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_item)

        videoView = findViewById(R.id.videoView)
        val videoUrl = intent.getStringExtra("videoUrl")

        if (videoUrl != null) {
            videoView.setVideoURI(Uri.parse(videoUrl))
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = false // 반복 재생 안 함
                videoView.start() // 비디오 시작
            }
        }
    }
}