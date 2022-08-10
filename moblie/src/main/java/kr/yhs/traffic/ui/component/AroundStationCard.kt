package kr.yhs.traffic.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun AroundStation(
    stationInfo: StationInfo
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(120.dp)
            .padding(4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stationInfo.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
    return
}


@Preview(showBackground = true)
@Composable
fun FavoriteStationPreview() {
    AppTheme {
        AroundStation(
            stationInfo = StationInfo(
                "서울역", "iwd101", 37.0, 38.0, "idk102", "idk102", 1
            )
        )
    }
}