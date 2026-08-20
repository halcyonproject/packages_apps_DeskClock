/*
 * Copyright (C) 2024 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock.timer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlin.math.abs

private val ITEM_HEIGHT = 70.dp
private val PILL_BG_COLOR = Color(0xFF1B1D29)
private const val ITEM_COUNT = 10_000

@Composable
fun TimerWheelPicker(
    hours: Int,
    minutes: Int,
    seconds: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        // Highlight background pill across the middle row (no divider lines)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                .background(
                    color = PILL_BG_COLOR,
                    shape = RoundedCornerShape(20.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Hours (0..99)
            WheelColumn(
                value = hours,
                maxValue = 100,
                unit = "H",
                onValueChange = onHoursChange,
                modifier = Modifier.weight(1f)
            )

            // Minutes (0..59)
            WheelColumn(
                value = minutes,
                maxValue = 60,
                unit = "M",
                onValueChange = onMinutesChange,
                modifier = Modifier.weight(1f)
            )

            // Seconds (0..59)
            WheelColumn(
                value = seconds,
                maxValue = 60,
                unit = "S",
                onValueChange = onSecondsChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelColumn(
    value: Int,
    maxValue: Int,
    unit: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val middle = remember(maxValue) {
        val half = ITEM_COUNT / 2
        half - (half % maxValue)
    }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = middle + value
    )

    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    // Sync external value changes (e.g. reset) to scroll state
    LaunchedEffect(value) {
        val currentSelected = lazyListState.firstVisibleItemIndex % maxValue
        if (currentSelected != value) {
            lazyListState.scrollToItem(middle + value)
        }
    }

    // Monitor scroll position changes to update value & trigger haptic feedback
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .drop(1)
            .collect { index ->
                val newVal = index % maxValue
                currentOnValueChange(newVal)
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
    }

    Box(
        modifier = modifier.height(ITEM_HEIGHT * 3),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = lazyListState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = ITEM_HEIGHT),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ITEM_COUNT) { index ->
                val itemValue = index % maxValue

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .graphicsLayer {
                            val itemInfo = lazyListState.layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == index }
                            if (itemInfo != null) {
                                val containerCenter = (lazyListState.layoutInfo.viewportStartOffset +
                                        lazyListState.layoutInfo.viewportEndOffset) / 2f
                                val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                val distanceFromCenter = abs(itemCenter - containerCenter)
                                val progress = (distanceFromCenter / itemInfo.size).coerceIn(0f, 2.5f)

                                // Continuous smooth scaling from 1.0 (center) to 0.76 (outer)
                                val scale = (1f - progress * 0.18f).coerceIn(0.74f, 1f)
                                scaleX = scale
                                scaleY = scale

                                // Continuous smooth alpha from 1.0 (center) to 0.28 (outer)
                                alpha = (1f - progress * 0.55f).coerceIn(0.25f, 1f)

                                // Subtle 3D cylinder rotation
                                val signedProgress = (itemCenter - containerCenter) / itemInfo.size
                                rotationX = (signedProgress * 16f).coerceIn(-40f, 40f)
                            } else {
                                alpha = 0.25f
                                scaleX = 0.74f
                                scaleY = 0.74f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%02d".format(itemValue),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = unit,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .offset(y = 2.dp)
                                .graphicsLayer {
                                    val itemInfo = lazyListState.layoutInfo.visibleItemsInfo
                                        .firstOrNull { it.index == index }
                                    if (itemInfo != null) {
                                        val containerCenter = (lazyListState.layoutInfo.viewportStartOffset +
                                                lazyListState.layoutInfo.viewportEndOffset) / 2f
                                        val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                        val distanceFromCenter = abs(itemCenter - containerCenter)
                                        val progress = (distanceFromCenter / itemInfo.size).coerceIn(0f, 2f)
                                        // Unit label smoothly fades in/out as item approaches center
                                        alpha = (1f - progress * 2.2f).coerceIn(0f, 1f)
                                    } else {
                                        alpha = 0f
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}
