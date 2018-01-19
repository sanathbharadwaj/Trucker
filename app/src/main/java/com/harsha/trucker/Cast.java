package com.harsha.trucker;

/**
 * Created by LENOVO on 14-01-2018.
 */

public final class Cast
{
    private Cast() {}

    /**
     * Helps to eliminate annoying NullPointerException lint warning.
     */
    @android.support.annotation.NonNull
    public static <T> T neverNull(T value)
    {
        return value;
    }
}