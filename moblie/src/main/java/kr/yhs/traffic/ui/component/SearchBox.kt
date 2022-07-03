package kr.yhs.traffic.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun SearchBox() {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .background(Color.Gray)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(6.dp)
                .height(8.dp)
        ) {
            val modifier = Modifier.padding(start = 3.dp, end = 3.dp)
            val colorFilter = ColorFilter.tint(Color.DarkGray)
            for (i in 1..3)
                Image(
                    Icons.Filled.Circle,
                    "Swipe circle${i}",
                    modifier = modifier,
                    colorFilter = colorFilter
                )
        }
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("버스 검색") },
            leadingIcon = { Icon(Icons.Filled.Search, "Search Icon") },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBoxPreview() {
    AppTheme {
        SearchBox()
    }
}