package com.example.bushelper;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.bushelper.GetTransfer.Segment;
import com.example.bushelper.GetTransfer.Transfer;

@SuppressLint("JavascriptInterface")
public class TransferActivity extends Activity {
	
	private String here;
	private String there;
	private String city;
	private WebView webview;
	private ProgressDialog pd; //�������Ի���
	private GetTransfer getTransfer;
	private String status;
	private ArrayList<Transfer> transferList;
	private final String url = "file:///android_asset/transfer.html";
	
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
	        
	        try {
	        	getTransfer = new GetTransfer();
	        	transferList = getTransfer.run(city, here, there);
	    		data.putString("status", "OK");
			} catch (Exception e) {
				data.putString("status", "false");
				e.printStackTrace();
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
		
		//��ʾprogressdialog
		pd = ProgressDialog.show(this, "���Ե�", "���ڲ�ѯ��...", false);
		
		//ȡ�������ؼ���
		Intent intent = getIntent();
		city = intent.getStringExtra("city");
		here = intent.getStringExtra("here");
		there = intent.getStringExtra("there");
		
		//��Runnable����HTTP�����Է�����UI�߳���NetworkOnMainThreadException
		new Thread(runnable).start();
		
		//����webview�Ĳ����ͼ��ر���ҳ��
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);	//��ʹ��������ռλ
		webview.getSettings().setBuiltInZoomControls(false);	//�������½����Ű�ť
		webview.getSettings().setSupportZoom(false);	//������html����
		webview.getSettings().setJavaScriptEnabled(true);	//���룡ʹwebview�е�html֧��javascript���ܹ��밲׿���н���
		webview.getSettings().setUseWideViewPort(true);	//ʹ����Ӧ�ֱ���
		webview.getSettings().setLoadWithOverviewMode(true);	//ʹ����Ӧ�ֱ���
		webview.setWebViewClient(new webViewClient()); //ΪWebView����WebViewClient����ĳЩ����	
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
//			 		for(int i = 0; i < busDataList.size(); i++) {
//			 			BusData busData = (BusData) busDataList.get(i);
//			 			webview.loadUrl("javascript:appendDetail('" + busData.name + "','" + busData.info + "','" + busData.stats + "')");
//			 		}
//			 		webview.loadUrl("javascript:resultCount('" + getbusdata.resultNum + "')");
		 			for(int i = 0; i < transferList.size(); i++) {
		 				StringBuffer sb = new StringBuffer("<br/>");
		 				Transfer transfer = (Transfer) transferList.get(i);
		 				for(int j = 0; j < transfer.segmentList.size(); j++) {
		 					Segment segment = (Segment) transfer.segmentList.get(j);
		 					sb.append("<code>���</code>" + segment.lineName + "<br><code>��ʼվ</code>" + segment.startStat + " <code>�յ�վ</code>" + segment.endStat + "<br><code>����վ��</code><br>" + segment.stats + "<br><code>��ʻ����</code>" + segment.lineDist + "m <code>���о���</code>" + segment.footDist + "m<hr>");
		 				}
		 				//ȥ�����ķָ���<hr>
		 				sb.deleteCharAt(sb.length() - 4);
		 				webview.loadUrl("javascript:appendTransfer('" + (i+1) + "','" + transfer.dist + "','" + transfer.transTimes + "','" + transfer.time + "','" + transfer.foot_dist + "','" + sb.toString() + "')");
		 			}
		 			webview.loadUrl("javascript:resultCount('" + getTransfer.resultNum + "')");
			 		
			 		//��̬����js
			 		String js = "var newscript = document.createElement(\"script\");";
			 		js += "newscript.src=\"file:///android_asset/js/amazeui.min.js\";";
			 		js += "document.body.appendChild(newscript);";
			 		webview.loadUrl("javascript:" + js);
			 	} else {
			 		Toast.makeText(getApplicationContext(), "û���ҵ���صĹ�����·~", Toast.LENGTH_SHORT).show();
			 		TransferActivity.this.finish();
			 	}
		 		
		 		//ҳ�������ɺ�ر�progressdialog
		 		pd.dismiss();
		 	}
	 }
}
