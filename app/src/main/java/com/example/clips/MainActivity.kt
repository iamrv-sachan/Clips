package com.example.clips

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.example.clips.player.GetVideosUsecase
import com.example.clips.player.PlayerPool
import com.example.clips.player.VideoPlayer
import com.example.clips.ui.theme.ClipsTheme

@OptIn(UnstableApi::class)
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

@OptIn(UnstableApi::class)
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
        beyondViewportPageCount = 1,
    ) { clip ->
        val player = playerPool.getPlayerForClips(clip)
        val lifecycleOwner = rememberLifecycleOwner()

        LaunchedEffect(clip) {
            snapshotFlow { state.settledPage }.collect { currentPage ->
                if (currentPage == clip) {
                    val url = videos(currentPage)
                    player.setMediaItem(
                        MediaItem.fromUri(url)
                    )
                    player.prepare()
                    player.playWhenReady = true
                } else {
                    player.playWhenReady = false
                    player.stop()
                }
            }
        }

        DisposableEffect(clip, lifecycleOwner) {
            Log.d("rajeev", "VideoPlayerItem: ${state.settledPage}  $clip")
            val lifecycle = LifecycleEventObserver{ _, event ->
                when {
                    state.settledPage != clip -> player.playWhenReady = false
                    event == Lifecycle.Event.ON_RESUME && state.settledPage == clip -> player.playWhenReady = true
                    event == Lifecycle.Event.ON_PAUSE -> player.playWhenReady = false
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycle)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycle)
                player.playWhenReady = false
                playerPool.resetClip(clip)
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