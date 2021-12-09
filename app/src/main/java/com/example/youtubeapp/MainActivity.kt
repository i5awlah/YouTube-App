package com.example.youtubeapp

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeapp.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import androidx.recyclerview.widget.GridLayoutManager







class MainActivity : AppCompatActivity() {
    lateinit var youTubePlayerView: YouTubePlayerView
    lateinit var player: YouTubePlayer
    val tracker = YouTubePlayerTracker()


    private lateinit var binding: ActivityMainBinding
    private lateinit var rvVideos: RecyclerView
    private lateinit var videoAdapter: VideoAdapter

    lateinit var playList: ArrayList<String>

    var currentID = 0
    var timeStamp = 0f


    private val videos = arrayListOf(
        Video("d0E0RVA8gcU","An-Nas"),
        Video("G20YLHz4mEc","Al-Falag"),
        Video("WWDpLIIwE5A","Al-Ikhlas"),
        Video("sjB7c19nvq8","Al-Masad"),
        Video("Ajgi1pGHeIY","An-Nasr"),
        Video("xFe_k12hR10","Al-Kafirun"),
        Video("7t_tu0uick8","Al-Kawthar"),
        Video("QUjUAVr7YOg","Quraysh"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Perform an internet connection check before trying to display a video
        if (checkConnection()) {
            Log.d("Main","Connect")
            setupYouTube()
        } else {
            Log.d("Main","Not Connected To The Internet")
        }

        binding.btnAdd.setOnClickListener {
            addVideosToPlayList()
        }
    }

    private fun setupYouTube() {
        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                player = youTubePlayer
                setupRecyclerView()
                youTubePlayer.addListener(tracker)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)
                if (state.toString() == "ENDED" && tracker.videoId == playList[currentID-1] && currentID < playList.size) {
                    player.loadVideo(playList[currentID], timeStamp)
                    currentID++
                }
            }
        })


    }

    private fun setupRecyclerView() {
        rvVideos = binding.rvVideo
        videoAdapter = VideoAdapter(videos, player)
        rvVideos.adapter = videoAdapter
        //rvVideos.layoutManager = LinearLayoutManager(this)

        //  Grid View
        rvVideos.layoutManager = GridLayoutManager(this, 2)
    }

    private fun checkConnection() : Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
    private fun addVideosToPlayList() {
        currentID = 0

        playList = arrayListOf()
        for (video in videos) {
            if (video.checked) {
                playList.add(video.id)
            }
        }
        if (playList.size >= 1) {
            playList.forEach { Log.d("Main", it) }
            player.loadVideo(playList[0], 0f)
            currentID++
        }
    }

    // Override onConfigurationChanged to track device rotation
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Main","ORIENTATION_LANDSCAPE")
            youTubePlayerView.enterFullScreen() // Set video to full screen when in landscape mode,
        } else {
            Log.d("Main","ORIENTATION_PORTRAIT") //  exit full screen in portrait mode
            youTubePlayerView.exitFullScreen()
        }
    }

    // Save video id and time stamp to allow continuous play after device rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentID", currentID)
        outState.putFloat("timeStamp", timeStamp)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentID = savedInstanceState.getInt("currentID", 0)
        timeStamp = savedInstanceState.getFloat("timeStamp", 0f)
    }
}