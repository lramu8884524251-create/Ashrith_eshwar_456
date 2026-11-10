package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppCategory
import com.example.data.model.AspectRatioMode
import com.example.data.model.TvApp
import com.example.ui.components.ContentRatingBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel
import com.example.ui.viewmodel.TvUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TvDisplayScreen(
    viewModel: RecoveryTvViewModel,
    uiState: TvUiState,
    modifier: Modifier = Modifier
) {
    val cal = uiState.displayCalibration
    val activeProfile = viewModel.getActiveProfile()
    val currentTimeStr = remember {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    // Outer box applying legacy CRT overscan padding margins
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TvDarkBackground)
            .padding(
                horizontal = cal.overscanPaddingHorizontal.dp,
                vertical = cal.overscanPaddingVertical.dp
            )
            .testTag("tv_display_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TV Top Leanback Bar: Clock, Active Profile, USB Status, Switch Mode Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = TvCyanContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "RECOVERY TV OS",
                            color = TvCyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = TvSurfaceDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${uiState.usbHardware.outputResolution} • ${cal.aspectRatio.label.split(" ").first()}",
                                color = TvTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Profile and Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = TvSurfaceElevated,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(activeProfile.avatarEmoji, fontSize = 14.sp)
                            Text(activeProfile.name, color = TvTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            ContentRatingBadge(rating = activeProfile.maxRating)
                        }
                    }

                    Text(
                        text = currentTimeStr,
                        color = TvCyanPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { viewModel.toggleTvScreenMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = TvCyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("tv_switch_to_remote_btn")
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mobile Remote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // TV Video Streaming Player Window
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp)),
                color = Color.Black,
                border = androidx.compose.foundation.BorderStroke(2.dp, TvCyanPrimary.copy(alpha = 0.4f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Video player background banner simulation
                    Image(
                        painter = painterResource(id = R.drawable.img_tv_hero_1792838649047),
                        contentDescription = "Video stream",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // CRT Scanlines Overlay effect if enabled
                    if (cal.crtScanlinesEnabled) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.Transparent),
                                        startY = 0f,
                                        endY = 8f
                                    )
                                )
                        )
                    }

                    // Phosphor warm overlay
                    if (cal.crtWarmthEnabled) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFFA500).copy(alpha = 0.04f))
                        )
                    }

                    // Bottom Player Controls HUD
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiState.currentStream.title,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${uiState.currentStream.sourceName} • ${uiState.currentStream.category}",
                                        color = TvCyanPrimary,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ContentRatingBadge(rating = uiState.currentStream.rating)

                                    Surface(
                                        color = Color.Red,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LIVE STREAM",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Progress Bar
                            val progressFloat = uiState.playbackPositionSec.toFloat() / uiState.playbackDurationSec.toFloat()
                            LinearProgressIndicator(
                                progress = { progressFloat.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = TvCyanPrimary,
                                trackColor = Color.DarkGray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val posMinutes = uiState.playbackPositionSec / 60
                                val posSec = uiState.playbackPositionSec % 60
                                Text(
                                    text = String.format("%02d:%02d / %s", posMinutes, posSec, uiState.currentStream.durationText),
                                    color = TvTextSecondary,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = "Remote Command: ${uiState.lastActionFeedback}",
                                    color = TvAmberAccent,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // TV Remote feedback bubble
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = uiState.lastActionFeedback,
                                color = TvTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // TV Leanback Apps Ribbon (10-Foot Navigation)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INSTALLED SMART TV APPS & CHANNELS",
                        color = TvTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Use Remote D-Pad to Navigate",
                        color = TvTextMuted,
                        fontSize = 11.sp
                    )
                }

                val installedApps = uiState.apps.filter { it.isInstalled }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(installedApps, key = { it.id }) { app ->
                        val isAllowed = app.rating.isAllowedUnder(activeProfile.maxRating)

                        Surface(
                            modifier = Modifier
                                .width(150.dp)
                                .height(95.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("tv_app_tile_${app.id}"),
                            color = TvSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (uiState.launchedApp?.id == app.id) TvCyanPrimary else TvSurfaceBorder
                            ),
                            onClick = { viewModel.launchApp(app) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(app.accentColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (app.category) {
                                                AppCategory.STREAMING -> Icons.Default.LiveTv
                                                AppCategory.KIDS -> Icons.Default.ChildCare
                                                AppCategory.GAMING -> Icons.Default.SportsEsports
                                                else -> Icons.Default.Tv
                                            },
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    ContentRatingBadge(rating = app.rating)
                                }

                                Text(
                                    text = app.name,
                                    color = TvTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = if (!isAllowed) "PIN Locked" else "Press OK to Watch",
                                    color = if (!isAllowed) SafetyRed else TvTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
