package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RemoteKey
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel
import com.example.ui.viewmodel.TvUiState

@Composable
fun RemoteCompanionScreen(
    viewModel: RecoveryTvViewModel,
    uiState: TvUiState,
    modifier: Modifier = Modifier
) {
    var isTouchpadMode by remember { mutableStateOf(false) }
    var showKeyboardDialog by remember { mutableStateOf(false) }
    var keyboardInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Remote Header & TV Status Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isTvPoweredOn) SafetyGreen else SafetyRed)
                    )
                    Text(
                        text = if (uiState.isTvPoweredOn) "Connected: ${uiState.activeInputSource}" else "TV Standby",
                        color = TvTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Remote mode toggle: D-Pad vs Touchpad
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TvDarkBackground)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = !isTouchpadMode,
                        onClick = { isTouchpadMode = false },
                        label = { Text("D-Pad", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Gamepad, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvCyanPrimary,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("tab_dpad_mode")
                    )

                    FilterChip(
                        selected = isTouchpadMode,
                        onClick = { isTouchpadMode = true },
                        label = { Text("Air Mouse", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvCyanPrimary,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("tab_touchpad_mode")
                    )
                }
            }
        }

        // Action Feedback HUD banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = TvCyanContainer.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = TvCyanPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = uiState.lastActionFeedback,
                    color = TvCyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Top Control Row: Power, Input Source, Mute, Keyboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteCircularButton(
                icon = Icons.Default.PowerSettingsNew,
                label = "Power",
                tint = if (uiState.isTvPoweredOn) SafetyRed else SafetyGreen,
                onClick = { viewModel.sendRemoteKey(RemoteKey.POWER) },
                testTag = "remote_btn_power"
            )

            RemoteCircularButton(
                icon = Icons.Default.Input,
                label = "Source",
                tint = TvAmberAccent,
                onClick = { viewModel.sendRemoteKey(RemoteKey.INPUT_SOURCE) },
                testTag = "remote_btn_input"
            )

            RemoteCircularButton(
                icon = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeMute,
                label = if (uiState.isMuted) "Unmute" else "Mute",
                tint = if (uiState.isMuted) SafetyRed else TvTextPrimary,
                onClick = { viewModel.sendRemoteKey(RemoteKey.MUTE) },
                testTag = "remote_btn_mute"
            )

            RemoteCircularButton(
                icon = Icons.Default.Keyboard,
                label = "Type",
                tint = TvCyanPrimary,
                onClick = { showKeyboardDialog = true },
                testTag = "remote_btn_keyboard"
            )
        }

        // Main Controller Body: D-Pad or Air Mouse Touchpad
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .shadow(8.dp, RoundedCornerShape(28.dp)),
            color = RemoteBodyColor,
            border = androidx.compose.foundation.BorderStroke(2.dp, TvSurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isTouchpadMode) {
                    // Tactile D-PAD Controller
                    DPadController(
                        onKey = { viewModel.sendRemoteKey(it) }
                    )
                } else {
                    // Air Mouse / Gesture Touchpad
                    TouchpadController(
                        onSwipe = { dx, dy ->
                            val key = if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                if (dx > 20) RemoteKey.DPAD_RIGHT else if (dx < -20) RemoteKey.DPAD_LEFT else null
                            } else {
                                if (dy > 20) RemoteKey.DPAD_DOWN else if (dy < -20) RemoteKey.DPAD_UP else null
                            }
                            key?.let { viewModel.sendRemoteKey(it) }
                        },
                        onTap = { viewModel.sendRemoteKey(RemoteKey.DPAD_CENTER) }
                    )
                }

                // Essential Navigation row: BACK, HOME, MENU
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteBarButton(
                        icon = Icons.Rounded.ArrowBack,
                        label = "BACK",
                        onClick = { viewModel.sendRemoteKey(RemoteKey.BACK) },
                        testTag = "remote_btn_back"
                    )

                    RemoteBarButton(
                        icon = Icons.Rounded.Home,
                        label = "HOME",
                        isPrimary = true,
                        onClick = { viewModel.sendRemoteKey(RemoteKey.HOME) },
                        testTag = "remote_btn_home"
                    )

                    RemoteBarButton(
                        icon = Icons.Rounded.Menu,
                        label = "MENU",
                        onClick = { viewModel.sendRemoteKey(RemoteKey.MENU) },
                        testTag = "remote_btn_menu"
                    )
                }
            }
        }

        // Secondary Controls: Volume Rocker & Playback
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Volume Column
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp)),
                color = TvSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("VOLUME", color = TvTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    IconButton(
                        onClick = { viewModel.sendRemoteKey(RemoteKey.VOL_UP) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(RemoteButtonColor, CircleShape)
                            .testTag("remote_vol_up")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Vol Up", tint = TvCyanPrimary)
                    }

                    Text(
                        text = if (uiState.isMuted) "MUTED" else "${uiState.tvVolume}%",
                        color = if (uiState.isMuted) SafetyRed else TvTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { viewModel.sendRemoteKey(RemoteKey.VOL_DOWN) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(RemoteButtonColor, CircleShape)
                            .testTag("remote_vol_down")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Vol Down", tint = TvCyanPrimary)
                    }
                }
            }

            // Playback Controls Column
            Surface(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(20.dp)),
                color = TvSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("MEDIA PLAYBACK", color = TvTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.sendRemoteKey(RemoteKey.REWIND) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(RemoteButtonColor, CircleShape)
                                .testTag("remote_btn_rewind")
                        ) {
                            Icon(Icons.Default.FastRewind, contentDescription = "Rewind", tint = TvTextPrimary)
                        }

                        IconButton(
                            onClick = { viewModel.sendRemoteKey(RemoteKey.PLAY_PAUSE) },
                            modifier = Modifier
                                .size(56.dp)
                                .background(TvCyanPrimary, CircleShape)
                                .testTag("remote_btn_play_pause")
                        ) {
                            Icon(
                                imageVector = if (uiState.isStreamPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.sendRemoteKey(RemoteKey.FAST_FORWARD) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(RemoteButtonColor, CircleShape)
                                .testTag("remote_btn_ff")
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = "Fast Forward", tint = TvTextPrimary)
                        }
                    }

                    Text(
                        text = uiState.currentStream.title,
                        color = TvTextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Quick Launch Streaming Apps Row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "QUICK TV APP LAUNCHERS",
                color = TvTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAppLauncherButton(
                    title = "YouTube TV",
                    color = Color(0xFFFF0000),
                    onClick = {
                        val app = uiState.apps.find { it.id == "app_smarttube" }
                        app?.let { viewModel.launchApp(it) }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "quick_app_youtube"
                )

                QuickAppLauncherButton(
                    title = "Netflix",
                    color = Color(0xFFE50914),
                    onClick = {
                        val app = uiState.apps.find { it.id == "app_netflix" }
                        app?.let { viewModel.launchApp(it) }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "quick_app_netflix"
                )

                QuickAppLauncherButton(
                    title = "Kids Toons",
                    color = SafetyGreen,
                    onClick = {
                        val app = uiState.apps.find { it.id == "app_cartoon_classics" }
                        app?.let { viewModel.launchApp(it) }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "quick_app_toons"
                )

                QuickAppLauncherButton(
                    title = "USB Media",
                    color = TvCyanPrimary,
                    onClick = {
                        val app = uiState.apps.find { it.id == "app_usb_explorer" }
                        app?.let { viewModel.launchApp(it) }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "quick_app_usb"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Keyboard Input Dialog
    if (showKeyboardDialog) {
        AlertDialog(
            onDismissRequest = { showKeyboardDialog = false },
            title = { Text("Type on TV Screen", color = TvTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Send search queries, URLs, or movie titles directly to your TV.", color = TvTextSecondary, fontSize = 13.sp)
                    OutlinedTextField(
                        value = keyboardInputText,
                        onValueChange = { keyboardInputText = it },
                        placeholder = { Text("Enter text or video search...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("remote_keyboard_textfield")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendRemoteTextInput(keyboardInputText)
                        keyboardInputText = ""
                        showKeyboardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TvCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.testTag("remote_keyboard_submit")
                ) {
                    Text("Send to TV")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKeyboardDialog = false }) {
                    Text("Cancel", color = TvTextSecondary)
                }
            },
            containerColor = TvSurfaceDark
        )
    }
}

/**
 * Circular Physical Remote Button.
 */
@Composable
private fun RemoteCircularButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(RemoteButtonColor)
                .border(1.dp, TvSurfaceBorder, CircleShape)
                .testTag(testTag)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Text(text = label, color = TvTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Horizontal Pill Bar button for BACK / HOME / MENU.
 */
@Composable
private fun RemoteBarButton(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) TvCyanContainer else RemoteButtonColor,
            contentColor = if (isPrimary) TvCyanPrimary else TvTextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPrimary) TvCyanPrimary.copy(alpha = 0.5f) else TvSurfaceBorder),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Precision D-Pad Controller with round directional rings and center OK button.
 */
@Composable
private fun DPadController(
    onKey: (RemoteKey) -> Unit
) {
    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(RemoteDpadColor)
            .border(2.dp, TvSurfaceBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // UP
        IconButton(
            onClick = { onKey(RemoteKey.DPAD_UP) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .size(56.dp)
                .testTag("dpad_up")
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = TvCyanPrimary, modifier = Modifier.size(32.dp))
        }

        // DOWN
        IconButton(
            onClick = { onKey(RemoteKey.DPAD_DOWN) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .size(56.dp)
                .testTag("dpad_down")
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = TvCyanPrimary, modifier = Modifier.size(32.dp))
        }

        // LEFT
        IconButton(
            onClick = { onKey(RemoteKey.DPAD_LEFT) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(56.dp)
                .testTag("dpad_left")
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = TvCyanPrimary, modifier = Modifier.size(32.dp))
        }

        // RIGHT
        IconButton(
            onClick = { onKey(RemoteKey.DPAD_RIGHT) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .size(56.dp)
                .testTag("dpad_right")
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = TvCyanPrimary, modifier = Modifier.size(32.dp))
        }

        // CENTER OK / SELECT
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TvCyanContainer, RemoteButtonColor)
                    )
                )
                .border(2.dp, TvCyanPrimary.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { onKey(RemoteKey.DPAD_CENTER) },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dpad_ok")
            ) {
                Text(
                    text = "OK",
                    color = TvCyanPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * Air Mouse gesture touchpad.
 */
@Composable
private fun TouchpadController(
    onSwipe: (Float, Float) -> Unit,
    onTap: () -> Unit
) {
    var dragAccumX by remember { mutableFloatStateOf(0f) }
    var dragAccumY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TvDarkBackground)
            .border(1.dp, TvCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragAccumX = 0f
                        dragAccumY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragAccumX += dragAmount.x
                        dragAccumY += dragAmount.y
                        if (kotlin.math.abs(dragAccumX) > 40 || kotlin.math.abs(dragAccumY) > 40) {
                            onSwipe(dragAccumX, dragAccumY)
                            dragAccumX = 0f
                            dragAccumY = 0f
                        }
                    }
                )
            }
            .testTag("air_mouse_touchpad"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PanTool,
                contentDescription = null,
                tint = TvCyanPrimary.copy(alpha = 0.7f),
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "Swipe to Pan Cursor • Tap for Click",
                color = TvTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Air Mouse Active",
                color = SafetyGreen,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Quick Launch buttons for major TV streaming apps.
 */
@Composable
private fun QuickAppLauncherButton(
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .testTag(testTag),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
            Text(
                text = title,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
