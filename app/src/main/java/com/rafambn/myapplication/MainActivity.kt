package com.rafambn.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafambn.frameprogressbar.FrameProgressBar
import com.rafambn.frameprogressbar.Marker
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.PointerSelection
import com.rafambn.myapplication.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
                    var pointer = Marker()
                    var markers = listOf(
                        Marker(width = 10.dp),
                        Marker(width = 10.dp, topOffset = 50.dp),
                        Marker(width = 10.dp),
                        Marker(width = 10.dp, topOffset = 20.dp),
                        Marker(width = 10.dp),
                        Marker(width = 10.dp, topOffset = 20.dp),
                        Marker(width = 10.dp)
                    )
                    Box() {
                        val teste = remember { mutableStateOf(0) }
//                        LaunchedEffect(0) {
//                            MainScope().launch {
//                                delay(4000L)
//                                teste.value = 300F
//                            }
//                        }
                        FrameProgressBar(
                            modifier = Modifier.align(Alignment.Center),
                            pointerSelection = PointerSelection.CENTER,
                            pointer = Marker(
                                width = 8.dp,
                                height = 40.dp,
                                topOffset = 5.dp,
                                color = Color.Yellow
                            ),
                            markers = markers,
                            index = teste.value,
                            onIndexChange = {
                                teste.value = it
                                println(it)
                            },
                            enabled = true,
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        var markers = listOf(
            Marker(width = 10.dp),
            Marker(width = 10.dp, topOffset = 50.dp),
            Marker(width = 10.dp),
            Marker(width = 10.dp, topOffset = 20.dp),
            Marker(width = 10.dp),
            Marker(width = 10.dp, topOffset = 20.dp),
            Marker(width = 10.dp)
        )
        Box() {
            val teste = remember { mutableIntStateOf(0) }
            FrameProgressBar(
                modifier = Modifier.align(Alignment.Center),
                markers = markers,
                index = teste.intValue,
                pointer = Marker(
                    width = 10.dp,
                    height = 40.dp,
                    topOffset = 5.dp,
                    color = Color.Yellow
                ),
                onIndexChange = {
                    teste.intValue = it
                }
            )
        }
    }
}