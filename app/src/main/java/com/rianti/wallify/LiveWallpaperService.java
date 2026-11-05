package com.rianti.wallify;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;

public class LiveWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new MotionEngine(); }

    class MotionEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint accent = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final long startedAt = System.currentTimeMillis();
        private boolean visible;
        private int width, height, style;
        private final int[][] palettes = {
            {0xff071b35, 0xff285b83, 0xffa77ac7},
            {0xff032b38, 0xff087e8b, 0xff72c9bb},
            {0xff321b45, 0xffb65370, 0xffffb36b},
            {0xff10102c, 0xff45428b, 0xffb58bd8},
            {0xff11151d, 0xff46515e, 0xffaab5c0}
        };
        private final Runnable frame = new Runnable() {
            @Override public void run() {
                if (!visible) return;
                renderFrame();
                handler.postDelayed(this, 42);
            }
        };

        MotionEngine() {
            style = getSharedPreferences("wallify", 0).getInt("live_style", 0);
            glow.setColor(0x55ffffff);
            accent.setColor(0x338be7ff);
        }

        @Override public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(frame);
            if (visible) handler.post(frame);
        }

        @Override public void onSurfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.onSurfaceChanged(holder, format, w, h);
            width = w; height = h;
            int[] p = palettes[Math.max(0, Math.min(style, palettes.length - 1))];
            background.setShader(new LinearGradient(0, 0, w, h, p[0], p[2], Shader.TileMode.CLAMP));
            renderFrame();
        }

        private void renderFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas == null || width <= 0 || height <= 0) return;
                float t = (System.currentTimeMillis() - startedAt) / 5000f;
                int[] p = palettes[Math.max(0, Math.min(style, palettes.length - 1))];
                canvas.drawRect(0, 0, width, height, background);
                Paint scenery = new Paint(Paint.ANTI_ALIAS_FLAG);
                scenery.setColor(0x55ffffff);
                for (int i = 0; i < 34; i++) {
                    float sx = (i * 97f) % width;
                    float sy = (i * 131f) % (height * 0.53f);
                    canvas.drawCircle(sx, sy, (i % 3) + 1, scenery);
                }
                Path farPeak = new Path();
                farPeak.moveTo(0, height * .72f);
                farPeak.lineTo(width * .18f, height * .38f);
                farPeak.lineTo(width * .31f, height * .61f);
                farPeak.lineTo(width * .55f, height * .29f);
                farPeak.lineTo(width * .78f, height * .64f);
                farPeak.lineTo(width, height * .4f);
                farPeak.lineTo(width, height);
                farPeak.lineTo(0, height);
                farPeak.close();
                scenery.setColor(0x88405070);
                canvas.drawPath(farPeak, scenery);
                Path nearPeak = new Path();
                nearPeak.moveTo(0, height * .82f);
                nearPeak.lineTo(width * .24f, height * .58f);
                nearPeak.lineTo(width * .43f, height * .79f);
                nearPeak.lineTo(width * .68f, height * .52f);
                nearPeak.lineTo(width, height * .8f);
                nearPeak.lineTo(width, height);
                nearPeak.lineTo(0, height);
                nearPeak.close();
                scenery.setColor(0xaa142b43);
                canvas.drawPath(nearPeak, scenery);
                scenery.setShader(new LinearGradient(0, height * .78f, 0, height,
                    0x5538a6c5, 0x0014263d, Shader.TileMode.CLAMP));
                canvas.drawRect(0, height * .78f, width, height, scenery);
                scenery.setShader(null);
                float x = (float)(width * (0.5 + 0.30 * Math.sin(t)));
                float y = (float)(height * (0.34 + 0.10 * Math.cos(t * 0.72)));
                glow.setColor(0x55ffffff);
                canvas.drawCircle(x, y, width * 0.22f, glow);
                accent.setColor((style == 1 ? 0x4476f2d0 : 0x338be7ff));
                float x2 = (float)(width * (0.5 + 0.28 * Math.cos(t * 0.63 + 1.2)));
                float y2 = (float)(height * (0.70 + 0.06 * Math.sin(t * 0.8)));
                canvas.drawCircle(x2, y2, width * 0.27f, accent);
                glow.setColor(0x18ffffff);
                canvas.drawRoundRect(width * 0.08f, height * 0.57f, width * 0.92f,
                    height * 0.89f, width * 0.12f, width * 0.12f, glow);
            } catch (RuntimeException ignored) {
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
        }

        @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(frame);
            super.onSurfaceDestroyed(holder);
        }

        @Override public void onDestroy() {
            visible = false;
            handler.removeCallbacks(frame);
            super.onDestroy();
        }
    }
}
