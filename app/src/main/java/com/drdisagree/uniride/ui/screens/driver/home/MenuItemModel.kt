package com.drdisagree.uniride.ui.screens.driver.home

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemModel(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: (() -> Unit)? = null
)