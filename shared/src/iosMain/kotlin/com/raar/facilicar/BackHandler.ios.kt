package com.raar.facilicar

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS usa gesto de swipe nativo — sem handler explícito necessário
}
