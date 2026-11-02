package com.personal.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;

public class MainActivity extends Activity {
    private static final int PICK = 1;
    private File modules;
    private int tab = 1;
    private WebView web;
    private boolean managing;
    private boolean fullscreen;

    @Override public void onCreate(Bundle state) { super.onCreate(state); modules = new File(getFilesDir(), "functions"); modules.mkdirs(); home(); }
    private Button button(String text, Runnable action) { Button b = new Button(this); b.setAllCaps(false); b.setText(text); b.setOnClickListener(v -> action.run()); return b; }
    private void message(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
    private boolean enabled(String id) { return getPreferences(MODE_PRIVATE).getBoolean(id, true); }
    private JSONObject metadata(File dir) throws Exception { return new JSONObject(new String(read(new File(dir, "manifest.json")), StandardCharsets.UTF_8)); }
    private byte[] read(File f) throws IOException { try (InputStream in = new FileInputStream(f); ByteArrayOutputStream out = new ByteArrayOutputStream()) { copy(in, out, 2_000_000); return out.toByteArray(); } }
    private static void copy(InputStream in, OutputStream out, long limit) throws IOException { byte[] b = new byte[8192]; long total = 0; int n; while ((n = in.read(b)) != -1) { total += n; if (total > limit) throw new IOException("File too large"); out.write(b, 0, n); } }
    private void home() {
        managing = false;
        setFullscreen(false);
        if (web != null) { web.destroy(); web = null; }
        LinearLayout root = new LinearLayout(this); root.setOrientation(1); root.setPadding(20, 12, 20, 12);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); LinearLayout body = new LinearLayout(this); body.setOrientation(1); body.setGravity(Gravity.CENTER); scroll.addView(body); root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        EditText search = new EditText(this); search.setSingleLine(); search.setHint("Search functions");
        LinearLayout list = new LinearLayout(this); list.setOrientation(1);
        if (tab == 2) {
            body.addView(button("Import", () -> { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("*/*"); startActivityForResult(i, PICK); }));
            body.addView(button("Manage", this::manage));
            body.addView(button("About", this::about));
        } else {
            body.addView(search, new LinearLayout.LayoutParams(-1, -2)); body.addView(list, new LinearLayout.LayoutParams(-1, -2));
        }
        LinearLayout nav = new LinearLayout(this); String[] labels = {"ALL", "Search", "Settings"};
        for (int i = 0; i < labels.length; i++) { final int n = i; nav.addView(button(labels[i], () -> { tab = n; home(); }), new LinearLayout.LayoutParams(0, -2, 1)); } root.addView(nav); setContentView(root);
        Runnable refresh = () -> { list.removeAllViews(); String query = search.getText().toString().trim().toLowerCase(Locale.ROOT); int count = 0;
            File[] dirs = modules.listFiles(); if (dirs != null) for (File d : dirs) try { JSONObject m = metadata(d); String id = m.getString("id"), name = m.getString("name"), category = m.getString("category");
                if (!enabled(id) || !name.toLowerCase(Locale.ROOT).contains(query)) continue;
                list.addView(button(name, () -> open(d, m))); count++;
            } catch (Exception ignored) { }
            if (count == 0) { TextView empty = new TextView(this); empty.setText("No functions found. Open Settings to import a function."); list.addView(empty); }
        };
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int a,int c,int f) {} public void onTextChanged(CharSequence s,int a,int b,int c) { refresh.run(); } public void afterTextChanged(Editable e) {} }); refresh.run();
    }
    private void about() {
        String url = "https://github.com/hiSean0909/Android-Magic-Box";
        new AlertDialog.Builder(this).setTitle("About").setMessage(url)
            .setNegativeButton("Close", null)
            .setPositiveButton("Copy", (dialog, which) -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("GitHub", url));
                message("GitHub address copied");
            }).show();
    }
    private void manage() {
        managing = true;
        LinearLayout root = new LinearLayout(this); root.setOrientation(1); root.setPadding(20, 12, 20, 12); root.addView(button("Back", this::home));
        ScrollView sc = new ScrollView(this); LinearLayout list = new LinearLayout(this); list.setOrientation(1); sc.addView(list); root.addView(sc); setContentView(root);
        File[] dirs = modules.listFiles(); if (dirs != null) for (File d : dirs) try { JSONObject m = metadata(d); String id = m.getString("id");
            TextView title = new TextView(this); title.setText(m.getString("name") + " " + m.getString("version") + (enabled(id) ? " (enabled)" : " (disabled)")); list.addView(title);
            list.addView(button(enabled(id) ? "Disable" : "Enable", () -> { getPreferences(MODE_PRIVATE).edit().putBoolean(id, !enabled(id)).apply(); manage(); }));
            list.addView(button("Uninstall", () -> new AlertDialog.Builder(this).setMessage("Uninstall " + m.optString("name") + "?").setNegativeButton("Cancel", null).setPositiveButton("Uninstall", (a,b) -> { delete(d); manage(); }).show()));
        } catch (Exception ignored) { }
    }
    private static void delete(File f) { if (f.isDirectory()) { File[] files = f.listFiles(); if (files != null) for (File x : files) delete(x); } f.delete(); }
    @Override protected void onActivityResult(int request, int result, Intent data) { super.onActivityResult(request, result, data); if (request != PICK || result != RESULT_OK || data == null) return; try { install(data.getData()); message("Function imported"); } catch (Exception e) { message("Import failed: " + e.getMessage()); } home(); }
    private void install(Uri uri) throws Exception {
        File stage = new File(getCacheDir(), "function-import"); delete(stage); if (!stage.mkdirs()) throw new IOException("Cannot create temporary directory");
        try {
            int count = 0; long total = 0; try (InputStream input = getContentResolver().openInputStream(uri); ZipInputStream zip = new ZipInputStream(input)) { ZipEntry entry; byte[] b = new byte[8192];
                while ((entry = zip.getNextEntry()) != null) { if (++count > 200) throw new IOException("Too many files"); File target = new File(stage, entry.getName()); if (!target.getCanonicalPath().startsWith(stage.getCanonicalPath() + File.separator)) throw new IOException("Invalid path");
                    if (entry.isDirectory()) { if (!target.mkdirs() && !target.isDirectory()) throw new IOException("Invalid directory"); continue; }
                    File parent = target.getParentFile(); if (!parent.mkdirs() && !parent.isDirectory()) throw new IOException("Invalid directory");
                    try (OutputStream out = new FileOutputStream(target)) { int n; while ((n = zip.read(b)) != -1) { total += n; if (total > 10_000_000) throw new IOException("Package too large"); out.write(b, 0, n); } }
                }
            }
            JSONObject m = metadata(stage); String id = m.getString("id"), name = m.getString("name"), category = m.getString("category"), entry = m.getString("entry"), version = m.getString("version");
            int api = m.getInt("hostApi");
            if (!id.matches("[a-z0-9][a-z0-9-]{0,63}") || name.trim().isEmpty() || version.trim().isEmpty() || !(category.equals("tool") || category.equals("game")) || api < 1 || api > 2) throw new IOException("Invalid or incompatible metadata");
            File page = new File(stage, entry); if (!page.getCanonicalPath().startsWith(stage.getCanonicalPath() + File.separator) || !page.isFile() || !entry.endsWith(".html")) throw new IOException("Invalid entry point");
            File dest = new File(modules, id), backup = new File(getCacheDir(), "function-backup"); delete(backup); boolean had = dest.exists(); if (had && !dest.renameTo(backup)) throw new IOException("Cannot prepare update");
            if (!stage.renameTo(dest)) { if (had) backup.renameTo(dest); throw new IOException("Cannot install function"); } delete(backup); if (!had) getPreferences(MODE_PRIVATE).edit().putBoolean(id, true).apply();
        } finally { delete(stage); }
    }
    private void open(File dir, JSONObject m) {
        String id = m.optString("id"), origin = "https://" + id + ".magicbox.local/"; web = new WebView(this); web.getSettings().setJavaScriptEnabled(true); web.getSettings().setDomStorageEnabled(true); web.getSettings().setAllowFileAccess(false); web.getSettings().setAllowContentAccess(false);
        web.addJavascriptInterface(new Object() { @JavascriptInterface public float screenDpi() { return getResources().getDisplayMetrics().ydpi; } @JavascriptInterface public float density() { return getResources().getDisplayMetrics().density; } @JavascriptInterface public void fullscreen(boolean value) { runOnUiThread(() -> setFullscreen(value)); } }, "MagicBox");
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { return !r.getUrl().toString().startsWith(origin); }
            @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) { try { String url = r.getUrl().toString(); if (!url.startsWith(origin)) return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
                String path = Uri.decode(r.getUrl().getEncodedPath().substring(1)); File f = new File(dir, path); if (!f.getCanonicalPath().startsWith(dir.getCanonicalPath() + File.separator) || !f.isFile()) throw new IOException(); String type = path.endsWith(".html") ? "text/html" : path.endsWith(".js") ? "text/javascript" : path.endsWith(".css") ? "text/css" : "application/octet-stream"; return new WebResourceResponse(type, "UTF-8", new FileInputStream(f));
            } catch (Exception e) { return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0])); } }
        });
        setContentView(web); web.loadUrl(origin + Uri.encode(m.optString("entry"), "/"));
    }
    private void setFullscreen(boolean value) {
        fullscreen = value;
        if (value) { getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); }
        else { getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); getWindow().getDecorView().setSystemUiVisibility(0); }
    }
    @Override protected void onPause() { if (web != null) web.evaluateJavascript("window.dispatchEvent(new Event('magicboxpause'))", null); super.onPause(); }
    @Override public void onBackPressed() { if (web != null && fullscreen) { web.evaluateJavascript("window.dispatchEvent(new Event('magicboxback'))", null); setFullscreen(false); } else if (web != null || managing) home(); else super.onBackPressed(); }
}
