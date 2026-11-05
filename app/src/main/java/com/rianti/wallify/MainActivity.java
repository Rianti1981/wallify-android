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
    String[] names={"Aurora Borealis","Danau Fuji","Senja Ungu","Kota Neon","Hutan Pinus","Pegunungan Alpen","Laut Tropis","Sakura Malam","Desert Dunes","Milky Way","Air Terjun","Kabut Pagi","Elaina Witch","Moon Castle","Cherry Blossom","Cyber City","Snow Mountain","Lavender Sky","Ocean Cliff","Golden Forest","Starry Lake","Pink Galaxy","Green Valley","Rainy Tokyo"};
    String[] types={"Pemandangan","Pemandangan","Pemandangan","Cityscapes","Pemandangan","Pemandangan","Pemandangan","Life","Pemandangan","Pemandangan","Pemandangan","Pemandangan","Anime","Anime","Life","Cityscapes","Pemandangan","Pemandangan","Pemandangan","Pemandangan","Pemandangan","Anime","Pemandangan","Cityscapes"};
    int[][] colors={{0xff071b35,0xff285b83,0xffa77ac7},{0xff172b50,0xff467da8,0xfff6c5a5},{0xff321b45,0xffb65370,0xffffb36b},{0xff10102c,0xff45428b,0xffb58bd8},{0xff092f2b,0xff28765b,0xffa4bd76},{0xff233b5c,0xff739bb8,0xffe6e6f0},{0xff00465b,0xff20a5a5,0xffb2e5d9},{0xff30204c,0xff9b5b9b,0xffffc2d8},{0xff563c39,0xffb77c54,0xffffd69b},{0xff080d2d,0xff253c75,0xffb8b5ff},{0xff103e55,0xff329e9a,0xffc5f2db},{0xff526b82,0xff9caebc,0xffedf0ec},{0xff211b4d,0xff7163b6,0xfff2c5e5},{0xff15163e,0xff4c4b91,0xfff3b8d4},{0xff4b285c,0xffbd6eaa,0xffffd6e8},{0xff081c43,0xff3155a5,0xffa75bd5},{0xff172b50,0xff6b9bbd,0xfff4f7ff},{0xff594c88,0xffad8fc7,0xffffd4df},{0xff083c50,0xff2d8191,0xffc4e9e6},{0xff1c4735,0xff688b4c,0xffe5c779},{0xff101d3c,0xff3e5f91,0xffd5e7ff},{0xff351b5c,0xffa05ac4,0xffffb9df},{0xff16483c,0xff4c986e,0xffc0dca1},{0xff111c3c,0xff405a94,0xffc2b9e8}};
    int selected=0; String category="Semua", theme="dark"; boolean settings=false; Bitmap custom;
    LinearLayout root,content,nav; TextView title,subtitle; float downX,downY; MediaPlayer player; Uri music;
    int bg(){return theme.equals("light")?0xfff4f2ef:0xff08090d;} int fg(){return theme.equals("light")?0xff171717:Color.WHITE;} int muted(){return theme.equals("light")?0xff6b6b6b:0xffa8a8b4;}
    @Override public void onCreate(Bundle b){super.onCreate(b);build();}
    void applyImmersiveMode(){try{View decor=getWindow().getDecorView();decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);}catch(RuntimeException ignored){}}
    @Override public void onWindowFocusChanged(boolean hasFocus){super.onWindowFocusChanged(hasFocus);if(hasFocus)applyImmersiveMode();}
    GradientDrawable shape(int c,int stroke,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(dp(r));d.setStroke(dp(1),stroke);return d;}
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    TextView txt(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg());if(bold)t.setTypeface(null,1);return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void build(){
      root=column();root.setBackgroundColor(bg());
      LinearLayout header=column();header.setPadding(dp(20),dp(18),dp(20),dp(12));root.addView(header);
      TextView brand=txt("W A L L I F Y   /   3.0",11,true);brand.setTextColor(0xff9b9bff);header.addView(brand);
      title=txt(settings?"Settings":"Discover",30,true);title.setPadding(0,dp(12),0,dp(3));header.addView(title);
      subtitle=txt(settings?"Make it feel like yours":"Wallpaper collections",14,false);subtitle.setTextColor(muted());header.addView(subtitle);
      ScrollView sc=new ScrollView(this);content=column();content.setPadding(dp(16),dp(8),dp(16),dp(100));sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
      HorizontalScrollView navScroll=new HorizontalScrollView(this);navScroll.setHorizontalScrollBarEnabled(false);navScroll.setFillViewport(true);nav=row();nav.setPadding(dp(7),dp(7),dp(7),dp(7));nav.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xdd554a86,0xcc22283f,0xdd594b83}));nav.getBackground().setCornerRadius(dp(30));nav.getBackground().setStroke(dp(1),0x77ffffff);
      navScroll.addView(nav);LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,dp(66));np.setMargins(dp(18),0,dp(18),dp(12));root.addView(navScroll,np);
      addNav("⌂   Beranda",0,()->{settings=false;category="Semua";build();});addNav("▦   Kategori",1,()->{settings=false;category="Pemandangan";build();});addNav("⚙   Pengaturan",2,()->{settings=true;build();});
      setContentView(root);applyImmersiveMode();if(settings)showSettings();else showWallpapers();
    }
    void addNav(String label,int index,Runnable action){TextView t=txt(label,13,(index==2)==settings);t.setGravity(Gravity.CENTER);t.setPadding(dp(20),dp(12),dp(20),dp(12));t.setBackground(((index==2)==settings)?shape(0x557e78d8,0x99ffffff,24):shape(Color.TRANSPARENT,Color.TRANSPARENT,24));nav.addView(t,new LinearLayout.LayoutParams(-2,-1));t.setOnClickListener(v->action.run());}
    void showWallpapers(){
      content.removeAllViews();
      TextView featured=txt("Explore your mood",20,true);content.addView(featured);TextView hint=txt("Pilih koleksi, lalu ketuk wallpaper favoritmu.",13,false);hint.setTextColor(muted());content.addView(hint);
      LinearLayout chips=row();HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);for(String c:cats){TextView x=txt(c,12,true);x.setPadding(dp(14),dp(10),dp(14),dp(10));x.setBackground(shape(category.equals(c)?0xff7779db:0x33222222,0x55ffffff,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2);p.setMargins(0,dp(14),dp(8),dp(14));chips.addView(x,p);x.setOnClickListener(v->{category=c;showWallpapers();});}hsv.addView(chips);content.addView(hsv);
      if(category.equals("Custom")){TextView c=txt("Wallpaper dari galeri",17,true);content.addView(c);button("＋  Pilih wallpaper dari galeri",()->pick(101));if(custom!=null){View preview=new Art(this,selected);content.addView(preview,new LinearLayout.LayoutParams(-1,dp(280)));TextView mine=txt("My Custom Wallpaper  ·  Ketuk untuk melihat",14,true);mine.setPadding(0,dp(10),0,dp(12));content.addView(mine);mine.setOnClickListener(v->showDetail());button("Terapkan wallpaper custom",()->apply(WallpaperManager.FLAG_SYSTEM|WallpaperManager.FLAG_LOCK));}return;}
      ArrayList<Integer> filtered=new ArrayList<>();for(int i=0;i<names.length;i++)if(category.equals("Semua")||types[i].equals(category))filtered.add(i);for(int k=0;k<filtered.size();k+=2){LinearLayout r=row();content.addView(r);addTile(r,filtered.get(k));if(k+1<filtered.size())addTile(r,filtered.get(k+1));} 
      section("Live wallpapers");TextView live=txt("✦  Liquid Motion",17,true);live.setPadding(dp(16),dp(20),dp(16),dp(20));live.setBackground(shape(0xff24283a,0xff555b88,22));content.addView(live);TextView liveHint=txt("Animasi lebih ringan • 5 tema bergerak",12,false);liveHint.setTextColor(muted());content.addView(liveHint);button("Pilih gaya live wallpaper",()->chooseLiveStyle());button("Preview & pasang live wallpaper",()->openLiveWallpaper());
      section("Custom wallpaper");button("＋  Pilih foto dari galeri",()->pick(101));
    }
    void addTile(LinearLayout parent,int i){LinearLayout tile=column();tile.setPadding(dp(5),dp(5),dp(5),dp(12));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(3),dp(4),dp(3),dp(4));parent.addView(tile,lp);View art=new Art(this,i);art.setBackground(shape(colors[i][1],0x55ffffff,24));tile.addView(art,new LinearLayout.LayoutParams(-1,dp(205)));TextView n=txt(names[i],14,true);n.setPadding(dp(3),dp(9),0,0);tile.addView(n);tile.setOnClickListener(v->{selected=i;showDetail();});}
    void showDetail(){content.removeAllViews();button("‹  Kembali ke koleksi",()->showWallpapers());TextView n=txt(custom==null?names[selected]:"My Custom Wallpaper",22,true);n.setPadding(0,dp(14),0,dp(12));content.addView(n);View art=new Art(this,selected);content.addView(art,new LinearLayout.LayoutParams(-1,dp(390)));button("Terapkan ke layar utama",()->apply(WallpaperManager.FLAG_SYSTEM));button("Terapkan ke layar kunci",()->apply(WallpaperManager.FLAG_LOCK));button("Terapkan ke keduanya",()->apply(WallpaperManager.FLAG_SYSTEM|WallpaperManager.FLAG_LOCK));}
    void chooseLiveStyle(){String[] styles={"Aurora Flow","Ocean Waves","Sunset Glow","Nebula","Monochrome"};new AlertDialog.Builder(this).setTitle("Pilih animasi").setItems(styles,(d,which)->{getSharedPreferences("wallify",0).edit().putInt("live_style",which).apply();Toast.makeText(this,"Tema "+styles[which]+" dipilih",Toast.LENGTH_SHORT).show();}).show();}
    void openLiveWallpaper(){startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new ComponentName(this,LiveWallpaperService.class)));}
    void showSettings(){section("Appearance");for(String s:new String[]{"Gelap","Terang","Ikuti tema HP"})button((theme.equals(s.equals("Terang")?"light":s.equals("Gelap")?"dark":"system")?"✓  ":"")+"Tema "+s,()->{theme=s.equals("Terang")?"light":s.equals("Gelap")?"dark":"system";if(theme.equals("system"))theme=(getResources().getConfiguration().uiMode&32)!=0?"dark":"light";build();});section("Wallpaper");button("Pilih gaya live wallpaper",()->chooseLiveStyle());button("Preview & pasang live wallpaper",()->openLiveWallpaper());button("Pilih wallpaper custom",()->pick(101));section("Audio");button("Pilih musik dari HP",()->pick(102));button("Putar / jeda musik",()->toggleMusic());section("Interface");TextView drag=txt("Geser navbar di bagian bawah untuk memindahkan posisinya.",13,false);drag.setTextColor(muted());content.addView(drag);}
    void section(String s){TextView t=txt(s.toUpperCase(),11,true);t.setTextColor(0xff9696e8);t.setPadding(0,dp(22),0,dp(8));content.addView(t);}
    void button(String s,Runnable a){TextView b=txt(s,14,true);b.setPadding(dp(16),dp(17),dp(16),dp(17));b.setBackground(shape(theme.equals("light")?0xffe8e5e1:0xff1b1d27,theme.equals("light")?0xffd6d1ca:0xff343746,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(5),0,dp(5));content.addView(b,p);b.setOnClickListener(v->a.run());}
    class Art extends View{
      int idx;Paint p=new Paint(3);Art(Context c,int i){super(c);idx=i;}
      protected void onDraw(Canvas c){super.onDraw(c);if(custom!=null&&idx==selected){c.drawBitmap(custom,null,new Rect(0,0,getWidth(),getHeight()),p);return;}
        int[] a=colors[idx];float w=getWidth(),h=getHeight();p.setShader(new LinearGradient(0,0,w,h,a[0],a[2],Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);
        if(idx==3||idx==15||idx==23){p.setColor(0xff11152e);for(int j=0;j<10;j++){float x=j*w/10f;p.setColor(j%2==0?0xff171b3b:0xff22234c);c.drawRect(x,h*.48f,x+w*.075f,h,p);for(int yy=0;yy<5;yy++)for(int xx=0;xx<2;xx++){p.setColor(0xffffd98a);c.drawRect(x+dp(5)+xx*dp(10),h*.55f+yy*dp(19),x+dp(10)+xx*dp(10),h*.55f+yy*dp(19)+dp(7),p);}}}
        else if(idx==12){p.setColor(0xff201943);c.drawCircle(w*.5f,h*.35f,w*.32f,p);p.setColor(0xffe8d9f7);c.drawOval(w*.34f,h*.25f,w*.65f,h*.68f,p);p.setColor(0xff6b4c9d);Path hat=new Path();hat.moveTo(w*.18f,h*.32f);hat.quadTo(w*.48f,h*.05f,w*.84f,h*.31f);hat.lineTo(w*.18f,h*.32f);c.drawPath(hat,p);p.setColor(0xff3b2b63);c.drawOval(w*.1f,h*.29f,w*.9f,h*.38f,p);p.setColor(0xffb79adf);c.drawRect(w*.37f,h*.31f,w*.64f,h*.36f,p);p.setColor(0xffe9d8f3);c.drawOval(w*.38f,h*.48f,w*.45f,h*.53f,p);c.drawOval(w*.55f,h*.48f,w*.62f,h*.53f,p);}
        else {p.setColor(0x44ffffff);c.drawCircle(w*.76f,h*.2f,w*.12f,p);p.setColor(0x22ffffff);c.drawCircle(w*.76f,h*.2f,w*.17f,p);
          if(idx==0||idx==9||idx==20||idx==21){Random rnd=new Random(idx+8);p.setColor(0xbbffffff);for(int i=0;i<38;i++){float sx=rnd.nextFloat()*w,sy=rnd.nextFloat()*h*.58f;c.drawCircle(sx,sy,dp(1)+(i%3),p);}}
          if(idx==1||idx==5||idx==8||idx==9||idx==10||idx==11||idx==16||idx==17||idx==18||idx==19||idx==20||idx==22){p.setColor(idx==8?0xff584331:idx==16?0xffdce8f5:0xff263e55);Path m=new Path();m.moveTo(0,h*.72f);m.lineTo(w*.25f,h*.32f);m.lineTo(w*.42f,h*.66f);m.lineTo(w*.65f,h*.25f);m.lineTo(w,h*.72f);m.close();c.drawPath(m,p);p.setColor(idx==16?0xfff7f8ff:0xff668b76);Path m2=new Path();m2.moveTo(0,h*.83f);m2.lineTo(w*.38f,h*.53f);m2.lineTo(w*.58f,h*.76f);m2.lineTo(w*.8f,h*.48f);m2.lineTo(w,h*.78f);m2.close();c.drawPath(m2,p);p.setColor(0x6659b8d0);c.drawRect(0,h*.78f,w,h,p);}
          if(idx==4||idx==7||idx==14||idx==19||idx==22){p.setColor(0xff173c35);for(int i=0;i<12;i++){float x=i*w/11f;p.setColor(i%2==0?0xff173c35:0xff285541);Path tree=new Path();tree.moveTo(x,h*.88f);tree.lineTo(x-w*.055f,h*.48f+(i%3)*dp(8));tree.lineTo(x+w*.055f,h*.88f);tree.close();c.drawPath(tree,p);}}
          if(idx==2||idx==6||idx==13||idx==17||idx==21){p.setColor(0x55ffffff);for(int i=0;i<5;i++)c.drawCircle(w*(.15f+i*.18f),h*(.78f+(i%2)*.06f),w*.16f,p);}
        }
      }
    }
    void pick(int req){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType(req==101?"image/*":"audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,req);}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(c!=RESULT_OK||d==null)return;try{if(r==101){InputStream in=getContentResolver().openInputStream(d.getData());custom=BitmapFactory.decodeStream(in);if(in!=null)in.close();selected=0;category="Custom";settings=false;build();}else if(r==102){music=d.getData();Toast.makeText(this,"Musik dipilih",0).show();}}catch(Exception e){Toast.makeText(this,"Tidak dapat membuka file",0).show();}}
    void apply(int flag){try{WallpaperManager wm=WallpaperManager.getInstance(this);android.util.DisplayMetrics dm=new android.util.DisplayMetrics();if(Build.VERSION.SDK_INT>=17)getWindowManager().getDefaultDisplay().getRealMetrics(dm);else getWindowManager().getDefaultDisplay().getMetrics(dm);int w=Math.max(1,dm.widthPixels),h=Math.max(1,dm.heightPixels);Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Art artwork=new Art(this,selected);artwork.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));artwork.layout(0,0,w,h);artwork.draw(new Canvas(b));if(Build.VERSION.SDK_INT>=24)wm.setBitmap(b,new Rect(0,0,w,h),true,flag);else wm.setBitmap(b);b.recycle();Toast.makeText(this,"Wallpaper berhasil diterapkan",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Gagal memasang wallpaper",Toast.LENGTH_SHORT).show();}}
    void toggleMusic(){if(music==null){Toast.makeText(this,"Pilih musik dulu",0).show();return;}try{if(player!=null&&player.isPlaying())player.pause();else if(player!=null)player.start();else{player=new MediaPlayer();player.setDataSource(this,music);player.setOnPreparedListener(MediaPlayer::start);player.prepareAsync();}}catch(Exception e){Toast.makeText(this,"Audio gagal diputar",0).show();}}
    @Override protected void onDestroy(){if(player!=null){player.release();player=null;}super.onDestroy();}
}
