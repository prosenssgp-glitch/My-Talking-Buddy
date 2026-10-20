package com.prosen.ishanbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.content.pm.PackageManager;
import android.Manifest;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.*;

public class MainActivity extends Activity {
  TextToSpeech tts; TextView chat; EditText input; LinearLayout root; SpeechRecognizer recognizer;
  int orange=Color.rgb(255,157,35);
  @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.rgb(255,183,77)); getWindow().setNavigationBarColor(Color.rgb(255,183,77));
    tts=new TextToSpeech(this, status->{ if(status==TextToSpeech.SUCCESS) tts.setLanguage(new Locale("bn","IN")); });
    root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(20,24,20,16); root.setBackgroundColor(Color.rgb(255,248,230)); setContentView(root);
    TextView title=new TextView(this); title.setText("👦 ঈশান AI বন্ধু"); title.setTextSize(27); title.setTypeface(null,Typeface.BOLD); title.setTextColor(Color.rgb(105,63,20)); title.setGravity(Gravity.CENTER); root.addView(title);
    TextView subtitle=new TextView(this); subtitle.setText("তোমার মজার পড়ার সাথী! ❤️"); subtitle.setTextSize(16); subtitle.setGravity(Gravity.CENTER); root.addView(subtitle);
    chat=new TextView(this); chat.setText("হ্যালো বন্ধু! আমি ঈশান! 😄\nআমার সঙ্গে ইংরেজি বলো, অঙ্ক করো অথবা মজার কথা বলো।\n\nপরিবার: বাবা প্রসেন, মা সাথী, কাকা দীপান, কাকি আশা, ঠাকুমা পূর্ণিমা, দাদু জগন্নাথ।"); chat.setTextSize(19); chat.setTextColor(Color.DKGRAY); chat.setPadding(18,18,18,18); chat.setBackgroundColor(Color.WHITE);
    ScrollView scroll=new ScrollView(this); scroll.addView(chat); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,0,1); sp.setMargins(0,20,0,12); root.addView(scroll,sp);
    addButton("📘 Spoken English",v->say("চলো ইংরেজি শিখি! বলো: My name is Ishan. মানে আমার নাম ঈশান। এবার তুমি বলো!"));
    addButton("➕ গণিত শেখো",v->say("চলো অঙ্ক করি! দুই যোগ তিন কত? উত্তর পাঁচ! এবার তুমি বলো, পাঁচ যোগ পাঁচ কত?"));
    addButton("🍚 খাবারের সময়",v->say("এই বন্ধু! খাওয়ার সময় হয়েছে! চলো খেয়ে নাও, নইলে পেটের মধ্যে ইঁদুর ফুটবল খেলবে! হিহিহি!"));
    addButton("🛁 গোসলের সময়",v->say("বন্ধু, গোসলের সময় হয়েছে! পরিষ্কার থাকলে শরীর ভালো থাকে। চলো তৈরি হও!"));
    addButton("🧼 ঈশানকে সাবান মাখাও",v->say("চলো খেলতে খেলতে ঈশানকে গোসল করাই! আগে পানি, তারপর সাবান দিয়ে হাত-পা আর শরীর পরিষ্কার করি। চোখে সাবান দেবে না! 🫧"));
    addButton("🚲 সাইকেল চালাও",v->say("ব্রুম ব্রুম! 🚲 ঈশান সাইকেল চালাচ্ছে! হেলমেট পরো, বড়দের সঙ্গে নিরাপদ জায়গায় চালাও, আর রাস্তার গাড়ি থেকে দূরে থাকো!"));
    addButton("🚗 গাড়ি চালানোর খেলা",v->say("পিপ পিপ! 🚗 ঈশান এখন কল্পনার খেলনা গাড়ি চালাচ্ছে! বাস্তব গাড়ি শুধু বড়রা চালাবে। তুমি খেলনা গাড়ি দিয়ে খেলতে পারো!"));
    LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); input=new EditText(this); input.setSingleLine(true); input.setHint("ঈশানকে কিছু বলো..."); row.addView(input,new LinearLayout.LayoutParams(0,-2,1)); Button send=new Button(this); send.setText("পাঠাও"); send.setOnClickListener(v->{String s=input.getText().toString().trim(); if(!s.isEmpty()){input.setText(""); respond(s);}}); row.addView(send); root.addView(row);
    Button mic=new Button(this); mic.setText("🎤 কথা বলো (চাপ দিলে শুনবে)"); mic.setOnClickListener(v->listen()); root.addView(mic);
  }
  void addButton(String text, View.OnClickListener l){Button b=new Button(this); b.setText(text); b.setTextSize(16); b.setOnClickListener(l); root.addView(b);}
  void say(String s){chat.setText(s); if(tts!=null) tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"ishan");}
  void respond(String s){String q=s.toLowerCase(Locale.ROOT); String a;
    if(q.matches(".*[0-9].*[+].*[0-9].*")){try{String[] p=q.replaceAll("[^0-9+\\-]"," ").trim().split("\\s*\\+\\s*"); a="বাহ! অঙ্ক নিয়ে কাজ করি! "+p[0]+" যোগ "+p[1]+" এর উত্তর "+(Integer.parseInt(p[0].trim())+Integer.parseInt(p[1].trim()))+"! 😄";}catch(Exception e){a="চলো একসঙ্গে অঙ্ক করি! ২ + ৩ = ৫।";}}
    else if(q.contains("english")||q.contains("ইংরেজি")) a="চলো ইংরেজি শিখি! বলো: How are you? মানে তুমি কেমন আছো?";
    else if(q.contains("খাবার")||q.contains("খেতে")) a="হিহিহি! খাবারের সময় হলে বড়দের সঙ্গে খেতে বসো!";
    else if(q.contains("নাম")||q.contains("name")) a="আমার নাম ঈশান! তোমার নাম কী বন্ধু? 😄";
    else a="হিহিহি! দারুণ বলেছো বন্ধু! 😄 আমি এখনো শিখছি। চলো ইংরেজি বা অঙ্ক অনুশীলন করি!";
    chat.setText("তুমি: "+s+"\n\nঈশান: "+a); if(tts!=null) tts.speak(a,TextToSpeech.QUEUE_FLUSH,null,"ishan"); }
  void listen(){ if(!SpeechRecognizer.isRecognitionAvailable(this)){say("এই ফোনে speech recognition পাওয়া যাচ্ছে না।");return;} if(android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},10);return;} if(recognizer!=null)recognizer.destroy(); recognizer=SpeechRecognizer.createSpeechRecognizer(this); recognizer.setRecognitionListener(new RecognitionListener(){public void onReadyForSpeech(Bundle b){say("বল বন্ধু, আমি শুনছি! 🎤");} public void onResults(Bundle b){ArrayList<String> r=b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); if(r!=null&&!r.isEmpty())respond(r.get(0));} public void onError(int e){say("আবার একবার বলো বন্ধু! 😄");} public void onBeginningOfSpeech(){} public void onRmsChanged(float v){} public void onBufferReceived(byte[] b){} public void onEndOfSpeech(){} public void onPartialResults(Bundle b){} public void onEvent(int a,Bundle b){} }); Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"bn-IN"); recognizer.startListening(i); }
  @Override public void onDestroy(){if(tts!=null)tts.shutdown(); if(recognizer!=null)recognizer.destroy();super.onDestroy();}
}
