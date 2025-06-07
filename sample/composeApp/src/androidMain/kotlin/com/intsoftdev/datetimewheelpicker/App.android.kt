package com.intsoftdev.datetimewheelpicker

import android.app.Application
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())
        setContent {
            App()
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED, showBackground = true)
@Composable
fun PreviewDefaultWheelDateTimePicker() {
    val startDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDate: LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val endDate = currentDate.plus(82, DateTimeUnit.DAY)
    val range = currentDate..endDate

    DateTimeWheelPicker(
        modifier = Modifier.padding(vertical = 8.dp),
        startDateTime = startDateTime,
        size = DpSize(256.dp, 96.dp),
        datesRange = range,
        textStyle = MaterialTheme.typography.titleLarge,
        onSelectClick = {}
    ) { snappedDateTime ->
        Napier.d(tag = "App", message = "snappedDateTime $snappedDateTime")
        //println(snappedDateTime)
    }
}

