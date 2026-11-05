package com.rianti.wallify;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {
    private final String[] names = {"Aurora", "Ocean", "Sunset", "Midnight", "Candy", "Forest"};
    private final int[][] palettes = {
        {0xFF142850, 0xFF4EA1D3, 0xFFB8E0F2},
        {0xFF003B46, 0xFF07575B, 0xFF66A5AD},
        {0xFF42275A, 0xFFD76D77, 0xFFFFAF7B},
        {0xFF101820, 0xFF263A5B, 0xFF647AA3},
        {0xFF614385, 0xFF516395, 0xFFFBC8D5},
        {0xFF134E5E, 0xFF3A7D44, 0xFFB5AC49}
    };
    private int selected = 0;
    private LinearLayout previewHolder;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(15, 19, 35));
        getWindow().setNavigationBarColor(Color.rgb(15, 19, 35));

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(15, 19, 35));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(24), dp(20), dp(28));
        scroll.addView(page);

        TextView brand = text("WALLIFY", 13, 0xFF9AB4FF, true);
        brand.setLetterSpacing(0.18f);
        page.addView(brand);

        TextView title = text("Wallpaper yang
bikin layar hidup.", 29, Color.WHITE, true);
        title.setPadding(0, dp(12), 0, dp(6));
        page.addView(title);
        TextView sub = text("Pilih desain favorit, lalu pasang langsung.", 14, 0xFFADB6CC, false);
        page.addView(sub);

        TextView previewLabel = text("PREVIEW", 11, 0xFF9AB4FF, true);
        previewLabel.setPadding(0, dp(26), 0, dp(10));
        page.addView(previewLabel);

        previewHolder = new LinearLayout(this);
        previewHolder.setGravity(Gravity.CENTER);
        previewHolder.setBackground(round(0xFF202943, 22));
        previewHolder.setPadding(dp(12), dp(12), dp(12), dp(12));
        page.addView(previewHolder, new LinearLayout.LayoutParams(-1, dp(300)));
        showPreview();

        TextView choose = text("PILIH WALLPAPER", 11, 0xFF9AB4FF, true);
        choose.setPadding(0, dp(24), 0, dp(12));
        page.addView(choose);

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            Button item = new Button(this);
            item.setText(names[i] + (i == selected ? "   ✓" : "   ›"));
            item.setAllCaps(false);
            item.setTextSize(15);
            item.setTextColor(Color.WHITE);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setBackground(round(0xFF252F49, 14));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(52));
            lp.bottomMargin = dp(8);
            page.addView(item, lp);
            item.setOnClickListener(v -> {
                selected = index;
                showPreview();
                Toast.makeText(this, names[index] + " dipilih", Toast.LENGTH_SHORT).show();
            });
        }

        Button home = actionButton("Pasang di layar utama", 0xFF7898FF);
        Button lock = actionButton("Pasang di layar kunci", 0xFF34415F);
        Button both = actionButton("Pasang di keduanya", 0xFF34415F);
        addAction(page, home, WallpaperManager.FLAG_SYSTEM);
        addAction(page, lock, WallpaperManager.FLAG_LOCK);
        addAction(page, both, WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);

        TextView note = text("Wallify • 6 wallpaper abstrak • Bisa digunakan offline", 11, 0xFF8490AA, false);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(22), 0, 0);
        page.addView(note);
        setContentView(scroll);
    }

    private void addAction(LinearLayout page, Button button, int flags) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(52));
        lp.topMargin = dp(9);
        page.addView(button, lp);
        button.setOnClickListener(v -> applyWallpaper(flags));
    }

    private void applyWallpaper(int flags) {
        try {
            Bitmap bitmap = createWallpaper(selected, 1080, 2400);
            WallpaperManager manager = WallpaperManager.getInstance(this);
            if (Build.VERSION.SDK_INT >= 24) manager.setBitmap(bitmap, null, true, flags);
            else manager.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper berhasil dipasang!", Toast.LENGTH_LONG).show();
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "Gagal memasang wallpaper. Coba lagi.", Toast.LENGTH_LONG).show();
        }
    }

    private void showPreview() {
        previewHolder.removeAllViews();
        View art = new ArtView(this, selected);
        previewHolder.addView(art, new LinearLayout.LayoutParams(dp(158), dp(274)));
    }

    private Bitmap createWallpaper(int index, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawArt(canvas, index, width, height);
        return bitmap;
    }

    private void drawArt(Canvas canvas, int index, int width, int height) {
        int[] p = palettes[index];
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(0, 0, width, height, p[0], p[2], Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);
        paint.setColor(p[1]);
        canvas.drawCircle(width * 0.76f, height * 0.29f, width * 0.31f, paint);
        paint.setColor(0x44FFFFFF);
        canvas.drawCircle(width * 0.18f, height * 0.68f, width * 0.42f, paint);
        paint.setColor(0x33FFFFFF);
        canvas.drawRoundRect(new RectF(width * 0.08f, height * 0.52f, width * 0.92f, height * 0.83f), width * 0.12f, width * 0.12f, paint);
        paint.setColor(0x22FFFFFF);
        canvas.drawCircle(width * 0.55f, height * 0.86f, width * 0.25f, paint);
    }

    private class ArtView extends View {
        private final int index;
        ArtView(Activity activity, int index) { super(activity); this.index = index; }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            canvas.scale(getWidth() / 1080f, getHeight() / 2400f);
            drawArt(canvas, index, 1080, 2400);
            canvas.restore();
        }
    }

    private Button actionButton(String label, int color) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setBackground(round(color, 16));
        return b;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        return t;
    }

    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
}
