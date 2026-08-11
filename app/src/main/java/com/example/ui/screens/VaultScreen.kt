package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WallpaperEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.WallpaperItemCard
import com.example.ui.theme.*
import com.example.viewmodel.AgeVerificationState
import com.example.utils.WallpaperHelper

@Composable
fun VaultScreen(
    ageState: AgeVerificationState,
    nsfwWallpapers: List<WallpaperEntity>,
    onVerifyGoogleOAuth: () -> Unit,
    onVerifyIdUpload: (Uri?) -> Unit,
    onRevokeVerification: () -> Unit,
    onWallpaperClick: (WallpaperEntity) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onApplyQuick: (WallpaperEntity) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showVerificationDialog by remember { mutableStateOf(false) }
    var selectedIdUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedIdUri = uri
        if (uri != null) {
            onVerifyIdUpload(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (ageState.is18PlusVerified) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (ageState.is18PlusVerified) ElectricCyan else NsfwWarningColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "RESTRICTED 18+ VAULT",
                    color = if (ageState.is18PlusVerified) ElectricCyan else NsfwWarningColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = if (ageState.is18PlusVerified) "Age Verified • Unlocked Adult Gallery" else "18+ Adult Consent Required",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!ageState.is18PlusVerified) {
            // Locked Vault Verification Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NsfwWarningColor
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NsfwWarningColor.copy(alpha = 0.2f))
                                .border(1.dp, NsfwWarningColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NsfwWarningColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Restricted Content Verification",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This category contains adult / 18+ artistic content. In compliance with safety standards, you must confirm that you are 18 years of age or older via Google OAuth or Government ID verification.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                WallpaperHelper.triggerHaptic(haptic)
                                showVerificationDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_age_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NsfwWarningColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verify Age (18+ Consent)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Unlocked Restricted 18+ Wallpapers Screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ElectricCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElectricCyan)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified: ${ageState.verificationMethod ?: "Age 18+"}",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = {
                        WallpaperHelper.triggerHaptic(haptic)
                        onRevokeVerification()
                    }
                ) {
                    Text("Re-Lock Vault", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(nsfwWallpapers, key = { it.id }) { wp ->
                    WallpaperItemCard(
                        wallpaper = wp,
                        onClick = { onWallpaperClick(wp) },
                        onFavoriteToggle = { onFavoriteToggle(wp.id) },
                        onApplyQuick = { onApplyQuick(wp) }
                    )
                }
            }
        }
    }

    // Verification Choice Modal Dialog
    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationDialog = false },
            title = {
                Text(
                    text = "Confirm 18+ Eligibility",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Choose your preferred verification system:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Option 1: Google OAuth
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                WallpaperHelper.triggerHaptic(haptic)
                                showVerificationDialog = false
                                onVerifyGoogleOAuth()
                            }
                            .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Google OAuth Verification", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Instant age check via Google account DOB", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 2: ID Upload System
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                WallpaperHelper.triggerHaptic(haptic)
                                showVerificationDialog = false
                                photoPickerLauncher.launch("image/*")
                            }
                            .border(1.dp, DeepViolet, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = DeepViolet)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Government ID Upload", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Upload photo of Driver License / Passport", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showVerificationDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
