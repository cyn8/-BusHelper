package com.example.bushelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.bushelper.GetLocation.Location;

@SuppressLint("JavascriptInterface")
public class MainActivity extends Activity {
	
	private WebView webview;
	private String status;
	private Location location;
	private ProgressDialog pd; //�������Ի���
	private final String url = "file:///android_asset/index.html";
	
	//����Handler����
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        status = data.getString("status");
	        webview.loadUrl(url);
	    }
	};
	//�½�һ���̶߳���
	Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
	    	Message msg = new Message();
	        Bundle data = new Bundle();
	        
	    	//��������
	    	GetLocation getLocation = new GetLocation();
	    	try {
//	    		GetTransfer gt = new GetTransfer();
//	    		gt.run("�麣", "����", "Բ����԰");
	    		location = getLocation.run();
	    		data.putString("status", "OK");
			} catch (Exception e) {
				e.printStackTrace();
				data.putString("status", "false");
			}
			
	        msg.setData(data);
	        handler.sendMessage(msg);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//��ʾprogressdialog
		pd = ProgressDialog.show(this, "���Ե�", "���ڻ�ȡ����λ��...", false);
		
		webview = (WebView) findViewById(R.id.webView);
		
		//��Runnable����HTTP�����Է�����UI�߳���NetworkOnMainThreadException
		new Thread(runnable).start();
		
		//����webview�Ĳ����ͼ��ر���ҳ��
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);	//��ʹ��������ռλ
		webview.getSettings().setBuiltInZoomControls(false);	//�������½����Ű�ť
		webview.getSettings().setSupportZoom(false);	//������html����
		webview.getSettings().setJavaScriptEnabled(true);	//���룡ʹwebview�е�html֧��javascript���ܹ��밲׿���н���
		webview.getSettings().setUseWideViewPort(true);	//ʹ����Ӧ�ֱ���
		webview.getSettings().setLoadWithOverviewMode(true);	//ʹ����Ӧ�ֱ���
		webview.setWebViewClient(new webViewClient()); ////ΪWebView����WebViewClient����ĳЩ����	
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// ��ֹ�������ݹ������º���������
		webview.addJavascriptInterface(this, "android");	//ע�⣡ʹ��������䣬���ڱ����onCreate�������ע��@SuppressLint("JavascriptInterface")���͵���android.annotation.SuppressLint������Ȼ�ᱨ������Ҫ��@JavascriptInterfaceע��Ĺ��з���������webview�б�����
		
	}
	
	
	/**
	 * �ؼ�����ΪWebView����WebViewClient��Ȼ����дshouldOverrideUrlLoading�������ɡ�����WebViewClientΪWebView��һ�������࣬��Ҫ�������֪ͨ�������¼���
	 * @author Dr.Chan
	 *
	 */
	 class webViewClient extends WebViewClient{ 
		 	/**
		 	 * ��дshouldOverrideUrlLoading������ʹ������Ӻ�ʹ��������������򿪡� 
		 	 */
		 	@Override 
		    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		        view.loadUrl(url); 
		        //�������Ҫ�����Ե�������¼��Ĵ�����true�����򷵻�false 
		        return true; 
		    }
		 	
		 	/**
		 	 * ҳ��������ɺ����
		 	 */
		 	@Override
		 	public void onPageFinished(WebView view, String url) {
		 		super.onPageFinished(view, url);
		 		if(status.equals("OK")) {
		 			webview.loadUrl("javascript:setCity('" + location.city + "','" + location.fullName + "')");
		 		}
		 		
		 		//ҳ�������ɺ�ر�progressdialog
		 		pd.dismiss();
		 	}
	 }
	 
	 /**
	 * ��javaScript���õķ�������Ҫ�������ؼ��ʴ�����һ��Activity
	 * @param some
	 */
	 @JavascriptInterface
	 public void callDetailActivity(String city, String keyword) {
		//�ж��Ƿ�����������
		ConnectivityManager con = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// ��ǰ���粻����
			Toast.makeText(getApplicationContext(), "����ֻ�����������", Toast.LENGTH_SHORT).show();
		} else if(city.equals("") || keyword.equals("")) {
			Toast.makeText(getApplicationContext(), "����ȷ����������ƻ򹫽���·", Toast.LENGTH_SHORT).show();
		} else {
			//ȥ���ַ�����β�Ŀո�  
			city = city.trim();
			keyword = keyword.trim();
			
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			intent.putExtra("city", city);
			intent.putExtra("keyword", keyword);
			startActivity(intent);
		}
	 }
	 
	 @JavascriptInterface
	 public void callTransferActivity(String city, String here, String there) {
		//�ж��Ƿ�����������
		ConnectivityManager con = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// ��ǰ���粻����
			Toast.makeText(getApplicationContext(), "����ֻ�����������", Toast.LENGTH_SHORT).show();
		} else if(city.equals("") || here.equals("") || there.equals("")) {
			Toast.makeText(getApplicationContext(), "����ȷ��������������ڵغ�Ŀ�ĵ�", Toast.LENGTH_SHORT).show();
		} else {
			//ȥ���ַ�����β�Ŀո�  
			city = city.trim();
			here = here.trim();
			there = there.trim();
			
			Intent intent = new Intent(MainActivity.this, TransferActivity.class);
			intent.putExtra("city", city);
			intent.putExtra("here", here);
			intent.putExtra("there", there);
			startActivity(intent);
		}
	 }
}
