# 🎬 Clips - Jetpack Compose Video Feed

<div align="center">
  <p><b>A high-performance Reels-style video scrolling engine built with Jetpack Compose and Media3.</b></p>

  [![Kotlin](https://img.shields.io/badge/kotlin-v2.0.0-blue.svg)](https://kotlinlang.org)
  [![Compose](https://img.shields.io/badge/Jetpack_Compose-v1.7.0-green.svg)](https://developer.android.com/jetpack/compose)
  [![Media3](https://img.shields.io/badge/Media3-ExoPlayer-orange.svg)](https://developer.android.com/guide/topics/media/media3)
</div>

---

## 📺 Project Preview


https://github.com/user-attachments/assets/60037362-f75f-4b9a-b592-da01133e935a


---

## ✨ Key Features

- 🔄 **Player Pooling**: Intelligent recycling of `ExoPlayer` instances to maintain a low memory footprint.
- 🏎️ **Disk Caching**: Integrated `SimpleCache` with LRU eviction for lightning-fast subsequent playbacks.
- 📏 **Vertical Pager**: Seamless full-screen snapping using Compose `VerticalPager`.
- 🔋 **Lifecycle Aware**: Intelligent pausing/resuming tied to Activity and Pager states to save battery.
- 🌐 **Network Optimized**: Powered by `OkHttp` data sources for robust media streaming.

---

## 🏗️ Code Structure

The architecture is divided into clear concerns for scalability:

```text
com.example.clips
├── 📱 MainActivity.kt          # Orchestrates the Pager and Lifecycle events
├── 🎥 player
│   ├── PlayerPool.kt           # The engine: Manages Player reuse & Cache setup
│   ├── VideoPlayer.kt          # UI: The Media3 PlayerSurface wrapper
│   └── GetVideosUsecase.kt     # Data: Repository providing the video URLs
└── 🎨 ui.theme                 # Styling: Material3 theme configuration
```

## 🛠️ Implementation Details

**1. The Player Pool Engine** Instead of allocating a player per video, we use a circular pool. For a pool size of 4, video index 5 uses player[1].

**2. Caching Strategy** We utilize SimpleCache to ensure that once a video starts loading, chunks are saved to the internal cache directory.
Default Cache: 100MB.
Eviction Strategy: Least Recently Used (LRU).

**3. Smart Playback Logic** Using snapshotFlow, the app monitors the settledPage. It only prepares and plays the video when the user has finished scrolling to that specific page.

## 🚀 How to Run

Clone the project

```bash
git clone https://github.com/yourusername/clips-media3.git

implementation("androidx.media3:media3-exoplayer:1.4.0")
implementation("androidx.media3:media3-ui-compose:1.4.0")
implementation("androidx.media3:media3-datasource-okhttp:1.4.0")
```
