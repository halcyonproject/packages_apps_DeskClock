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

package com.android.deskclock.actionbarmenu;

import static android.view.Menu.NONE;

import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.R;
import com.android.deskclock.uidata.UiDataModel;

/**
 * {@link MenuItemController} for adding an alarm from the top action bar.
 */
public final class AddAlarmMenuItemController implements MenuItemController {

    private static final int ADD_ALARM_MENU_RES_ID = R.id.menu_item_add_alarm;

    private final FragmentActivity mActivity;

    public AddAlarmMenuItemController(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getId() {
        return ADD_ALARM_MENU_RES_ID;
    }

    @Override
    public void onCreateOptionsItem(Menu menu) {
        menu.add(NONE, ADD_ALARM_MENU_RES_ID, 0, R.string.button_alarms)
                .setIcon(R.drawable.ic_add_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsItem(MenuItem item) {
        item.setVisible(UiDataModel.getUiDataModel().getSelectedTab() == UiDataModel.Tab.ALARMS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (Fragment fragment : mActivity.getSupportFragmentManager().getFragments()) {
            if (fragment instanceof AlarmClockFragment && fragment.isVisible()) {
                ((AlarmClockFragment) fragment).startCreatingAlarm();
                return true;
            }
        }
        return false;
    }
}
