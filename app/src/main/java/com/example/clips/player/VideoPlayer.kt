package com.example.clips.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.ui.compose.PlayerSurface

@Composable
fun VideoPlayer(
    modifier: Modifier,
    player: Player,
) {
    PlayerSurface(
        modifier = modifier.fillMaxSize(),
        player = player,
    )
}