/*
 * Copyright (C) 2015 Hippo Seven
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

package com.hippo.ripple;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

import com.hippo.hotspot.Hotspot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Ripple {
    private Ripple() {}

    private static final int RIPPLE_MATERIAL_DARK = 0x4dffffff;
    private static final int RIPPLE_MATERIAL_LIGHT = 0x1f000000;

    private static Method sSetTargetDensityMethod;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Class<?> clazz = android.graphics.drawable.RippleDrawable.class;
            try {
                sSetTargetDensityMethod = clazz.getDeclaredMethod("setTargetDensity", DisplayMetrics.class);
                sSetTargetDensityMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private static void applyDensity(Context context, android.graphics.drawable.RippleDrawable rippleDrawable) {
        if (sSetTargetDensityMethod == null) {
            return;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        try {
            sSetTargetDensityMethod.invoke(rippleDrawable, displayMetrics);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void addRipple(@NonNull View c, boolean dark) {
        ColorStateList color = ColorStateList.valueOf(
                dark ? RIPPLE_MATERIAL_DARK : RIPPLE_MATERIAL_LIGHT);
        addRipple(c, color);
    }

    public static void addRipple(@NonNull View v, @NonNull ColorStateList color) {
        Drawable bg = v.getBackground();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (bg instanceof RippleDrawable) {
                return;
            }
        } else {
            if (bg instanceof android.graphics.drawable.RippleDrawable) {
                return;
            }
        }
        addRipple(v, color, v.getBackground());
    }

    @SuppressWarnings("deprecation")
    public static void addRipple(@NonNull View v, @NonNull ColorStateList color,
            @Nullable Drawable content) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable rippleDrawable = new RippleDrawable(v.getContext(), color, content);
            Hotspot.addHotspotable(v, rippleDrawable);
            v.setBackgroundDrawable(rippleDrawable);
        } else {
            android.graphics.drawable.RippleDrawable rippleDrawable =
                    new android.graphics.drawable.RippleDrawable(color, content, new ColorDrawable(Color.BLACK));
            applyDensity(v.getContext(), rippleDrawable);
            v.setBackground(rippleDrawable);
        }
    }

    public static Drawable generateRippleDrawable(@NonNull Context context, boolean dark) {
        return generateRippleDrawable(context, dark, null);
    }

    public static Drawable generateRippleDrawable(@NonNull Context context,
            @NonNull ColorStateList color) {
        return generateRippleDrawable(context, color, null);
    }

    public static Drawable generateRippleDrawable(@NonNull Context context,
            boolean dark, @Nullable Drawable content) {
        ColorStateList color = ColorStateList.valueOf(
                dark ? RIPPLE_MATERIAL_DARK : RIPPLE_MATERIAL_LIGHT);
        return generateRippleDrawable(context, color, content);
    }

    public static Drawable generateRippleDrawable(@NonNull Context context,
            @NonNull ColorStateList color, @Nullable Drawable content) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(context, color, content);
        } else {
            android.graphics.drawable.RippleDrawable rippleDrawable =
                    new android.graphics.drawable.RippleDrawable(color, content, new ColorDrawable(Color.BLACK));
            applyDensity(context, rippleDrawable);
            return rippleDrawable;
        }
    }
}
