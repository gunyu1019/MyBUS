package kr.yhs.traffic.ui.pages.pager

import androidx.compose.foundation.background
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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R
import kr.yhs.traffic.models.DropdownQuery

@Composable
fun StationSearch(
    title: String,
    description: String,
    items: List<DropdownQuery>,
    onClick: (Int) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember { mutableStateOf(0) }
    // val items = listOf("수도권", "부울권")
    // will v1.1 (수도권, 부울권)
    Column(
        modifier = Modifier.fillMaxWidth(),
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
                    items[selectedIndex].title,
                    modifier = Modifier
                        .fillMaxWidth(.35f)
                        .size(ButtonDefaults.LargeButtonSize)
                        .clickable { expanded = true }
                        .align(Alignment.Center)
                        .padding(10.dp)
                        .wrapContentSize(Alignment.Center),
                    style = MaterialTheme.typography.body1,
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
                            Text(text = value.title)
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .size(ButtonDefaults.LargeButtonSize),
                onClick = { onClick(items[selectedIndex].cityCode) }
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
        Spacer(modifier = Modifier.height(16.dp))
    }
}
