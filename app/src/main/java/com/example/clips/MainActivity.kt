package com.example.clips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import com.example.clips.player.GetVideosUsecase
import com.example.clips.player.PlayerPool
import com.example.clips.player.VideoPlayer
import com.example.clips.ui.theme.ClipsTheme

class MainActivity : ComponentActivity() {

    lateinit var playerPool: PlayerPool
    lateinit var getVideosUsecase: GetVideosUsecase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            playerPool = PlayerPool(LocalContext.current)
            getVideosUsecase = GetVideosUsecase()
            ClipsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoPlayerItem(
                        modifier = Modifier.padding(innerPadding),
                        playerPool = playerPool,
                        videos = getVideosUsecase,
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoPlayerItem(
    modifier: Modifier,
    playerPool: PlayerPool,
    videos: GetVideosUsecase,
) {
    val state = rememberPagerState { videos.getVideosSize() }
    VerticalPager(
        state = state,
        modifier = modifier
            .fillMaxSize(),
    ) { clip ->
        val player = playerPool.getPlayerForClips(clip)

        LaunchedEffect(state.currentPage, clip) {
            if (state.currentPage == clip) {
                val url = videos(state.currentPage)
                player.setMediaItem(
                    MediaItem.fromUri(url)
                )
                player.prepare()
                player.setPlaybackSpeed(3.0f)
                player.playWhenReady = true
            } else {
                player.playWhenReady = false
                player.stop()
            }
        }

        VideoPlayer(
            player = player,
            modifier = Modifier.fillMaxSize()
        )
    }

    DisposableEffect(Unit) {
        onDispose { playerPool.releaseAll() }
    }
}