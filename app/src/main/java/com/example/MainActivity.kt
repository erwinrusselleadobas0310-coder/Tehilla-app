package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.TehillaAppShell
import com.example.ui.theme.TehillaTheme
import com.example.ui.viewmodel.TehillaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        val container = (application as TehillaApplication).container

        setContent {
            TehillaTheme {
                val viewModel: TehillaViewModel = viewModel(
                    factory = TehillaViewModel.provideFactory(
                        songRepository = container.songRepositoryImpl,
                        userRepository = container.songRepositoryImpl,
                        audioController = container.audioController
                    )
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                TehillaAppShell(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}
