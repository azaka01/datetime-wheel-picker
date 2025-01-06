package com.intsoftdev.datetimewheelpicker.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

@Composable
internal fun DefaultWheelDateTimePicker(
    modifier: Modifier = Modifier,
    startDateTime: LocalDateTime = LocalDateTime.now(),
    datesRange: ClosedRange<LocalDate>,
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    size: DpSize = DpSize(256.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onClick: () -> Unit,
    onSnappedDateTime: (snappedDateTime: SnappedDateTime) -> Int? = { _ -> null }
) {

    var snappedDateTime by remember { mutableStateOf(startDateTime.truncatedTo(ChronoUnit.MINUTES)) }

    Column {

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            if (selectorProperties.enabled().value) {
                Surface(
                    modifier = Modifier
                        .size(size.width, size.height / rowCount),
                    shape = selectorProperties.shape().value,
                    color = selectorProperties.color().value,
                    border = selectorProperties.border().value
                ) {}
            }
            Row {
                //Date
                DefaultWheelDatePicker(
                    startDate = startDateTime.date,
                    datesRange = datesRange,
                    size = DpSize(
                        width = size.width * 3 / 5,
                        height = size.height
                    ),
                    rowCount = rowCount,
                    textStyle = textStyle,
                    textColor = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onSnappedDate = { snappedDate ->
                        Napier.d(
                            tag = "DefaultWheelDateTimePicker",
                            message = "Date: onSnappedDate: $snappedDate"
                        )
                        val newDateTime = when (snappedDate) {

                            is SnappedDate.NewDate -> {
                                snappedDateTime.withNewDate(snappedDate.snappedLocalDate)
                            }

                            is SnappedDate.DayOfMonth -> {
                                snappedDateTime.withDayOfMonth(snappedDate.snappedLocalDate.dayOfMonth)
                            }

                            is SnappedDate.Month -> {
                                snappedDateTime.withMonthNumber(snappedDate.snappedLocalDate.monthNumber)
                            }
                        }

                        Napier.d(
                            tag = "DefaultWheelDateTimePicker",
                            message = "newDateTime: $newDateTime"
                        )

                        if (newDateTime.date in datesRange) {
                            snappedDateTime = newDateTime
                            Napier.d(
                                tag = "DefaultWheelDateTimePicker",
                                message = "In Range snappedDateTime: $newDateTime"
                            )
                        } else {
                            Napier.d(
                                tag = "DefaultWheelDateTimePicker",
                                message = "OUTSIDE RANGE use current $snappedDateTime"
                            )
                        }

                        return@DefaultWheelDatePicker when (snappedDate) {

                            is SnappedDate.NewDate -> {
                                Napier.d(
                                    tag = "DefaultWheelDateTimePicker",
                                    message = "return NewDate"
                                )
                                var daysToDisplay = getDatesAroundCurrent(datesRange)
                                val index =
                                    daysToDisplay.find { it.localDate == snappedDateTime.date }?.index
                                        ?: 0
                                onSnappedDateTime(
                                    SnappedDateTime.NewDate(
                                        snappedDateTime,
                                        index
                                    )
                                )
                                index
                            }

                            is SnappedDate.DayOfMonth -> {
                                Napier.d(
                                    tag = "DefaultWheelDateTimePicker",
                                    message = "return DayOfMonth"
                                )
                                onSnappedDateTime(
                                    SnappedDateTime.DayOfMonth(
                                        snappedDateTime,
                                        snappedDateTime.dayOfMonth - 1
                                    )
                                )
                                snappedDateTime.dayOfMonth - 1
                            }

                            is SnappedDate.Month -> {
                                onSnappedDateTime(
                                    SnappedDateTime.Month(
                                        snappedDateTime,
                                        snappedDateTime.month.number - 1
                                    )
                                )
                                snappedDateTime.month.number - 1
                            }
                        }
                    }
                )
                //Time
                DefaultWheelTimePicker(
                    startTime = startDateTime.time,
                    timeFormat = timeFormat,
                    size = DpSize(
                        width = size.width * 2 / 5,
                        height = size.height
                    ),
                    rowCount = rowCount,
                    textStyle = textStyle,
                    textColor = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onSnappedTime = { snappedTime, timeFormat ->
                        Napier.d(
                            tag = "DefaultWheelDateTimePicker",
                            message = "snappedTime hour: ${snappedTime.snappedLocalTime.hour}"
                        )
                        val newDateTime = when (snappedTime) {
                            is SnappedTime.Hour -> {
                                snappedDateTime.withHour(snappedTime.snappedLocalTime.hour)
                            }

                            is SnappedTime.Minute -> {
                                snappedDateTime.withMinute(snappedTime.snappedLocalTime.minute)
                            }
                        }

                        if (newDateTime.date in datesRange) {
                            snappedDateTime = newDateTime
                            Napier.d(
                                tag = "DefaultWheelDateTimePicker",
                                message = "new datetime IN RANGE $snappedDateTime"
                            )

                        } else {
                            Napier.d(
                                tag = "DefaultWheelDateTimePicker",
                                message = "IGNORE new datetime out of range"
                            )
                        }

                        return@DefaultWheelTimePicker when (snappedTime) {
                            is SnappedTime.Hour -> {
                                onSnappedDateTime(
                                    SnappedDateTime.Hour(
                                        snappedDateTime,
                                        snappedDateTime.hour
                                    )
                                )
                                if (timeFormat == TimeFormat.HOUR_24) snappedDateTime.hour else
                                    localTimeToAmPmHour(snappedDateTime.time) - 1
                            }

                            is SnappedTime.Minute -> {
                                onSnappedDateTime(
                                    SnappedDateTime.Minute(
                                        snappedDateTime,
                                        snappedDateTime.minute
                                    )
                                )
                                snappedDateTime.minute
                            }
                        }
                    }
                )
            }
        }
        Row(modifier.width(size.width)) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.align(Alignment.Bottom)
                    .size(width = 140.dp, height = 32.dp)
                    .padding(top = 0.dp),
                enabled = true, // TODO
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)  // Green when enabled, grey when disabled
                ),
                onClick = {
                    onClick()
                }
            ) {
                Text("Select", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}










