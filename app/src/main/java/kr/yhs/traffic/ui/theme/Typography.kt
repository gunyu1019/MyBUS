package kr.yhs.traffic.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography
import kr.yhs.traffic.utils.nonScaledSp


val typography
    @Composable
    get() = Typography (
    display1 = TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 0.5.sp
    ),
    display2 = TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    display3 = TextStyle.Default.copy(
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        letterSpacing = 0.8.sp
    ),
    title1 = TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.2.sp
    ),
    button = TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.38.sp
    ),
    body1 = TextStyle.Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.18.sp
    ),
    body2 = TextStyle.Default.copy(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        letterSpacing = 0.2.sp
    ),
    caption1 = TextStyle.Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.1.sp
    ),
    caption2 = TextStyle.Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp.nonScaledSp,
        letterSpacing = 0.1.sp
    ),
)
