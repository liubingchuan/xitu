package com.xitu.app.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.service.es.ESHttpClient;


public class DataDiscoveryServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static int num = 1; 
	static List<String> listhzlw = new ArrayList();
	
	
	
	 public String getClientIP(HttpServletRequest request) {
		 String ip = request.getHeader("x-forwarded-for"); 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("WL-Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getRemoteAddr(); 
	       } 
	       return ip; 
	    }
	public String getPaperEsData(JSONObject data,String name){
		String abs = "";
		List<Object> article_abstract = (List<Object>)data.get(name);
		if(article_abstract!=null && article_abstract.size()>0){
			List<Object> abstractList = (List<Object>)article_abstract.get(0);
			List<Object> abstractLists  = null;
			if(abstractList!=null){
				abstractLists = (List<Object>)abstractList.get(0);
			}
			if(abstractLists!=null){
				abs += abstractLists.get(0).toString();
			}
		}
		return abs;
	}


	

	/*public JSONObject searchJiJinAgg(HttpServletRequest request, String param, String year,String type, String ins,String author, String xmzz,String lang,int pageNo,
			String orderBy, String method) {
		JSONObject conES = null;
		try {
			
			conES = ESHttpClient
					.conESJiJin(ESHttpClient.createQqueryJiJinJsonGLAgg(param, year, type,ins,author,xmzz, lang,pageNo, orderBy, method));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (param != null && !param.equals("") && !param.equals("null")) {
//			conES = ESHttpClient
//					.conESJiJin(ESHttpClient.createQqueryJiJinJsonGL(param, year, type,ins,author,xmzz, pageNo, orderBy, method));
//		} else {
//			conES = ESHttpClient
//					.conESJiJin(ESHttpClient.createQqueryFundJson("a", year, type, ins,author,xmzz,pageNo, orderBy, method));
//		}
		//conES = findJJAuthorAndIns(conES);
		return conES;
	}*/

	public JSONObject findPaperBycs(String doi,String type){
		JSONObject redata = new JSONObject();
		int countcgs = 0;
		String[] arr = doi.split(",");
		JSONArray arrcgs = new JSONArray();
		for(String doiid : arr){
			if(!doiid.equals("") && doiid != ""){
				countcgs = getHtmlBypc(doiid,type);
			}else{
				countcgs = 0;
			}
			arrcgs.add(countcgs);
		}
		redata.put("countbycs", arrcgs);
		return redata;
	}

	public int getHtmlBypc(String doiid,String type){
		String result = "";
		int bycs = 0;
		try {
			// List formParams = new ArrayList();
			// formParams.add(new BasicNameValuePair("keyword", keyword));
			// formParams.add(new BasicNameValuePair("domain", domaintype));
			// HttpEntity entity = new UrlEncodedFormEntity(formParams,
			// "UTF-8");
			String url = "http://iswitch.las.ac.cn/wos/wosSearch?act=identify";
			if(type.equals("doi")){
				url += "&doi="+doiid;
			}
			if(type.equals("ut")){
				url += "&ut="+doiid;
			}
			HttpPost request = new HttpPost(url);
			HttpClient client = new DefaultHttpClient();
			result = client.execute(request, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							return new String(EntityUtils.toByteArray(entity), "UTF-8");
						}
					}
					return "";
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (result != null && !result.equals("")) {
			// 解析结果
			try {
				Document doc = DocumentHelper.parseText(result);
				Element mapElement = doc.getRootElement().element("fn").element("map");
				Element fooElement;
				for (Iterator i = mapElement.elementIterator("map"); i.hasNext();) {
					fooElement = (Element) i.next();
					String city = fooElement.attributeValue("name");
					String citylx = "";
					if(type.equals("doi")){
						citylx = "cite_2";
					}
					if(type.equals("ut")){
						citylx = "cite_1";
					}
					if (city.equals(citylx)) {
						Element c2 = fooElement.element("map");
						for (Iterator j = c2.elementIterator("val"); j.hasNext();) {
							Element val = (Element) j.next();
							if (val.attributeValue("name").equals("timesCited")) {
								////System.out.println(val.getText());
								bycs = new Integer(val.getText());
							}
						}
					}
					// //System.out.println(""+fooElement.elementText("NO"));
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return bycs;
	}


}
