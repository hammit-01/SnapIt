package com.example.snapit.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.snapit.R
import com.example.snapit.VideoPlayerActivity

class VideoAdapter(private val context: Context, private val videoUrls: List<String>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoUrls = videoUrls[position]

        // 비디오 썸네일 로드
        Glide.with(context)
            .load(videoUrls)
            .apply(RequestOptions().frame(1000000)) // 비디오의 첫 번째 프레임을 가져옴
            .into(holder.imageView) // ImageView에 썸네일 로드

        // 썸네일 클릭 시 비디오 재생
        holder.imageView.setOnClickListener {
            // 비디오 재생하는 액티비티로 이동
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("videoUrl", videoUrls)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoUrls.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView) // 썸네일을 표시할 ImageView
    }
}
