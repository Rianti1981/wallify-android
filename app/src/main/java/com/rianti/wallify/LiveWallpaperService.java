package com.rianti.wallify;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.graphics.*;
import android.os.*;
public class LiveWallpaperService extends WallpaperService {
 @Override public Engine onCreateEngine(){return new MotionEngine();}
 class MotionEngine extends Engine {
  Handler h=new Handler(); boolean visible=false; long start=System.currentTimeMillis();
  Runnable draw=new Runnable(){public void run(){render();}};
  @Override public void onVisibilityChanged(boolean v){visible=v;if(v)h.post(draw);else h.removeCallbacks(draw);}
  @Override public void onSurfaceChanged(SurfaceHolder sh,int f,int w,int he){super.onSurfaceChanged(sh,f,w,he);render();}
  @Override public void onSurfaceDestroyed(SurfaceHolder sh){visible=false;h.removeCallbacks(draw);super.onSurfaceDestroyed(sh);}
  void render(){SurfaceHolder sh=getSurfaceHolder();Canvas c=null;try{c=sh.lockCanvas();if(c!=null){float t=(System.currentTimeMillis()-start)/2200f;int w=c.getWidth(),he=c.getHeight();Paint p=new Paint(3);p.setShader(new LinearGradient(0,0,w,he,new int[]{0xff172b4d,0xff6b72a8,0xffd6a7a0},null,Shader.TileMode.CLAMP));c.drawRect(0,0,w,he,p);p.setShader(null);p.setColor(0x66ffffff);float x=(float)(w*(.5+.28*Math.sin(t)));float y=(float)(he*(.32+.08*Math.cos(t*.7)));c.drawCircle(x,y,w*.24f,p);p.setColor(0x448be0ff);c.drawCircle(w-x,he*.72f,w*.32f,p);p.setColor(0x22ffffff);c.drawRoundRect(w*.08f,he*.58f,w*.92f,he*.88f,w*.12f,w*.12f,p);}}catch(Exception ignored){}finally{if(c!=null)sh.unlockCanvasAndPost(c);}if(visible)h.postDelayed(draw,40);}
 }
}