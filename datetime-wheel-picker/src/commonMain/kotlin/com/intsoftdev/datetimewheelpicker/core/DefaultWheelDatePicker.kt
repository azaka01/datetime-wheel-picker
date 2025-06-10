package com.intsoftdev.datetimewheelpicker.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun DefaultWheelDatePicker(
    modifier: Modifier = Modifier,
    startDate: LocalDate = LocalDate.now(),
    datesRange: ClosedRange<LocalDate>,
    size: DpSize = DpSize(256.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDate: (snappedDate: SnappedDate) -> Int? = { _ -> null }
) {
    val itemCount = 1
    val itemWidth = size.width / itemCount

    var snappedDate by remember { mutableStateOf(startDate) }

    var daysToDisplay = getDatesAroundCurrent(datesRange)

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
            //Day of Month
            WheelTextPicker(
                size = DpSize(
                    width = itemWidth,
                    height = size.height
                ),
                texts = daysToDisplay.map {
                    it.localDate.toDisplayable()
                    // newDate.dayOfWeek.name.substring(0,3) + " " + it.text + " " +  newDate.month.name.substring(0,3)
                },
                rowCount = rowCount,
                itemCount = null,
                style = textStyle,
                color = textColor,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                startIndex = daysToDisplay.find { it.localDate == startDate }?.index ?: 0,
                onScrollFinished = { snappedIndex ->

                    Napier.d(
                        tag = "WheelTextPicker",
                        message = "onScrollFinished snappedIndex: $snappedIndex"
                    )
                    val newDate = daysToDisplay.find { it.index == snappedIndex }?.localDate

                    newDate?.let {
                        Napier.d(tag = "WheelTextPicker", message = "newDate: $newDate")

                        if (newDate in datesRange) {
                            Napier.d(tag = "WheelTextPicker", message = "NEW snappedDate: $newDate")
                            snappedDate = newDate
                        } else {
                            Napier.d(tag = "WheelTextPicker", message = "IGNORE out of range")
                        }

                        val newIndex = daysToDisplay.find { it.localDate == snappedDate }?.index

                        newIndex?.let {
                            Napier.d(tag = "WheelTextPicker", message = "newIndex: $newIndex")

                            onSnappedDate(
                                SnappedDate.NewDate(
                                    localDate = snappedDate,
                                    index = newIndex
                                )
                            )?.let { return@WheelTextPicker it }
                        }
                    }

                    return@WheelTextPicker daysToDisplay.find { it.localDate == snappedDate }?.index
                }
            )

            /**
             * To make the UI compact and consistent with iOS, the month and year fields
             * have been removed. They can be reintroduced as a configuration option if needed.
             */
        }
    }
}

internal data class DayOfMonth(
    val text: String,
    val value: Int,
    val index: Int
)

private data class Month(
    val text: String,
    val value: Int,
    val index: Int
)

private data class Year(
    val text: String,
    val value: Int,
    val index: Int
)

internal data class DateSelectionElement(
    val text: String,
    val localDate: LocalDate,
    val index: Int
)

fun LocalDate.toDisplayable(): String {
    return this.toDayOfWeekText() + " " + "${this.dayOfMonth}" + " " + this.month.name.lowercase()
        .replaceFirstChar { char -> char.titlecase() }.subSequence(0, 3)
}

internal fun getDatesAroundCurrent(datesRange: ClosedRange<LocalDate>): List<DateSelectionElement> {

    val currentDate: LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val newStartDate = datesRange.start.minus(1, DateTimeUnit.DAY)
    val newDatesRange = newStartDate..datesRange.endInclusive

    return newDatesRange.toList().mapIndexed { index, localDate ->
        DateSelectionElement(
            text = localDate.toDisplayable(),
            localDate = localDate,
            index = index
        ).also {
            Napier.d(
                tag = "getDatesAroundCurrent",
                message = "text: ${it.text}, index: ${it.index}"
            )
        }
    }
}

// Extension function to convert ClosedRange to List
fun ClosedRange<LocalDate>.toList(): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var tempDate = this.start
    while (tempDate <= this.endInclusive) {
        dates.add(tempDate)
        tempDate = tempDate.plus(DatePeriod(days = 1))
    }
    return dates
}
