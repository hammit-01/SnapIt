package com.example.snapit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView

    private lateinit var back: ImageView
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

        // 뒤로가기 버튼
        back = findViewById(R.id.back)
        back.setOnClickListener {
            if (isTaskRoot) {
                // 현재 액티비티가 스택에서 마지막 액티비티라면 앱을 종료하지 않고 홈으로
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                // 이전 액티비티가 있으면 뒤로가기 동작 실행
                finish()
            }
        }
    }
}