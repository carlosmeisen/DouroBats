package pt.dourobats.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// Uncomment these imports after adding font files:
// import dourobats.core.ui.generated.resources.Res
// import dourobats.core.ui.generated.resources.montserrat_bold
// import dourobats.core.ui.generated.resources.montserrat_medium
// import dourobats.core.ui.generated.resources.montserrat_regular
// import dourobats.core.ui.generated.resources.montserrat_semibold
// import org.jetbrains.compose.resources.Font

// TODO: Add custom fonts using Compose Resources (KMP-compatible)
//
// Steps to add custom fonts:
// 1. Download Montserrat from https://fonts.google.com/specimen/Montserrat
// 2. Place TTF files in: core/ui/src/commonMain/composeResources/font/
//    - montserrat_regular.ttf
//    - montserrat_medium.ttf
//    - montserrat_semibold.ttf
//    - montserrat_bold.ttf
// 3. Uncomment the code below
// 4. Uncomment the imports above
// 5. Rebuild the project
//
// Uncomment this code after adding font files:
// val MontserratFontFamily = FontFamily(
//     Font(Res.font.montserrat_regular, FontWeight.Normal),
//     Font(Res.font.montserrat_medium, FontWeight.Medium),
//     Font(Res.font.montserrat_semibold, FontWeight.SemiBold),
//     Font(Res.font.montserrat_bold, FontWeight.Bold),
// )
//
// Then change these lines to use MontserratFontFamily:
// val bodyFontFamily = MontserratFontFamily
// val displayFontFamily = MontserratFontFamily

// Currently using default system fonts
val bodyFontFamily = FontFamily.Default
val displayFontFamily = FontFamily.Default

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

