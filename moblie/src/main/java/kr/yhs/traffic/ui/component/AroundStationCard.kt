package kr.yhs.traffic.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationAroundInfo
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun AroundStation(
    stationInfo: StationAroundInfo
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(180.dp)
            .height(110.dp)
            .padding(4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.primary
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 14.dp)
        ) {
            Text(
                stationInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary,
                maxLines = 1
            )
            Text(
                "(${stationInfo.displayId as String})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            )
            Row (
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                )
                Icon(
                    Icons.Filled.ArrowUpward,
                    contentDescription = "arrow position",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                        .rotate(30.0f)
                )
                Text(
                    "${stationInfo.distance}m",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 2.dp)
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
            stationInfo = StationAroundInfo(
                "서울역", "iwd101", 37.0, 38.0, "idk102", "idk102", 1, distance = 300
            )
        )
    }
}