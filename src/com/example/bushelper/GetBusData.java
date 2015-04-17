package com.example.bushelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * ͨ������ ���﹫��api ��ȡ������Ϣ
 * @author Dr.Chan
 *
 */

public class GetBusData {
	
	//����appkey
	private final String appKey = "d706b1f36e6adfdb862f7f54c132390f";
	
	//����url
	private final String url = "http://openapi.aibang.com/bus/lines";
	
	public int resultNum;
	
	//��װλ����Ϣ�Ľṹ��
	public class BusData {
		public String name;
		public String info;
		public String stats;
	}
	
	/**
	 * 
	 * @param city
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	private String getJsonFromServer(String city, String keyword) throws Exception {
		BufferedReader in = null;
		String result = null;
		
		HttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet(url + "?app_key=" + appKey + "&alt=json&city=" + city + "&q=" + keyword);
        
        // ִ������  
        HttpResponse response = client.execute(request);
        
        // ���ղ�����������
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {  
            sb.append(line + NL);
        } 
        in.close(); 
        result = sb.toString();

        //�����ã�logcat��ӡ���ص�json�ַ���
        Log.i("myTag", "���������صĽ����" + result);
		
		return result;
	}
	
	private ArrayList<BusData> jsonToBusData(String json) throws JSONException {
		
		//ʵ����ArrayList<BusData>����
		ArrayList<BusData> busDataList = new ArrayList<BusData>();
		
		//���result_num
		JSONObject jsonObject = new JSONObject(json);
		resultNum = jsonObject.getInt("result_num");
		
		//���ÿ����·line����Ϣ
		JSONObject lines = new JSONObject(jsonObject.getString("lines"));
		JSONArray line = new JSONArray(lines.getString("line"));
		for(int i = 0; i < line.length(); i++) {
			BusData busData = new BusData();
			JSONObject jsonObjLine = (JSONObject) line.get(i);
			
			busData.name = jsonObjLine.getString("name");
			busData.info = jsonObjLine.getString("info").replace(";", "<br/>");
			
			//�ı��ʽ��ʹ������
			int j = 2;
			String temp = "(1)" + jsonObjLine.getString("stats");
			while(temp.indexOf(";") > 0) {
				temp = temp.replaceFirst(";", "<br/>(" + j +")");
				j++;
			}
			busData.stats = temp;
				
			busDataList.add(busData);
		}
		
		//�����ã��������
		for(BusData busdata:busDataList) {
			Log.i("mytag", busdata.name + "---" + busdata.info +"---" + busdata.stats);
		}
		
		return busDataList;
	}
	
	public ArrayList<BusData> run(String city, String keyword) throws Exception {
		return jsonToBusData(getJsonFromServer(city, keyword));
	}
}
