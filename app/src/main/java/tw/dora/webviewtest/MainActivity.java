package tw.dora.webviewtest;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText myname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myname = findViewById(R.id.myname);
        webView = findViewById(R.id.webview);
        initWebView();
    }

    private void initWebView(){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        //linux系統根路徑為一根/,外加本機檔案通訊協定file://
        //webView.loadUrl("file:///android_asset/brad01.html");
        webView.loadUrl("file:///android_asset/map.html");
        //webView.loadUrl("https://www.iii.org.tw");
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            new AlertDialog.Builder(this)
                    .setMessage("Exit?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            super.onBackPressed();
        }

    }


    public void test1(View view) {
        //String name = myname.getText().toString();
        //webView.loadUrl("javascript:test4('"+name+"')");
        //webView.loadUrl("javascript:test4('brad')");
        webView.loadUrl("javascript:gotoKD()");
    }
}
