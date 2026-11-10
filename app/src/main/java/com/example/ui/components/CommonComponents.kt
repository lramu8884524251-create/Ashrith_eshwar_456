package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ContentRating
import com.example.data.model.ProfileType
import com.example.data.model.UserProfile
import com.example.ui.theme.*

/**
 * Visual badge for Content Rating (G, PG, PG-13, TV-14, TV-MA).
 */
@Composable
fun ContentRatingBadge(
    rating: ContentRating,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (rating) {
        ContentRating.G -> SafetyGreen.copy(alpha = 0.2f) to SafetyGreen
        ContentRating.PG -> TvAmberAccent.copy(alpha = 0.2f) to TvAmberAccent
        ContentRating.PG_13 -> Color(0xFFFB923C).copy(alpha = 0.2f) to Color(0xFFFB923C)
        ContentRating.TV_14 -> SafetyYellow.copy(alpha = 0.2f) to SafetyYellow
        ContentRating.TV_MA -> SafetyRed.copy(alpha = 0.2f) to SafetyRed
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rating.code,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Profile header chip showing current user and rating gate.
 */
@Composable
fun ProfileHeaderChip(
    profile: UserProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("profile_header_chip"),
        color = TvSurfaceElevated,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (profile.type) {
                            ProfileType.PARENT -> TvCyanContainer
                            ProfileType.TEEN -> TvAmberContainer
                            ProfileType.KIDS -> SafetyGreenContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = profile.avatarEmoji, fontSize = 16.sp)
            }

            Column {
                Text(
                    text = profile.name,
                    color = TvTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${profile.type.label} • Max: ${profile.maxRating.code}",
                    color = TvTextSecondary,
                    fontSize = 10.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Switch Profile",
                tint = TvTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Master PIN verification modal dialog.
 */
@Composable
fun ParentPinDialog(
    isOpen: Boolean,
    errorMessage: String?,
    onVerify: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var pinText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("parent_pin_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(TvCyanContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Parent Lock",
                        tint = TvCyanPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Parental Master PIN",
                    color = TvTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter the 4-digit Parent PIN to authorize this action or bypass content restrictions.",
                    color = TvTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    placeholder = { Text("••••", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .testTag("pin_input_field"),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        letterSpacing = 8.sp,
                        color = TvCyanPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvCyanPrimary,
                        unfocusedBorderColor = TvSurfaceBorder,
                        focusedContainerColor = TvDarkBackground,
                        unfocusedContainerColor = TvDarkBackground
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = SafetyRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Hint: Default master PIN is 1234",
                        color = TvTextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("pin_cancel_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = TvTextSecondary)
                    }

                    Button(
                        onClick = { onVerify(pinText) },
                        enabled = pinText.length == 4,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("pin_confirm_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TvCyanPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Screen Time or Bedtime lockout overlay.
 */
@Composable
fun ScreenTimeLockoutDialog(
    isOpen: Boolean,
    isBedtime: Boolean,
    profile: UserProfile,
    onExtendRequest: () -> Unit,
    onSwitchProfile: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = { /* Modal must be addressed */ }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SafetyYellow.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("lockout_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SafetyYellow.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBedtime) Icons.Default.Bedtime else Icons.Default.Timer,
                        contentDescription = "Lockout",
                        tint = SafetyYellow,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = if (isBedtime) "Bedtime Curfew Active" else "Screen Time Limit Reached!",
                    color = TvTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isBedtime) {
                        "TV viewing is paused for ${profile.name} past scheduled bedtime (${String.format("%02d:%02d", profile.bedtimeHour, profile.bedtimeMinute)}). Time for sleep and rest!"
                    } else {
                        "Great job taking a break! ${profile.name} has used all ${profile.timeLimitMinutes} minutes of daily TV time. Ask a parent to extend time."
                    },
                    color = TvTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Button(
                    onClick = onExtendRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("extend_time_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvCyanPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Parent PIN: Extend Time +30m", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSwitchProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("switch_profile_from_lockout"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Switch to Adult / Parent Profile", color = TvTextPrimary)
                }
            }
        }
    }
}

/**
 * Profile Switcher Modal Sheet / Dialog.
 */
@Composable
fun ProfileSwitcherDialog(
    isOpen: Boolean,
    profiles: List<UserProfile>,
    activeProfileId: String,
    onSelectProfile: (String) -> Unit,
    onManageProfiles: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = TvSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("profile_switcher_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Who is Watching?",
                        color = TvTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    profiles.forEach { profile ->
                        val isSelected = profile.id == activeProfileId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectProfile(profile.id)
                                    onDismiss()
                                }
                                .testTag("select_profile_${profile.id}"),
                            color = if (isSelected) TvCyanContainer.copy(alpha = 0.4f) else TvSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TvCyanPrimary else TvSurfaceBorder
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
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
                                        Text(
                                            text = profile.name,
                                            color = TvTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        ContentRatingBadge(rating = profile.maxRating)
                                    }

                                    Text(
                                        text = if (profile.timeLimitMinutes > 0) {
                                            "Daily limit: ${profile.timeLimitMinutes}m (${(profile.remainingSeconds / 60)}m left)"
                                        } else {
                                            "Unlimited viewing access"
                                        },
                                        color = TvTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                if (profile.isPinProtected || profile.type == ProfileType.PARENT) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "PIN Protected",
                                        tint = TvCyanPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = TvCyanPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        onManageProfiles()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("manage_parental_controls_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TvSurfaceElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.SettingsSuggest, contentDescription = null, tint = TvCyanPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Parental Controls", color = TvTextPrimary)
                }
            }
        }
    }
}
