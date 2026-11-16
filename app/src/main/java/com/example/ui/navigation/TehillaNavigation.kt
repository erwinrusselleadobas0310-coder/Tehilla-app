package com.example.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.screens.analysis.SongAnalysisScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.insights.MusicInsightsScreen
import com.example.ui.screens.library.MySongsScreen
import com.example.ui.screens.profile.ProfileSettingsScreen
import com.example.ui.screens.studio.InstrumentStudioScreen
import com.example.ui.screens.upload.AudioUploadScreen
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.TehillaViewModel

const val ROUTE_HOME = "home"
const val ROUTE_MY_SONGS = "my_songs"
const val ROUTE_STUDIO = "studio"
const val ROUTE_INSIGHTS = "insights"
const val ROUTE_PROFILE = "profile"
const val ROUTE_UPLOAD = "upload"
const val ROUTE_SONG_ANALYSIS = "song_analysis"

data class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val FiveBottomNavItems = listOf(
    BottomNavItem(ROUTE_HOME, R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
    BottomNavItem(ROUTE_MY_SONGS, R.string.nav_my_songs, Icons.Rounded.LibraryMusic, Icons.Outlined.LibraryMusic, "nav_my_songs"),
    BottomNavItem(ROUTE_STUDIO, R.string.nav_studio, Icons.Rounded.Tune, Icons.Outlined.Tune, "nav_studio"),
    BottomNavItem(ROUTE_INSIGHTS, R.string.nav_insights, Icons.Rounded.MusicNote, Icons.Outlined.MusicNote, "nav_insights"),
    BottomNavItem(ROUTE_PROFILE, R.string.nav_profile, Icons.Rounded.Person, Icons.Outlined.Person, "nav_profile")
)

@Composable
fun TehillaAppShell(
    uiState: TehillaUiState,
    viewModel: TehillaViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in FiveBottomNavItems.map { it.route }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = NavyBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = NavySurface,
                    contentColor = TextPrimary
                ) {
                    FiveBottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val label = stringResource(item.labelResId)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTopLevel(item.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = { Text(label) },
                            modifier = Modifier.testTag(item.testTag),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealBright,
                                selectedTextColor = TealBright,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = TealContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    state = uiState,
                    onAnalyzeNewSongClick = { navController.navigate(ROUTE_UPLOAD) },
                    onOpenSongAnalysis = { songId ->
                        viewModel.selectActiveSong(songId)
                        navController.navigate(ROUTE_SONG_ANALYSIS)
                    },
                    onOpenStudio = { navigateTopLevel(ROUTE_STUDIO) },
                    onOpenInsights = { navigateTopLevel(ROUTE_INSIGHTS) }
                )
            }

            composable(ROUTE_MY_SONGS) {
                MySongsScreen(
                    state = uiState,
                    onSearchChange = viewModel::updateSearchQuery,
                    onFilterChange = viewModel::updateLibraryFilter,
                    onUploadClick = { navController.navigate(ROUTE_UPLOAD) },
                    onSongClick = { songId ->
                        viewModel.selectActiveSong(songId)
                        navController.navigate(ROUTE_SONG_ANALYSIS)
                    },
                    onDeleteSong = viewModel::deleteSong
                )
            }

            composable(ROUTE_UPLOAD) {
                AudioUploadScreen(
                    uploadState = uiState.uploadState,
                    isCloudConfigured = uiState.isCloudConfigured,
                    onBack = { navController.popBackStack() },
                    onFileSelected = viewModel::onAudioFilePicked,
                    onInvalidFile = viewModel::onInvalidFileSelected,
                    onTitleChange = viewModel::updateUploadTitle,
                    onArtistChange = viewModel::updateUploadArtist,
                    onToggleChords = viewModel::toggleChordDetection,
                    onAnalyzeClick = {
                        viewModel.startAudioAnalysis {
                            navController.navigate(ROUTE_SONG_ANALYSIS) {
                                popUpTo(ROUTE_UPLOAD) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(ROUTE_SONG_ANALYSIS) {
                SongAnalysisScreen(
                    state = uiState,
                    onBack = { navController.popBackStack() },
                    onOpenStudio = { navigateTopLevel(ROUTE_STUDIO) },
                    onOpenInsights = { navigateTopLevel(ROUTE_INSIGHTS) },
                    onSeek = viewModel::seekTo,
                    onTogglePlay = viewModel::togglePlayPause,
                    onToggleLoop = viewModel::toggleLoopSection,
                    onCycleSpeed = viewModel::cyclePlaybackSpeed,
                    onSkipSeconds = viewModel::skipSeconds,
                    onSectionSelected = viewModel::jumpToSection
                )
            }

            composable(ROUTE_STUDIO) {
                InstrumentStudioScreen(
                    state = uiState,
                    onVolumeChange = viewModel::updateStemVolume,
                    onToggleMute = viewModel::toggleStemMute,
                    onToggleSolo = viewModel::toggleStemSolo,
                    onSeek = viewModel::seekTo,
                    onTogglePlay = viewModel::togglePlayPause,
                    onToggleLoop = viewModel::toggleLoopSection,
                    onCycleSpeed = viewModel::cyclePlaybackSpeed,
                    onSkipSeconds = viewModel::skipSeconds
                )
            }

            composable(ROUTE_INSIGHTS) {
                MusicInsightsScreen(
                    state = uiState,
                    onTranspose = viewModel::shiftTranspose
                )
            }

            composable(ROUTE_PROFILE) {
                ProfileSettingsScreen(
                    state = uiState,
                    onSelectRole = viewModel::updateWorshipRole,
                    onToggleNashville = viewModel::toggleNashvilleNumbers,
                    onToggleWavDownloads = viewModel::toggleHighQualityWav,
                    onToggleAutoLoop = viewModel::toggleAutoLoopChorus
                )
            }
        }
    }
}
