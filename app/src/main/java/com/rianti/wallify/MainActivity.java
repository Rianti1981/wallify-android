package com.rianti.wallify;

import android.app.*;
import android.app.WallpaperManager;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.service.wallpaper.WallpaperService;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    String[] cats={"Semua","Warna","Pemandangan","Cityscapes","Life","Textures","Custom"};
    String[] names={"Aurora","Ocean","Sunset","Midnight","Candy","Forest","Dune","Misty Hills","Geometry","Soft Forms","Feather","Stone"};
    String[] types={"Warna","Warna","Warna","Warna","Warna","Pemandangan","Pemandangan","Pemandangan","Cityscapes","Life","Textures","Textures"};
    int[][] colors={{0xff142850,0xff4ea1d3,0xffb8e0f2},{0xff003b46,0xff07575b,0xff66a5ad},{0xff42275a,0xffd76d77,0xffffaf7b},{0xff101820,0xff263a5b,0xff647aa3},{0xff614385,0xff516395,0xfffbc8d5},{0xff134e5e,0xff3a7d44,0xffb5ac49},{0xff292d3e,0xffaaa6a0,0xffe8ded1},{0xff61758b,0xffaeb9bd,0xffe8e4df},{0xff9d5140,0xfff5dcd0,0xff74c8c9},{0xffd6c9b8,0xfff5eee5,0xffb9a38b},{0xffe8e8e5,0xffc9cfca,0xfff8f8f4},{0xffc8b49b,0xffeee2d3,0xff8c7664}};
    int selected=0; String category="Semua", theme="dark"; boolean settings=false; Bitmap custom;
    LinearLayout root,content,nav; TextView title,subtitle; float downX,downY; MediaPlayer player; Uri music;
    int bg(){return theme.equals("light")?0xfff4f2ef:0xff08090d;} int fg(){return theme.equals("light")?0xff171717:Color.WHITE;} int muted(){return theme.equals("light")?0xff6b6b6b:0xffa8a8b4;}
    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    GradientDrawable shape(int c,int stroke,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(dp(r));d.setStroke(dp(1),stroke);return d;}
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    TextView txt(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg());if(bold)t.setTypeface(null,1);return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void build(){
      getWindow().setStatusBarColor(bg());getWindow().setNavigationBarColor(bg());getWindow().getDecorView().setSystemUiVisibility(theme.equals("light")?View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR:0);
      root=column();root.setBackgroundColor(bg());
      LinearLayout header=column();header.setPadding(dp(20),dp(18),dp(20),dp(12));root.addView(header);
      TextView brand=txt("W A L L I F Y   /   3.0",11,true);brand.setTextColor(0xff9b9bff);header.addView(brand);
      title=txt(settings?"Settings":"Discover",30,true);title.setPadding(0,dp(12),0,dp(3));header.addView(title);
      subtitle=txt(settings?"Make it feel like yours":"Wallpaper collections",14,false);subtitle.setTextColor(muted());header.addView(subtitle);
      ScrollView sc=new ScrollView(this);content=column();content.setPadding(dp(16),dp(8),dp(16),dp(100));sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
      nav=row();nav.setPadding(dp(8),dp(8),dp(8),dp(8));nav.setBackground(shape(theme.equals("light")?0xeeffffff:0xee24242b,0x55ffffff,28));
      LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,dp(64));np.setMargins(dp(18),0,dp(18),dp(12));root.addView(nav,np);
      addNav("▦   Wallpaper",false,()->{settings=false;build();});addNav("⚙   Settings",true,()->{settings=true;build();});
      nav.setOnTouchListener((v,e)->{if(e.getAction()==0){downX=e.getRawX();downY=e.getRawY();return true;}if(e.getAction()==2){float dx=e.getRawX()-downX;float dy=e.getRawY()-downY;nav.setTranslationX(Math.max(-dp(70),Math.min(dp(70),nav.getTranslationX()+dx)));nav.setTranslationY(Math.max(-dp(22),Math.min(dp(22),nav.getTranslationY()+dy)));downX=e.getRawX();downY=e.getRawY();return true;}if(e.getAction()==1){if(Math.abs(e.getRawX()-downX)<dp(8)&&Math.abs(e.getRawY()-downY)<dp(8)){if(e.getX()<nav.getWidth()/2){settings=false;}else{settings=true;}build();}return true;}return true;});
      setContentView(root);if(settings)showSettings();else showWallpapers();
    }
    void addNav(String label,boolean isSettings,Runnable action){TextView t=txt(label,13,settings==isSettings);t.setGravity(Gravity.CENTER);t.setPadding(dp(8),dp(12),dp(8),dp(12));t.setBackground(settings==isSettings?shape(0x447c83ff,0x668c91ff,22):shape(Color.TRANSPARENT,Color.TRANSPARENT,22));nav.addView(t,new LinearLayout.LayoutParams(0,-1,1));t.setOnClickListener(v->action.run());}
    void showWallpapers(){
      TextView featured=txt("Explore your mood",20,true);content.addView(featured);TextView hint=txt("Pilih koleksi, lalu ketuk wallpaper favoritmu.",13,false);hint.setTextColor(muted());content.addView(hint);
      LinearLayout chips=row();HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);for(String c:cats){TextView x=txt(c,12,true);x.setPadding(dp(14),dp(10),dp(14),dp(10));x.setBackground(shape(category.equals(c)?0xff7779db:0x33222222,0x55ffffff,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2);p.setMargins(0,dp(14),dp(8),dp(14));chips.addView(x,p);x.setOnClickListener(v->{category=c;showWallpapers();});}hsv.addView(chips);content.addView(hsv);
      if(category.equals("Custom")){TextView c=txt("Wallpaper dari galeri",17,true);content.addView(c);button("＋  Pilih wallpaper dari galeri",()->pick(101));if(custom!=null){View preview=new Art(this,selected);content.addView(preview,new LinearLayout.LayoutParams(-1,dp(280)));TextView mine=txt("My Custom Wallpaper  ·  Ketuk untuk melihat",14,true);mine.setPadding(0,dp(10),0,dp(12));content.addView(mine);mine.setOnClickListener(v->showDetail());button("Terapkan wallpaper custom",()->apply(WallpaperManager.FLAG_SYSTEM|WallpaperManager.FLAG_LOCK));}return;}
      ArrayList<Integer> filtered=new ArrayList<>();for(int i=0;i<names.length;i++)if(category.equals("Semua")||types[i].equals(category))filtered.add(i);for(int k=0;k<filtered.size();k+=2){LinearLayout r=row();content.addView(r);addTile(r,filtered.get(k));if(k+1<filtered.size())addTile(r,filtered.get(k+1));} 
      section("Live wallpapers");TextView live=txt("✨  Liquid Motion — wallpaper animasi bawaan",15,true);live.setPadding(dp(14),dp(18),dp(14),dp(18));live.setBackground(shape(0x337b83ff,0x66ffffff,20));content.addView(live);live.setOnClickListener(v->startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new ComponentName(this,LiveWallpaperService.class))));
      section("Custom wallpaper");button("＋  Pilih foto dari galeri",()->pick(101));
    }
    void addTile(LinearLayout parent,int i){LinearLayout tile=column();tile.setPadding(dp(5),dp(5),dp(5),dp(12));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(3),dp(4),dp(3),dp(4));parent.addView(tile,lp);View art=new Art(this,i);art.setBackground(shape(colors[i][1],0x55ffffff,24));tile.addView(art,new LinearLayout.LayoutParams(-1,dp(205)));TextView n=txt(names[i],14,true);n.setPadding(dp(3),dp(9),0,0);tile.addView(n);tile.setOnClickListener(v->{selected=i;showDetail();});}
    void showDetail(){content.removeAllViews();button("‹  Kembali ke koleksi",()->showWallpapers());TextView n=txt(custom==null?names[selected]:"My Custom Wallpaper",22,true);n.setPadding(0,dp(14),0,dp(12));content.addView(n);View art=new Art(this,selected);content.addView(art,new LinearLayout.LayoutParams(-1,dp(390)));button("Terapkan ke layar utama",()->apply(WallpaperManager.FLAG_SYSTEM));button("Terapkan ke layar kunci",()->apply(WallpaperManager.FLAG_LOCK));button("Terapkan ke keduanya",()->apply(WallpaperManager.FLAG_SYSTEM|WallpaperManager.FLAG_LOCK));}
    void showSettings(){section("Appearance");for(String s:new String[]{"Gelap","Terang","Ikuti tema HP"})button((theme.equals(s.equals("Terang")?"light":s.equals("Gelap")?"dark":"system")?"✓  ":"")+"Tema "+s,()->{theme=s.equals("Terang")?"light":s.equals("Gelap")?"dark":"system";if(theme.equals("system"))theme=(getResources().getConfiguration().uiMode&32)!=0?"dark":"light";build();});section("Wallpaper");button("Atur live wallpaper",()->startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new ComponentName(this,LiveWallpaperService.class))));button("Pilih wallpaper custom",()->pick(101));section("Audio");button("Pilih musik dari HP",()->pick(102));button("Putar / jeda musik",()->toggleMusic());section("Interface");TextView drag=txt("Geser navbar di bagian bawah untuk memindahkan posisinya.",13,false);drag.setTextColor(muted());content.addView(drag);}
    void section(String s){TextView t=txt(s.toUpperCase(),11,true);t.setTextColor(0xff9696e8);t.setPadding(0,dp(22),0,dp(8));content.addView(t);}
    void button(String s,Runnable a){TextView b=txt(s,14,true);b.setPadding(dp(16),dp(16),dp(16),dp(16));b.setBackground(shape(theme.equals("light")?0xffe8e5e1:0x22ffffff,0x55ffffff,18));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(5),0,dp(5));content.addView(b,p);b.setOnClickListener(v->a.run());}
    class Art extends View{int idx;Paint p=new Paint(3);Art(Context c,int i){super(c);idx=i;}protected void onDraw(Canvas c){super.onDraw(c);if(custom!=null&&idx==selected){c.drawBitmap(custom,null,new Rect(0,0,getWidth(),getHeight()),p);return;}int[] a=colors[idx];p.setShader(new LinearGradient(0,0,getWidth(),getHeight(),a[0],a[2],Shader.TileMode.CLAMP));c.drawRect(0,0,getWidth(),getHeight(),p);p.setShader(null);p.setColor(a[1]);c.drawCircle(getWidth()*.72f,getHeight()*.28f,getWidth()*.38f,p);p.setColor(0x44ffffff);c.drawCircle(getWidth()*.16f,getHeight()*.75f,getWidth()*.45f,p);p.setColor(0x22ffffff);c.drawRoundRect(getWidth()*.1f,getHeight()*.52f,getWidth()*.9f,getHeight()*.86f,30,30,p);}}
    void pick(int req){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType(req==101?"image/*":"audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,req);}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(c!=RESULT_OK||d==null)return;try{if(r==101){InputStream in=getContentResolver().openInputStream(d.getData());custom=BitmapFactory.decodeStream(in);if(in!=null)in.close();selected=0;category="Custom";settings=false;build();}else if(r==102){music=d.getData();Toast.makeText(this,"Musik dipilih",0).show();}}catch(Exception e){Toast.makeText(this,"Tidak dapat membuka file",0).show();}}
    void apply(int flag){try{WallpaperManager wm=WallpaperManager.getInstance(this);Bitmap b;if(custom!=null)b=custom;else{b=Bitmap.createBitmap(1080,2400,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);Paint p=new Paint(3);p.setShader(new LinearGradient(0,0,1080,2400,colors[selected][0],colors[selected][2],Shader.TileMode.CLAMP));c.drawPaint(p);}if(Build.VERSION.SDK_INT>=24)wm.setBitmap(b,null,true,flag);else wm.setBitmap(b);Toast.makeText(this,"Wallpaper berhasil diterapkan",0).show();}catch(Exception e){Toast.makeText(this,"Gagal memasang wallpaper",0).show();}}
    void toggleMusic(){if(music==null){Toast.makeText(this,"Pilih musik dulu",0).show();return;}try{if(player!=null&&player.isPlaying())player.pause();else if(player!=null)player.start();else{player=new MediaPlayer();player.setDataSource(this,music);player.setOnPreparedListener(MediaPlayer::start);player.prepareAsync();}}catch(Exception e){Toast.makeText(this,"Audio gagal diputar",0).show();}}
    @Override protected void onDestroy(){if(player!=null){player.release();player=null;}super.onDestroy();}
}
