package com.prosen.ishanbuddy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

public class BuddySceneView extends View {
  public static final int MODE_HOME=0, MODE_BATH=1, MODE_CAR=2, MODE_BIKE=3, MODE_FOOD=4, MODE_SLEEP=5;
  public interface BuddyAction { void onAction(String text); }
  private BuddyAction action;
  private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
  private int mode = MODE_HOME, clean = 0;
  private float time = 0, buddyX = .5f, vehicleX = .76f;
  private final ArrayList<Bubble> bubbles = new ArrayList<>();
  private long lastFrame = System.currentTimeMillis();

  public BuddySceneView(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
  public void setOnBuddyAction(BuddyAction a) { action = a; }
  public void setMode(int m) {
    mode=m; clean=0; bubbles.clear(); buddyX=.5f; vehicleX=.76f;
    if (mode==MODE_BATH) for(int i=0;i<8;i++) bubbles.add(new Bubble(.15f+(i%4)*.19f,.28f+(i/4)*.13f, .018f+(i%3)*.009f));
    invalidate();
  }
  private void color(Canvas c,int col) { p.setColor(col); p.setStyle(Paint.Style.FILL); p.setStrokeWidth(2); }
  private void rect(Canvas c,float l,float t,float r,float b,int col,float rad) { color(c,col); c.drawRoundRect(new RectF(l,t,r,b),rad,rad,p); }
  private void circle(Canvas c,float x,float y,float r,int col) { color(c,col); c.drawCircle(x,y,r,p); }
  private void line(Canvas c,float x,float y,float x2,float y2,int col,float width) { color(c,col); p.setStrokeWidth(width); p.setStrokeCap(Paint.Cap.ROUND); c.drawLine(x,y,x2,y2,p); }
  private void text(Canvas c,String s,float x,float y,float size,int col) { color(c,col); p.setTextSize(size); p.setTextAlign(Paint.Align.CENTER); p.setTypeface(android.graphics.Typeface.create("sans-serif",android.graphics.Typeface.BOLD)); c.drawText(s,x,y,p); }

  @Override protected void onDraw(Canvas c) {
    super.onDraw(c);
    float w=getWidth(), h=getHeight();
    if(w<=0||h<=0)return;
    float scale=Math.min(w/360f,h/390f); c.save(); c.translate((w-360*scale)/2,(h-390*scale)/2); c.scale(scale,scale);
    int sky=mode==MODE_BATH?Color.rgb(205,240,250):mode==MODE_SLEEP?Color.rgb(39,52,91):Color.rgb(183,229,255);
    rect(c,0,0,360,390,sky,22);
    if(mode==MODE_HOME||mode==MODE_FOOD||mode==MODE_SLEEP) {
      rect(c,14,20,346,330,mode==MODE_SLEEP?Color.rgb(56,69,109):Color.rgb(255,248,224),20);
      rect(c,14,285,346,330,mode==MODE_SLEEP?Color.rgb(37,45,78):Color.rgb(242,206,155),10);
      circle(c,54,53,17,mode==MODE_SLEEP?Color.rgb(255,238,169):Color.WHITE);
      if(mode!=MODE_SLEEP){circle(c,300,53,20,Color.rgb(255,205,86));}
      else {circle(c,286,50,2,Color.WHITE);circle(c,315,72,2,Color.WHITE);circle(c,247,44,2,Color.WHITE);}
    } else if(mode==MODE_BATH) {
      rect(c,16,20,344,331,Color.rgb(235,251,255),18);
      rect(c,30,36,330,245,Color.rgb(195,235,246),12);
      for(int x=48;x<330;x+=42) line(c,x,36,x,245,Color.rgb(169,218,234),1);
      for(int y=65;y<245;y+=42) line(c,30,y,330,y,Color.rgb(169,218,234),1);
      rect(c,18,274,342,330,Color.rgb(114,196,219),12);
      rect(c,28,287,332,326,Color.rgb(84,171,203),8);
      for(int i=0;i<6;i++) circle(c,32+i*57,306+(i%2)*7,3,Color.WHITE);
    } else {
      rect(c,15,20,345,330,Color.rgb(235,248,221),18);
      rect(c,15,260,345,330,Color.rgb(138,206,127),12);
      for(int i=0;i<5;i++){circle(c,28+i*75,270,14,Color.rgb(105,183,95));}
      line(c,24,300,337,300,Color.rgb(247,247,247),3);
      line(c,24,320,337,320,Color.rgb(247,247,247),3);
    }

    if(mode==MODE_CAR) drawCar(c,vehicleX*360,296);
    if(mode==MODE_BIKE) drawBike(c,vehicleX*360,292);

    if(mode==MODE_FOOD) {
      circle(c,286,208,22,Color.WHITE); circle(c,286,208,17,Color.rgb(244,99,73)); line(c,286,189,291,181,Color.rgb(105,68,40),3); circle(c,295,184,5,Color.rgb(81,163,82));
    }
    if(mode==MODE_SLEEP) {
      rect(c,38,228,320,285,Color.rgb(115,147,211),15);
      text(c,"Z z z",285,104,22,Color.WHITE);
    }

    drawBuddy(c,180+(float)Math.sin(time*2)*4,mode==MODE_SLEEP?224:236);
    if(mode==MODE_BATH) {
      for(Bubble b:bubbles) {
        color(c,Color.argb(80,255,255,255)); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); c.drawCircle(b.x*360,b.y*390,b.r*390,p); p.setStyle(Paint.Style.FILL);
        circle(c,b.x*360-b.r*120,b.y*390-b.r*120,2,Color.WHITE);
      }
      text(c,"সাবানের বুদবুদে টোকা দাও!",180,358,14,Color.rgb(27,103,129));
      text(c,"পরিষ্কার: "+clean+"%",180,378,13,Color.rgb(27,103,129));
    } else if(mode==MODE_CAR) text(c,"গাড়িতে টোকা দাও →",180,360,15,Color.rgb(49,103,45));
    else if(mode==MODE_BIKE) text(c,"সাইকেলে টোকা দাও →",180,360,15,Color.rgb(49,103,45));
    else if(mode==MODE_FOOD) text(c,"ঈশানকে খাবার দাও!",180,360,15,Color.rgb(108,76,40));
    else if(mode==MODE_SLEEP) text(c,"শুভরাত্রি বন্ধু 🌙",180,360,15,Color.WHITE);
    else text(c,"ঈশানকে টোকা দাও! ❤️",180,360,15,Color.rgb(108,76,40));

    c.restore();
    long now=System.currentTimeMillis(); time+=(Math.min(60,now-lastFrame))/1000f; lastFrame=now;
    if(mode==MODE_BATH) {
      for(Bubble b:bubbles){ b.y-=.0012f; if(b.y<.14f)b.y=.55f; }
    }
    if(mode==MODE_CAR||mode==MODE_BIKE) vehicleX+=.0018f;
    if(vehicleX>1.18f)vehicleX=-.18f;
    postInvalidateDelayed(50);
  }

