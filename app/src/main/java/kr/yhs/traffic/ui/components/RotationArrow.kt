package kr.yhs.traffic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import kr.yhs.traffic.R


@Composable
fun RotationArrow(modifier: Modifier, direction: Float) = Icon(
    painter = painterResource(id = R.drawable.ic_baseline_arrow),
    contentDescription = "Rotation",
    modifier = Modifier
        .size(14.dp)
        .wrapContentSize(align = Alignment.BottomEnd)
        .rotate(direction)
        .background(Color.White)
        .border(
            width = 1.dp, brush = Brush.radialGradient(
                colors = listOf(Color.Black, Color.LightGray)
            ), shape = CircleShape
        )
)