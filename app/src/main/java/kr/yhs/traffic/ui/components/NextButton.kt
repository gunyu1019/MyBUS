package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun NextButton(modifier: Modifier = Modifier, title: String = "다음", onClick: () -> Unit) = Button(
    modifier = modifier
        .height(40.dp)
        .width(100.dp),
    onClick = onClick
) {
    Text(
        text = title,
        style = MaterialTheme.typography.button,
        textAlign = TextAlign.Center
    )
}