package com.example.clips.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW

@Composable
fun VideoPlayer(
    modifier: Modifier,
    player: Player,
) {
    PlayerSurface(
        modifier = modifier.fillMaxSize(),
        player = player,
        surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
    )
}