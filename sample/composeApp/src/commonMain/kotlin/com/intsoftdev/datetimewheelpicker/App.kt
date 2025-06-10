package com.intsoftdev.datetimewheelpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.intsoftdev.datetimewheelpicker.theme.AppTheme
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun App() = AppTheme {
    Surface(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val startDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val currentDate: LocalDate =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val endDate = currentDate.plus(82, DateTimeUnit.DAY)
            val range = currentDate..endDate

            DateTimeWheelPicker(
                startDateTime = startDateTime,
                datesRange = range,
                textStyle = MaterialTheme.typography.headlineSmall,
                onSelectClick = {}
            ) { snappedDateTime ->
                Napier.d(tag = "App", message = "snappedDateTime $snappedDateTime")
                //println(snappedDateTime)
            }
        }
    }
}

