package com.example.clips.player

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import java.io.File
import okhttp3.OkHttpClient

@UnstableApi
class PlayerPool(
    private val context: Context,
    poolSize: Int = 3,
    private val cacheSize: Long = 100L,
) {

    private val database by lazy { StandaloneDatabaseProvider(context) }
    private val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, "media")
        if(cacheDir.exists().not()) cacheDir.mkdirs()
        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(cacheSize * 1024 * 1024),
            database,
        )
    }
    private val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()
    private val upstreamDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
    private val mediaSourceFactory: DefaultMediaSourceFactory by lazy {
        DefaultMediaSourceFactory(cacheDataSourceFactory)
    }
    private val players: List<Player> = List(poolSize) {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    fun getPlayerForClips(clip: Int) = players[clip % players.size]

    fun resetClip(clip: Int) {
        val player = players[clip % players.size]
        player.stop()
        player.clearMediaItems()
    }

    fun releaseAll() {
        players.forEach {
            it.stop()
            it.release()
        }
        cache.release()
    }
}