/*
 * Copyright (C) 2013 Square, Inc.
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
package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.ImageView;

import static android.graphics.Color.WHITE;
import static com.squareup.picasso.Picasso.LoadedFrom.MEMORY;

/**
 * Picasso处理图片
 */
final class PicassoDrawable extends BitmapDrawable {
    // Only accessed from main thread.
    private static final Paint DEBUG_PAINT = new Paint();
    private static final float FADE_DURATION = 200f; //ms

    /**
     * Create or update the drawable on the target {@link ImageView} to display the supplied bitmap
     * image.
     */
    static void setBitmap(ImageView target, Context context, Bitmap bitmap,
                          Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        Drawable placeholder = target.getDrawable();
        if (placeholder instanceof AnimationDrawable) {
            ((AnimationDrawable) placeholder).stop();
        }
        PicassoDrawable drawable =
                new PicassoDrawable(context, bitmap, placeholder, loadedFrom, noFade, debugging);
        target.setImageDrawable(drawable);
    }

    /**
     * Create or update the drawable on the target {@link ImageView} to display the supplied
     * placeholder image.
     * 设置ImageView背景，如果动画，执行动画
     */
    static void setPlaceholder(ImageView target, Drawable placeholderDrawable) {
        target.setImageDrawable(placeholderDrawable);
        if (target.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) target.getDrawable()).start();
        }
    }

    private final boolean debugging;
    private final float density;
    private final Picasso.LoadedFrom loadedFrom;

    Drawable placeholder;

    long startTimeMillis;
    boolean animating;
    int alpha = 0xFF;

    PicassoDrawable(Context context, Bitmap bitmap, Drawable placeholder,
                    Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        super(context.getResources(), bitmap);

        this.debugging = debugging;
        this.density = context.getResources().getDisplayMetrics().density;

        this.loadedFrom = loadedFrom;

        boolean fade = loadedFrom != MEMORY && !noFade;
        if (fade) {
            this.placeholder = placeholder;
            animating = true;
            startTimeMillis = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!animating) {
            super.draw(canvas);
        } else {
            float normalized = (SystemClock.uptimeMillis() - startTimeMillis) / FADE_DURATION;
            if (normalized >= 1f) {
                animating = false;
                placeholder = null;
                super.draw(canvas);
            } else {
                if (placeholder != null) {
                    placeholder.draw(canvas);
                }

                // setAlpha will call invalidateSelf and drive the animation.
                int partialAlpha = (int) (alpha * normalized);
                super.setAlpha(partialAlpha);
                super.draw(canvas);
                super.setAlpha(alpha);
            }
        }

        if (debugging) {
            drawDebugIndicator(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        if (placeholder != null) {
            placeholder.setAlpha(alpha);
        }
        super.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (placeholder != null) {
            placeholder.setColorFilter(cf);
        }
        super.setColorFilter(cf);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (placeholder != null) {
            placeholder.setBounds(bounds);
        }
        super.onBoundsChange(bounds);
    }

    /**
     * 绘制调试指示灯
     *
     * @param canvas
     */
    private void drawDebugIndicator(Canvas canvas) {
        DEBUG_PAINT.setColor(WHITE);
        Path path = getTrianglePath(0, 0, (int) (16 * density));
        canvas.drawPath(path, DEBUG_PAINT);

        DEBUG_PAINT.setColor(loadedFrom.debugColor);
        path = getTrianglePath(0, 0, (int) (15 * density));
        canvas.drawPath(path, DEBUG_PAINT);
    }

    private static Path getTrianglePath(int x1, int y1, int width) {
        final Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x1 + width, y1);
        path.lineTo(x1, y1 + width);

        return path;
    }
}
