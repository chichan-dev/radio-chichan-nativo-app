package com.sliding;

import android.content.res.Resources;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

public class ViewUtil {
    // don't allow for instantiation
    private ViewUtil() {

    }

    /**
     * Adds a rectangular outline to a view. This can be useful when you want to add a shadow
     * to a transparent view. See b/16856049.
     * @param view view that the outline is added to
     * @param res The resources file.
     */
    public static void addRectangularOutlineProvider(View view, Resources res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider rectOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outline.setRect(0, 0, view.getWidth(), view.getHeight());
                    }
                }
            };

            view.setOutlineProvider(rectOutlineProvider);
        }
    }
}
