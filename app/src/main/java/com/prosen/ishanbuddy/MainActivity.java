package com.prosen.ishanbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
  private TextToSpeech tts;
  private SpeechRecognizer recognizer;
  private TextView status;
  private EditText input;
  private BuddySceneView scene;

  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    getWindow().setStatusBarColor(Color.rgb(255, 176, 74));
    getWindow().setNavigationBarColor(Color.rgb(255, 176, 74));
    tts = new TextToSpeech(this, result -> {
      if (result == TextToSpeech.SUCCESS && tts != null) {
        int language = tts.setLanguage(new Locale("bn", "IN"));
        if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
          tts.setLanguage(Locale.getDefault());
        }
      }
    });

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(12), dp(8), dp(12), dp(8));
    root.setBackgroundColor(Color.rgb(255, 247, 225));
    setContentView(root);

    TextView title = new TextView(this);
    title.setText("👦 ঈশান AI বন্ধু");
    title.setTextSize(24);
    title.setTextColor(Color.rgb(95, 53, 25));
    title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    root.addView(title, new LinearLayout.LayoutParams(-1, dp(38)));

    TextView subtitle = new TextView(this);
    subtitle.setText("আমার সঙ্গে খেলো, কথা বলো, শেখো! ✨");
    subtitle.setTextSize(14);
    subtitle.setTextColor(Color.rgb(105, 77, 53));
    subtitle.setGravity(Gravity.CENTER);
    root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(24)));

    scene = new BuddySceneView(this);
    scene.setOnBuddyAction(action -> say(action));
    LinearLayout.LayoutParams sceneParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
    sceneParams.setMargins(0, dp(5), 0, dp(5));
    root.addView(scene, sceneParams);

    HorizontalScrollView modeScroll = new HorizontalScrollView(this);
    modeScroll.setHorizontalScrollBarEnabled(false);
    LinearLayout modes = new LinearLayout(this);
    modes.setOrientation(LinearLayout.HORIZONTAL);
    modeScroll.addView(modes);
    addMode(modes, "🏠 ঘর", BuddySceneView.MODE_HOME, "স্বাগতম বন্ধু! আমি ঈশান। আমাকে টোকা দাও, আমরা কথা বলব!");
    addMode(modes, "🛁 গোসল", BuddySceneView.MODE_BATH, "চলো গোসলের খেলা খেলি! সাবানের বুদবুদে টোকা দাও। চোখে সাবান নয়!");
    addMode(modes, "🚗 গাড়ি", BuddySceneView.MODE_CAR, "খেলনা গাড়ি চালাই! দৃশ্যের গাড়িতে টোকা দাও, গাড়ি এগোবে।");
    addMode(modes, "🚲 সাইকেল", BuddySceneView.MODE_BIKE, "সাইকেল চালানোর মজা! সাইকেলে টোকা দাও। বাস্তবে হেলমেট পরবে এবং বড়দের সঙ্গে থাকবে।");
    addMode(modes, "🍎 খাবার", BuddySceneView.MODE_FOOD, "ইয়াম! খাবারের সময়। ফল আর স্বাস্থ্যকর খাবার খেলে শরীর শক্তিশালী হয়!");
    addMode(modes, "😴 ঘুম", BuddySceneView.MODE_SLEEP, "শুভরাত্রি বন্ধু। আরাম করে ঘুমাও, কাল আবার খেলব!");
    root.addView(modeScroll, new LinearLayout.LayoutParams(-1, dp(48)));

    status = new TextView(this);
    status.setText("ঈশানকে টোকা দাও অথবা নিচে একটি খেলা বেছে নাও।");
    status.setTextColor(Color.rgb(70, 55, 42));
    status.setTextSize(14);
    status.setPadding(dp(10), dp(6), dp(10), dp(6));
    status.setBackgroundColor(Color.WHITE);
    root.addView(status, new LinearLayout.LayoutParams(-1, dp(48)));

    LinearLayout chatRow = new LinearLayout(this);
    chatRow.setOrientation(LinearLayout.HORIZONTAL);
    input = new EditText(this);
    input.setSingleLine(true);
    input.setTextSize(14);
    input.setHint("ঈশানকে কিছু বলো...");
    chatRow.addView(input, new LinearLayout.LayoutParams(0, dp(48), 1));
    Button send = new Button(this);
    send.setText("পাঠাও");
    send.setOnClickListener(v -> {
      String text = input.getText().toString().trim();
      if (!text.isEmpty()) { input.setText(""); respond(text); }
    });
    chatRow.addView(send, new LinearLayout.LayoutParams(-2, dp(48)));
    root.addView(chatRow);

    Button mic = new Button(this);
    mic.setText("🎤 কথা বলে বন্ধু হও");
    mic.setOnClickListener(v -> listen());
    root.addView(mic, new LinearLayout.LayoutParams(-1, dp(46)));
  }

  private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

  private void addMode(LinearLayout parent, String label, int mode, String greeting) {
    Button button = new Button(this);
    button.setText(label);
    button.setTextSize(13);
    button.setOnClickListener(v -> {
      scene.setMode(mode);
      status.setText(greeting);
      speak(greeting);
    });
    parent.addView(button, new LinearLayout.LayoutParams(-2, dp(46)));
  }

  private void say(String text) {
    status.setText(text);
    speak(text);
  }

  private void speak(String text) {
    if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ishan-speech");
  }

  private void respond(String text) {
    String q = text.toLowerCase(Locale.ROOT);
    String answer;
    if (q.matches(".*[0-9]+\\s*\\+\\s*[0-9]+.*")) {
      try {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)").matcher(q);
        if (m.find()) answer = "বাহ! " + m.group(1) + " যোগ " + m.group(2) + " সমান " +
            (Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2))) + "! আরেকটা অঙ্ক করব?";
        else answer = "চলো একসঙ্গে অঙ্ক করি! যেমন ২ + ৩ = ৫।";
      } catch (Exception e) { answer = "চলো সহজ অঙ্ক দিয়ে শুরু করি: ২ + ৩ = ৫।"; }
    } else if (q.contains("নাম") || q.contains("name")) answer = "আমার নাম ঈশান! তোমার সঙ্গে খেলতে আমার খুব ভালো লাগে!";
    else if (q.contains("ইংরেজি") || q.contains("english")) answer = "চলো ইংরেজি শিখি! বলো: My name is Ishan. মানে আমার নাম ঈশান।";
    else if (q.contains("গোসল") || q.contains("সাবান") || q.contains("bath")) {
      scene.setMode(BuddySceneView.MODE_BATH);
      answer = "চলো গোসলের খেলা খেলি! সাবানের বুদবুদে টোকা দাও। চোখে সাবান দেবে না!";
    } else if (q.contains("গাড়ি") || q.contains("car")) {
      scene.setMode(BuddySceneView.MODE_CAR); answer = "ব্রুম ব্রুম! দৃশ্যের খেলনা গাড়িতে টোকা দাও!";
    } else if (q.contains("সাইকেল") || q.contains("bike")) {
      scene.setMode(BuddySceneView.MODE_BIKE); answer = "চলো সাইকেলের খেলা খেলি! বাস্তবে হেলমেট পরবে এবং বড়দের সঙ্গে থাকবে।";
    } else if (q.contains("খাবার") || q.contains("খেতে")) answer = "ফল, সবজি আর ভালো খাবার খেলে আমরা শক্তিশালী হই!";
    else if (q.contains("হ্যালো") || q.contains("hello") || q.contains("কেমন")) answer = "আমি ভালো আছি বন্ধু! তুমি কেমন আছো? আজ কী খেলবে?";
    else answer = "তোমার কথা শুনে ভালো লাগল বন্ধু! আমি এখন ছোট্ট অফলাইন সহকারী। অঙ্ক, ইংরেজি, গোসল বা গাড়ির খেলা বলতে পারো।";
    status.setText("তুমি: " + text + "\nঈশান: " + answer);
    speak(answer);
  }

  private void listen() {
    if (!SpeechRecognizer.isRecognitionAvailable(this)) {
      say("এই ফোনে কথা শনাক্ত করার সুবিধা পাওয়া যাচ্ছে না।"); return;
    }
    if (android.os.Build.VERSION.SDK_INT >= 23 &&
        checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 10); return;
    }
    if (recognizer != null) recognizer.destroy();
    recognizer = SpeechRecognizer.createSpeechRecognizer(this);
    recognizer.setRecognitionListener(new RecognitionListener() {
      public void onReadyForSpeech(Bundle b) { status.setText("বল বন্ধু, আমি শুনছি! 🎤"); }
      public void onResults(Bundle b) {
        ArrayList<String> results = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (results != null && !results.isEmpty()) respond(results.get(0));
      }
      public void onError(int e) { status.setText("আবার একবার বলো বন্ধু!"); }
      public void onBeginningOfSpeech() {}
      public void onRmsChanged(float v) {}
      public void onBufferReceived(byte[] b) {}
      public void onEndOfSpeech() {}
      public void onPartialResults(Bundle b) {}
      public void onEvent(int a, Bundle b) {}
    });
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN");
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ঈশানের সঙ্গে কথা বলো");
    recognizer.startListening(intent);
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
    super.onRequestPermissionsResult(requestCode, permissions, results);
    if (requestCode == 10 && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) listen();
  }

  @Override public void onDestroy() {
    if (tts != null) { tts.stop(); tts.shutdown(); }
    if (recognizer != null) recognizer.destroy();
    super.onDestroy();
  }
}
