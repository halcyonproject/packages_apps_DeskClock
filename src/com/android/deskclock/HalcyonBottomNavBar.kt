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

package com.android.deskclock

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.ComposeView
import org.hlcyn.ui.components.HalcyonFloatingBottomBar
import org.hlcyn.ui.components.HalcyonFloatingBottomBarItem
import org.hlcyn.ui.theme.HalcyonTheme

/**
 * A floating bottom navigation bar for DeskClock, built with [HalcyonFloatingBottomBar].
 * Hosts the four tabs: Alarm, Clock, Timer, Stopwatch.
 *
 * Usage from Java:
 * <pre>
 *   halcyonBottomNavBar.setOnTabSelectedListener(tab -> { ... });
 *   halcyonBottomNavBar.setSelectedTab(R.id.page_clock);
 *   int current = halcyonBottomNavBar.getSelectedTab(); // returns R.id.page_*
 * </pre>
 */
class HalcyonBottomNavBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /** Listener called when a tab is selected by the user. */
    fun interface OnTabSelectedListener {
        fun onTabSelected(tabResId: Int)
    }

    private val selectedTabState = mutableIntStateOf(R.id.page_alarm)
    private var listener: OnTabSelectedListener? = null

    init {
        val composeView = ComposeView(context)
        addView(composeView)
        composeView.setContent {
            HalcyonTheme(darkTheme = true) {
                HalcyonFloatingBottomBar {
                    HalcyonFloatingBottomBarItem(
                        selected = selectedTabState.value == R.id.page_alarm,
                        onClick = { selectTab(R.id.page_alarm) },
                        icon = Icons.Default.Alarm
                    )
                    HalcyonFloatingBottomBarItem(
                        selected = selectedTabState.value == R.id.page_clock,
                        onClick = { selectTab(R.id.page_clock) },
                        icon = Icons.Default.AccessTime
                    )
                    HalcyonFloatingBottomBarItem(
                        selected = selectedTabState.value == R.id.page_timer,
                        onClick = { selectTab(R.id.page_timer) },
                        icon = Icons.Default.Timer
                    )
                    HalcyonFloatingBottomBarItem(
                        selected = selectedTabState.value == R.id.page_stopwatch,
                        onClick = { selectTab(R.id.page_stopwatch) },
                        icon = Icons.Default.Timelapse
                    )
                }
            }
        }
    }

    /**
     * Set the currently selected tab without triggering [OnTabSelectedListener].
     * Called by [DeskClock] to sync the view with UiDataModel.
     */
    fun setSelectedTab(tabResId: Int) {
        selectedTabState.value = tabResId
    }

    /** Returns the currently selected tab resource id (e.g. [R.id.page_alarm]). */
    fun getSelectedTab(): Int = selectedTabState.value

    /** Set a listener that is invoked when the user taps a tab. */
    fun setOnTabSelectedListener(l: OnTabSelectedListener) {
        listener = l
    }

    /** Set a listener from Java lambda / SAM conversion. */
    fun setOnTabSelectedListener(l: (Int) -> Unit) {
        listener = OnTabSelectedListener { tabResId -> l(tabResId) }
    }

    private fun selectTab(tabResId: Int) {
        selectedTabState.value = tabResId
        listener?.onTabSelected(tabResId)
    }
}
