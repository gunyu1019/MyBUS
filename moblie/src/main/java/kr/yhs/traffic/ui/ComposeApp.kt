package kr.yhs.traffic.ui

import android.app.Activity
import android.hardware.biometrics.BiometricManager
import android.provider.Settings.Global.getString
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.res.TypedArrayUtils.getString
import de.charlex.compose.BottomDrawerScaffold
import de.charlex.compose.rememberBottomDrawerScaffoldState
import kotlinx.coroutines.launch
import kr.yhs.traffic.ui.component.SearchBox
import kr.yhs.traffic.ui.theme.AppTheme
import kotlin.math.max
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ComposeApp(activity: Activity? = null) {
    val bottomDrawerScaffoldState = rememberBottomDrawerScaffoldState()
    val scope = rememberCoroutineScope()
    val source = remember { MutableInteractionSource() }
    var bottomDrawerGestureEnableStatus by remember { mutableStateOf(true) }

    LaunchedEffect(source) {
        source.interactions.collect {
            if (it is PressInteraction.Release) {
                bottomDrawerScaffoldState.bottomDrawerState.expand()
                bottomDrawerGestureEnableStatus = false
            }
        }
    }

    BottomDrawerScaffold(
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 10.dp
                    ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                elevation = 4.dp
            ) {
                SearchBox(source) {
                    bottomDrawerGestureEnableStatus = true
                }
            }
        },
        drawerElevation = 0.dp,
        drawerPeekHeight = 150.dp,
        drawerBackgroundColor = Color.Transparent,
        gesturesEnabled = bottomDrawerGestureEnableStatus,
        scaffoldState = bottomDrawerScaffoldState
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun ComposeAppPreview() {
    AppTheme {
        ComposeApp()
    }
}