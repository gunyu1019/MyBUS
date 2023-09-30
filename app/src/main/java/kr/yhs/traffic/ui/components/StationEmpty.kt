package kr.yhs.traffic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R


@Composable
fun StationEmpty() {
    val context = LocalContext.current
    return Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_search_off),
            contentDescription = "Not Found",
            modifier = Modifier.height(30.dp)
        )
        Text(
            text = context.getString(R.string.result_not_found),
            style = MaterialTheme.typography.body1
        )
    }
}