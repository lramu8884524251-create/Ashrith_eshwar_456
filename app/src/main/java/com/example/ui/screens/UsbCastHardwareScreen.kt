package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatioMode
import com.example.data.model.UsbConnectionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel
import com.example.ui.viewmodel.TvUiState

@Composable
fun UsbCastHardwareScreen(
    viewModel: RecoveryTvViewModel,
    uiState: TvUiState,
    modifier: Modifier = Modifier
) {
    val cal = uiState.displayCalibration
    val usb = uiState.usbHardware

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // USB Connection Telemetry Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TvCyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(24.dp))
                        }

                        Column {
                            Text("USB Hardware Bridge", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(usb.deviceName, color = TvTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        color = SafetyGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SafetyGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SafetyGreen))
                            Text("LIVE PLUGGED", color = SafetyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider(color = TvSurfaceBorder)

                // Telemetry Specs Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    HardwareStatPill("Output Mode", usb.outputResolution)
                    HardwareStatPill("Latency", "Zero Lag (Direct)")
                    HardwareStatPill("USB Audio DAC", if (usb.isAudioOverUsb) "Enabled" else "Standard")
                }

                // Adapter Selectors
                Text("Select Connected Hardware Adapter:", color = TvTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UsbConnectionType.values().forEach { connType ->
                        val isSelected = usb.activeConnection == connType
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("usb_adapter_${connType.name}"),
                            color = if (isSelected) TvCyanContainer.copy(alpha = 0.4f) else TvSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TvCyanPrimary else TvSurfaceBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            onClick = { viewModel.switchUsbConnectionType(connType) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.switchUsbConnectionType(connType) },
                                    colors = RadioButtonDefaults.colors(selectedColor = TvCyanPrimary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(connType.title, color = TvTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(connType.description, color = TvTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Universal Display Calibration (CRT Tube & Overscan Correction)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = TvAmberAccent, modifier = Modifier.size(24.dp))
                    Column {
                        Text("Legacy Display & CRT Calibration", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Correct vintage tube stretch, overscan borders, and scanlines.", color = TvTextSecondary, fontSize = 12.sp)
                    }
                }

                Divider(color = TvSurfaceBorder)

                // Aspect Ratio Selector
                Text("Aspect Ratio:", color = TvTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AspectRatioMode.values().forEach { mode ->
                        val isSelected = cal.aspectRatio == mode
                        Button(
                            onClick = { viewModel.updateAspectRatio(mode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TvCyanPrimary else TvSurfaceElevated,
                                contentColor = if (isSelected) Color.Black else TvTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_aspect_${mode.name}")
                        ) {
                            Text(mode.label.split(" ").first(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Overscan Correction Slider
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CRT Horizontal Overscan Margin:", color = TvTextSecondary, fontSize = 12.sp)
                        Text("${cal.overscanPaddingHorizontal} dp", color = TvCyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = cal.overscanPaddingHorizontal.toFloat(),
                        onValueChange = { viewModel.updateOverscanMargins(it.toInt(), cal.overscanPaddingVertical) },
                        valueRange = 0f..48f,
                        colors = SliderDefaults.colors(
                            thumbColor = TvCyanPrimary,
                            activeTrackColor = TvCyanPrimary,
                            inactiveTrackColor = TvSurfaceBorder
                        ),
                        modifier = Modifier.testTag("slider_overscan_h")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CRT Vertical Overscan Margin:", color = TvTextSecondary, fontSize = 12.sp)
                        Text("${cal.overscanPaddingVertical} dp", color = TvCyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = cal.overscanPaddingVertical.toFloat(),
                        onValueChange = { viewModel.updateOverscanMargins(cal.overscanPaddingHorizontal, it.toInt()) },
                        valueRange = 0f..48f,
                        colors = SliderDefaults.colors(
                            thumbColor = TvCyanPrimary,
                            activeTrackColor = TvCyanPrimary,
                            inactiveTrackColor = TvSurfaceBorder
                        ),
                        modifier = Modifier.testTag("slider_overscan_v")
                    )
                }

                // Scanline & Phosphor Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = cal.crtScanlinesEnabled,
                        onClick = { viewModel.toggleCrtScanlines() },
                        label = { Text("CRT Scanlines", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvAmberAccent,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("toggle_scanlines")
                    )

                    FilterChip(
                        selected = cal.crtWarmthEnabled,
                        onClick = { viewModel.toggleCrtWarmth() },
                        label = { Text("Phosphor Glow", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvAmberAccent,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("toggle_crt_warmth")
                    )
                }
            }
        }

        // Wireless Cast Stream Server Card (Zero Subscription)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CastConnected, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(24.dp))
                    Column {
                        Text("Wireless Cast & Streaming Server", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("No subscription fees. Plug & play local streaming.", color = TvTextSecondary, fontSize = 12.sp)
                    }
                }

                Surface(
                    color = TvDarkBackground,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Local Stream URL:", color = TvTextSecondary, fontSize = 11.sp)
                            Text(
                                text = "http://${uiState.localServerIp}",
                                color = TvCyanPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.triggerHaptic() },
                            colors = ButtonDefaults.buttonColors(containerColor = TvCyanContainer),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Stream", color = TvCyanPrimary, fontSize = 11.sp)
                        }
                    }
                }

                Text(
                    text = "Any display device or TV browser connected to your Wi-Fi or USB tether can instantly receive this feed in full resolution.",
                    color = TvTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HardwareStatPill(title: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TvDarkBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = TvTextMuted, fontSize = 10.sp)
        Text(text = value, color = TvTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
