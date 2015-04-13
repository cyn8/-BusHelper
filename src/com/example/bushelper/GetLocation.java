package com.example.bushelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * ͨ������ �ٶ�ip��λ api��ȡ����
 * @author Dr.Chan
 *
 */
public class GetLocation {
	
	//�ٶ�api��ak
	private final String appKey = "08f6ef39dd1e939ab7438b5847842dbc";
	
	//����url
	private final String url = "http://api.map.baidu.com/location/ip";
	
	//��װλ����Ϣ�Ľṹ��
	public class Location {
		public String city;
		public String fullName;
		public String x;
		public String y;
	}
	
	/**
	 * ��������
	 */
	private String getJsonFromServer() throws Exception{
		BufferedReader in = null;
		String result = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		
        // ������/ֵ���б�  
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("ak", appKey));
        parameters.add(new BasicNameValuePair("coor", "bd09ll"));
        
        // ����UrlEncodedFormEntity����  
        UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);//���ñ��룬��ֹ�������� 
        request.setEntity(formEntiry);
        
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
	
	/**
	 * 
	 */
	private Location jsonToLocation(String json) throws JSONException {
		
		//ʵ����Location����
		Location location = new Location();
		
		//���status
		JSONObject jsonObject = new JSONObject(json);
		int status = jsonObject.getInt("status");
		
		//statusΪ0�ٽ���
		if(status == 0) {
			//��ó���
			String address = jsonObject.getString("address");
			String addressArray[] = address.split("\\|");
			location.city = addressArray[2];
			
			//��ó���ȫ��
			JSONObject content = new JSONObject(jsonObject.getString("content"));
			location.fullName = content.getString("address");
			
			//���x��y����
			JSONObject point = new JSONObject(content.getString("point"));
			location.x = point.getString("x");
			location.y = point.getString("y");
		}
		
		//Debug
		Log.i("location", location.city + "---" + location.fullName + "---" + location.x + "---" + location.y);
		
		return location;
	}
	
	public Location run() throws Exception{
		return jsonToLocation(getJsonFromServer());
	}
	
	/* ����ʾ��
	 {
   "address": "CN|�㶫|�麣|None|UNICOM|0|0",
   "content": {
       "address_detail": {
           "province": "�㶫ʡ",
           "city": "�麣��",
           "district": "",
           "street": "",
           "street_number": "",
           "city_code": 140
       },
       "address": "�㶫ʡ�麣��",
       "point": {
           "y": "2526069.55",
           "x": "12641851.33"
       }
   },
   "status": 0
	} 
	 */
}
