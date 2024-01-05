package com.rafambn.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
                    val markers = remember {
                        mutableStateListOf(
                            Marker(width = 10.dp),
                            Marker(width = 10.dp, topOffset = 50.dp),
                            Marker(width = 10.dp),
                            Marker(width = 10.dp, topOffset = 20.dp),
                            Marker(width = 10.dp),
                            Marker(width = 10.dp, topOffset = 20.dp),
                            Marker(width = 10.dp)
                        )
                    }
                    val pointer = remember {
                        mutableStateOf(
                            Marker(
                                width = 10.dp,
                                height = 40.dp,
                                topOffset = 5.dp,
                                color = Color.Yellow
                            )
                        )
                    }
//                    LaunchedEffect(0) {
//                        MainScope().launch {
//                            delay(4000L)
//                            Log.e("teste", pointer.hashCode().toString())
//                            markers[0].width = 20.dp
//                            Log.e("teste", pointer.hashCode().toString())
//                        }
//                    }
                    val teste = remember { mutableStateOf(5F) }
                    Box {
                        FrameProgressBar(
                            modifier = Modifier.align(Alignment.Center),
                            pointerSelection = PointerSelection.CENTER,
                            coercedPointer = CoercePointer.COERCED,
                            pointer = pointer.value,
                            markers = markers,
                            value = teste.value,
                            onValueChange  = {
                                teste.value = it
                                println(it)
                            },
                            valueRange = 1F..10F,
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
        val markers = remember {
            mutableStateListOf<Marker>(
//            Marker(width = 10.dp),
//            Marker(width = 10.dp, topOffset = 50.dp),
//            Marker(width = 10.dp),
//            Marker(width = 10.dp, topOffset = 20.dp),
//            Marker(width = 10.dp),
//            Marker(width = 10.dp, topOffset = 20.dp),
//            Marker(width = 10.dp)
            )
        }
        val teste = remember { mutableIntStateOf(0) }
        Box {
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