package kr.yhs.traffic.ui.pages.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R

@Composable
fun StationStar(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 2.dp),
            text = title,
            style = MaterialTheme.typography.display1
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
            text = description,
            style = MaterialTheme.typography.caption2,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(ButtonDefaults.LargeButtonSize),
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star),
                contentDescription = "Star Icon",
                modifier = Modifier
                    .size(32.dp)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
