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

public class GetTransfer {
	//����appkey
	private final String appKey = "d706b1f36e6adfdb862f7f54c132390f";
		
	//����url
	private final String url = "http://openapi.aibang.com/bus/transfer";
	
	public int resultNum;
	
	//��װ������Ϣ�Ľṹ��
	public class Transfer {
		public int dist;	//�ܾ���
		public int time;	//���ƺķ�ʱ��
		public int foot_dist;	//�ܲ��о���
		public int last_foot_dist;	//���յ�վ�ߵ��յ�ľ���
		public int transTimes; //���˴���
		public ArrayList<Segment> segmentList = new ArrayList<Segment>(); //���˵Ĺ�����Ϣ
	}
	public class Segment {
		public String startStat;	//���վ��
		public String endStat;	//�յ�վ��
		public String lineName; //��·����
		public String stats; //��;վ��
		public int lineDist;	//��ʻ����
		public int footDist;	//���о���
	}
	
	private String getJsonFromServer(String city, String here, String there) throws Exception {
		BufferedReader in = null;
		String result = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url + "?app_key=" + appKey + "&alt=json&city=" + city + "&start_addr=" + here + "&end_addr=" + there);
		
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
	
	private ArrayList<Transfer> jsonToBusData(String json) throws JSONException {
		
		//ʵ����ArrayList<BusData>����
		ArrayList<Transfer> transferList = new ArrayList<Transfer>();
		
		//���result_num
		JSONObject jsonObject = new JSONObject(json);
		resultNum = jsonObject.getInt("result_num");
		
		//���ÿ��������Ϣ
		JSONObject buses = new JSONObject(jsonObject.getString("buses"));
		JSONArray bus = new JSONArray(buses.getString("bus"));
		for(int i = 0; i < bus.length(); i++) {
			Transfer transfer = new Transfer();
			JSONObject busObject = (JSONObject) bus.get(i);
			JSONObject segments = new JSONObject(busObject.getString("segments"));
			JSONArray segmentArr = new JSONArray(segments.getString("segment"));
			
			transfer.dist = busObject.getInt("dist");
			System.out.println(transfer.dist + "sdfsdf");
			transfer.time = busObject.getInt("time");
			transfer.foot_dist = busObject.getInt("foot_dist");
			transfer.last_foot_dist = busObject.getInt("last_foot_dist");
			transfer.transTimes = segmentArr.length();
			
			for(int j = 0; j < segmentArr.length(); j++) {
				Segment segment = new Segment();
				JSONObject segmentObj = (JSONObject) segmentArr.get(j);
				
				segment.startStat = segmentObj.getString("start_stat");
				segment.endStat = segmentObj.getString("end_stat");
				segment.lineName = segmentObj.getString("line_name");
				segment.stats = segmentObj.getString("stats");
				segment.lineDist = segmentObj.getInt("line_dist");
				segment.footDist = segmentObj.getInt("foot_dist");
				
				transfer.segmentList.add(segment);
			}
			transferList.add(transfer);
		}
		
		//�����ã��������
		for(Transfer transfer:transferList) {
			Log.i("mytag", transfer.dist + "----" + transfer.foot_dist +"----" + transfer.last_foot_dist);
		}
		
		return transferList;
	}
	
	public ArrayList<Transfer> run(String city, String here, String there) throws Exception {
		return jsonToBusData(getJsonFromServer(city, here, there));
	}
}
