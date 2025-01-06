package com.intsoftdev.datetimewheelpicker

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.intsoftdev.datetimewheelpicker.core.DefaultWheelDateTimePicker
import com.intsoftdev.datetimewheelpicker.core.SelectorProperties
import com.intsoftdev.datetimewheelpicker.core.TimeFormat
import com.intsoftdev.datetimewheelpicker.core.WheelPickerDefaults
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DateTimeWheelPicker(
    modifier: Modifier = Modifier,
    startDateTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    datesRange: ClosedRange<LocalDate>,
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    size: DpSize = DpSize(256.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSelectClick: () -> Unit,
    onSnappedDateTime: (snappedDateTime: LocalDateTime) -> Unit = {}
) {
    DefaultWheelDateTimePicker(
        modifier,
        startDateTime,
        datesRange,
        timeFormat,
        size,
        rowCount,
        textStyle,
        textColor,
        selectorProperties,
        onClick = onSelectClick,
        onSnappedDateTime = { snappedDateTime ->
            onSnappedDateTime(snappedDateTime.snappedLocalDateTime)
            snappedDateTime.snappedIndex
        }
    )
}