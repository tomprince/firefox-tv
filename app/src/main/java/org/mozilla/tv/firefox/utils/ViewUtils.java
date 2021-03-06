/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.StringRes;
import org.mozilla.tv.firefox.R;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;

public class ViewUtils {

    /**
     * Runnable to show the keyboard for a specific view.
     */
    private static class ShowKeyboard implements Runnable {
        private static final int INTERVAL_MS = 100;

        private final WeakReference<View> viewReferemce;
        private final Handler handler;

        private int tries;

        private ShowKeyboard(View view) {
            this.viewReferemce = new WeakReference<>(view);
            this.handler = new Handler(Looper.getMainLooper());
            this.tries = 10;
        }

        @Override
        public void run() {
            if (tries <= 0) {
                return;
            }

            final View view = viewReferemce.get();
            if (view == null) {
                // The view is gone. No need to continue.
                return;
            }

            if (!view.isFocusable() || !view.isFocusableInTouchMode()) {
                // The view is not focusable - we can't show the keyboard for it.
                return;
            }

            if (!view.requestFocus()) {
                // Focus this view first.
                post();
                return;
            }

            final Activity activity = (Activity) view.getContext();
            if (activity == null) {
                return;
            }

            final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }

            if (!imm.isActive(view)) {
                // This view is not the currently active view for the input method yet.
                post();
                return;
            }

            if (!imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)) {
                // Showing they keyboard failed. Try again later.
                post();
            }
        }

        private void post() {
            tries--;
            handler.postDelayed(this, INTERVAL_MS);
        }
    }

    public static void showKeyboard(View view) {
        final ShowKeyboard showKeyboard = new ShowKeyboard(view);
        showKeyboard.post();
    }

    public static boolean hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return false;
        }

        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isRTL(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static void showCenteredTopToast(Context context, int resId) {
        showToast(context, resId, "top");
    }

    public static void showCenteredBottomToast(Context context, int resId) {
        showToast(context, resId, "bottom");
    }

    private static void showToast(final Context context, @StringRes final int resId, final String toastLocation) {
        final String text = context.getResources().getString(resId);
        showToast(context, text, toastLocation);
    }

    public static void showCenteredBottomToast(final Context context, final String text) {
        showToast(context, text, "bottom");
    }

    private static void showToast(final Context context, final String text, final String toastLocation) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        final TextView textView = layout.findViewById(R.id.toast_text);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        if (toastLocation.equals("top")) {
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 200);
        }
        if (toastLocation.equals("bottom")) {
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
        }
        toast.show();
    }
}
