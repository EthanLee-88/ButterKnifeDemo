package com.butterknife;

import androidx.annotation.UiThread;

public interface Unbinder {
    @UiThread
    void unBind();

    Unbinder EMPTY = () -> { };
}