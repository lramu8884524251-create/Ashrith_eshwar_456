package com.example.data.model

/**
 * Standard content ratings for movies, series, and TV apps.
 */
enum class ContentRating(
    val code: String,
    val label: String,
    val ageThreshold: Int,
    val description: String
) {
    G("G", "All Ages / Kids", 0, "General audience, suitable for young children"),
    PG("PG", "Parental Guidance", 8, "Mild themes, suitable for most children"),
    PG_13("PG-13", "Teens 13+", 13, "Some intense scenes or language, suitable for teens"),
    TV_14("TV-14", "Teens 14+", 14, "Strong themes, parents strongly cautioned"),
    TV_MA("TV-MA", "Mature 18+", 18, "Restricted to adults, mature language and content");

    fun isAllowedUnder(maxRating: ContentRating): Boolean {
        return this.ordinal <= maxRating.ordinal
    }
}

/**
 * Family user profile types.
 */
enum class ProfileType(val label: String, val badgeColorHex: Long) {
    PARENT("Parent / Admin", 0xFF00E5FF),
    TEEN("Teen Profile", 0xFFFFB300),
    KIDS("Kids Safe Profile", 0xFF10B981)
}

/**
 * User Profile representation.
 */
data class UserProfile(
    val id: String,
    val name: String,
    val type: ProfileType,
    val avatarEmoji: String,
    val maxRating: ContentRating,
    val timeLimitMinutes: Int, // 0 means unlimited
    val timeUsedSeconds: Long = 0,
    val bedtimeEnabled: Boolean = false,
    val bedtimeHour: Int = 20, // 8 PM
    val bedtimeMinute: Int = 30,
    val isPinProtected: Boolean = false,
    val allowedCategories: Set<AppCategory> = AppCategory.values().toSet()
) {
    val isTimeLimitExceeded: Boolean
        get() = timeLimitMinutes > 0 && timeUsedSeconds >= (timeLimitMinutes * 60)

    val remainingSeconds: Long
        get() {
            if (timeLimitMinutes <= 0) return Long.MAX_VALUE
            val rem = (timeLimitMinutes * 60) - timeUsedSeconds
            return if (rem > 0) rem else 0
        }
}

/**
 * TV App Store categories.
 */
enum class AppCategory(val title: String, val iconName: String) {
    ALL("All Apps", "apps"),
    STREAMING("Streaming & Movies", "movie"),
    LIVE_TV("Live TV & News", "tv"),
    KIDS("Kids & Cartoons", "child_care"),
    GAMING("Retro Gaming", "videogame_asset"),
    UTILITIES("Tools & Players", "build"),
    MUSIC("Music & Radio", "music_note")
}

/**
 * A TV application or streaming channel in TV PlayStore.
 */
data class TvApp(
    val id: String,
    val name: String,
    val developer: String,
    val category: AppCategory,
    val rating: ContentRating,
    val ratingScore: Float,
    val downloads: String,
    val description: String,
    val sizeMb: Int,
    val isInstalled: Boolean = false,
    val isEssential: Boolean = false,
    val packageName: String,
    val streamSampleUrl: String = "",
    val accentColorHex: Long = 0xFF1E293B
)

/**
 * Physical USB and Display connection types for legacy TV recovery.
 */
enum class UsbConnectionType(val title: String, val connectorType: String, val description: String) {
    USB_TO_HDMI("USB to HDMI Adapter", "MHL / DisplayPort Alt", "For HDTVs, LCD monitors, and Projectors"),
    USB_TO_RCA("USB to RCA / AV Composite", "Composite (Yellow/Red/White)", "Universal adapter for vintage CRT Tube TVs"),
    WIRELESS_DONGLE("Wireless Display / Cast", "Miracast / Wi-Fi Direct", "Zero-cable wireless streaming receiver"),
    USB_STORAGE("USB Flash Drive / OTG", "Mass Storage Device", "Play offline video library directly from pen drive"),
    DISPLAYLINK("DisplayLink USB Hub", "External Graphics Engine", "Hardware video rendering for older phones")
}

/**
 * Real-time hardware status of USB and display video out.
 */
data class UsbHardwareState(
    val isConnected: Boolean = true,
    val activeConnection: UsbConnectionType = UsbConnectionType.USB_TO_RCA,
    val deviceName: String = "RetroCast USB-to-AV Composite Bridge v2.4",
    val bandwidthMbps: Int = 480,
    val outputResolution: String = "480i / 576i (NTSC/PAL 60Hz)",
    val signalQuality: String = "Optimal (Zero Lag)",
    val isAudioOverUsb: Boolean = true,
    val otgPowerDeliveryWatts: Int = 18
)

/**
 * Aspect Ratio modes for CRT and modern screens.
 */
enum class AspectRatioMode(val label: String, val ratioFloat: Float) {
    RATIO_4_3("4:3 Vintage Tube", 4f / 3f),
    RATIO_16_9("16:9 Widescreen", 16f / 9f),
    RATIO_FIT("Smart Fit / Zoom", 0f),
    RATIO_LETTERBOX("Pillarbox / 4:3 in 16:9", 0f)
}

/**
 * Display calibration and CRT overscan correction.
 */
data class DisplayCalibration(
    val aspectRatio: AspectRatioMode = AspectRatioMode.RATIO_4_3,
    val overscanPaddingHorizontal: Int = 16, // in Dp or percent
    val overscanPaddingVertical: Int = 12,
    val crtScanlinesEnabled: Boolean = false,
    val crtWarmthEnabled: Boolean = true,
    val sharpnessLevel: Int = 3, // 1 to 5
    val brightnessPercent: Int = 100
)

/**
 * Virtual Remote Keys.
 */
enum class RemoteKey {
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, DPAD_CENTER,
    BACK, HOME, MENU, INFO,
    VOL_UP, VOL_DOWN, MUTE,
    POWER, INPUT_SOURCE,
    PLAY_PAUSE, REWIND, FAST_FORWARD,
    NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9
}

/**
 * Stream sample item for the TV recovery player.
 */
data class StreamMediaItem(
    val id: String,
    val title: String,
    val sourceName: String,
    val category: String,
    val rating: ContentRating,
    val durationText: String,
    val description: String,
    val streamUrl: String
)
