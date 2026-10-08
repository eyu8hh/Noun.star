package com.nounstar.app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.webkit.WebViewAssetLoader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
  private WebView wv;
  private ValueCallback<Uri[]> cb;
  private final ActivityResultLauncher<Intent> picker = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(), r -> {
        if (cb != null) {
          cb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(r.getResultCode(), r.getData()));
          cb = null;
        }
      });

  @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    FrameLayout root = new FrameLayout(this);
    root.setBackgroundColor(0xFF050505);
    wv = new WebView(this);
    wv.setBackgroundColor(0xFF050505);
    root.addView(wv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    setContentView(root);
    WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(false);
    ViewCompat.setOnApplyWindowInsetsListener(root, (v, w) -> {
      Insets i = w.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
      v.setPadding(i.left, i.top, i.right, i.bottom);
      return WindowInsetsCompat.CONSUMED;
    });
    ViewCompat.requestApplyInsets(root);
    WebSettings s = wv.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setTextZoom(100);
    s.setAllowFileAccess(false);
    final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
    wv.setWebViewClient(new WebViewClient() {
      @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
        return loader.shouldInterceptRequest(r.getUrl());
      }
      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { return true; }
    });
    wv.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> c, FileChooserParams p) {
        if (cb != null) cb.onReceiveValue(null);
        cb = c;
        try { picker.launch(p.createIntent()); } catch (Exception e) { cb = null; return false; }
        return true;
      }
    });
    wv.addJavascriptInterface(new Object() {
      @JavascriptInterface public void saveFile(String name, String mime, String text) {
        try {
          ContentValues v = new ContentValues();
          v.put(MediaStore.Downloads.DISPLAY_NAME, name);
          v.put(MediaStore.Downloads.MIME_TYPE, mime);
          v.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NounStar");
          Uri u = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
          try (OutputStream o = getContentResolver().openOutputStream(u)) { o.write(text.getBytes(StandardCharsets.UTF_8)); }
          toast("تم الحفظ في التنزيلات/NounStar: " + name);
        } catch (Exception e) { toast("تعذر حفظ الملف"); }
      }
    }, "Android");
    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
      @Override public void handleOnBackPressed() {
        wv.evaluateJavascript("(function(){var o=document.querySelectorAll('.ov');if(o.length){o[o.length-1].remove();return 1}if(typeof tab!=='undefined'&&tab!=='home'){go('home');return 1}return 0})()",
            val -> { if (!"1".equals(val)) finish(); });
      }
    });
    wv.loadUrl("https://appassets.androidplatform.net/assets/www/index.html");
  }
  private void toast(String m) { runOnUiThread(() -> Toast.makeText(this, m, Toast.LENGTH_LONG).show()); }
}
