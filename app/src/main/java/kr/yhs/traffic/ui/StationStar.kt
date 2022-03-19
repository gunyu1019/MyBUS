package kr.yhs.traffic.ui

import android.app.RemoteInput
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import kr.yhs.traffic.R

@Composable
fun StationStar() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        androidx.wear.compose.material.Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 3.dp),
            text = "즐겨찾기",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        androidx.wear.compose.material.Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 3.dp, bottom = 12.dp),
            text = "즐겨찾기한 정류장 정보를 찾아보세요.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(ButtonDefaults.LargeButtonSize),
            onClick = {

            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star),
                contentDescription = "Star Icon",
                modifier = Modifier
                    .size(32.dp)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
    }
}
