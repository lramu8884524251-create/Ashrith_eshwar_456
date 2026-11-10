package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ParentPinDialog
import com.example.ui.components.ProfileHeaderChip
import com.example.ui.components.ProfileSwitcherDialog
import com.example.ui.components.ScreenTimeLockoutDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RecoveryTvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showProfileSwitcher by remember { mutableStateOf(false) }
                val activeProfile = viewModel.getActiveProfile()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TvDarkBackground
                ) {
                    if (uiState.isTvScreenMode) {
                        // 10-Foot TV Leanback Screen Mode (for when phone is plugged into TV or in display testing)
                        TvDisplayScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.safeDrawing)
                        )
                    } else {
                        // Mobile Companion App Mode (Remote Control, PlayStore, USB & Cast, Parental Controls)
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = TvDarkBackground,
                            contentWindowInsets = WindowInsets.safeDrawing,
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TvDarkBackground)
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // App Branding
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = TvCyanContainer,
                                                shape = RoundedCornerShape(10.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Tv,
                                                    contentDescription = null,
                                                    tint = TvCyanPrimary,
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(20.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "RecoveryTV",
                                                    color = TvTextPrimary,
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Smart TV Bridge & Remote",
                                                    color = TvTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        // Mode Switcher: "TV Screen" vs "Remote"
                                        Button(
                                            onClick = { viewModel.toggleTvScreenMode() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TvSurfaceElevated,
                                                contentColor = TvCyanPrimary
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("toggle_tv_mode_button")
                                        ) {
                                            Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("TV Screen Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Secondary bar: Active Profile Chip and Screen Time countdown
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        ProfileHeaderChip(
                                            profile = activeProfile,
                                            onClick = { showProfileSwitcher = true }
                                        )

                                        // Screen time pill
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (activeProfile.isTimeLimitExceeded) SafetyRedContainer else TvSurfaceElevated,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (activeProfile.isTimeLimitExceeded) SafetyRed else TvSurfaceBorder
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (activeProfile.timeLimitMinutes > 0) Icons.Default.HourglassBottom else Icons.Default.AllInclusive,
                                                    contentDescription = null,
                                                    tint = if (activeProfile.isTimeLimitExceeded) SafetyRed else TvCyanPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )

                                                Text(
                                                    text = if (activeProfile.timeLimitMinutes > 0) {
                                                        "${activeProfile.remainingSeconds / 60}m left"
                                                    } else "Unlimited",
                                                    color = if (activeProfile.isTimeLimitExceeded) SafetyRed else TvTextPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = TvSurfaceDark,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.testTag("main_navigation_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = uiState.activeNavTab == 0,
                                        onClick = { viewModel.setNavTab(0) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.activeNavTab == 0) Icons.Default.Gamepad else Icons.Outlined.Gamepad,
                                                contentDescription = "Remote"
                                            )
                                        },
                                        label = { Text("Remote", fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = TvCyanPrimary,
                                            selectedTextColor = TvCyanPrimary,
                                            unselectedIconColor = TvTextSecondary,
                                            unselectedTextColor = TvTextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_tab_remote")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.activeNavTab == 1,
                                        onClick = { viewModel.setNavTab(1) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.activeNavTab == 1) Icons.Default.Shop else Icons.Outlined.Shop,
                                                contentDescription = "PlayStore"
                                            )
                                        },
                                        label = { Text("PlayStore", fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = TvCyanPrimary,
                                            selectedTextColor = TvCyanPrimary,
                                            unselectedIconColor = TvTextSecondary,
                                            unselectedTextColor = TvTextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_tab_playstore")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.activeNavTab == 2,
                                        onClick = { viewModel.setNavTab(2) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.activeNavTab == 2) Icons.Default.Usb else Icons.Outlined.Usb,
                                                contentDescription = "USB & Cast"
                                            )
                                        },
                                        label = { Text("USB & Cast", fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = TvCyanPrimary,
                                            selectedTextColor = TvCyanPrimary,
                                            unselectedIconColor = TvTextSecondary,
                                            unselectedTextColor = TvTextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_tab_usb")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.activeNavTab == 3,
                                        onClick = { viewModel.setNavTab(3) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.activeNavTab == 3) Icons.Default.FamilyRestroom else Icons.Outlined.FamilyRestroom,
                                                contentDescription = "Parental"
                                            )
                                        },
                                        label = { Text("Parental", fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            indicatorColor = SafetyGreen,
                                            selectedTextColor = SafetyGreen,
                                            unselectedIconColor = TvTextSecondary,
                                            unselectedTextColor = TvTextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_tab_parental")
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (uiState.activeNavTab) {
                                    0 -> RemoteCompanionScreen(viewModel = viewModel, uiState = uiState)
                                    1 -> TvPlayStoreScreen(viewModel = viewModel, uiState = uiState)
                                    2 -> UsbCastHardwareScreen(viewModel = viewModel, uiState = uiState)
                                    3 -> ParentalControlsScreen(viewModel = viewModel, uiState = uiState)
                                }
                            }
                        }
                    }

                    // Global Modals: Master PIN, Lockout Dialog, Profile Switcher
                    ParentPinDialog(
                        isOpen = uiState.isPinPromptVisible,
                        errorMessage = uiState.pinErrorMessage,
                        onVerify = { viewModel.verifyPin(it) },
                        onDismiss = { viewModel.dismissPinPrompt() }
                    )

                    ScreenTimeLockoutDialog(
                        isOpen = uiState.isScreenTimeLocked || uiState.isBedtimeLocked,
                        isBedtime = uiState.isBedtimeLocked,
                        profile = activeProfile,
                        onExtendRequest = { viewModel.extendScreenTime(30) },
                        onSwitchProfile = { showProfileSwitcher = true }
                    )

                    ProfileSwitcherDialog(
                        isOpen = showProfileSwitcher,
                        profiles = uiState.profiles,
                        activeProfileId = uiState.activeProfileId,
                        onSelectProfile = { viewModel.switchProfile(it) },
                        onManageProfiles = {
                            viewModel.setNavTab(3)
                        },
                        onDismiss = { showProfileSwitcher = false }
                    )
                }
            }
        }
    }
}
