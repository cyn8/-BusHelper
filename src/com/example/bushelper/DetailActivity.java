package com.example.bushelper;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.bushelper.GetBusData.BusData;

@SuppressLint("JavascriptInterface")
public class DetailActivity extends Activity {
	
	String city;
	String keyword;
	String status;
	ArrayList<BusData> busDataList;
	GetBusData getbusdata = new GetBusData();
	private final String url = "file:///android_asset/detail.html";
	private WebView webview;
	
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
//	    	GetLocation getLocation = new GetLocation();
	    	try {
	    		busDataList = getbusdata.run(city, keyword);
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
		
		webview = (WebView) findViewById(R.id.webView);
		
		//ȡ�������ؼ���
		Intent intent = getIntent();
		city = intent.getStringExtra("city");
		keyword = intent.getStringExtra("keyword");
		
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
			 		//����arrylist����js�ķ���������append��html��
			 		for(int i = 0; i < busDataList.size(); i++) {
			 			BusData busData = (BusData) busDataList.get(i);
			 			webview.loadUrl("javascript:appendDetail('" + busData.name + "','" + busData.info + "','" + busData.stats + "')");
			 		}
			 		webview.loadUrl("javascript:resultCount('" + getbusdata.resultNum + "')");
			 		
			 		//��̬����js
			 		String js = "var newscript = document.createElement(\"script\");";
			 		js += "newscript.src=\"file:///android_asset/js/amazeui.min.js\";";
			 		js += "document.body.appendChild(newscript);";
			 		webview.loadUrl("javascript:" + js);
			 	} else {
			 		Toast.makeText(getApplicationContext(), "û���ҵ���صĹ�����·~", Toast.LENGTH_SHORT).show();
			 		DetailActivity.this.finish();
			 	}
		 	}
	 }
	 
}
