package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.PianoSynthesizer
import com.example.data.entity.SongProgress
import com.example.data.entity.UserStats
import com.example.data.model.Difficulty
import com.example.data.model.Note
import com.example.data.model.Song
import com.example.ui.viewmodel.PianoViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Color Token Constants (PRD Section 6)
val ColorPrimary = Color(0xFF1A4DC9)
val ColorKeyWhite = Color(0xFFF4F6FB)
val ColorKeyWhitePressed = Color(0xFFD6DBE5)
val ColorKeyBlack = Color(0xFF3D4E6B)
val ColorKeyBlackPressed = Color(0xFF1E293B)
val ColorAccent = Color(0xFFFFD84D)
val ColorBg = Color(0xFFF0F2F8)
val ColorSuccess = Color(0xFF28C76F)
val ColorError = Color(0xFFEA5455)

@Composable
fun PianoAppMain(viewModel: PianoViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ColorBg
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is Screen.Splash -> SplashScreen(viewModel)
                is Screen.Onboarding -> OnboardingScreen(viewModel)
                is Screen.Home -> HomeScreen(viewModel)
                is Screen.PianoUtama -> PianoUtamaScreen(viewModel)
                is Screen.SongList -> SongListScreen(viewModel)
                is Screen.SongDetail -> SongDetailScreen(viewModel, screen.song)
                is Screen.PracticeMode -> PracticeModeScreen(viewModel, screen.song)
                is Screen.Stats -> StatsScreen(viewModel)
                is Screen.Profile -> ProfileScreen(viewModel)
                is Screen.SettingsPiano -> SettingsPianoScreen(viewModel)
            }
        }
    }
}

// --- 4.1 SPLASH SCREEN ---
@Composable
fun SplashScreen(viewModel: PianoViewModel) {
    LaunchedEffect(Unit) {
        delay(2200) // 2.2 seconds loading animation
        viewModel.navigateTo(Screen.Onboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ColorPrimary, Color(0xFF0F2C7E), Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Keys Logo
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) { index ->
                    val delayVal = index * 150
                    val infiniteTransition = rememberInfiniteTransition(label = "keys")
                    val heightRatio by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1000
                                0.4f at delayVal
                                1.0f at (delayVal + 400)
                                0.4f at 1000
                            },
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "height"
                    )

                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(60.dp * heightRatio)
                            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .background(if (index % 2 == 0) Color.White else ColorAccent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "NadaKu",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = "Solfège & Virtual Piano",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = ColorAccent,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("splash_loader")
            )
        }
    }
}

