package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
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
import com.example.data.model.TvApp
import com.example.ui.components.ContentRatingBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecoveryTvViewModel
import com.example.ui.viewmodel.TvUiState

@Composable
fun TvPlayStoreScreen(
    viewModel: RecoveryTvViewModel,
    uiState: TvUiState,
    modifier: Modifier = Modifier
) {
    val activeProfile = viewModel.getActiveProfile()
    var appDetailModal by remember { mutableStateOf<TvApp?>(null) }

    // Filter apps by category, search query, and identify parental restrictions
    val filteredApps = remember(uiState.apps, uiState.selectedCategory, uiState.searchQuery) {
        uiState.apps.filter { app ->
            val matchesCategory = uiState.selectedCategory == AppCategory.ALL || app.category == uiState.selectedCategory
            val matchesSearch = uiState.searchQuery.isEmpty() ||
                    app.name.contains(uiState.searchQuery, ignoreCase = true) ||
                    app.developer.contains(uiState.searchQuery, ignoreCase = true) ||
                    app.description.contains(uiState.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Hero Store Banner with Generated Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, TvSurfaceBorder, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_tv_hero_1792838649047),
                    contentDescription = "TV Store Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, TvDarkBackground.copy(alpha = 0.92f)),
                                startY = 60f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = TvCyanPrimary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "TV PLAYSTORE • FREE & PLUG-AND-PLAY",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "Vast Library of Streaming Apps",
                        color = TvTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Optimized for older CRT tubes, LCDs, and USB streaming dongles.",
                        color = TvTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Active Parental Rating Guard Notice
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (activeProfile.type.name == "KIDS") SafetyGreenContainer.copy(alpha = 0.35f) else TvSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (activeProfile.type.name == "KIDS") SafetyGreen.copy(alpha = 0.5f) else TvSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (activeProfile.type.name == "KIDS") Icons.Default.Shield else Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = if (activeProfile.type.name == "KIDS") SafetyGreen else TvCyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Profile Filter: ${activeProfile.name}",
                            color = TvTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Allowed Content Rating: ${activeProfile.maxRating.code} (${activeProfile.maxRating.label}). Mature apps require Parent PIN.",
                            color = TvTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    ContentRatingBadge(rating = activeProfile.maxRating)
                }
            }
        }

        // Search Bar & Sideload Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search TV apps, channels, tools...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TvCyanPrimary) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_apps_textfield"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvCyanPrimary,
                        unfocusedBorderColor = TvSurfaceBorder,
                        focusedContainerColor = TvSurfaceDark,
                        unfocusedContainerColor = TvSurfaceDark
                    )
                )

                // USB APK Sideload button
                OutlinedButton(
                    onClick = {
                        val app = uiState.apps.find { it.id == "app_usb_explorer" }
                        app?.let { viewModel.launchApp(it) }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.height(52.dp).testTag("btn_usb_sideload")
                ) {
                    Icon(Icons.Default.Usb, contentDescription = null, tint = TvCyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("USB APK", color = TvCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AppCategory.values()) { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvCyanPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = TvSurfaceElevated,
                            labelColor = TvTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) TvCyanPrimary else TvSurfaceBorder
                        ),
                        modifier = Modifier.testTag("filter_cat_${category.name}")
                    )
                }
            }
        }

        // Section Title: Available Apps
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredApps.size} Applications Available",
                    color = TvTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "No Subscriptions Required",
                    color = TvCyanPrimary,
                    fontSize = 12.sp
                )
            }
        }

        // List of Apps
        items(filteredApps, key = { it.id }) { app ->
            val isAllowed = app.rating.isAllowedUnder(activeProfile.maxRating)
            val isInstalling = uiState.installingAppId == app.id

            AppCatalogCard(
                app = app,
                isAllowedByParent = isAllowed,
                isInstalling = isInstalling,
                onInstall = { viewModel.installApp(app) },
                onLaunch = { viewModel.launchApp(app) },
                onCardClick = { appDetailModal = app }
            )
        }
    }

    // App Detail Bottom Sheet / Modal
    appDetailModal?.let { app ->
        AlertDialog(
            onDismissRequest = { appDetailModal = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(app.accentColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (app.category) {
                                AppCategory.STREAMING -> Icons.Default.Movie
                                AppCategory.LIVE_TV -> Icons.Default.Tv
                                AppCategory.KIDS -> Icons.Default.ChildCare
                                AppCategory.GAMING -> Icons.Default.SportsEsports
                                AppCategory.UTILITIES -> Icons.Default.Build
                                AppCategory.MUSIC -> Icons.Default.MusicNote
                                else -> Icons.Default.Apps
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(app.name, color = TvTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(app.developer, color = TvTextSecondary, fontSize = 12.sp)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ContentRatingBadge(rating = app.rating)
                        Text("★ ${app.ratingScore}  •  ${app.downloads}", color = TvAmberAccent, fontSize = 12.sp)
                        Text("${app.sizeMb} MB", color = TvTextSecondary, fontSize = 12.sp)
                    }

                    Text(app.description, color = TvTextSecondary, fontSize = 13.sp)

                    Surface(
                        color = TvDarkBackground,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Package: ${app.packageName}", color = TvTextMuted, fontSize = 11.sp)
                            Text("Compatibility: Universal (CRT Tube 480i / HDMI 1080p)", color = TvCyanPrimary, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (app.isInstalled) {
                            viewModel.launchApp(app)
                        } else {
                            viewModel.installApp(app)
                        }
                        appDetailModal = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvCyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("modal_action_button")
                ) {
                    Text(if (app.isInstalled) "Launch on TV" else "Install Free")
                }
            },
            dismissButton = {
                TextButton(onClick = { appDetailModal = null }) {
                    Text("Close", color = TvTextSecondary)
                }
            },
            containerColor = TvSurfaceDark
        )
    }
}

@Composable
private fun AppCatalogCard(
    app: TvApp,
    isAllowedByParent: Boolean,
    isInstalling: Boolean,
    onInstall: () -> Unit,
    onLaunch: () -> Unit,
    onCardClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("app_card_${app.id}"),
        color = TvSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!isAllowedByParent) SafetyRed.copy(alpha = 0.4f) else TvSurfaceBorder
        ),
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Icon with accent color
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(app.accentColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (app.category) {
                        AppCategory.STREAMING -> Icons.Default.LiveTv
                        AppCategory.LIVE_TV -> Icons.Default.Tv
                        AppCategory.KIDS -> Icons.Default.ChildCare
                        AppCategory.GAMING -> Icons.Default.VideogameAsset
                        AppCategory.UTILITIES -> Icons.Default.Dns
                        AppCategory.MUSIC -> Icons.Default.Headphones
                        else -> Icons.Default.Apps
                    },
                    contentDescription = app.name,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                if (!isAllowedByParent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Parent Locked",
                            tint = SafetyRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Info Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = app.name,
                        color = TvTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    ContentRatingBadge(rating = app.rating)
                }

                Text(
                    text = "${app.developer} • ★ ${app.ratingScore} • ${app.sizeMb}MB",
                    color = TvTextSecondary,
                    fontSize = 11.sp
                )

                Text(
                    text = app.description,
                    color = TvTextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Button: Open, Install, or Locked
            if (isInstalling) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = TvCyanPrimary,
                    strokeWidth = 3.dp
                )
            } else if (!isAllowedByParent) {
                OutlinedButton(
                    onClick = onLaunch, // Triggers PIN prompt
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafetyRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("app_locked_btn_${app.id}")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SafetyRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PIN Gate", color = SafetyRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else if (app.isInstalled) {
                Button(
                    onClick = onLaunch,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvCyanContainer,
                        contentColor = TvCyanPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TvCyanPrimary.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("app_launch_btn_${app.id}")
                ) {
                    Text("Launch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onInstall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvCyanPrimary,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("app_install_btn_${app.id}")
                ) {
                    Text("Get Free", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
