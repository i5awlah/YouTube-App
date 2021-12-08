package com.example.youtubeapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeapp.databinding.ItemRowBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

class VideoAdapter(private val videos: ArrayList<Video>, val player: YouTubePlayer): RecyclerView.Adapter<VideoAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val video = videos[position]

        holder.binding.apply {
            btnPlay.text = video.name
            btnPlay.setOnClickListener {
                //player.loadVideo(video.id, 0f)

                if(video.checked) {
                    btnPlay.setBackgroundColor(Color.GRAY)
                    video.checked = false
                } else {
                    btnPlay.setBackgroundColor(Color.GREEN)
                    video.checked = true
                }

            }
        }
    }

    override fun getItemCount() = videos.size
}