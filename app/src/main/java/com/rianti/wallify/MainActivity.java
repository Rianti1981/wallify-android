package com.rianti.wallify;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private final String[] names = {"Aurora", "Ocean", "Sunset", "Midnight", "Candy", "Forest"};
    private final int[][] palettes = {
        {0xFF142850, 0xFF4EA1D3, 0xFFB8E0F2}, {0xFF003B46, 0xFF07575B, 0xFF66A5AD},
        {0xFF42275A, 0xFFD76D77, 0xFFFFAF7B}, {0xFF101820, 0xFF263A5B, 0xFF647AA3},
        {0xFF614385, 0xFF516395, 0xFFFBC8D5}, {0xFF134E5E, 0xFF3A7D44, 0xFFB5AC49}
    };
    private int selected = 0;
    private Bitmap customBitmap;
    private LinearLayout previewHolder;
    private TextView selectedName, musicStatus;
    private MediaPlayer player;
    private Uri musicUri;
    private Button playButton;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(13, 18, 35));
        getWindow().setNavigationBarColor(Color.rgb(13, 18, 35));
        getWindow().getDecorView().setSystemUiVisibility(0);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF101B38, 0xFF17152F, 0xFF080F20}));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(25), dp(20), dp(30));
        scroll.addView(page);

        TextView brand = text("W A L L I F Y   /   2.0", 12, 0xFFBBD4FF, true);
        page.addView(brand);
        TextView title = text("A little more\nliquid. A lot more you.", 30, Color.WHITE, true);
        title.setPadding(0, dp(12), 0, dp(7));
        page.addView(title);
        page.addView(text("Glass-inspired wallpapers for your screen.", 14, 0xFFB8C4DE, false));

        TextView label = text("LIVE PREVIEW  ·  SWIPE TO EXPLORE", 10, 0xFFBBD4FF, true);
        label.setPadding(0, dp(26), 0, dp(11));
        page.addView(label);
        previewHolder = new LinearLayout(this);
        previewHolder.setGravity(Gravity.CENTER);
        previewHolder.setPadding(dp(12), dp(12), dp(12), dp(12));
        previewHolder.setBackground(glass(0x2BFFFFFF, 28));
        LinearLayout.LayoutParams previewLp = new LinearLayout.LayoutParams(-1, dp(320));
        page.addView(previewHolder, previewLp);
        showPreview();

        selectedName = text(names[selected], 17, Color.WHITE, true);
        selectedName.setGravity(Gravity.CENTER);
        selectedName.setPadding(0, dp(12), 0, dp(5));
        page.addView(selectedName);
        TextView hint = text("Swipe kiri atau kanan pada preview", 12, 0xFF9EACCA, false);
        hint.setGravity(Gravity.CENTER);
        page.addView(hint);

        section(page, "YOUR WALLPAPER");
        addButton(page, "＋  Pilih foto dari galeri", true, v -> pickImage());
        addButton(page, "Pasang di layar utama", false, v -> applyWallpaper(WallpaperManager.FLAG_SYSTEM));
        addButton(page, "Pasang di layar kunci", false, v -> applyWallpaper(WallpaperManager.FLAG_LOCK));
        addButton(page, "Pasang di keduanya", false, v -> applyWallpaper(WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK));

        section(page, "YOUR SOUNDTRACK");
        LinearLayout musicCard = new LinearLayout(this);
        musicCard.setOrientation(LinearLayout.VERTICAL);
        musicCard.setPadding(dp(16), dp(14), dp(16), dp(14));
        musicCard.setBackground(glass(0x25FFFFFF, 22));
        page.addView(musicCard, new LinearLayout.LayoutParams(-1, -2));
        musicStatus = text("Belum ada musik dipilih", 13, 0xFFDCE7FF, false);
        musicCard.addView(musicStatus);
        addButton(musicCard, "Pilih musik dari HP", false, v -> pickMusic());
        playButton = addButton(musicCard, "▶  Putar musik", true, v -> toggleMusic());
        TextView footer = text("WALLIFY 2.0  •  MADE FOR YOUR SCREEN", 10, 0xFF8796B8, true);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(24), 0, 0);
        page.addView(footer);
        setContentView(scroll);
    }

    private void section(LinearLayout parent, String name) {
        TextView t = text(name, 10, 0xFFBBD4FF, true);
        t.setPadding(2, dp(24), 0, dp(10));
        parent.addView(t);
    }

    private Button addButton(LinearLayout parent, String label, boolean bright, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(glass(bright ? 0x557CA8FF : 0x28FFFFFF, 17));
        b.setElevation(dp(2));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(51));
        lp.topMargin = dp(8);
        parent.addView(b, lp);
        b.setOnClickListener(click);
        return b;
    }

    private void showPreview() {
        previewHolder.removeAllViews();
        View art = new ArtView(this);
        previewHolder.addView(art, new LinearLayout.LayoutParams(dp(174), dp(292)));
        if (selectedName != null) selectedName.setText(customBitmap == null ? names[selected] : "My custom wallpaper");
    }

    private class ArtView extends View {
        private final GestureDetector gestures;
        ArtView(Activity activity) {
            super(activity);
            gestures = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onDown(MotionEvent e) { return true; }
                @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                    float dx = e2.getX() - e1.getX();
                    if (Math.abs(dx) > dp(45) && Math.abs(dx) > Math.abs(e2.getY() - e1.getY())) {
                        customBitmap = null;
                        selected = (selected + (dx < 0 ? 1 : names.length - 1)) % names.length;
                        showPreview();
                        return true;
                    }
                    return false;
                }
            });
        }
        @Override public boolean onTouchEvent(MotionEvent event) {
            gestures.onTouchEvent(event);
            return true;
        }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (customBitmap != null) {
                RectF dst = new RectF(0, 0, getWidth(), getHeight());
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                float scale = Math.max(dst.width() / customBitmap.getWidth(), dst.height() / customBitmap.getHeight());
                float w = customBitmap.getWidth() * scale, h = customBitmap.getHeight() * scale;
                canvas.save();
                canvas.clipRect(dst);
                canvas.drawBitmap(customBitmap, null, new RectF((getWidth()-w)/2, (getHeight()-h)/2, (getWidth()+w)/2, (getHeight()+h)/2), p);
                canvas.restore();
            } else {
                canvas.save();
                canvas.scale(getWidth() / 1080f, getHeight() / 2400f);
                drawArt(canvas, selected, 1080, 2400);
                canvas.restore();
            }
            Paint shine = new Paint(Paint.ANTI_ALIAS_FLAG);
            shine.setShader(new LinearGradient(0, 0, getWidth(), getHeight() * .55f,
                    new int[]{0x55FFFFFF, 0x08FFFFFF, 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), dp(20), dp(20), shine);
            shine.setShader(null);
            shine.setStyle(Paint.Style.STROKE);
            shine.setStrokeWidth(dp(1));
            shine.setColor(0x88FFFFFF);
            canvas.drawRoundRect(new RectF(.5f, .5f, getWidth()-.5f, getHeight()-.5f), dp(20), dp(20), shine);
        }
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 101);
    }

    private void pickMusic() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("audio/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 102);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == 101) {
            try (InputStream in = getContentResolver().openInputStream(uri)) {
                Bitmap picked = BitmapFactory.decodeStream(in);
                if (picked == null) throw new IOException("Unsupported image");
                customBitmap = picked;
                showPreview();
                Toast.makeText(this, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Foto tidak bisa dibuka", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 102) {
            musicUri = uri;
            stopMusic();
            musicStatus.setText("Musik siap diputar");
            playButton.setText("▶  Putar musik");
        }
    }

    private void toggleMusic() {
        if (musicUri == null) {
            Toast.makeText(this, "Pilih file musik dulu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (player != null && player.isPlaying()) {
            player.pause();
            playButton.setText("▶  Lanjutkan musik");
            return;
        }
        if (player != null) {
            player.start();
            playButton.setText("Ⅱ  Jeda musik");
            return;
        }
        try {
            player = new MediaPlayer();
            player.setDataSource(this, musicUri);
            player.setOnPreparedListener(mp -> {
                mp.start();
                playButton.setText("Ⅱ  Jeda musik");
                musicStatus.setText("Sedang memutar musik");
            });
            player.setOnCompletionListener(mp -> {
                playButton.setText("▶  Putar lagi");
                musicStatus.setText("Musik selesai");
            });
            player.setOnErrorListener((mp, what, extra) -> {
                stopMusic();
                musicStatus.setText("File audio tidak dapat diputar");
                return true;
            });
            player.prepareAsync();
            playButton.setText("Memuat musik…");
        } catch (IOException e) {
            stopMusic();
            Toast.makeText(this, "Tidak bisa membuka file audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopMusic() {
        if (player != null) {
            try { player.stop(); } catch (IllegalStateException ignored) {}
            player.release();
            player = null;
        }
    }

    private void applyWallpaper(int flags) {
        try {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            if (customBitmap != null) {
                if (Build.VERSION.SDK_INT >= 24) manager.setBitmap(customBitmap, null, true, flags);
                else manager.setBitmap(customBitmap);
            } else {
                Bitmap bitmap = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888);
                drawArt(new Canvas(bitmap), selected, 1080, 2400);
                if (Build.VERSION.SDK_INT >= 24) manager.setBitmap(bitmap, null, true, flags);
                else manager.setBitmap(bitmap);
                bitmap.recycle();
            }
            Toast.makeText(this, "Wallpaper berhasil dipasang!", Toast.LENGTH_LONG).show();
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "Gagal memasang wallpaper. Coba lagi.", Toast.LENGTH_LONG).show();
        }
    }

    private void drawArt(Canvas canvas, int index, int width, int height) {
        int[] p = palettes[index];
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(0, 0, width, height, p[0], p[2], Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);
        paint.setColor(p[1]);
        canvas.drawCircle(width * .76f, height * .29f, width * .31f, paint);
        paint.setColor(0x44FFFFFF);
        canvas.drawCircle(width * .18f, height * .68f, width * .42f, paint);
        paint.setColor(0x33FFFFFF);
        canvas.drawRoundRect(new RectF(width*.08f, height*.52f, width*.92f, height*.83f), width*.12f, width*.12f, paint);
        paint.setColor(0x22FFFFFF);
        canvas.drawCircle(width*.55f, height*.86f, width*.25f, paint);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private GradientDrawable glass(int color, int radius) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{color | 0x00000000, color, color | 0x00000000});
        d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), 0x66FFFFFF);
        return d;
    }

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }

    @Override protected void onDestroy() {
        stopMusic();
        super.onDestroy();
    }
}