  private void drawBuddy(Canvas c,float x,float foot) {
    float bob=(float)Math.sin(time*3)*3;
    // soft shadow
    color(c,Color.argb(38,40,70,80)); c.drawOval(new RectF(x-54,foot+8,x+54,foot+20),p);
    // legs and shoes
    line(c,x-18,foot-35,x-22,foot+2,Color.rgb(232,165,111),11);
    line(c,x+18,foot-35,x+23,foot+2,Color.rgb(232,165,111),11);
    rect(c,x-39,foot-2,x-13,foot+8,Color.rgb(65,90,157),7);
    rect(c,x+12,foot-2,x+39,foot+8,Color.rgb(65,90,157),7);
    // shirt torso
    rect(c,x-37,foot-105+bob,x+37,foot-31+bob,Color.rgb(255,145,54),20);
    // arms
    line(c,x-31,foot-90+bob,x-49,foot-51+bob,Color.rgb(232,165,111),12);
    line(c,x+31,foot-90+bob,x+49,foot-51+bob,Color.rgb(232,165,111),12);
    circle(c,x-50,foot-48+bob,7,Color.rgb(232,165,111));
    circle(c,x+50,foot-48+bob,7,Color.rgb(232,165,111));
    // neck and ears
    rect(c,x-10,foot-129+bob,x+10,foot-105+bob,Color.rgb(232,165,111),7);
    circle(c,x-29,foot-154+bob,11,Color.rgb(232,165,111));
    circle(c,x+29,foot-154+bob,11,Color.rgb(232,165,111));
    // face
    color(c,Color.rgb(242,190,143)); c.drawOval(new RectF(x-31,foot-188+bob,x+31,foot-124+bob),p);
    // hair cap and fringe
    color(c,Color.rgb(72,43,29)); c.drawArc(new RectF(x-33,foot-196+bob,x+33,foot-140+bob),180,180,true,p);
    circle(c,x-19,foot-183+bob,10,Color.rgb(72,43,29)); circle(c,x,foot-191+bob,12,Color.rgb(72,43,29)); circle(c,x+17,foot-184+bob,10,Color.rgb(72,43,29));
    // eyes and glints
    circle(c,x-12,foot-157+bob,4,Color.rgb(55,40,32)); circle(c,x+12,foot-157+bob,4,Color.rgb(55,40,32));
    circle(c,x-13,foot-159+bob,1.4f,Color.WHITE); circle(c,x+11,foot-159+bob,1.4f,Color.WHITE);
    // cheeks and smile
    circle(c,x-20,foot-143+bob,5,Color.rgb(246,139,130)); circle(c,x+20,foot-143+bob,5,Color.rgb(246,139,130));
    color(c,Color.rgb(129,53,43)); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2.5f); c.drawArc(new RectF(x-10,foot-151+bob,x+10,foot-135+bob),15,150,false,p); p.setStyle(Paint.Style.FILL);
    // shirt badge
    circle(c,x,foot-80+bob,9,Color.rgb(255,223,91)); text(c,"ঈ",x,foot-76+bob,11,Color.rgb(128,75,32));
    if(mode==MODE_BATH) {
      for(int i=0;i<4;i++) circle(c,x-24+i*16,foot-103+bob-(i%2)*8,4,Color.rgb(177,232,255));
      // towel
      rect(c,x-37,foot-120+bob,x+37,foot-109+bob,Color.rgb(88,194,221),5);
    }
    if(mode==MODE_FOOD) { circle(c,x+53,foot-75,7,Color.rgb(230,63,52)); line(c,x+53,foot-82,x+57,foot-88,Color.rgb(60,135,70),2); }
  }

  private void drawCar(Canvas c,float x,float y) {
    rect(c,x-43,y-25,x+43,y+3,Color.rgb(239,80,70),9);
    rect(c,x-24,y-42,x+19,y-20,Color.rgb(255,150,106),8);
    rect(c,x-18,y-38,x-2,y-24,Color.rgb(191,236,255),4); rect(c,x+2,y-38,x+14,y-24,Color.rgb(191,236,255),4);
    circle(c,x-26,y+3,10,Color.rgb(50,55,62)); circle(c,x+27,y+3,10,Color.rgb(50,55,62));
    circle(c,x-26,y+3,4,Color.LTGRAY); circle(c,x+27,y+3,4,Color.LTGRAY);
  }
  private void drawBike(Canvas c,float x,float y) {
    color(c,Color.rgb(45,117,205)); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(4);
    c.drawCircle(x-27,y,18,p); c.drawCircle(x+28,y,18,p); p.setStyle(Paint.Style.FILL);
    line(c,x-27,y,x-4,y-25,Color.rgb(45,117,205),4); line(c,x-4,y-25,x+9,y,Color.rgb(45,117,205),4);
    line(c,x+9,y,x-27,y,Color.rgb(45,117,205),4); line(c,x+9,y,x+28,y,Color.rgb(45,117,205),4);
    line(c,x-9,y-28,x+1,y-28,Color.DKGRAY,4); line(c,x+18,y-30,x+32,y-30,Color.DKGRAY,4);
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    if(e.getAction()!=MotionEvent.ACTION_UP)return true;
    float scale=Math.min(getWidth()/360f,getHeight()/390f);
    float ox=(getWidth()-360*scale)/2, oy=(getHeight()-390*scale)/2;
    float x=(e.getX()-ox)/scale, y=(e.getY()-oy)/scale;
    if(x<0||x>360||y<0||y>390)return true;
    if(mode==MODE_BATH) {
      if(!bubbles.isEmpty()) {
        Bubble nearest=null; float dmin=Float.MAX_VALUE;
        for(Bubble b:bubbles){float dx=x-b.x*360,dy=y-b.y*390,d=dx*dx+dy*dy;if(d<dmin){dmin=d;nearest=b;}}
        if(nearest!=null){bubbles.remove(nearest);clean=Math.min(100,clean+12); if(bubbles.isEmpty()){if(action!=null)action.onAction("ইয়েই! ঈশান পরিষ্কার হয়ে গেছে! 🫧 এবার তোয়ালে দিয়ে মুছে নাও।");} else if(action!=null)action.onAction("পপ! সাবানের বুদবুদ ফেটে গেল! আরেকটায় টোকা দাও!");}
      } else if(action!=null)action.onAction("দারুণ! ঈশান পরিষ্কার। গোসলের পরে ভালো করে মুছে নাও!");
    } else if(mode==MODE_CAR) {
      vehicleX+=.16f; if(vehicleX>1.12f)vehicleX=.18f;
      if(action!=null)action.onAction("ব্রুম ব্রুম! খেলনা গাড়ি এগিয়ে গেল! 🚗");
    } else if(mode==MODE_BIKE) {
      vehicleX+=.16f; if(vehicleX>1.12f)vehicleX=.18f;
      if(action!=null)action.onAction("টিং টিং! ঈশানের সাইকেল এগিয়ে গেল! 🚲");
    } else if(mode==MODE_FOOD) {
      if(action!=null)action.onAction("মমম! মজার ফল খেয়ে ঈশান খুশি! 🍎");
    } else if(mode==MODE_SLEEP) {
      if(action!=null)action.onAction("শুভরাত্রি! চোখ বন্ধ করে আরাম করো। 🌙");
    } else {
      if(action!=null)action.onAction("হিহি! তুমি আমাকে টোকা দিয়েছ! আমার সঙ্গে অঙ্ক বা ইংরেজি খেলবে?");
    }
    invalidate(); return true;
  }
  private static class Bubble { float x,y,r; Bubble(float x,float y,float r){this.x=x;this.y=y;this.r=r;} }
}
