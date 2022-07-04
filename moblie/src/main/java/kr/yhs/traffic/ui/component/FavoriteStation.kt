package kr.yhs.traffic.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.yhs.traffic.models.ArrivalInfo
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun FavoriteStation(
    stationInfo: StationInfo
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(210.dp)
            .height(120.dp)
            .padding(4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
    }
    return
}


@Preview(showBackground = true)
@Composable
fun FavoriteStationPreview() {
    AppTheme {
        FavoriteStation(
            stationInfo = StationInfo(
                "서울역", "iwd101", 37.0, 38.0, "idk102", "idk102", 1
            )
        )
    }
}