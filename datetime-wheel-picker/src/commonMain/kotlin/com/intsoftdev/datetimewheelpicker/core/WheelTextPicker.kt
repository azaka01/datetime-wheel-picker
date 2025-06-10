package com.intsoftdev.datetimewheelpicker.core

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier

@Composable
fun WheelTextPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    size: DpSize = DpSize(128.dp, 128.dp),
    texts: List<String>,
    rowCount: Int,
    itemCount: Int?,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
) {
    fun getDisplayText(index: Int) : String{
        if (itemCount == null) {
            return texts[index]
        }
        if (index >= texts.size) {
            return texts[index % itemCount]
        }
        return texts[index]
    }

    WheelPicker(
        modifier = modifier,
        startIndex = startIndex,
        size = size,
        count = texts.size,
        rowCount = rowCount,
        itemCount = itemCount,
        selectorProperties = selectorProperties,
        onScrollFinished = onScrollFinished
    ) { index ->

        Text(
            text = getDisplayText(index),
            style = style,
            color = color,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}