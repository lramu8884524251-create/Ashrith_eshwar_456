package com.example.data.repository

import com.example.data.model.*

object TvRepository {

    fun getDefaultProfiles(): List<UserProfile> = listOf(
        UserProfile(
            id = "profile_parent",
            name = "Parent (Admin)",
            type = ProfileType.PARENT,
            avatarEmoji = "👑",
            maxRating = ContentRating.TV_MA,
            timeLimitMinutes = 0, // Unlimited
            timeUsedSeconds = 1200,
            bedtimeEnabled = false,
            bedtimeHour = 23,
            bedtimeMinute = 0,
            isPinProtected = true
        ),
        UserProfile(
            id = "profile_teen",
            name = "Alex (Teen)",
            type = ProfileType.TEEN,
            avatarEmoji = "🚀",
            maxRating = ContentRating.TV_14,
            timeLimitMinutes = 120, // 2 hours
            timeUsedSeconds = 2400, // 40 mins used
            bedtimeEnabled = true,
            bedtimeHour = 22, // 10:00 PM
            bedtimeMinute = 0,
            isPinProtected = false,
            allowedCategories = setOf(
                AppCategory.STREAMING,
                AppCategory.LIVE_TV,
                AppCategory.GAMING,
                AppCategory.UTILITIES,
                AppCategory.MUSIC
            )
        ),
        UserProfile(
            id = "profile_kids",
            name = "Maya (Kids Safe)",
            type = ProfileType.KIDS,
            avatarEmoji = "🧸",
            maxRating = ContentRating.G,
            timeLimitMinutes = 45, // 45 mins
            timeUsedSeconds = 1500, // 25 mins used
            bedtimeEnabled = true,
            bedtimeHour = 20, // 8:00 PM
            bedtimeMinute = 30,
            isPinProtected = false,
            allowedCategories = setOf(
                AppCategory.KIDS,
                AppCategory.UTILITIES,
                AppCategory.MUSIC
            )
        )
    )

