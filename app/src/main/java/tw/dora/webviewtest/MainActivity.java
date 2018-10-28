package tw.dora.webviewtest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText myname;
    private LocationManager lmgr;
    private MyListener myListener;
    private TextView username;
    private UIHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myname = findViewById(R.id.myname);
        username = findViewById(R.id.username);

        //確認是否沒有足夠權限,若無就去要權限,有就直接初始化
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){

        uiHandler = new UIHandler();

        lmgr =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //最小間隔時間單位為分鐘,最小距離單位為公尺,若為零代表即時
        myListener = new MyListener();
        lmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,0,0,myListener);

        webView = findViewById(R.id.webview);
        initWebView();

    }

    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.v("brad",lat+" x "+lng);
            webView.loadUrl("javascript:gotoWhere("+lat+","+lng+")");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            //webView.loadUrl("javascript:gotoWhere(30,120)");

        }

        @Override
        public void onProviderDisabled(String provider) {
            //webView.loadUrl("javascript:gotoWhere(22,119)");

        }
    }

    @Override
    public void finish() {
        lmgr.removeUpdates(myListener);
        super.finish();
    }

    private void initWebView(){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        //以下為介紹WebView認識java內的物件實體
        webView.addJavascriptInterface(new MyJSObject(),"brad");

        //linux系統根路徑為一根/,外加本機檔案通訊協定file://
        webView.loadUrl("file:///android_asset/brad01.html");
        //webView.loadUrl("file:///android_asset/map.html");
        //webView.loadUrl("https://www.iii.org.tw");
    }

    public class MyJSObject {
        //若這個方法會被javascript介面呼叫,則一定要加上以下的annotation
        @JavascriptInterface
        public void callFromJS(String username){

            //Javascript Interface
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("歡迎:"+username).show();

            //以下這段顯現在前景的view這邊,交給後台的handler處理,才比較會即時
            Message message = new Message();
            Bundle data = new Bundle();
            data.putString("username",username);
            message.setData(data);

            uiHandler.sendMessage(message);

            //方法二: 不即時,不建議這樣做
            //MainActivity.this.username.setText(username);



        }
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String name = msg.getData().getString("username");
            username.setText(name);

            Log.v("brad","username = "+name);

        }
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
                    finish();
                }
            }).show();
            //super.onBackPressed();
        }

    }


    public void test1(View view) {
        //String name = myname.getText().toString();
        //webView.loadUrl("javascript:test4('"+name+"')");
        //webView.loadUrl("javascript:test4('brad')");
        webView.loadUrl("file:///android_asset/map.html");
       //webView.loadUrl("javascript:gotoKD()");

    }
}
