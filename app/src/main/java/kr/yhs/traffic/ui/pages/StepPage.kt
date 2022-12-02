package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import java.time.format.TextStyle

@Composable
fun StepPage(
    title: String,
    description: String,
    buttonText: String = "다음",
    nextButtonCallback: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 3.dp),
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 4.dp, end = 4.dp, bottom = 20.dp),
            text = description,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        NextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            buttonText,
            nextButtonCallback
        )
    }
}