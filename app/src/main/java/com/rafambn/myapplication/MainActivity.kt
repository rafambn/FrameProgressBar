package com.rafambn.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafambn.frameprogressbar.composablePart.FrameProgressBarCompose
import com.rafambn.frameprogressbar.composablePart.MarkerCompose
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.PointerSelection
import com.rafambn.myapplication.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
                    var pointer = MarkerCompose()
                    var markers = listOf(
                        MarkerCompose(width = 10.dp,),
                        MarkerCompose(width = 10.dp,topOffset = 50.dp),
                        MarkerCompose(width = 10.dp,),
                        MarkerCompose(width = 10.dp,topOffset = 20.dp),
                        MarkerCompose(width = 10.dp,),
                        MarkerCompose(width = 10.dp,topOffset = 20.dp),
                        MarkerCompose(width = 10.dp,)
                    )
                    Box() {
                        val teste = remember { mutableStateOf(0F) }
                        FrameProgressBarCompose(
                            markers = markers,
                            value = teste.value,
                            pointer = MarkerCompose(
                                width = 5.dp,
                                height = 40.dp,
                                topOffset = 5.dp,
                                color = Color.Yellow
                            ),
                            pointerSelection = PointerSelection.CENTER,
                            onValueChange = {
                                teste.value = it
                            },
                            enabled = true,
                            modifier = Modifier.align(Alignment.Center)
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
            MarkerCompose(width = 10.dp,),
            MarkerCompose(width = 10.dp,topOffset = 50.dp),
            MarkerCompose(width = 10.dp,),
            MarkerCompose(width = 10.dp,topOffset = 20.dp),
            MarkerCompose(width = 10.dp,),
            MarkerCompose(width = 10.dp,topOffset = 20.dp),
            MarkerCompose(width = 10.dp,)
        )
        Box() {
            val teste = remember { mutableStateOf(0F) }
            FrameProgressBarCompose(
                markers = markers,
                value = teste.value,
                pointerSelection = PointerSelection.LEFT,
                coercedPointer = CoercePointer.COERCED,
                pointer = MarkerCompose(
                    width = 10.dp,
                    height = 40.dp,
                    topOffset = 5.dp,
                    color = Color.Yellow
                ),
                onValueChange = {
                    teste.value = it
                },
                onValueChangeStarted = {
                    println("comecou")
                },
                onValueChangeFinished = {
                    println("acabou")
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}