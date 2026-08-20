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

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.android.deskclock.R;
import com.android.deskclock.uidata.UiDataModel;
import com.android.deskclock.worldclock.CitySelectionActivity;

/**
 * {@link MenuItemController} for opening city selection from the top action bar in Clock tab.
 */
public final class CitiesMenuItemController implements MenuItemController {

    private static final int CITIES_MENU_RES_ID = R.id.menu_item_cities;

    private final Activity mActivity;

    public CitiesMenuItemController(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getId() {
        return CITIES_MENU_RES_ID;
    }

    @Override
    public void onCreateOptionsItem(Menu menu) {
        menu.add(NONE, CITIES_MENU_RES_ID, 0, R.string.button_cities)
                .setIcon(R.drawable.ic_add_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsItem(MenuItem item) {
        item.setVisible(UiDataModel.getUiDataModel().getSelectedTab() == UiDataModel.Tab.CLOCKS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mActivity.startActivity(new Intent(mActivity, CitySelectionActivity.class));
        return true;
    }
}
