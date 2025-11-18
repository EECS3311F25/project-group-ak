package org.example.project.presentation.uishared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier)
