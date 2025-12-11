package com.intsoftdev.datetimewheelpicker.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import kotlin.math.abs

private val enableLogs = false

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WheelPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    count: Int,
    rowCount: Int,
    itemCount: Int? = null,
    size: DpSize = DpSize(128.dp, 128.dp),
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
    content: @Composable LazyItemScope.(index: Int) -> Unit,
    // TODO consider haptic configuration - enabled, HapticFeedbackType
) {
    val lazyListState = rememberLazyListState(startIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState)

    val haptic = LocalHapticFeedback.current
    var lastCenterIndex by remember { mutableStateOf<Int?>(null) }

    val isScrollInProgress = lazyListState.isScrollInProgress

    // Local helper
    fun wheelTickHaptic() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    LaunchedEffect(isScrollInProgress, count) {
        if (!isScrollInProgress) {
            val snappedItemIndex = calculateSnappedItemIndex(lazyListState)
            if (enableLogs) {
                Napier.d(
                    tag = "WheelPicker",
                    message = "onScrollFinished shouldLog snappedIndex:$snappedItemIndex"
                )
            }
            onScrollFinished(snappedItemIndex)?.let {
                var scrollItem = it
                if (itemCount != null && snappedItemIndex < itemCount) {
                    scrollItem = snappedItemIndex + itemCount
                }
                if (enableLogs) {
                    Napier.d(
                        tag = "WheelPicker",
                        message = "onScrollFinished snappedIndex: $it, rowCount: $rowCount count:$count"
                    )
                }
                if (itemCount != null && scrollItem < itemCount) {
                    val itemToScroll = scrollItem + itemCount
                    if (enableLogs) {
                        Napier.d(tag = "WheelPicker", message = "scrollToItem: $itemToScroll")
                    }
                    lazyListState.scrollToItem(scrollItem + itemCount)
                } else {
                    lazyListState.scrollToItem(scrollItem)
                }
            }
        }
    }

    // per-step haptics while scrolling
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo to lazyListState.isScrollInProgress }
            .collect { (layoutInfo, scrolling) ->
                if (!scrolling) return@collect

                val visible = layoutInfo.visibleItemsInfo
                if (visible.isEmpty()) return@collect

                val viewportHeight = layoutInfo.viewportSize.height
                if (viewportHeight == 0) return@collect

                val viewportCenter = viewportHeight / 2f

                // Find item whose centre is closest to viewport centre
                val centerItem = visible.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2f
                    abs(itemCenter - viewportCenter)
                } ?: return@collect

                val rawIndex = centerItem.index
                // For infinite wheels, map to logical index
                val logicalIndex =
                    if (itemCount != null && itemCount > 0) rawIndex % itemCount else rawIndex

                if (logicalIndex != lastCenterIndex) {
                    lastCenterIndex = logicalIndex
                    wheelTickHaptic()
                }
            }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier
                    .size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        LazyColumn(
            modifier = Modifier
                .height(size.height)
                .width(size.width),
            state = lazyListState,
            contentPadding = PaddingValues(vertical = size.height / rowCount * ((rowCount - 1) / 2)),
            flingBehavior = flingBehavior
        ) {
            items(if (itemCount != null) Int.MAX_VALUE else count) { index ->
                val (newAlpha, newRotationX) = calculateAnimatedAlphaAndRotationX(
                    lazyListState = lazyListState,
                    index = index,
                    rowCount = rowCount
                )

                if (enableLogs) {
                    Napier.d(
                        tag = "WheelPicker",
                        message = "count: $count, rowCount: $rowCount index:$index, alpha: $newAlpha"
                    )
                }

                Box(
                    modifier = Modifier
                        .height(size.height / rowCount)
                        .width(size.width)
                        .alpha(newAlpha)
                        .graphicsLayer {
                            rotationX = newRotationX
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (enableLogs) {
                        Napier.d(tag = "WheelPicker", message = "content index:$index")
                    }
                    content(index)
                }
            }
        }
    }
}

private fun calculateSnappedItemIndex(lazyListState: LazyListState): Int {
    val currentItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
    val currentItemIndex = lazyListState.firstVisibleItemIndex
    if (enableLogs) {
        Napier.d(tag = "WheelPicker", message = "currentItemIndex $currentItemIndex")
        Napier.d(tag = "WheelPicker", message = "currentItem index ${currentItem?.index}")
        Napier.d(tag = "WheelPicker", message = "currentItem offset ${currentItem?.offset}")
        Napier.d(tag = "WheelPicker", message = "currentItem size ${currentItem?.size}")
    }
    val itemCount = lazyListState.layoutInfo.totalItemsCount
    val offset = lazyListState.firstVisibleItemScrollOffset
    val itemHeight =
        lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: return currentItemIndex

    return if (offset > itemHeight / 2 && currentItemIndex < itemCount - 1) {
        if (enableLogs) {
            Napier.d(tag = "WheelPicker", message = "return ${(currentItemIndex + 1)}")
        }
        currentItemIndex + 1
    } else {
        if (enableLogs) {
            Napier.d(tag = "WheelPicker", message = "return $currentItemIndex")
        }
        currentItemIndex
    }
}

@Composable
private fun calculateAnimatedAlphaAndRotationX(
    lazyListState: LazyListState,
    index: Int,
    rowCount: Int
): Pair<Float, Float> {

    val layoutInfo = remember { derivedStateOf { lazyListState.layoutInfo } }.value
    val viewPortHeight = layoutInfo.viewportSize.height.toFloat()
    val singleViewPortHeight = viewPortHeight / rowCount

    val centerIndex = remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }.value
    val centerIndexOffset =
        remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }.value

    val distanceToCenterIndex = index - centerIndex

    val distanceToIndexSnap =
        distanceToCenterIndex * singleViewPortHeight.toInt() - centerIndexOffset
    val distanceToIndexSnapAbs = abs(distanceToIndexSnap)

    val animatedAlpha = if (abs(distanceToIndexSnap) in 0..singleViewPortHeight.toInt()) {
        1.2f - (distanceToIndexSnapAbs / singleViewPortHeight)
    } else {
        0.2f
    }

    val animatedRotationX =
        (-20 * (distanceToIndexSnap / singleViewPortHeight)).takeUnless { it.isNaN() } ?: 0f

    return animatedAlpha to animatedRotationX
}

object WheelPickerDefaults {
    @Composable
    fun selectorProperties(
        enabled: Boolean = true,
        shape: Shape = RoundedCornerShape(16.dp),
        color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ): SelectorProperties = DefaultSelectorProperties(
        enabled = enabled,
        shape = shape,
        color = color,
        border = border
    )
}

interface SelectorProperties {
    @Composable
    fun enabled(): State<Boolean>

    @Composable
    fun shape(): State<Shape>

    @Composable
    fun color(): State<Color>

    @Composable
    fun border(): State<BorderStroke?>
}

@Immutable
internal class DefaultSelectorProperties(
    private val enabled: Boolean,
    private val shape: Shape,
    private val color: Color,
    private val border: BorderStroke?
) : SelectorProperties {

    @Composable
    override fun enabled(): State<Boolean> {
        return rememberUpdatedState(enabled)
    }

    @Composable
    override fun shape(): State<Shape> {
        return rememberUpdatedState(shape)
    }

    @Composable
    override fun color(): State<Color> {
        return rememberUpdatedState(color)
    }

    @Composable
    override fun border(): State<BorderStroke?> {
        return rememberUpdatedState(border)
    }
}


