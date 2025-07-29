// app/src/main/java/com/goaltrackr/ui/theme/Color.kt
package com.goaltrackr.ui.theme

import androidx.compose.ui.graphics.Color

// Uygulamanın renk paletini tanımlar.
// Material Design renk sistemine uygun olarak Primary, Secondary vb. renkler belirlenir.

// Açık Tema Renkleri
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// Koyu Tema Renkleri
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Uygulamaya özel renkler
val PrimaryColor = Color(0xFF6200EE) // Ana renk
val PrimaryVariantColor = Color(0xFF3700B3) // Ana rengin varyantı
val SecondaryColor = Color(0xFF03DAC6) // İkincil renk
val SecondaryVariantColor = Color(0xFF018786) // İkincil rengin varyantı
val BackgroundColor = Color(0xFFFFFFFF) // Arka plan rengi (Açık tema)
val SurfaceColor = Color(0xFFFFFFFF) // Yüzey rengi (Açık tema)
val ErrorColor = Color(0xFFB00020) // Hata rengi
val OnPrimaryColor = Color(0xFFFFFFFF) // Primary üzerinde kullanılan renk
val OnSecondaryColor = Color(0xFF000000) // Secondary üzerinde kullanılan renk
val OnBackgroundColor = Color(0xFF000000) // Background üzerinde kullanılan renk
val OnSurfaceColor = Color(0xFF000000) // Surface üzerinde kullanılan renk
val OnErrorColor = Color(0xFFFFFFFF) // Error üzerinde kullanılan renk

// Koyu tema için alternatif renkler (isteğe bağlı, MaterialTheme'in varsayılanları genellikle yeterlidir)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFFFFFFF)
