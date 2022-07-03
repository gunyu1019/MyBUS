package kr.yhs.traffic.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kr.yhs.traffic.ui.component.SearchBox
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun ComposeApp(activity: Activity? = null) {
    Column() {
        SearchBox()
    }
}


@Preview(showBackground = true)
@Composable
fun ComposeAppPreview() {
    AppTheme {
        ComposeApp()
    }
}