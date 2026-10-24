/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.deskclock.widget.toast;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import com.android.deskclock.R;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

/**
 * Manages visibility of Snackbar and allow preemptive dismiss of current displayed Snackbar.
 */
public final class SnackbarManager {

    private static WeakReference<Snackbar> sSnackbar = null;

    private SnackbarManager() {}

    public static void show(Snackbar snackbar) {
        sSnackbar = new WeakReference<>(snackbar);
        if (snackbar.getAnchorView() == null) {
            final View bottomView = findBottomView(snackbar);
            if (bottomView != null) {
                snackbar.setAnchorView(bottomView);
            }
        }
        snackbar.show();
    }

    public static void dismiss() {
        final Snackbar snackbar = sSnackbar == null ? null : sSnackbar.get();
        if (snackbar != null) {
            snackbar.dismiss();
            sSnackbar = null;
        }
    }

    private static View findBottomView(Snackbar snackbar) {
        Context context = snackbar.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                final View bottomView = ((Activity) context).findViewById(R.id.bottom_view);
                if (bottomView != null && bottomView.getVisibility() == View.VISIBLE) {
                    return bottomView;
                }
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        final View snackbarView = snackbar.getView();
        final View rootView = snackbarView.getRootView();
        if (rootView != null) {
            final View bottomView = rootView.findViewById(R.id.bottom_view);
            if (bottomView != null && bottomView.getVisibility() == View.VISIBLE) {
                return bottomView;
            }
        }
        return null;
    }
}
