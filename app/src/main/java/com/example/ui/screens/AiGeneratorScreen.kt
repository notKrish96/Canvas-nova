package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.WallpaperEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.theme.*
import com.example.utils.WallpaperHelper

@Composable
fun AiGeneratorScreen(
    onGenerate: (String, String, String, String, (WallpaperEntity) -> Unit) -> Unit,
    onApplyWallpaper: (WallpaperEntity) -> Unit,
    onDownloadWallpaper: (WallpaperEntity) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var prompt by remember { mutableStateOf("Neon glowing cyberpunk dragon hovering above a futuristic dark city at rain night") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk Neon") }
    var selectedResolution by remember { mutableStateOf("4K (3840x2160)") }
    var selectedAspectRatio by remember { mutableStateOf("9:16 Mobile") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<WallpaperEntity?>(null) }

    val stylePresets = listOf(
        "Cyberpunk Neon",
        "Glassmorphism 3D",
        "Synthwave Retro",
        "AMOLED Minimal",
        "Anime Dream",
        "Fluid Chromatic"
    )

    val resolutionOptions = listOf("4K (3840x2160)", "1440p", "1080p")
    val aspectRatioOptions = listOf("9:16 Mobile", "16:9 Desktop", "1:1 Square")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AI WALLPAPER CREATOR",
                    color = ElectricCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Powered by Gemini Neural Art Engine",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt Box
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Describe your dream wallpaper:",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .testTag("ai_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Style Selector
        Text(
            text = "Art Style Preset:",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(stylePresets) { style ->
                GlassChip(
                    text = style,
                    isSelected = selectedStyle == style,
                    onClick = { selectedStyle = style }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resolution & Aspect Ratio Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Resolution:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(resolutionOptions) { res ->
                        GlassChip(
                            text = res,
                            isSelected = selectedResolution == res,
                            onClick = { selectedResolution = res }
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Aspect Ratio:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(aspectRatioOptions) { ratio ->
                        GlassChip(
                            text = ratio,
                            isSelected = selectedAspectRatio == ratio,
                            onClick = { selectedAspectRatio = ratio }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generate Action Button
        Button(
            onClick = {
                WallpaperHelper.triggerHaptic(haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                isGenerating = true
                onGenerate(prompt, selectedStyle, selectedResolution, selectedAspectRatio) { result ->
                    isGenerating = false
                    generatedResult = result
                }
            },
            enabled = !isGenerating && prompt.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_ai_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Synthesizing AI Art...", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate AI Wallpaper", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Card Preview
        AnimatedVisibility(visible = generatedResult != null) {
            generatedResult?.let { res ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricCyan
                ) {
                    Text(
                        text = "✨ AI Masterpiece Created!",
                        color = ElectricCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Brush.linearGradient(listOf(ElectricCyan, NeonMagenta)), RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = res.hdUrl,
                            contentDescription = res.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = res.resolution,
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = res.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onApplyWallpaper(res) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Set Wallpaper", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onDownloadWallpaper(res) },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, ElectricCyan)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
