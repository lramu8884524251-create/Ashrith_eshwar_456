package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ContentRating
import com.example.data.model.ProfileType
import com.example.data.model.UserProfile
import com.example.ui.components.ContentRatingBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel
import com.example.ui.viewmodel.TvUiState

@Composable
fun ParentalControlsScreen(
    viewModel: RecoveryTvViewModel,
    uiState: TvUiState,
    modifier: Modifier = Modifier
) {
    val activeProfile = viewModel.getActiveProfile()

    var showAddProfileDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    // New profile form state
    var newProfileName by remember { mutableStateOf("") }
    var newProfileType by remember { mutableStateOf(ProfileType.KIDS) }
    var newProfileRating by remember { mutableStateOf(ContentRating.G) }
    var newProfileTimeLimit by remember { mutableStateOf(60) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Family Safety Hero Graphic
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SafetyGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_family_safety_1792838662864),
                    contentDescription = "Family Safety",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = SafetyGreen,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "FAMILY SAFE TV CONTROLS",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "Content Restrictions & Time Limits",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Protect children with ratings gates, bedtime curfews, and PIN security.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Master PIN Status & Management
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = TvSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TvCyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text("Master Parental PIN", color = TvTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Active PIN: •••• (Default 1234)", color = TvTextSecondary, fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { showChangePinDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("btn_change_pin")
                    ) {
                        Text("Change PIN", color = TvCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Profile Viewing Time Limits & Curfew
        item {
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
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(22.dp))
                        Column {
                            Text("Daily Viewing Time Limit", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Configuring for: ${activeProfile.name}", color = TvTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Divider(color = TvSurfaceBorder)

                    // Current Usage Progress Bar
                    val usedMins = (activeProfile.timeUsedSeconds / 60).toInt()
                    val totalLimitMins = activeProfile.timeLimitMinutes
                    val progress = if (totalLimitMins > 0) {
                        (usedMins.toFloat() / totalLimitMins.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Today's Screen Time:", color = TvTextSecondary, fontSize = 12.sp)
                            Text(
                                text = if (totalLimitMins > 0) "$usedMins / $totalLimitMins min" else "$usedMins min (Unlimited)",
                                color = if (progress >= 1f) SafetyRed else TvCyanPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progress >= 1f) SafetyRed else TvCyanPrimary,
                            trackColor = TvSurfaceBorder
                        )
                    }

                    // Preset Time Limit Buttons
                    Text("Select Allowed Daily Limit:", color = TvTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(30 to "30m", 45 to "45m", 60 to "1h", 120 to "2h", 0 to "No Limit").forEach { (mins, label) ->
                            val isSelected = activeProfile.timeLimitMinutes == mins
                            Button(
                                onClick = { viewModel.updateProfileTimeLimit(activeProfile.id, mins) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) TvCyanPrimary else TvSurfaceElevated,
                                    contentColor = if (isSelected) Color.Black else TvTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("time_limit_btn_$mins")
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Divider(color = TvSurfaceBorder)

                    // Bedtime Curfew Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = TvAmberAccent, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Scheduled Bedtime Curfew", color = TvTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (activeProfile.bedtimeEnabled) {
                                        "TV locks at ${String.format("%02d:%02d", activeProfile.bedtimeHour, activeProfile.bedtimeMinute)}"
                                    } else "Disabled",
                                    color = TvTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = activeProfile.bedtimeEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.updateBedtime(
                                    activeProfile.id,
                                    isChecked,
                                    activeProfile.bedtimeHour,
                                    activeProfile.bedtimeMinute
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = TvAmberAccent, checkedTrackColor = TvAmberContainer),
                            modifier = Modifier.testTag("switch_bedtime_curfew")
                        )
                    }
                }
            }
        }

        // Content Rating Restriction Matrix
        item {
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
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(22.dp))
                        Column {
                            Text("Content Rating Restrictions", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Maximum rating permitted for: ${activeProfile.name}", color = TvTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Divider(color = TvSurfaceBorder)

                    ContentRating.values().forEach { rating ->
                        val isSelected = activeProfile.maxRating == rating
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("rating_option_${rating.code}"),
                            color = if (isSelected) SafetyGreenContainer.copy(alpha = 0.35f) else TvSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SafetyGreen else TvSurfaceBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            onClick = { viewModel.updateProfileRating(activeProfile.id, rating) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateProfileRating(activeProfile.id, rating) },
                                    colors = RadioButtonDefaults.colors(selectedColor = SafetyGreen)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(rating.code, color = TvTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(rating.label, color = TvCyanPrimary, fontSize = 12.sp)
                                    }
                                    Text(rating.description, color = TvTextSecondary, fontSize = 11.sp)
                                }

                                ContentRatingBadge(rating = rating)
                            }
                        }
                    }
                }
            }
        }

        // Family Profiles Management List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Family User Profiles", color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Button(
                    onClick = { showAddProfileDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TvCyanContainer),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_profile")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Profile", color = TvCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(uiState.profiles, key = { it.id }) { profile ->
            val isActive = profile.id == uiState.activeProfileId
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("profile_card_${profile.id}"),
                color = if (isActive) TvCyanContainer.copy(alpha = 0.25f) else TvSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isActive) TvCyanPrimary else TvSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TvDarkBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = profile.avatarEmoji, fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(profile.name, color = TvTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            ContentRatingBadge(rating = profile.maxRating)
                        }

                        Text(
                            text = if (profile.timeLimitMinutes > 0) "${profile.timeLimitMinutes} min limit" else "Unlimited time",
                            color = TvTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (isActive) {
                        Surface(
                            color = TvCyanPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.switchProfile(profile.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_switch_to_${profile.id}")
                        ) {
                            Text("Switch", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Change Master PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Change Master Parental PIN", color = TvTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a new 4-digit PIN for parent authorization:", color = TvTextSecondary, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinInput = it },
                        placeholder = { Text("4 digits (e.g. 1234)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_pin_textfield")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMasterPin(newPinInput)
                        newPinInput = ""
                        showChangePinDialog = false
                    },
                    enabled = newPinInput.length == 4,
                    colors = ButtonDefaults.buttonColors(containerColor = TvCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.testTag("save_new_pin_btn")
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("Cancel", color = TvTextSecondary)
                }
            },
            containerColor = TvSurfaceDark
        )
    }

    // Add Profile Dialog
    if (showAddProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAddProfileDialog = false },
            title = { Text("Create New Family Profile", color = TvTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Profile Name") },
                        placeholder = { Text("e.g. Liam (Toddler)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_profile_name_field")
                    )

                    Text("Profile Type:", color = TvTextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProfileType.values().forEach { pType ->
                            FilterChip(
                                selected = newProfileType == pType,
                                onClick = {
                                    newProfileType = pType
                                    newProfileRating = when (pType) {
                                        ProfileType.KIDS -> ContentRating.G
                                        ProfileType.TEEN -> ContentRating.TV_14
                                        ProfileType.PARENT -> ContentRating.TV_MA
                                    }
                                },
                                label = { Text(pType.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text("Max Rating:", color = TvTextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ContentRating.values().forEach { r ->
                            FilterChip(
                                selected = newProfileRating == r,
                                onClick = { newProfileRating = r },
                                label = { Text(r.code, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProfileName.isNotBlank()) {
                            viewModel.addCustomProfile(
                                name = newProfileName,
                                type = newProfileType,
                                rating = newProfileRating,
                                timeLimitMin = newProfileTimeLimit
                            )
                            newProfileName = ""
                            showAddProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TvCyanPrimary, contentColor = Color.Black),
                    modifier = Modifier.testTag("save_profile_btn")
                ) {
                    Text("Create Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProfileDialog = false }) {
                    Text("Cancel", color = TvTextSecondary)
                }
            },
            containerColor = TvSurfaceDark
        )
    }
}
