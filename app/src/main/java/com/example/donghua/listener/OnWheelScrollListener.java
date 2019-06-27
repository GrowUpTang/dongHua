package com.example.donghua.listener;

import com.example.donghua.view.PickerScrollView;

/**
 * Wheel scrolled listener interface.
 */
public interface OnWheelScrollListener {
    /**
     * Callback method to be invoked when scrolling started.
     *
     * @param wheel the wheel view whose state has changed.
     */
    void onScrollingStarted(PickerScrollView wheel);

    /**
     * Callback method to be invoked when scrolling ended.
     *
     * @param wheel the wheel view whose state has changed.
     */
    void onScrollingFinished(PickerScrollView wheel);
}
