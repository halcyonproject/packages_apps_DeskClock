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

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.android.deskclock.FabContainer
import com.android.deskclock.FabContainer.FAB_SHRINK_AND_EXPAND
import org.hlcyn.ui.theme.HalcyonTheme
import java.io.Serializable

class TimerSetupView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var hours by mutableIntStateOf(0)
    var minutes by mutableIntStateOf(0)
    var seconds by mutableIntStateOf(0)

    /** Updates to the fab are requested via this container. */
    private var fabContainer: FabContainer? = null

    init {
        val composeView = ComposeView(context)
        addView(composeView)
        composeView.setContent {
            HalcyonTheme(darkTheme = true) {
                TimerWheelPicker(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    onHoursChange = {
                        hours = it
                        updateFab()
                    },
                    onMinutesChange = {
                        minutes = it
                        updateFab()
                    },
                    onSecondsChange = {
                        seconds = it
                        updateFab()
                    }
                )
            }
        }
    }

    fun setFabContainer(container: FabContainer?) {
        fabContainer = container
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    private fun updateFab() {
        fabContainer?.updateFab(FAB_SHRINK_AND_EXPAND)
    }

    fun reset() {
        hours = 0
        minutes = 0
        seconds = 0
        updateFab()
    }

    fun hasValidInput(): Boolean {
        return getTimeInMillis() > 0
    }

    fun getTimeInMillis(): Long {
        return seconds * DateUtils.SECOND_IN_MILLIS +
                minutes * DateUtils.MINUTE_IN_MILLIS +
                hours * DateUtils.HOUR_IN_MILLIS
    }

    fun getState(): Serializable {
        return intArrayOf(hours, minutes, seconds)
    }

    fun setState(state: Serializable?) {
        if (state is IntArray && state.size == 3) {
            hours = state[0]
            minutes = state[1]
            seconds = state[2]
            updateFab()
        }
    }
}
