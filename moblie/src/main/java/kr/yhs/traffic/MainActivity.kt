package kr.yhs.traffic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.FusedLocationProviderClient
import kr.yhs.traffic.module.TrafficClient
import kr.yhs.traffic.ui.ComposeApp
import kr.yhs.traffic.ui.theme.AppTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    var client: TrafficClient? = null
    var spClient: SharedPreferencesClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val httpClient = OkHttpClient.Builder()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yhs.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        client = retrofit.create(TrafficClient::class.java)
        spClient = SharedPreferencesClient("traffic", this)
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ComposeApp(this@MainActivity)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        ComposeApp()
    }
}