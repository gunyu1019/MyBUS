package kr.yhs.traffic.ui

import android.app.RemoteInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.Text
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
import androidx.wear.input.wearableExtender
import kr.yhs.traffic.R

@Composable
fun StationSearch() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 3.dp),
            text = "정류장 검색",
            fontSize = 18.sp,
            fontWeight= FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 3.dp, bottom = 12.dp),
            text = "버튼을 눌러 정류소를 검색해보세요",
            fontSize = 12.sp,
            fontWeight= FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(ButtonDefaults.LargeButtonSize),
            onClick = {
                val remoteInput = RemoteInput.Builder("search_text_input")
                    .setLabel("검색하실 정류소를 입력해주세요.")
                    .setAllowFreeFormInput(true)
                    .wearableExtender {
                        setEmojisAllowed(false)
                    }.build()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_search),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(32.dp)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
    }
}
