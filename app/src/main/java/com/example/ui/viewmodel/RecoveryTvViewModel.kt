package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TvRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class TvUiState(
    // Mode: Handheld Remote Companion vs 10-Foot TV Display
    val isTvScreenMode: Boolean = false,
    val activeNavTab: Int = 0, // 0: Remote, 1: PlayStore, 2: USB & Cast, 3: Parental Controls

    // User Profiles & Parental Control
    val masterPin: String = "1234",
    val profiles: List<UserProfile> = TvRepository.getDefaultProfiles(),
    val activeProfileId: String = "profile_kids", // Default to kids profile to demonstrate parental controls
    val isPinPromptVisible: Boolean = false,
    val pendingActionRequiresPin: (() -> Unit)? = null,
    val pinErrorMessage: String? = null,
    val isScreenTimeLocked: Boolean = false,
    val isBedtimeLocked: Boolean = false,

    // TV Apps & PlayStore
    val apps: List<TvApp> = TvRepository.getDefaultApps(),
    val selectedCategory: AppCategory = AppCategory.ALL,
    val searchQuery: String = "",
    val installingAppId: String? = null,
    val launchedApp: TvApp? = null,

    // USB & Legacy TV Hardware Bridge
    val usbHardware: UsbHardwareState = UsbHardwareState(),
    val displayCalibration: DisplayCalibration = DisplayCalibration(),
    val localServerIp: String = "192.168.1.108:8080/stream",
    val isStreamServerRunning: Boolean = true,

    // Remote Control State
    val lastRemoteKey: RemoteKey? = null,
    val lastActionFeedback: String = "Ready to Control TV",
    val tvVolume: Int = 35,
    val isMuted: Boolean = false,
    val isTvPoweredOn: Boolean = true,
    val activeInputSource: String = "USB-AV (RCA)",
    val remoteTextInput: String = "",

    // TV Playback
    val sampleStreams: List<StreamMediaItem> = TvRepository.getDefaultStreams(),
    val currentStream: StreamMediaItem = TvRepository.getDefaultStreams().first(),
    val isStreamPlaying: Boolean = true,
    val playbackPositionSec: Int = 45,
    val playbackDurationSec: Int = 596
)

class RecoveryTvViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TvUiState())
    val uiState: StateFlow<TvUiState> = _uiState.asStateFlow()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        startScreenTimeTicker()
        startPlaybackTicker()
    }

    private fun startScreenTimeTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                checkParentalLimits()
            }
        }
    }

    private fun startPlaybackTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    if (state.isStreamPlaying && !state.isScreenTimeLocked && !state.isBedtimeLocked) {
                        val nextPos = if (state.playbackPositionSec < state.playbackDurationSec) {
                            state.playbackPositionSec + 1
                        } else {
                            0
                        }
                        state.copy(playbackPositionSec = nextPos)
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun checkParentalLimits() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        _uiState.update { state ->
            val activeProfile = state.profiles.find { it.id == state.activeProfileId }
                ?: return@update state

            // Increment time used if in active TV playback
            val updatedProfiles = state.profiles.map { profile ->
                if (profile.id == activeProfile.id && profile.timeLimitMinutes > 0) {
                    profile.copy(timeUsedSeconds = profile.timeUsedSeconds + 1)
                } else {
                    profile
                }
            }

            val updatedActive = updatedProfiles.find { it.id == activeProfile.id } ?: activeProfile

            // Check time limit
            val timeLocked = updatedActive.timeLimitMinutes > 0 &&
                    updatedActive.timeUsedSeconds >= (updatedActive.timeLimitMinutes * 60)

            // Check bedtime
            val bedtimeLocked = updatedActive.bedtimeEnabled && (
                    currentHour > updatedActive.bedtimeHour ||
                            (currentHour == updatedActive.bedtimeHour && currentMinute >= updatedActive.bedtimeMinute)
                    )

            state.copy(
                profiles = updatedProfiles,
                isScreenTimeLocked = timeLocked,
                isBedtimeLocked = bedtimeLocked
            )
        }
    }

    // --- Mode & Navigation ---
    fun toggleTvScreenMode() {
        triggerHaptic()
        _uiState.update { it.copy(isTvScreenMode = !it.isTvScreenMode) }
    }

    fun setNavTab(tabIndex: Int) {
        triggerHaptic()
        _uiState.update { it.copy(activeNavTab = tabIndex) }
    }

    // --- Remote Control Actions ---
    fun sendRemoteKey(key: RemoteKey) {
        triggerHaptic()
        _uiState.update { state ->
            var vol = state.tvVolume
            var muted = state.isMuted
            var powered = state.isTvPoweredOn
            var input = state.activeInputSource
            var isPlaying = state.isStreamPlaying

            val feedback = when (key) {
                RemoteKey.DPAD_UP -> "Navigated Up [▲]"
                RemoteKey.DPAD_DOWN -> "Navigated Down [▼]"
                RemoteKey.DPAD_LEFT -> "Navigated Left [◀]"
                RemoteKey.DPAD_RIGHT -> "Navigated Right [▶]"
                RemoteKey.DPAD_CENTER -> "Selected Item [OK]"
                RemoteKey.BACK -> "Back [⮌]"
                RemoteKey.HOME -> "TV Home Screen"
                RemoteKey.MENU -> "Opened Quick Menu"
                RemoteKey.INFO -> "Display Info: 480i NTSC 60Hz"
                RemoteKey.VOL_UP -> {
                    vol = (vol + 5).coerceAtMost(100)
                    muted = false
                    "Volume: $vol%"
                }
                RemoteKey.VOL_DOWN -> {
                    vol = (vol - 5).coerceAtLeast(0)
                    "Volume: $vol%"
                }
                RemoteKey.MUTE -> {
                    muted = !muted
                    if (muted) "Audio Muted" else "Audio Unmuted ($vol%)"
                }
                RemoteKey.POWER -> {
                    powered = !powered
                    if (powered) "TV Screen Powered On" else "TV Standby Mode"
                }
                RemoteKey.INPUT_SOURCE -> {
                    input = when (input) {
                        "USB-AV (RCA)" -> "USB-HDMI Port 1"
                        "USB-HDMI Port 1" -> "Miracast Wireless"
                        "Miracast Wireless" -> "USB Storage OTG"
                        else -> "USB-AV (RCA)"
                    }
                    "Switched Input to: $input"
                }
                RemoteKey.PLAY_PAUSE -> {
                    isPlaying = !isPlaying
                    if (isPlaying) "Playback Resumed" else "Playback Paused"
                }
                RemoteKey.REWIND -> "Rewound 15s"
                RemoteKey.FAST_FORWARD -> "Fast Forward 15s"
                else -> "Key ${key.name} sent"
            }

            state.copy(
                lastRemoteKey = key,
                lastActionFeedback = feedback,
                tvVolume = vol,
                isMuted = muted,
                isTvPoweredOn = powered,
                activeInputSource = input,
                isStreamPlaying = isPlaying
            )
        }
    }

    fun sendRemoteTextInput(text: String) {
        triggerHaptic()
        _uiState.update { it.copy(remoteTextInput = text, lastActionFeedback = "Typed on TV: \"$text\"") }
    }

    // --- TV PlayStore / App Management ---
    fun selectCategory(category: AppCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun installApp(app: TvApp) {
        triggerHaptic()
        viewModelScope.launch {
            _uiState.update { it.copy(installingAppId = app.id, lastActionFeedback = "Downloading ${app.name}...") }
            delay(1200) // Realistic installation simulation
            _uiState.update { state ->
                val updatedApps = state.apps.map {
                    if (it.id == app.id) it.copy(isInstalled = true) else it
                }
                state.copy(
                    apps = updatedApps,
                    installingAppId = null,
                    lastActionFeedback = "${app.name} installed successfully!"
                )
            }
        }
    }

    fun launchApp(app: TvApp) {
        val activeProfile = getActiveProfile()
        if (!app.rating.isAllowedUnder(activeProfile.maxRating)) {
            // Require PIN to bypass parental control
            requestPinVerification {
                executeLaunchApp(app)
            }
            return
        }

        executeLaunchApp(app)
    }

    private fun executeLaunchApp(app: TvApp) {
        triggerHaptic()
        _uiState.update { state ->
            val stream = state.sampleStreams.find { it.category.equals(app.category.name, ignoreCase = true) }
                ?: state.sampleStreams.first()
            state.copy(
                launchedApp = app,
                currentStream = stream,
                isStreamPlaying = true,
                lastActionFeedback = "Launched ${app.name} on TV"
            )
        }
    }

    fun closeLaunchedApp() {
        _uiState.update { it.copy(launchedApp = null) }
    }

    // --- Parental Controls & Profiles ---
    fun getActiveProfile(): UserProfile {
        val state = _uiState.value
        return state.profiles.find { it.id == state.activeProfileId } ?: state.profiles.first()
    }

    fun switchProfile(profileId: String) {
        val targetProfile = _uiState.value.profiles.find { it.id == profileId } ?: return
        if (targetProfile.isPinProtected || targetProfile.type == ProfileType.PARENT) {
            requestPinVerification {
                _uiState.update { it.copy(activeProfileId = profileId, lastActionFeedback = "Switched to ${targetProfile.name}") }
            }
        } else {
            _uiState.update { it.copy(activeProfileId = profileId, lastActionFeedback = "Switched to ${targetProfile.name}") }
        }
    }

    fun updateProfileRating(profileId: String, newRating: ContentRating) {
        _uiState.update { state ->
            val updated = state.profiles.map {
                if (it.id == profileId) it.copy(maxRating = newRating) else it
            }
            state.copy(profiles = updated, lastActionFeedback = "Rating updated to ${newRating.code}")
        }
    }

    fun updateProfileTimeLimit(profileId: String, minutes: Int) {
        _uiState.update { state ->
            val updated = state.profiles.map {
                if (it.id == profileId) it.copy(timeLimitMinutes = minutes, timeUsedSeconds = 0) else it
            }
            state.copy(
                profiles = updated,
                isScreenTimeLocked = false,
                lastActionFeedback = if (minutes == 0) "Unlimited viewing set" else "Time limit set to $minutes min"
            )
        }
    }

    fun updateBedtime(profileId: String, enabled: Boolean, hour: Int, minute: Int) {
        _uiState.update { state ->
            val updated = state.profiles.map {
                if (it.id == profileId) it.copy(bedtimeEnabled = enabled, bedtimeHour = hour, bedtimeMinute = minute) else it
            }
            state.copy(profiles = updated, lastActionFeedback = "Bedtime curfew updated")
        }
    }

    fun addCustomProfile(name: String, type: ProfileType, rating: ContentRating, timeLimitMin: Int) {
        val emoji = when (type) {
            ProfileType.PARENT -> "👑"
            ProfileType.TEEN -> "🎮"
            ProfileType.KIDS -> "🦄"
        }
        val newProfile = UserProfile(
            id = "profile_${System.currentTimeMillis()}",
            name = name,
            type = type,
            avatarEmoji = emoji,
            maxRating = rating,
            timeLimitMinutes = timeLimitMin,
            isPinProtected = type == ProfileType.PARENT
        )
        _uiState.update { it.copy(profiles = it.profiles + newProfile, lastActionFeedback = "Created profile $name") }
    }

    fun updateMasterPin(newPin: String) {
        if (newPin.length == 4) {
            _uiState.update { it.copy(masterPin = newPin, lastActionFeedback = "Parental Master PIN changed") }
        }
    }

    fun requestPinVerification(onSuccess: () -> Unit) {
        _uiState.update {
            it.copy(
                isPinPromptVisible = true,
                pendingActionRequiresPin = onSuccess,
                pinErrorMessage = null
            )
        }
    }

    fun verifyPin(enteredPin: String) {
        if (enteredPin == _uiState.value.masterPin) {
            val action = _uiState.value.pendingActionRequiresPin
            _uiState.update {
                it.copy(
                    isPinPromptVisible = false,
                    pendingActionRequiresPin = null,
                    pinErrorMessage = null,
                    isScreenTimeLocked = false, // Unlock on parent PIN
                    isBedtimeLocked = false
                )
            }
            action?.invoke()
        } else {
            _uiState.update { it.copy(pinErrorMessage = "Incorrect PIN. Default is 1234") }
        }
    }

    fun dismissPinPrompt() {
        _uiState.update { it.copy(isPinPromptVisible = false, pendingActionRequiresPin = null, pinErrorMessage = null) }
    }

    fun extendScreenTime(minutes: Int) {
        requestPinVerification {
            _uiState.update { state ->
                val active = state.profiles.find { it.id == state.activeProfileId }
                if (active != null) {
                    val updatedProfiles = state.profiles.map {
                        if (it.id == active.id) {
                            it.copy(timeUsedSeconds = (it.timeUsedSeconds - minutes * 60).coerceAtLeast(0))
                        } else it
                    }
                    state.copy(
                        profiles = updatedProfiles,
                        isScreenTimeLocked = false,
                        lastActionFeedback = "Extended by $minutes minutes"
                    )
                } else state
            }
        }
    }

    // --- USB & Display Calibration ---
    fun updateAspectRatio(mode: AspectRatioMode) {
        triggerHaptic()
        _uiState.update {
            it.copy(
                displayCalibration = it.displayCalibration.copy(aspectRatio = mode),
                lastActionFeedback = "Display aspect ratio: ${mode.label}"
            )
        }
    }

    fun updateOverscanMargins(horizontal: Int, vertical: Int) {
        _uiState.update {
            it.copy(
                displayCalibration = it.displayCalibration.copy(
                    overscanPaddingHorizontal = horizontal,
                    overscanPaddingVertical = vertical
                )
            )
        }
    }

    fun toggleCrtScanlines() {
        triggerHaptic()
        _uiState.update {
            val current = it.displayCalibration.crtScanlinesEnabled
            it.copy(
                displayCalibration = it.displayCalibration.copy(crtScanlinesEnabled = !current),
                lastActionFeedback = if (!current) "CRT Scanline Filter Enabled" else "CRT Scanlines Disabled"
            )
        }
    }

    fun toggleCrtWarmth() {
        triggerHaptic()
        _uiState.update {
            val current = it.displayCalibration.crtWarmthEnabled
            it.copy(
                displayCalibration = it.displayCalibration.copy(crtWarmthEnabled = !current),
                lastActionFeedback = if (!current) "CRT Tube Phosphor Warmth Active" else "Phosphor Warmth Off"
            )
        }
    }

    fun switchUsbConnectionType(type: UsbConnectionType) {
        triggerHaptic()
        _uiState.update {
            val updatedState = when (type) {
                UsbConnectionType.USB_TO_RCA -> UsbHardwareState(
                    isConnected = true,
                    activeConnection = type,
                    deviceName = "RetroCast USB-to-RCA Composite Cable (Yellow/White/Red)",
                    outputResolution = "480i / 576i CRT (NTSC/PAL)",
                    signalQuality = "Optimal (Zero Latency Analog)"
                )
                UsbConnectionType.USB_TO_HDMI -> UsbHardwareState(
                    isConnected = true,
                    activeConnection = type,
                    deviceName = "USB-C to HDMI 4K Adapter Hub",
                    outputResolution = "1080p Full HD 60Hz",
                    signalQuality = "Digital Direct Stream"
                )
                UsbConnectionType.WIRELESS_DONGLE -> UsbHardwareState(
                    isConnected = true,
                    activeConnection = type,
                    deviceName = "RetroCast Wi-Fi Display Bridge Receiver",
                    outputResolution = "720p / 1080p Miracast",
                    signalQuality = "Wireless 5GHz (12ms)"
                )
                UsbConnectionType.USB_STORAGE -> UsbHardwareState(
                    isConnected = true,
                    activeConnection = type,
                    deviceName = "Kingston 128GB USB 3.2 Pen Drive",
                    outputResolution = "Local Media Storage Mounted",
                    signalQuality = "480 MB/s Fast Read"
                )
                UsbConnectionType.DISPLAYLINK -> UsbHardwareState(
                    isConnected = true,
                    activeConnection = type,
                    deviceName = "DisplayLink DL-3900 Graphics Dock",
                    outputResolution = "1080p VGA/DVI/HDMI Out",
                    signalQuality = "Hardware Graphics Acceleration"
                )
            }
            it.copy(usbHardware = updatedState, lastActionFeedback = "Connected to ${type.title}")
        }
    }

    fun selectStream(stream: StreamMediaItem) {
        val activeProfile = getActiveProfile()
        if (!stream.rating.isAllowedUnder(activeProfile.maxRating)) {
            requestPinVerification {
                _uiState.update { it.copy(currentStream = stream, isStreamPlaying = true, playbackPositionSec = 0) }
            }
            return
        }
        triggerHaptic()
        _uiState.update { it.copy(currentStream = stream, isStreamPlaying = true, playbackPositionSec = 0) }
    }

    fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {
            // Graceful fallback if vibration not permitted or unavailable
        }
    }
}