    fun getDefaultApps(): List<TvApp> = listOf(
        TvApp(
            id = "app_smarttube",
            name = "SmartTube & YouTube TV",
            developer = "OpenTV Community",
            category = AppCategory.STREAMING,
            rating = ContentRating.G,
            ratingScore = 4.9f,
            downloads = "25M+",
            description = "Ad-free ultra HD modern video player engineered for older and smart TVs alike.",
            sizeMb = 18,
            isInstalled = true,
            isEssential = true,
            packageName = "com.teamsmart.videotube",
            streamSampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            accentColorHex = 0xFFFF0000
        ),
        TvApp(
            id = "app_pbs_kids",
            name = "PBS KIDS Safe Video",
            developer = "PBS KIDS",
            category = AppCategory.KIDS,
            rating = ContentRating.G,
            ratingScore = 4.8f,
            downloads = "10M+",
            description = "100% kid-safe cartoons, educational videos, and live broadcast with zero ads.",
            sizeMb = 24,
            isInstalled = true,
            isEssential = true,
            packageName = "org.pbskids.video",
            streamSampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            accentColorHex = 0xFF10B981
        ),
        TvApp(
            id = "app_cartoon_classics",
            name = "Retro Toons & Cartoons",
            developer = "Vintage Media Lab",
            category = AppCategory.KIDS,
            rating = ContentRating.G,
            ratingScore = 4.7f,
            downloads = "5M+",
            description = "Classic Looney Tunes, Popeye, and Fleischer cartoons perfectly color-calibrated for CRT tube TVs.",
            sizeMb = 32,
            isInstalled = true,
            isEssential = false,
            packageName = "com.retrocast.toons",
            streamSampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            accentColorHex = 0xFFF59E0B
        ),
        TvApp(
            id = "app_netflix",
            name = "Netflix for TV",
            developer = "Netflix, Inc.",
            category = AppCategory.STREAMING,
            rating = ContentRating.PG_13,
            ratingScore = 4.6f,
            downloads = "1B+",
            description = "Award-winning series, films, documentaries, and stand-up specials.",
            sizeMb = 48,
            isInstalled = true,
            packageName = "com.netflix.ninja",
            accentColorHex = 0xFFE50914
        ),
        TvApp(
            id = "app_disney_plus",
            name = "Disney+ Family",
            developer = "Disney",
            category = AppCategory.STREAMING,
            rating = ContentRating.PG,
            ratingScore = 4.7f,
            downloads = "500M+",
            description = "Endless entertainment from Disney, Pixar, Marvel, Star Wars, and National Geographic.",
            sizeMb = 42,
            isInstalled = false,
            packageName = "com.disney.disneyplus",
            accentColorHex = 0xFF113CCF
        ),
        TvApp(
            id = "app_prime_video",
            name = "Prime Video TV",
            developer = "Amazon Mobile LLC",
            category = AppCategory.STREAMING,
            rating = ContentRating.TV_14,
            ratingScore = 4.5f,
            downloads = "500M+",
            description = "Watch movies, popular TV shows, and live sports on any connected screen.",
            sizeMb = 36,
            isInstalled = false,
            packageName = "com.amazon.amazonvideo.livingroom",
            accentColorHex = 0xFF00A8E1
        ),
        TvApp(
            id = "app_pluto_tv",
            name = "Pluto TV (Free 250+ Live Channels)",
            developer = "Paramount Streaming",
            category = AppCategory.LIVE_TV,
            rating = ContentRating.PG_13,
            ratingScore = 4.6f,
            downloads = "100M+",
            description = "No subscription fee! Free 24/7 live television channels and thousands of on-demand movies.",
            sizeMb = 28,
            isInstalled = true,
            packageName = "tv.pluto.android",
            accentColorHex = 0xFFFFD700
        ),
        TvApp(
            id = "app_retroarch",
            name = "RetroArch CRT TV Console",
            developer = "Libretro",
            category = AppCategory.GAMING,
            rating = ContentRating.G,
            ratingScore = 4.8f,
            downloads = "20M+",
            description = "Turn your old CRT television into a retro arcade console (NES, SNES, Genesis, Arcade).",
            sizeMb = 85,
            isInstalled = true,
            packageName = "com.retroarch.aarch64",
            accentColorHex = 0xFF3F51B5
        ),
        TvApp(
            id = "app_vlc_tv",
            name = "VLC Media Player for TV",
            developer = "Videolabs",
            category = AppCategory.UTILITIES,
            rating = ContentRating.G,
            ratingScore = 4.7f,
            downloads = "100M+",
            description = "Plays any video format (MKV, MP4, AVI, ISO) directly from connected USB sticks or network streams.",
            sizeMb = 31,
            isInstalled = true,
            isEssential = true,
            packageName = "org.videolan.vlc",
            accentColorHex = 0xFFFF9800
        ),
        TvApp(
            id = "app_usb_explorer",
            name = "USB Flash Drive & OTG Player",
            developer = "RetroCast Tools",
            category = AppCategory.UTILITIES,
            rating = ContentRating.G,
            ratingScore = 4.9f,
            downloads = "15M+",
            description = "Browse pen drives, external hard disks, and memory cards connected via OTG cable.",
            sizeMb = 12,
            isInstalled = true,
            isEssential = true,
            packageName = "com.retrocast.usbfilemanager",
            accentColorHex = 0xFF00E5FF
        ),
        TvApp(
            id = "app_spotify_tv",
            name = "Spotify TV & Ambient Music",
            developer = "Spotify AB",
            category = AppCategory.MUSIC,
            rating = ContentRating.PG,
            ratingScore = 4.6f,
            downloads = "1B+",
            description = "Enjoy your favorite music and podcasts on the big screen with beautiful album art.",
            sizeMb = 35,
            isInstalled = true,
            packageName = "com.spotify.tv.android",
            accentColorHex = 0xFF1DB954
        ),
        TvApp(
            id = "app_tunein",
            name = "TuneIn Global Live Radio",
            developer = "TuneIn Inc",
            category = AppCategory.MUSIC,
            rating = ContentRating.G,
            ratingScore = 4.5f,
            downloads = "100M+",
            description = "100,000+ free live radio stations, news, sports, and music channels worldwide.",
            sizeMb = 26,
            isInstalled = false,
            packageName = "tunein.player",
            accentColorHex = 0xFF14D8CC
        ),
        TvApp(
            id = "app_max_tv",
            name = "Max TV Premium Cinema",
            developer = "WarnerMedia Global",
            category = AppCategory.STREAMING,
            rating = ContentRating.TV_MA,
            ratingScore = 4.4f,
            downloads = "100M+",
            description = "Exclusive blockbuster movies and series. Restricted to mature audience.",
            sizeMb = 52,
            isInstalled = false,
            packageName = "com.wbd.stream",
            accentColorHex = 0xFF7C3AED
        ),
        TvApp(
            id = "app_twitch_tv",
            name = "Twitch TV Live Gaming",
            developer = "Twitch Interactive",
            category = AppCategory.LIVE_TV,
            rating = ContentRating.TV_14,
            ratingScore = 4.5f,
            downloads = "100M+",
            description = "Watch top live gaming streams, esports broadcasts, and creator chats on TV.",
            sizeMb = 38,
            isInstalled = false,
            packageName = "tv.twitch.android.app",
            accentColorHex = 0xFF9146FF
        )
    )

    fun getDefaultStreams(): List<StreamMediaItem> = listOf(
        StreamMediaItem(
            id = "stream_1",
            title = "Big Buck Bunny 4K Remaster",
            sourceName = "Blender Open Cinema",
            category = "Animation",
            rating = ContentRating.G,
            durationText = "09:56",
            description = "Open source high bitrate showcase animated film, ideal for testing display fidelity.",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        StreamMediaItem(
            id = "stream_2",
            title = "Elephants Dream Sci-Fi Classic",
            sourceName = "Open Movie Project",
            category = "Sci-Fi",
            rating = ContentRating.PG,
            durationText = "10:53",
            description = "Cyberpunk vintage aesthetic, stunning contrast test on legacy CRT and plasma displays.",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ),
        StreamMediaItem(
            id = "stream_3",
            title = "Sintel Fantasy Adventure",
            sourceName = "Durian Project",
            category = "Adventure",
            rating = ContentRating.PG_13,
            durationText = "14:48",
            description = "Epic cinematic tale of a girl searching for a lost baby dragon across dramatic snowy peaks.",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        ),
        StreamMediaItem(
            id = "stream_4",
            title = "Tears of Steel Futuristic VFX",
            sourceName = "Mango Open VFX",
            category = "Action",
            rating = ContentRating.TV_14,
            durationText = "12:14",
            description = "Action-packed dystopian Amsterdam live-action and CGI short film.",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        )
    )
}
