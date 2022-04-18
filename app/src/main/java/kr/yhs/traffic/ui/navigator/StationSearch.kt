package kr.yhs.traffic.ui.navigator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R

@Composable
fun StationSearch(
    onClick: (Int) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf("수도권", "부울권")
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
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier.wrapContentSize(Alignment.CenterStart)
            ) {
                Text(
                    items[selectedIndex],
                    modifier = Modifier
                        .fillMaxWidth(.3f)
                        .size(ButtonDefaults.LargeButtonSize)
                        .clickable {
                            expanded = true
                        }
                        .align(Alignment.Center)
                        .padding(10.dp)
                        .wrapContentSize(Alignment.Center),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.DarkGray)
                ) {
                    items.forEachIndexed { index, value ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            selectedIndex = index
                        }) {
                            Text(text = value)
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .size(ButtonDefaults.LargeButtonSize),
                onClick = { onClick(selectedIndex) }
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
}
