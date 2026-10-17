package com.apexai.lab;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.*;
import java.util.*;
import java.io.*;
public class MainActivity extends Activity {
 Lab view;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);view=new Lab();setContentView(view);}
 @Override protected void onPause(){super.onPause();view.save();}
 class Lab extends View {
  final int bg=0xff090d18,panel=0xff141c2d,cyan=0xff34daf2,green=0xff5df0a6,muted=0xff8a9ab2,white=0xfff2f7ff;
  Paint p=new Paint(3);Random random=new Random();float w,h,scale;boolean running=true;int speed=4,episodes=0,crashes=0,best=0,steps=0;float reward=0,epsilon=1f;int state=0,action=1;float x,y,heading,angle,progress=0;float[] q=new float[11*7*3];ArrayList<Float> history=new ArrayList<>();
  Lab(){super(MainActivity.this);resetCar();load();}
  void resetCar(){angle=(float)(-Math.PI/2);x=0;y=-122;heading=0;steps=0;progress=0;reward=0;state=getState();}
  float wrap(float a){while(a>Math.PI)a-=Math.PI*2;while(a< -Math.PI)a+=Math.PI*2;return a;}
  int getState(){float r=(float)Math.hypot(x,y);float err=wrap(heading-(float)(Math.atan2(y,x)+Math.PI/2));int radial=Math.max(0,Math.min(10,(int)((r-92)/6)));int directional=Math.max(0,Math.min(6,(int)((err+1.05f)/.30f)));return radial*7+directional;}
  int choose(int s){if(random.nextFloat()<epsilon)return random.nextInt(3);int a=0;for(int i=1;i<3;i++)if(q[s*3+i]>q[s*3+a])a=i;return a;}
  void tick(){int old=state;action=choose(old);heading+=(action-1)*.095f;x+=(float)Math.cos(heading)*2.65f;y+=(float)Math.sin(heading)*2.65f;steps++;float r=(float)Math.hypot(x,y);float err=Math.abs(wrap(heading-(float)(Math.atan2(y,x)+Math.PI/2)));boolean dead=r<92||r>152||steps>=900;float a=(float)Math.atan2(y,x);float delta=wrap(a-angle);float rr=dead?-24:(delta>0?1.3f:-1.3f)-.015f*Math.abs(r-122)-.8f*err;reward+=rr;int next=getState();float max=Math.max(q[next*3],Math.max(q[next*3+1],q[next*3+2]));q[old*3+action]+=.16f*(rr+.94f*(dead?0:max)-q[old*3+action]);state=next;if(delta>0)progress+=delta;angle=a;best=Math.max(best,(int)(progress*100));
   if(dead){episodes++;if(r<92||r>152)crashes++;history.add(Math.min(1f,progress/(float)(2*Math.PI)));if(history.size()>90)history.remove(0);epsilon=Math.max(.035f,epsilon*.996f);resetCar();if(episodes%30==0)save();}
  }
  void color(int co){p.setColor(co);p.setStyle(Paint.Style.FILL);p.setStrokeWidth(1);}
  void rect(Canvas c,float l,float t,float r,float b,float radius,int co){color(co);c.drawRoundRect(l,t,r,b,radius,radius,p);}
  void txt(Canvas c,String s,float x,float y,float size,int co,boolean bold){color(co);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));p.setTextSize(size);c.drawText(s,x,y,p);}
  void line(Canvas c,float x,float y,float x2,float y2,int co,float sw){color(co);p.setStrokeWidth(sw);c.drawLine(x,y,x2,y2,p);}
  @Override protected void onDraw(Canvas c){super.onDraw(c);w=getWidth();h=getHeight();scale=w/390f;c.drawColor(bg);c.save();c.scale(scale,scale);float height=h/scale;
   txt(c,"APEX",22,44,27,white,true);txt(c,"AI",105,44,27,cyan,true);txt(c,"SELF-DRIVING LAB",22,63,10,muted,false);
   rect(c,294,24,368,52,14,running?0xff123e36:0xff483223);txt(c,running?"● LIVE":"Ⅱ PAUSE",305,43,11,running?green:0xffffbd78,true);
   rect(c,16,82,374,420,24,panel);txt(c,"TRAINING CIRCUIT",32,109,12,white,true);txt(c,"TRACK 01 / LOOP",248,109,10,muted,false);
   c.save();c.translate(195,260);color(0xff303f58);c.drawCircle(0,0,152,p);color(0xff101827);c.drawCircle(0,0,92,p);color(0xff172338);c.drawCircle(0,0,88,p);color(0xff667b97);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);c.drawCircle(0,0,122,p);p.setStyle(Paint.Style.FILL);
   for(int i=0;i<24;i++){float a=(float)(i*Math.PI/12);float cs=(float)Math.cos(a),sn=(float)Math.sin(a);line(c,cs*116,sn*116,cs*128,sn*128,0xff52647d,2);}
   c.save();c.translate(x,y);c.rotate((float)Math.toDegrees(heading));color(0x6634daf2);c.drawCircle(0,0,20,p);rect(c,-14,-8,15,8,4,cyan);rect(c,3,-6,10,6,2,0xff0d3046);line(c,15,0,35,0,0x9934daf2,2);c.restore();c.restore();
   txt(c,"AGENT 01",32,401,11,cyan,true);txt(c,"Q-LEARNING  •  ONLINE",170,401,10,muted,false);
   rect(c,16,434,188,535,18,panel);rect(c,202,434,374,535,18,panel);txt(c,"EPISODES",31,461,11,muted,true);txt(c,""+episodes,31,508,36,white,true);txt(c,"COLLISIONS",217,461,11,muted,true);txt(c,""+crashes,217,508,36,white,true);
   rect(c,16,548,188,649,18,panel);rect(c,202,548,374,649,18,panel);txt(c,"EXPLORATION",31,575,11,muted,true);txt(c,String.format(Locale.US,"%.1f%%",epsilon*100),31,621,31,green,true);txt(c,"BEST PROGRESS",217,575,11,muted,true);txt(c,String.format(Locale.US,"%.1f laps",best/628f),217,621,28,white,true);
   rect(c,16,662,374,797,18,panel);txt(c,"LEARNING CURVE",32,689,12,white,true);txt(c,"Progress per attempt",230,689,10,muted,false);
   for(int i=0;i<3;i++)line(c,32,713+i*29,357,713+i*29,0xff2c3b52,1);
   for(int i=1;i<history.size();i++){float x1=32+(i-1)*325f/Math.max(89,history.size()-1),x2=32+i*325f/Math.max(89,history.size()-1);float y1=781-history.get(i-1)*67,y2=781-history.get(i)*67;line(c,x1,y1,x2,y2,cyan,2.5f);}
   float by=Math.max(815,height-80);rect(c,16,by,192,by+56,16,cyan);txt(c,running?"Ⅱ  PAUSE":"▶  RESUME",48,by+35,16,0xff07131f,true);rect(c,204,by,374,by+56,16,panel);txt(c,"SPEED  ×"+speed,232,by+35,15,white,true);txt(c,"LONG PRESS RESET • MODEL AUTO-SAVED",26,by+76,9,muted,false);
   c.restore();if(running){for(int i=0;i<Math.min(300,speed*3);i++)tick();}postInvalidateDelayed(33);
  }
  long pressed;@Override public boolean onTouchEvent(MotionEvent e){float xx=e.getX()/scale,yy=e.getY()/scale;float by=Math.max(815,h/scale-80);if(e.getAction()==MotionEvent.ACTION_DOWN){pressed=System.currentTimeMillis();return true;}if(e.getAction()==MotionEvent.ACTION_UP){if(System.currentTimeMillis()-pressed>1100){Arrays.fill(q,0);episodes=crashes=best=0;epsilon=1;history.clear();resetCar();save();}else if(yy>by&&yy<by+60){if(xx<196)running=!running;else speed=speed==1?4:speed==4?12:speed==12?30:1;}invalidate();return true;}return true;}
  void save(){try{DataOutputStream d=new DataOutputStream(openFileOutput("agent.dat",MODE_PRIVATE));d.writeInt(1);d.writeInt(episodes);d.writeInt(crashes);d.writeInt(best);d.writeFloat(epsilon);for(float v:q)d.writeFloat(v);d.close();}catch(Exception ignored){}}
  void load(){try{DataInputStream d=new DataInputStream(openFileInput("agent.dat"));if(d.readInt()!=1){d.close();return;}episodes=d.readInt();crashes=d.readInt();best=d.readInt();epsilon=d.readFloat();for(int i=0;i<q.length;i++)q[i]=d.readFloat();d.close();}catch(Exception ignored){}}
 }
}