// --- 4.2 ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(viewModel: PianoViewModel) {
    var activePage by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val pages = listOf(
        Triple(
            "Kenali Pianomu 🎹",
            "Pelajari 7 nada dasar secara intuitif dengan visual solfège DO RE MI dan nomor nada tepat pada tuts piano.",
            Icons.Default.MusicNote
        ),
        Triple(
            "Ikuti Lagumu 🎵",
            "Mainkan ratusan lagu populer Indonesia & dunia secara interaktif dipandu air terjun nada (falling notes) real-time.",
            Icons.Default.PlayLesson
        ),
        Triple(
            "Rekam & Kuasai 🏆",
            "Latih ketukan jarimu, kumpulkan bintang 🔥, pertahankan streak harian, dan bagikan progres bermainmu bersama teman.",
            Icons.Default.TrendingUp
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.testTag("skip_onboarding")
            ) {
                Text("Lewati", color = ColorPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        // Feature Slider Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(ColorPrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pages[activePage].third,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = pages[activePage].first,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ColorKeyBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pages[activePage].second,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 22.sp
            )
        }

        // Indicator and CTA Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == activePage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == activePage) ColorPrimary else Color.Gray.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Buttons
            Button(
                onClick = {
                    if (activePage < 2) {
                        activePage++
                    } else {
                        viewModel.navigateTo(Screen.Home)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (activePage == 2) "Mulai Sekarang" else "Lanjut",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// --- 4.3 HOME / BERANDA DASHBOARD ---
@Composable
fun HomeScreen(viewModel: PianoViewModel) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val songsWithProgress by viewModel.annotatedSongs.collectAsStateWithLifecycle()
    val username by viewModel.userName.collectAsStateWithLifecycle()

    var activeCategory by remember { mutableStateOf("Semua") }

    Scaffold(
        bottomBar = { AppBottomNavigation(viewModel, activeTab = "home") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
                .verticalScroll(rememberScrollState())
        ) {
            // Elegant Welcome Header (PRD Section 4.3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(ColorPrimary, ColorPrimary.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Halo, $username! 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Lanjut asah jari dan latih musikmu hari ini.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Streak Flame indicator
                    Row(
                        modifier = Modifier
                            .background(ColorAccent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stats?.dailyStreak ?: 1} Hari",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Quick Stats Cards (XP / Level Progress)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sertifikat Progres", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Level ${stats?.level ?: 1} Pianis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val xpProgress = ((stats?.xp ?: 0) % 100) / 100f
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ColorSuccess,
                            trackColor = ColorBg
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(ColorAccent.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${(stats?.xp ?: 0)} XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
                    }
                }
            }

            // Play Keyboard CTA (Core visual trigger)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { viewModel.navigateTo(Screen.PianoUtama) },
                colors = CardDefaults.cardColors(containerColor = ColorKeyBlack)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Piano, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Piano Tiga Oktaf", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Main bebas dengan synthesizer tanpa tunda", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }

            // Category Scroll Chips
            val categories = listOf("Semua", "Populer", "Klasik", "Anak-anak", "Tradisional")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = activeCategory == cat
                    Card(
                        modifier = Modifier.clickable { activeCategory = cat },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ColorPrimary else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else ColorKeyBlack,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Catalog Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daftar Lagu Latihan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                TextButton(onClick = { viewModel.navigateTo(Screen.SongList) }) {
                    Text("Semua Lagu", color = ColorPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            // Horizontal / Vertical Songs Filter list
            val filteredSongs = if (activeCategory == "Semua") songsWithProgress 
                                else songsWithProgress.filter { it.first.category == activeCategory }

            if (filteredSongs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada lagu untuk kategori ini.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredSongs.take(4).forEach { (song, progress) ->
                        SongCardItem(song, progress, onClick = {
                            viewModel.setSong(song)
                            viewModel.navigateTo(Screen.SongDetail(song))
                        })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- SONG CARD ITEM COMPONENT ---
@Composable
fun SongCardItem(song: Song, progress: SongProgress?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("song_item_${song.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Note symbol image
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(ColorPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicVideo,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ColorKeyBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge Difficulty (PRD Section 4.5)
                    val badgeColor = when (song.difficulty) {
                        Difficulty.MUDAH -> ColorSuccess
                        Difficulty.SEDANG -> ColorAccent
                        Difficulty.SULIT -> ColorError
                    }
                    Text(
                        text = song.difficulty.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (song.difficulty == Difficulty.SEDANG) ColorKeyBlack else Color.White,
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Category Chip
                    Text(
                        text = song.category,
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .background(ColorBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Star score if completed
            Column(horizontalAlignment = Alignment.End) {
                if (progress != null && progress.highScore > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { starIndex ->
                            Icon(
                                imageVector = if (starIndex < progress.stars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = "${progress.highScore} Pts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai Latihan",
                        tint = Color.Gray.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// --- 4.4 PIANO UTAMA (CORE SCREEN - IDENTICAL TO REFERENCE IMAGE) ---
@Composable
fun PianoUtamaScreen(viewModel: PianoViewModel) {
    val selectedSong by viewModel.selectedSong.collectAsStateWithLifecycle()
    val showNoteNumbers by viewModel.showNoteNumbers.collectAsStateWithLifecycle()
    val showSolfege by viewModel.showSolfege.collectAsStateWithLifecycle()
    val keyScale by viewModel.keySizeScale.collectAsStateWithLifecycle()
    val activePitches by viewModel.highlightedPitches.collectAsStateWithLifecycle()

    var showSongSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // --- MATCHING IMAGE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .shadow(1.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.testTag("piano_menu_button")
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu Back To Home", tint = ColorPrimary)
            }

            // Dropdown selection for active songs
            Box {
                Row(
                    modifier = Modifier
                        .clickable { showSongSelector = true }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedSong.title,
                        color = ColorPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Lagu", tint = ColorPrimary)
                }

                DropdownMenu(
                    expanded = showSongSelector,
                    onDismissRequest = { showSongSelector = false }
                ) {
                    viewModel.annotatedSongs.value.forEach { (song, _) ->
                        DropdownMenuItem(
                            text = { Text(song.title) },
                            onClick = {
                                viewModel.setSong(song)
                                showSongSelector = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { viewModel.navigateTo(Screen.SettingsPiano) },
                modifier = Modifier.testTag("piano_settings_button")
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Konfigurasi Keyboard", tint = ColorPrimary)
            }
        }

        // Quick Controls Floating Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleNoteNumbers() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (showNoteNumbers) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Digit ${if (showNoteNumbers) "On" else "Off"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ColorKeyBlack)
            }

            Row(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleSolfege() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (showSolfege) Icons.Default.Label else Icons.Default.LabelOff,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Solfège ${if (showSolfege) "On" else "Off"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ColorKeyBlack)
            }

            // CTA Play Training Mode
            Button(
                onClick = { viewModel.navigateTo(Screen.PracticeMode(selectedSong)) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mode Latihan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- THE FULL STACKED 3-OCTAVE VIRTUAL KEYBOARD LAYOUT (SAME AS PIANO-1.PNG) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ROW 1: OKTAF TINGGI (High Octave - dotted numbers 1• to 1•)
                Text(
                    "OKTAF TINGGI (DOT DI ATAS)", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Gray, 
                    modifier = Modifier.align(Alignment.Start)
                )
                RefOctaveRow(
                    octaveType = OctaveType.HIGH,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = keyScale,
                    activePitches = activePitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )

                // ROW 2: OKTAF TENGAH (Middle Octave - C4 standard numbers 1 to 7)
                Text(
                    "OKTAF TENGAH (STANDAR)", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Gray, 
                    modifier = Modifier.align(Alignment.Start)
                )
                RefOctaveRow(
                    octaveType = OctaveType.MIDDLE,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = keyScale,
                    activePitches = activePitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )

                // ROW 3: OKTAF RENDAH (Low Octave - underlined numbers 1_ to 7_)
                Text(
                    "OKTAF RENDAH (GARIS BAWAH)", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Gray, 
                    modifier = Modifier.align(Alignment.Start)
                )
                RefOctaveRow(
                    octaveType = OctaveType.LOW,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = keyScale,
                    activePitches = activePitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )
            }
        }
    }
}

// Octave Level Enum
enum class OctaveType {
    LOW, MIDDLE, HIGH
}

// --- SINGLE OCTAVE KEYBOARD GRAPHICS RENDERER ---
@Composable
fun RefOctaveRow(
    octaveType: OctaveType,
    showNoteNumbers: Boolean,
    showSolfege: Boolean,
    keyScale: Float,
    activePitches: Set<Int>,
    onKeyTrigger: (Int) -> Unit
) {
    // Determine target MIDI pitch range for this octave mapping
    val (whitePitches, blackPitches) = remember(octaveType) {
        when (octaveType) {
            OctaveType.LOW -> {
                listOf(48, 50, 52, 53, 55, 57, 59) to listOf(49, 51, 54, 56, 58)
            }
            OctaveType.MIDDLE -> {
                listOf(60, 62, 64, 65, 67, 69, 71) to listOf(61, 63, 66, 68, 70)
            }
            OctaveType.HIGH -> {
                // High octave includes C6 (8rd white keys)
                listOf(72, 74, 76, 77, 79, 81, 83, 84) to listOf(73, 75, 78, 80, 82)
            }
        }
    }

    val labels = listOf("DO", "RE", "MI", "FA", "SOL", "LA", "SI", "DO")
    val digits = listOf("1", "2", "3", "4", "5", "6", "7", "1")

    val whiteKeyWidth = (50.dp * keyScale)
    val whiteKeyHeight = (130.dp * keyScale)
    val blackKeyWidth = (32.dp * keyScale)
    val blackKeyHeight = (68.dp * keyScale)
    val whiteKeySpacing = (8.dp * keyScale)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // 1. White keys arranged horizontally
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(whiteKeySpacing)
        ) {
            whitePitches.forEachIndexed { index, pitch ->
                val isLit = activePitches.contains(pitch)
                val label = labels[index]
                val digit = digits[index]

                Box(
                    modifier = Modifier
                        .width(whiteKeyWidth)
                        .height(whiteKeyHeight)
                        .shadow(
                            elevation = if (isLit) 2.dp else 6.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isLit) ColorAccent 
                            else Color.White
                        )
                        .clickable { onKeyTrigger(pitch) }
                        .testTag("key_white_$pitch")
                        .padding(bottom = 14.dp, top = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Top digit presentation
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(36.dp)
                        ) {
                            if (showNoteNumbers) {
                                when (octaveType) {
                                    OctaveType.HIGH -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "•",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorKeyBlack,
                                                lineHeight = 10.sp
                                            )
                                            Text(
                                                text = digit,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorKeyBlack,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                    OctaveType.MIDDLE -> {
                                        Text(
                                            text = digit,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorKeyBlack
                                        )
                                    }
                                    OctaveType.LOW -> {
                                        Text(
                                            text = digit,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorKeyBlack,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom solfege verbal label
                        if (showSolfege) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray.copy(alpha = 0.8f)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        // 2. Black Keys placed procedurally on division lines (floating capsules/pills)
        val blackKeySlots = listOf(0, 1, 3, 4, 5)

        blackKeySlots.forEachIndexed { i, localIndex ->
            if (i < blackPitches.size && localIndex < whitePitches.size - 1) {
                val pitch = blackPitches[i]
                val isLit = activePitches.contains(pitch)

                // Mathematically centered formula for floating pill keys
                val leftOffset = (whiteKeyWidth + whiteKeySpacing) * (localIndex + 1).toFloat() - (whiteKeySpacing / 2f) - (blackKeyWidth / 2f)

                Box(
                    modifier = Modifier
                        .offset(x = leftOffset, y = 0.dp)
                        .width(blackKeyWidth)
                        .height(blackKeyHeight)
                        .shadow(
                            elevation = if (isLit) 2.dp else 6.dp,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            if (isLit) ColorAccent 
                            else ColorKeyBlack
                        )
                        .clickable { onKeyTrigger(pitch) }
                        .testTag("key_black_$pitch")
                )
            }
        }
    }
}

// --- 4.5 SONG CATALOGUE ---
@Composable
fun SongListScreen(viewModel: PianoViewModel) {
    val annotatedSongs by viewModel.annotatedSongs.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficultyFilter by remember { mutableStateOf("Semua") }

    Scaffold(
        bottomBar = { AppBottomNavigation(viewModel, activeTab = "songs") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
        ) {
            // Elegant search card header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPrimary)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari lagu atau artis...", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("song_search_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    singleLine = true
                )
            }

            // Filters Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Semua", "MUDAH", "SEDANG", "SULIT")
                filters.forEach { tag ->
                    val isSelected = selectedDifficultyFilter == tag
                    val color = if (isSelected) ColorPrimary else Color.White
                    val txtColor = if (isSelected) Color.White else ColorKeyBlack
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDifficultyFilter = tag },
                        colors = CardDefaults.cardColors(containerColor = color),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = txtColor
                        )
                    }
                }
            }

            // List of filtered catalog
            val songsToDisplay = annotatedSongs.filter { (song, _) ->
                val matchesSearch = song.title.contains(searchQuery, ignoreCase = true) || 
                                    song.artist.contains(searchQuery, ignoreCase = true)
                val matchesDiff = selectedDifficultyFilter == "Semua" || 
                                  song.difficulty.name == selectedDifficultyFilter
                matchesSearch && matchesDiff
            }

            if (songsToDisplay.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tidak ada lagu yang cocok.", color = Color.Gray, fontSize = 15.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(songsToDisplay) { (song, progress) ->
                        SongCardItem(song, progress, onClick = {
                            viewModel.setSong(song)
                            viewModel.navigateTo(Screen.SongDetail(song))
                        })
                    }
                }
            }
        }
    }
}

// --- 4.6 DETAIL LAGU SCREEN ---
@Composable
fun SongDetailScreen(viewModel: PianoViewModel, song: Song) {
    val progressList by viewModel.songProgressList.collectAsStateWithLifecycle()
    val progress = progressList.find { it.songId == song.id }
    val isFavorite = progress?.isFavorite ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Transparent Top Header back bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }

            Text("Detail Lagu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorKeyBlack)

            IconButton(
                onClick = { viewModel.toggleSongFavorite(song.id, isFavorite) },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .size(40.dp)
                    .testTag("fav_btn_${song.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (isFavorite) ColorError else Color.Gray
                )
            }
        }

        // Cover artwork simulated
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ColorPrimary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorAccent.copy(alpha = 0.3f), Color.Transparent),
                            radius = 400f
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(song.category.uppercase(), color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metadata grid and high scores
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(1f)
        ) {
            Text("Statistik Belajarmu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // High Score metric
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Skor Tertinggi", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${progress?.highScore ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
                    }
                }

                // Stars performance metric
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Bintang Sempurna", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            repeat(3) { index ->
                                Icon(
                                    imageVector = if (index < (progress?.stars ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = ColorAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Deskripsi Lagu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lagu '${song.title}' oleh ${song.artist} dikategorikan dalam tingkat kesulitan ${song.difficulty.name}. Terdiri dari ${song.notes.size} kunci rangkaian nada dasar yang ideal dimainkan untuk melatih kelincahan transisi tuts keyboard virtual.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Sheet Music Preview (PRD Section 4.6)
            Text("Preview Partitur Nada", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(song.notes.take(15)) { note ->
                        Column(
                            modifier = Modifier
                                .background(ColorBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when {
                                    note.pitch in 48..59 -> "1"
                                    note.pitch in 60..71 -> "1"
                                    else -> "1•"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ColorKeyBlack
                            )
                            Text(
                                text = when (note.pitch % 12) {
                                    0 -> "DO"
                                    2 -> "RE"
                                    4 -> "MI"
                                    5 -> "FA"
                                    7 -> "SOL"
                                    9 -> "LA"
                                    11 -> "SI"
                                    else -> "♯"
                                },
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    item {
                        Text("...", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }

        // Action CTA Buttons in row (PRD Section 4.6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.PianoUtama) },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, ColorPrimary)
            ) {
                Text("Main Bebas", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorPrimary)
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.PracticeMode(song)) },
                modifier = Modifier
                    .weight(1.5f)
                    .height(54.dp)
                    .testTag("start_practice_cta"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mode Latihan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// --- 4.7 MODE LATIHAN (PRACTICE ENGINE SCREEN WITH WATERFALL FALLING NOTES) ---
@Composable
fun PracticeModeScreen(viewModel: PianoViewModel, song: Song) {
    val isPracticeRunning by viewModel.isPracticeRunning.collectAsStateWithLifecycle()
    val activeFallingNotes by viewModel.activeFallingNotes.collectAsStateWithLifecycle()
    val score by viewModel.practiceScore.collectAsStateWithLifecycle()
    val perfects by viewModel.perfectCount.collectAsStateWithLifecycle()
    val goods by viewModel.goodCount.collectAsStateWithLifecycle()
    val misses by viewModel.missCount.collectAsStateWithLifecycle()
    val highlightPitches by viewModel.highlightedPitches.collectAsStateWithLifecycle()
    val elapsedTime by viewModel.elapsedTimeMs.collectAsStateWithLifecycle()
    
    val showNoteNumbers by viewModel.showNoteNumbers.collectAsStateWithLifecycle()
    val showSolfege by viewModel.showSolfege.collectAsStateWithLifecycle()
    val keyScale by viewModel.keySizeScale.collectAsStateWithLifecycle()

    val totalDurationOfSong = remember(song) {
        song.notes.maxOfOrNull { it.timeMs } ?: 15000L
    }

    // Auto trigger play loop upon entering the screen
    LaunchedEffect(song) {
        viewModel.startPractice(song)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPractice()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorKeyBlackPressed) // Dark atmospheric mode for training focus
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Stats and score header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.SongDetail(song)) },
                modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(song.title, color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Skor: $score Pts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Simple pause/restart controls
            IconButton(
                onClick = {
                    if (isPracticeRunning) {
                        viewModel.stopPractice()
                    } else {
                        viewModel.startPractice(song)
                    }
                },
                modifier = Modifier.background(
                    if (isPracticeRunning) ColorError.copy(alpha = 0.2f) else ColorSuccess.copy(alpha = 0.2f),
                    CircleShape
                )
            ) {
                Icon(
                    imageVector = if (isPracticeRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isPracticeRunning) ColorError else ColorSuccess
                )
            }
        }

        // Accuracies feedback board
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Perfect: $perfects", color = ColorSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Good: $goods", color = ColorAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Miss: $misses", color = ColorError, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Live Horizontal Progress Timeline bar
        val horizontalRatio = (elapsedTime.toFloat() / totalDurationOfSong).coerceIn(0.0f, 1.0f)
        LinearProgressIndicator(
            progress = { horizontalRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(horizontal = 24.dp, vertical = 4.dp),
            color = ColorAccent,
            trackColor = Color.White.copy(alpha = 0.15f)
        )

        // --- 4.7 FALLING WATERFALL NOTES DISPLAY AREA ---
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxWidth()
                .background(Color(0xFF0F172A)) // Ambient background (deep dark)
                .padding(bottom = 6.dp)
        ) {
            // Target bottom trigger threshold bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .align(Alignment.BottomCenter)
                    .background(ColorPrimary)
            )

            // Waterfall visual rendering based on note positions
            activeFallingNotes.forEach { note ->
                // Visual vertical position representing remaining delay
                val offsetRatio = ((note.timeMs - elapsedTime).toFloat() / 3000f).coerceIn(0.0f, 1.0f)
                val isTargetThreshold = offsetRatio < 0.15f

                // Horizontal column mapping based on note frequencies
                val absoluteLeftPercent = remember(note) {
                    val pitchFraction = ((note.pitch - 48).toFloat() / 36f).coerceIn(0.01f, 0.95f)
                    pitchFraction
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val widthPx = maxWidth
                    val heightPx = maxHeight

                    val noteX = widthPx * absoluteLeftPercent
                    val noteY = heightPx * (1f - offsetRatio)

                    Box(
                        modifier = Modifier
                            .offset(x = noteX - 16.dp, y = noteY - 14.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isTargetThreshold) ColorAccent 
                                else ColorPrimary.copy(alpha = 0.85f)
                            )
                            .border(
                                width = 2.dp,
                                color = if (isTargetThreshold) Color.White else ColorAccent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (note.pitch % 12) {
                                0 -> "1"
                                2 -> "2"
                                4 -> "3"
                                5 -> "4"
                                7 -> "5"
                                9 -> "6"
                                11 -> "7"
                                else -> "♯"
                            },
                            color = if (isTargetThreshold) ColorKeyBlack else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Countdown banner
            if (activeFallingNotes.isEmpty() && elapsedTime < 1000L) {
                Text(
                    text = "Persiapkan jarimu...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // --- THREE OCTAVE KEYBOARD (Bottom panel for real-time play triggers) ---
        Box(
            modifier = Modifier
                .weight(1.7f)
                .fillMaxWidth()
                .background(ColorKeyBlack)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stacked Octaves Mini-size for optimal practice on portrait mode
                RefOctaveRow(
                    octaveType = OctaveType.HIGH,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = 0.82f,
                    activePitches = highlightPitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )

                RefOctaveRow(
                    octaveType = OctaveType.MIDDLE,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = 0.82f,
                    activePitches = highlightPitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )

                RefOctaveRow(
                    octaveType = OctaveType.LOW,
                    showNoteNumbers = showNoteNumbers,
                    showSolfege = showSolfege,
                    keyScale = 0.82f,
                    activePitches = highlightPitches,
                    onKeyTrigger = { viewModel.triggerKeyManual(it) }
                )
            }
        }
    }
}

// --- 4.8 HALAMAN PROGRES / STATISTIK ---
@Composable
fun StatsScreen(viewModel: PianoViewModel) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val songsWithProgress by viewModel.annotatedSongs.collectAsStateWithLifecycle()

    val totalCompletedSongs = songsWithProgress.count { it.second?.isCompleted == true }

    Scaffold(
        bottomBar = { AppBottomNavigation(viewModel, activeTab = "progress") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Progres Belajarmu", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
            Text("Lacak kemahiran serta konsistensi belajarmu di sini.", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Daily practice streak
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ColorPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Beruntun Nada", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${stats?.dailyStreak ?: 1} Hari Rutin Latihan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Terus pertahankan streak latihan harianmu!", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Stats details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total XP", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${stats?.xp ?: 0}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+20 XP per lagu dilatih", fontSize = 11.sp, color = ColorSuccess)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lagu Selesai", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$totalCompletedSongs / ${viewModel.annotatedSongs.value.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Lagu lulus latihan", fontSize = 11.sp, color = ColorPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Simplified Activity Weekly bar chart (Canvas drawings - PRD Section 4.8)
            Text("Menit Latihan Minggu Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weeklyStats = listOf(8, 12, 5, 20, 15, 30, 10) // simulated practice minutes
                        val days = listOf("Se", "Se", "Ra", "Ka", "Ju", "Sa", "Mi")

                        weeklyStats.forEachIndexed { idx, mins ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                val normalizedHeight = (mins / 30f).coerceIn(0.1f, 1f)
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .fillMaxHeight(normalizedHeight)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (idx == 5) ColorAccent else ColorPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(days[idx], fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                    Text(
                        "Pencapaian: Latihan paling konsisten pada hari Sabtu (30 menit).",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Badges Achievement Grid
            Text("Piala Penghargaan 🏆", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(12.dp))
            val badges = listOf(
                "Inisiasi" to "Melatih 1 Lagu Pertama",
                "Streak 3" to "Latihan 3 Hari Beruntun",
                "Grandmaster" to "Raih Skor Sempurna",
                "Pecinta Pop" to "Selesaikan Kategori Pop"
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badges) { (badgeName, badgeDesc) ->
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(ColorPrimary.copy(alpha = 0.08f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ColorAccent)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(badgeName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ColorKeyBlack, textAlign = TextAlign.Center)
                            Text(badgeDesc, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- 4.9 HALAMAN PROFIL ---
@Composable
fun ProfileScreen(viewModel: PianoViewModel) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val songsWithProgress by viewModel.annotatedSongs.collectAsStateWithLifecycle()
    val username by viewModel.userName.collectAsStateWithLifecycle()

    var editingUsername by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf(username) }

    Scaffold(
        bottomBar = { AppBottomNavigation(viewModel, activeTab = "profile") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Akun Profil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(20.dp))

            // Profile info card (editable nickname)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(ColorPrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (editingUsername) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = tempUsername,
                                onValueChange = { tempUsername = it },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("edit_username_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    viewModel.updateUserName(tempUsername)
                                    editingUsername = false
                                },
                                modifier = Modifier.testTag("save_username_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Simpan", tint = ColorSuccess)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(username, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ColorKeyBlack)
                            IconButton(onClick = { editingUsername = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Nama", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text("Tingkat: Level ${stats?.level ?: 1} Pianis", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action lists
            Text("Pengaturan Aplikasi & Bantuan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                ProfileSettingRow(
                    icon = Icons.Outlined.MusicNote,
                    title = "Konfigurasi Suara Piano",
                    onClick = { viewModel.navigateTo(Screen.SettingsPiano) }
                )
                HorizontalDivider(color = ColorBg)
                ProfileSettingRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifikasi Pengingat Latihan",
                    onClick = { }
                )
                HorizontalDivider(color = ColorBg)
                ProfileSettingRow(
                    icon = Icons.Outlined.Language,
                    title = "Pilihan Bahasa (Indonesia)",
                    onClick = { }
                )
                HorizontalDivider(color = ColorBg)
                ProfileSettingRow(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Pusat Bantuan & FAQ",
                    onClick = { }
                )
                HorizontalDivider(color = ColorBg)
                ProfileSettingRow(
                    icon = Icons.Outlined.Share,
                    title = "Bagikan Aplikasi NadaKu",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign out
            Button(
                onClick = { viewModel.navigateTo(Screen.Splash) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ColorError),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Keluar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileSettingRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ColorKeyBlack)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
    }
}

// --- 4.10 HALAMAN PENGATURAN PIANO CONFIG ---
@Composable
fun SettingsPianoScreen(viewModel: PianoViewModel) {
    val instrument by viewModel.instrument.collectAsStateWithLifecycle()
    val volume by viewModel.masterVolume.collectAsStateWithLifecycle()
    val reverb by viewModel.reverbEnabled.collectAsStateWithLifecycle()
    val keyScale by viewModel.keySizeScale.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Pengaturan Piano", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorKeyBlack)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Volume master
            Text("Volume Master Synthesizer", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ColorPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Slider(
                        value = volume,
                        onValueChange = { viewModel.updateVolume(it) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("setting_volume_slider")
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${(volume * 100).toInt()}%", fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sound choices
            Text("Jenis Instrumen Audio", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val types = listOf(
                        Triple(PianoSynthesizer.InstrumentType.GRAND_PIANO, "Grand Piano (Klasik)", Icons.Default.Piano),
                        Triple(PianoSynthesizer.InstrumentType.UPRIGHT_PIANO, "Upright Piano (Terang)", Icons.Default.MusicVideo),
                        Triple(PianoSynthesizer.InstrumentType.ELECTRIC_PIANO, "Electric Piano (Warm EP)", Icons.Default.MusicNote),
                        Triple(PianoSynthesizer.InstrumentType.ORGAN, "Organ / Seruling", Icons.Default.Keyboard)
                    )

                    types.forEach { (type, label, icon) ->
                        val isSelected = instrument == type
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateInstrument(type) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, tint = if (isSelected) ColorPrimary else Color.Gray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(label, fontWeight = FontWeight.SemiBold, color = if (isSelected) ColorPrimary else ColorKeyBlack)
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateInstrument(type) },
                                modifier = Modifier.testTag("instrument_radio_${type.name}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reverb toggle
            Text("Efek Ambien Audio", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Efek Reverb / Gema", fontWeight = FontWeight.Bold, color = ColorKeyBlack)
                        Text("Simulasikan gema bunyi pada ruang konser besar.", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = reverb,
                        onCheckedChange = { viewModel.updateReverb(it) },
                        modifier = Modifier.testTag("reverb_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Keyboard key scaling sizes
            Text("Skala Ukuran Tuts Keyboard", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorKeyBlack)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val scales = listOf(
                        "Kecil" to 0.82f,
                        "Sedang" to 1.0f,
                        "Besar" to 1.2f
                    )
                    scales.forEach { (name, factor) ->
                        val isSelected = keyScale == factor
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.updateKeyScale(factor) }
                                .padding(8.dp)
                        ) {
                            Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) ColorPrimary else Color.Gray)
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateKeyScale(factor) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.navigateTo(Screen.PianoUtama) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan & Buka Piano", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- ELEGANT APP BOTTOM NAVIGATION BAR BAR ---
@Composable
fun AppBottomNavigation(viewModel: PianoViewModel, activeTab: String) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars // Ensure edge-to-edge safe margins inside bottom navigation
    ) {
        val items = listOf(
            Triple("home", "Beranda", Icons.Default.Home),
            Triple("songs", "Lagu-Lagu", Icons.Default.LibraryMusic),
            Triple("piano", "Keyboard", Icons.Default.Piano),
            Triple("progress", "Statistik", Icons.Default.TrendingUp),
            Triple("profile", "Profil", Icons.Default.Person)
        )

        items.forEach { (tabId, label, icon) ->
            val isActive = activeTab == tabId
            NavigationBarItem(
                selected = isActive,
                onClick = {
                    when (tabId) {
                        "home" -> viewModel.navigateTo(Screen.Home)
                        "songs" -> viewModel.navigateTo(Screen.SongList)
                        "piano" -> viewModel.navigateTo(Screen.PianoUtama)
                        "progress" -> viewModel.navigateTo(Screen.Stats)
                        "profile" -> viewModel.navigateTo(Screen.Profile)
                    }
                },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(label, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ColorPrimary,
                    selectedTextColor = ColorPrimary,
                    indicatorColor = ColorAccent.copy(alpha = 0.2f),
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray
                ),
                modifier = Modifier.testTag("nav_item_$tabId")
            )
        }
    }
}
