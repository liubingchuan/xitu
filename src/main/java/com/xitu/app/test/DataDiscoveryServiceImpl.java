package com.xitu.app.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.xitu.app.test.ESHttpClient;


public class DataDiscoveryServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static int num = 1; 
	static List<String> listhzlw = new ArrayList();


	public JSONObject searchPaper(HttpServletRequest request,String param, String author, String date,String jou,String ins,String sub,String lwlx,String uesremail,String userip, int pageNo, String source, String orderBy,
			String method) {
		// TODO Auto-generated method stub
		JSONObject conES = new JSONObject();
		List<String> plist = new ArrayList();
		if (param != null && !param.equals("") && !param.equals("null")) {
			plist.add(param);
			conES = ESHttpClient.conES(ESHttpClient.createQqueryJsonPage(param, plist, author, date, jou, ins, sub,
					lwlx, pageNo, source, orderBy, method));
			if (conES != null) {
				JSONObject hits = (JSONObject) conES.get("hits");
				JSONObject aggregations = (JSONObject) conES.get("aggregations");
				if (aggregations != null) {
					JSONObject article_year = (JSONObject) aggregations.get("article_year");
					JSONArray bucketnian = (JSONArray) article_year.get("buckets");
					List<Object> janum = new ArrayList();

					Set<String> s = new TreeSet();
					Map<String, Object> job = new LinkedHashMap<String, Object>();
					if (bucketnian != null && bucketnian != null) {
						if (bucketnian.size() > 0) {
							for (int i = 0; i < bucketnian.size(); i++) {
								JSONObject jo = (JSONObject) (bucketnian.get(i));
								String key = jo.get("key") + "";
								if (!key.equals("") && !key.equals("null") && key != null) {
									// job.put("name", key);
									s.add(key);
									job.put(key, jo.get("doc_count"));

								}
							}
						}
					}
					Iterator<String> it = s.iterator();
					while (it.hasNext()) {
						String key = it.next();
						if (job.containsKey(key)) {
							janum.add(job.get(key));
						}
					}

					conES.put("date", janum);
					conES.put("sj", s);
				}
				int count = new Integer(hits.get("total") + "");
				int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
				conES.put("pageCount", pageCount);
				conES.put("pageNo", pageNo);
		
				String abs = "";
				JSONArray arrdd = (JSONArray) hits.get("hits");
				if (arrdd.size() > 0) {
					for (int i = 0; i < arrdd.size(); i++) {
						JSONObject jo = (JSONObject) (arrdd.get(i));
						JSONObject data = (JSONObject) jo.get("_source");
						abs += getPaperEsData(data, "article_abstract");
						String consource = getPaperEsData(data, "source");
						if (consource.equals("WOSNSTL_JournalArticle") || consource.equals("Elsevier_JournalArticle")
								|| consource.equals("CSCD_JournalArticle") || consource.equals("VIP_JournalArticle")
								|| consource.equals("IR_Article")) {
							String jouname = getPaperEsData(data, "source_source-title");
							String jouissn = getPaperEsData(data, "source_issn");
							if (!jouissn.equals("")) {
								if (jouissn.indexOf("-") >= 0) {
									jouissn = jouissn;
								} else {
									jouissn = jouissn.substring(0, 4) + "-" + jouissn.substring(4, 8);
								}
							}

							if (jouname.equals("")) {
								data.put("light", "yellow");
								continue;
							}
							
						}
					}
					
					conES.put("abs", abs);
				}

			}
		}
		
		
		return conES;
	}
	
	public JSONObject jiance(String q,String year,String institution,String lanmu, Integer pageIndex) {
		// TODO Auto-generated method stub
		JSONObject conES = new JSONObject();
		List<String> plist = new ArrayList();
		
			
		conES = ESHttpClient.conES(ESHttpClient.jcreateQqueryJsonPage(q, plist, year, institution, lanmu, pageIndex));
//			if (conES != null) {
//				JSONObject hits = (JSONObject) conES.get("hits");
//				JSONObject aggregations = (JSONObject) conES.get("aggregations");
//				if (aggregations != null) {
//					JSONObject article_year = (JSONObject) aggregations.get("article_year");
//					JSONArray bucketnian = (JSONArray) article_year.get("buckets");
//					List<Object> janum = new ArrayList();
//
//					Set<String> s = new TreeSet();
//					Map<String, Object> job = new LinkedHashMap<String, Object>();
//					if (bucketnian != null && bucketnian != null) {
//						if (bucketnian.size() > 0) {
//							for (int i = 0; i < bucketnian.size(); i++) {
//								JSONObject jo = (JSONObject) (bucketnian.get(i));
//								String key = jo.get("key") + "";
//								if (!key.equals("") && !key.equals("null") && key != null) {
//									// job.put("name", key);
//									s.add(key);
//									job.put(key, jo.get("doc_count"));
//
//								}
//							}
//						}
//					}
//					Iterator<String> it = s.iterator();
//					while (it.hasNext()) {
//						String key = it.next();
//						if (job.containsKey(key)) {
//							janum.add(job.get(key));
//						}
//					}
//
//					conES.put("date", janum);
//					conES.put("sj", s);
//				}
//				int count = new Integer(hits.get("total") + "");
//				int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
//				conES.put("pageCount", pageCount);
//				//conES.put("pageNo", pageNo);
//
//			}
		
		
		
		return conES;
	}
	
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

	
	public JSONObject searchPaperZtDis(HttpServletRequest request, String stkgjc,String kwdIK,String uuid) {
		
		
		JSONObject redata = new JSONObject();
		//专家：
		JSONObject conESzj = ESHttpClient.conESExpert(ESHttpClient.createQqueryJsonPaperJiJinInsExper(uuid,stkgjc,kwdIK,"contrib_major_ik","contrib_research-subject_ik","","",1));
		//机构：
		JSONObject conESins = ESHttpClient.conESIns(ESHttpClient.createQqueryJsonPaperJiJinInsExper(uuid,stkgjc,kwdIK,"contrib_institution_research-domain_ik","contrib_institution_research-domain-en_ik","","",1));
		//项目：
		JSONObject conESjj = ESHttpClient.conESJiJin(ESHttpClient.createQqueryJsonPaperJiJinInsExper(uuid,stkgjc,kwdIK,"award_subjectClass_ik","award_keyword-cn_ik","","award_apply_abstraction_ik",1));
		
		
		//专利：
		JSONObject conESzl = null;
		//论文：
		JSONObject conESlw = null;
		try {
			conESzl = ESHttpClient.conESPatent(ESHttpClient.createQqueryJsonPaperJiJinInsExper(uuid,stkgjc,kwdIK,"subj-class-kwd_kwdIK","article_article-title_ik","article_abstract_ik","",2));
			conESlw = ESHttpClient.conES(ESHttpClient.createQqueryJsonPaperJiJinInsExper(uuid,stkgjc,kwdIK,"json.subjClassKwd.subj-class-kwd_kwd","article_article-title_ik","article_abstract_ik","",2));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		redata.put("conESzj",conESzj);
		redata.put("conESins",conESins);
		redata.put("conESzl",conESzl);
		redata.put("conESjj",conESjj);
		redata.put("conESlw",conESlw);
		
		return redata;
		
	}

	
	public JSONObject searchPaperOne(HttpServletRequest request, String value, String uuid, int pageNo,int pageNoxs) {
		JSONObject reData = new JSONObject();
		JSONObject conESxs = ESHttpClient.conES(ESHttpClient.createQqueryJsonPagePaper(value, uuid, pageNoxs));
		
		JSONObject hitsxs = (JSONObject) conESxs.get("hits");
		int countxs = new Integer(hitsxs.get("total") + "");
		int pageCountxs = (countxs % 10 == 0 ? countxs / 10 : countxs / 10 + 1);
		reData.put("pageCountxs", pageCountxs);
		reData.put("pageNoxs", pageNoxs);
		reData.put("countxs", countxs);
		reData.put("dataxs", conESxs);
		return reData;
	}

	public JSONObject searchReportOne(HttpServletRequest request, String uuid) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient.conESReport(ESHttpClient.createQqueryByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}
	public JSONObject searchConfOne(HttpServletRequest request, String uuid) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient.conESConference(ESHttpClient.createQqueryByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}
	public JSONObject searchPatentOne(HttpServletRequest request, String uuid) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient.conESPatent(ESHttpClient.createQqueryByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}
	public JSONObject searchAllTotal(){
		
		JSONObject redata = new JSONObject();
		JSONObject conESjj = null;
		JSONObject conESzl = null;
		JSONObject conESlw = null;
		JSONObject conESqk = null;
		JSONObject conESjg = null;
		JSONObject conESzj = null;
		JSONObject conESzx = null;
		JSONObject conESbz = null;
		JSONObject conESbg = null;
		JSONObject conESgx = null;
		JSONObject conEShy = null;
		try {
			//会议：
			conEShy = ESHttpClient.conESConference(ESHttpClient.createQqueryAllJson());
			conESjj = ESHttpClient.conESJiJin(ESHttpClient.createQqueryAllJson());
			conESzl = ESHttpClient.conESPatent(ESHttpClient.createQqueryAllJson());
			conESlw = ESHttpClient.conES(ESHttpClient.createQqueryAllJson());
			conESqk = ESHttpClient.conESJou(ESHttpClient.createQqueryAllJson());
			conESjg = ESHttpClient.conESIns(ESHttpClient.createQqueryAllJson());
			conESzj = ESHttpClient.conESExpert(ESHttpClient.createQqueryAllJson());
			conESzx = ESHttpClient.conESZiXun(ESHttpClient.createQqueryAllJson());
			conESbz = ESHttpClient.conESStandard(ESHttpClient.createQqueryAllJson());
			conESbg = ESHttpClient.conESReport(ESHttpClient.createQqueryAllJson());
			//conESgx = ESHttpClient.conESRelations(ESHttpClient.createQqueryAllJson());
			
			
			JSONObject hitshy = (JSONObject) conEShy.get("hits");
			int counthy = 0;
			if(hitshy!=null){
				counthy = new Integer(hitshy.get("total") + "");
			}
			redata.put("counthy", counthy);
			JSONObject hitszx = (JSONObject) conESzx.get("hits");
			int countzx = 0;
			if(hitszx!=null){
				countzx = new Integer(hitszx.get("total") + "");
			}
			redata.put("countzx", countzx);
			JSONObject hitsbz = (JSONObject) conESbz.get("hits");
			int countbz = 0;
			if(hitsbz!=null){
				countbz = new Integer(hitsbz.get("total") + "");
			}
			redata.put("countbz", countbz);
			JSONObject hitsbg = (JSONObject) conESbg.get("hits");
			int countbg = 0;
			if(hitsbg!=null){
				countbg = new Integer(hitsbg.get("total") + "");
			}
			redata.put("countbg", countbg);
			
			JSONObject hitsjj = (JSONObject) conESjj.get("hits");
			int countjj = 0;
			if(hitsjj!=null){
				countjj = new Integer(hitsjj.get("total") + "");
			}
			redata.put("countjj", countjj);
			JSONObject hitszl = (JSONObject) conESzl.get("hits");
			int countzl = 0;
			if(hitszl!=null){
				countzl = new Integer(hitszl.get("total") + "");
			}
			redata.put("countzl", countzl);
			JSONObject hitslw = (JSONObject) conESlw.get("hits");
			int countlw = 0;
			if(hitslw!=null){
				countlw = new Integer(hitslw.get("total") + "");
			}
			redata.put("countlw", countlw);
			JSONObject hitsqk = (JSONObject) conESqk.get("hits");
			int countqk = 0;
			if(hitsqk!=null){
				countqk = new Integer(hitsqk.get("total") + "");
			}
			redata.put("countqk", countqk);
			JSONObject hitszj = (JSONObject) conESzj.get("hits");
			int countzj = 0;
			if(hitszj!=null){
				countzj = new Integer(hitszj.get("total") + "");
			}
			redata.put("countzj", countzj);
			JSONObject hitsjg = (JSONObject) conESjg.get("hits");
			int countjg = 0;
			if(hitsjg!=null){
				countjg = new Integer(hitsjg.get("total") + "");
			}
			redata.put("countjg", countjg);
			//JSONObject hitsgx = (JSONObject) conESgx.get("hits");
			int countgx = 0;
			if(conESgx!=null){
				countgx = new Integer(0);
			}
			redata.put("countgx", countgx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return redata;
	}
	public JSONObject searchJiJin(HttpServletRequest request, String param, String year,String type, String ins,String author, String xmzz,String lang,String capitalsection,String fundgj,String state,int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient.conESJiJin(ESHttpClient.createQqueryJiJinJsonGL(param, year, type, ins, author,
						xmzz, lang, capitalsection, fundgj, state, pageNo, orderBy, method));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// if (param != null && !param.equals("") && !param.equals("null"))
			// {
			// conES = ESHttpClient
			// .conESJiJin(ESHttpClient.createQqueryJiJinJsonGL(param, year,
			// type,ins,author,xmzz, pageNo, orderBy, method));
			// } else {
			// conES = ESHttpClient
			// .conESJiJin(ESHttpClient.createQqueryFundJson("a", year, type,
			// ins,author,xmzz,pageNo, orderBy, method));
			// }
			// conES = findJJAuthorAndIns(conES);
			if (conES != null) {
				JSONObject hitss = (JSONObject) conES.get("hits");
				JSONObject aggregations = (JSONObject) conES.get("aggregations");
				JSONObject article_year = (JSONObject) aggregations.get("award_year");
				JSONArray bucketnian = (JSONArray) article_year.get("buckets");
				List<Object> janum = new ArrayList();

				Set<String> s = new TreeSet();
				Map<String, Object> job = new LinkedHashMap<String, Object>();
				if (bucketnian != null && bucketnian != null) {
					if (bucketnian.size() > 0) {
						for (int i = 0; i < bucketnian.size(); i++) {
							JSONObject jo = (JSONObject) (bucketnian.get(i));
							String key = jo.get("key") + "";
							if (!key.equals("") && !key.equals("null") && key != null && !key.equals("0000")) {
								// job.put("name", key);
								s.add(key);
								job.put(key, jo.get("doc_count"));

							}
						}
					}
				}
				Iterator<String> it = s.iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (job.containsKey(key)) {
						janum.add(job.get(key));
					}
				}
				conES.put("date", janum);
				conES.put("sj", s);
				int count = new Integer(hitss.get("total") + "");
				int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
				conES.put("pageCount", pageCount);
				conES.put("pageNo", pageNo);

				JSONArray arrdd = (JSONArray) hitss.get("hits");
				String abs = "";
				if (arrdd.size() > 0) {
					for (int i = 0; i < arrdd.size(); i++) {
						JSONObject jo = (JSONObject) (arrdd.get(i));
						JSONObject data = (JSONObject) jo.get("_source");
						List<Object> article_abstract = (List<Object>) data.get("award_apply_abstraction");
						if (article_abstract != null && article_abstract.size() > 0) {
							List<Object> abstractList = (List<Object>) article_abstract.get(0);

							if (abstractList != null) {
								abs += abstractList.get(0).toString();
							}
						}
					}
				
					conES.put("abs", abs);
				}
			}
		}
		return conES;
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
	public JSONObject searchPatent(HttpServletRequest request, String param, String date,String ipcfl,String cpcfl,String gj,String type,String esi, int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient.conESPatent(ESHttpClient.createQqueryPatentJsonGL(param, date, ipcfl, cpcfl, gj,
						type, esi, pageNo, orderBy, method));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (conES != null) {
				JSONObject hits = (JSONObject) conES.get("hits");
				JSONObject aggregations = (JSONObject) conES.get("aggregations");
				JSONObject article_year = (JSONObject) aggregations.get("article_issue-year_bg");
				JSONArray bucketnian = (JSONArray) article_year.get("buckets");
				List<Object> janum = new ArrayList();

				Set<String> s = new TreeSet();
				Map<String, Object> job = new LinkedHashMap<String, Object>();
				if (bucketnian != null && bucketnian != null) {
					if (bucketnian.size() > 0) {
						for (int i = 0; i < bucketnian.size(); i++) {
							JSONObject jo = (JSONObject) (bucketnian.get(i));
							String key = jo.get("key") + "";
							if (!key.equals("") && !key.equals("null") && key != null && !key.equals("0000")) {
								// job.put("name", key);
								s.add(key);
								job.put(key, jo.get("doc_count"));

							}
						}
					}
				}
				Iterator<String> it = s.iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (job.containsKey(key)) {
						janum.add(job.get(key));
					}
				}
				conES.put("date", janum);
				conES.put("sj", s);
				int count = new Integer(hits.get("total") + "");
				int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
				conES.put("pageCount", pageCount);
				conES.put("pageNo", pageNo);

				JSONArray arrdd = (JSONArray) hits.get("hits");
				String abs = "";
				if (arrdd.size() > 0) {
					for (int i = 0; i < arrdd.size(); i++) {
						JSONObject jo = (JSONObject) (arrdd.get(i));
						JSONObject data = (JSONObject) jo.get("_source");
						List<Object> article_abstract = (List<Object>) data.get("article_abstract");
						if (article_abstract != null && article_abstract.size() > 0) {
							List<Object> abstractList = (List<Object>) article_abstract.get(0);
							List<Object> abstractLists = null;
							if (abstractList != null) {
								abstractLists = (List<Object>) abstractList.get(0);
							}
							if (abstractLists != null) {
								abs += abstractLists.get(0).toString().replaceAll("\"", "");
							}
						}
					}
					
					conES.put("abs", abs);
				}

			}
		}
		return conES;
	}

	public JSONObject searchExpert(HttpServletRequest request, String param,String uuid, String subject, String ins,String role,String researchSubject, int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				if (uuid.equals("1")) {
					conES = ESHttpClient.conESExpert(ESHttpClient.createQqueryExpertJsonGL(param, subject, ins, role,
							researchSubject, pageNo, orderBy, method));
				} else {
					List<String> listhz = new ArrayList();
					JSONObject jahz = new JSONObject();
					// JSONArray jahz = new JSONArray();
					for (String kk : listhzlw) {
						JSONObject conEShz = ESHttpClient
								.conESRelations(ESHttpClient.createQqueryByRelations_s(kk, 1, "contributor"));
						JSONObject hitshz = (JSONObject) conEShz.get("hits");
						JSONArray arrdd = (JSONArray) hitshz.get("hits");
						if (arrdd.size() > 0) {
							for (int i = 0; i < arrdd.size(); i++) {
								JSONObject jo = (JSONObject) (arrdd.get(i));
								JSONObject data = (JSONObject) jo.get("_source");
								listhz.add(data.get("relations_o") + "");
							}
						}
					}
					conES = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listhz, pageNo, uuid));
				}
				if (conES != null) {
					JSONObject hits = (JSONObject) conES.get("hits");
					int count = new Integer(hits.get("total") + "");
					int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
					conES.put("pageCount", pageCount);
					conES.put("pageNo", pageNo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conES;
	}
	public JSONObject searchZiXun(HttpServletRequest request, String param,String siteClassify,String siteName,String siteCountry ,String contentClassify,String  year,  int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient.conESZiXun(ESHttpClient.createQqueryZiXunJsonGL(param, siteClassify, siteName,
						siteCountry, contentClassify, year, pageNo, orderBy, method));

				if (conES != null) {
					JSONObject hits = (JSONObject) conES.get("hits");
					int count = new Integer(hits.get("total") + "");
					int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
					conES.put("pageCount", pageCount);
					conES.put("pageNo", pageNo);

					JSONArray arrdd = (JSONArray) hits.get("hits");
					String abs = "";
					if (arrdd.size() > 0) {
						for (int i = 0; i < arrdd.size(); i++) {
							JSONObject jo = (JSONObject) (arrdd.get(i));
							JSONObject data = (JSONObject) jo.get("_source");
							abs = data.get("enAbstract").toString();

						}
						
						conES.put("abs", abs);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conES;
	}
	public JSONObject searchExpertGx(HttpServletRequest request, String param,String uuid, String subject, String ins,String role,String researchSubject, int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient.conESExpert(ESHttpClient.createQqueryExpertJsonGL(param, subject, ins, role,
						researchSubject, 1, 200, orderBy, method));

				if (conES != null) {
					JSONObject hits = (JSONObject) conES.get("hits");
					int count = new Integer(hits.get("total") + "");
					int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
					conES.put("pageCount", pageCount);
					conES.put("pageNo", pageNo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conES;
	}
	public JSONObject findExpertCGS(String uuid){
		JSONObject redata = new JSONObject();
		int countcgs = 0;
		String[] arr = uuid.split(",");
		JSONArray arrcgs = new JSONArray();
		for(String ss : arr){
			JSONObject esCGS = ESHttpClient
					.conESRelations(ESHttpClient.createQueryByRelations_oCGS(ss));
			JSONObject hits = (JSONObject) esCGS.get("hits");
			countcgs = new Integer(hits.get("total") + "");
			arrcgs.add(countcgs);
		}
		redata.put("countcgs", arrcgs);
		return redata;
	}
	public JSONObject findExpertBypc(String uuid){
		JSONObject redata = new JSONObject();
		int countcgs = 0;
		String[] arr = uuid.split(",");
		JSONArray arrcgs = new JSONArray();
		for(String ss : arr){
			JSONObject conES = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_o(ss,1,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",0));
			JSONObject hits = (JSONObject) conES.get("hits");
			int countlw = new Integer(hits.get("total") + "");
			JSONObject conESlw = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_o(ss,1,countlw,"contributor","relations_s_index","t_ods_las_nstl_value","Patent"));
			JSONObject hitslw = (JSONObject) conESlw.get("hits");
			JSONArray arrdd = (JSONArray) hitslw.get("hits");
			List<String> listlwlw = new ArrayList();
			if (arrdd.size() > 0) {
				for (int i = 0; i < arrdd.size(); i++) {
					JSONObject jo = (JSONObject) (arrdd.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					listlwlw.add(data.getString("relations_s")+"");
				}
			}
			int htmlBypc = 0;
			if(listlwlw.size()>0){
				JSONObject conESlwss = ESHttpClient
						.conES(ESHttpClient.createQqueryByUuid(listlwlw));
				JSONObject hitslws = (JSONObject) conESlwss.get("hits");
				JSONArray arrll = (JSONArray) hitslws.get("hits");
				if (arrll.size() > 0) {
					for (int i = 0; i < arrll.size(); i++) {
						JSONObject jo = (JSONObject) (arrll.get(i));
						JSONObject data = (JSONObject) jo.get("_source");
						JSONObject json = (JSONObject)data.get("json");
						JSONObject artilce = (JSONObject)json.get("article");
						List<Object> article_article_id = (List<Object>)artilce.get("article_article-id");
						String ut = "";
						if(article_article_id!=null && article_article_id.size()>0){
							List<Object> articleList = (List<Object>)article_article_id.get(0);
							List<Object> articleLists  = null;
							if(articleList!=null){
								articleLists = (List<Object>)articleList.get(0);
							}
							if(articleLists!=null){
								ut = articleLists.get(0).toString();
							}
						}
						htmlBypc += getHtmlBypc(ut,"ut");
					}
				}
			}
			arrcgs.add(htmlBypc);
		}
		redata.put("countbypc", arrcgs);
		return redata;
	}
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

	
	public JSONObject searchJou(HttpServletRequest request, String param, String yuZhong, String dataBase, String subClass,String initial,String access_license,String frequency,String collectionType,int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient
						.conESJou(ESHttpClient.createQqueryJouJsonGL(param, yuZhong, dataBase, subClass,initial,access_license,frequency,collectionType,pageNo, orderBy, method));
				if(conES!=null){
					JSONObject hits = (JSONObject) conES.get("hits");
					int count = new Integer(hits.get("total") + "");
					int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
					conES.put("pageCount", pageCount);
					conES.put("pageNo", pageNo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conES;
	}
	
	public JSONObject searchRelationaSubject(HttpServletRequest request, String uuid) {
		JSONObject redata = new JSONObject();
		//有关的主题：
		JSONObject conESzt = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_s(uuid,1,"subject_is"));
		JSONObject hitszt = (JSONObject) conESzt.get("hits");
		JSONArray arrzt = (JSONArray) hitszt.get("hits");
		List<String> listzt = new ArrayList();
		JSONObject jobzt = new JSONObject();
		if (arrzt.size() > 0) {
			for (int i = 0; i < arrzt.size(); i++) {
				JSONObject jo = (JSONObject) (arrzt.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_o_index")+"";
				listzt.add(data.get("relations_o") + "");
			}
		}
		if(listzt.size()>0){
			jobzt = ESHttpClient.conESSubject(ESHttpClient.createQqueryByUuid(listzt));
			redata.put("jszt", jobzt);
		}
		return redata;
	}
	public JSONObject searchJouOne(HttpServletRequest request, String uuid) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient.conESJou(ESHttpClient.createQqueryByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}
	
	
	
	public List<String> getJouUuidAllByRelaJou(String uuid){
		List<String> listfwl = new ArrayList();
		JSONObject conES = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, 1, "source_is"));
		JSONObject hits = (JSONObject) conES.get("hits");
		int count = new Integer(hits.get("total") + "");
		JSONObject conESfwl = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, 1,500, "source_is"));
		JSONObject hitsfwl = (JSONObject) conESfwl.get("hits");
		if(hitsfwl!=null){
			JSONArray arrfwl = (JSONArray) hitsfwl.get("hits");
			if (arrfwl.size() > 0) {
				for (int i = 0; i < arrfwl.size(); i++) {
					JSONObject job = new JSONObject();
					JSONObject jo = (JSONObject) (arrfwl.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					String type = data.get("relations_s") + "";
					listfwl.add(data.get("relations_s") + "");
				}
			}
		}
		
		
		return listfwl;
	}
	
	public JSONObject searchJouFaPaperExAndIns(HttpServletRequest request,String uuid){
		JSONObject reData = new JSONObject();
		//作者：
		JSONObject conESex = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_s(uuid, 1, 20,"contributor","journal_count"));
		JSONObject hits = (JSONObject) conESex.get("hits");
		List<String> listzj = new ArrayList();
		JSONObject jazj = new JSONObject();
		Map<String,String> mapzj = new HashMap();
		JSONArray arr = (JSONArray) hits.get("hits");
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String relations_o = data.get("relations_o") + "";
				String journal_count = data.get("journal_count") + "";
				mapzj.put(relations_o, journal_count);
				listzj.add(data.get("relations_o") + "");
			}
		}
		if(listzj.size()>0){
			jazj = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listzj));
			reData.put("expertlist", jazj);
			reData.put("mapzj", mapzj);
		}
		//作者：
		JSONObject conESins = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, 1, 20,"publish","journal_count"));
		JSONObject hitsins = (JSONObject) conESins.get("hits");
		List<String> listjg = new ArrayList();
		JSONObject jajg = new JSONObject();
		JSONArray arrins = (JSONArray) hitsins.get("hits");
		Map<String,String> mapjg = new HashMap();
		if (arrins.size() > 0) {
			for (int i = 0; i < arrins.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrins.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String relations_s = data.get("relations_s") + "";
				String journal_count = data.get("journal_count") + "";
				mapjg.put(relations_s, journal_count);
				listjg.add(data.get("relations_s") + "");
			}
		}
		if(listjg.size()>0){
			jajg = ESHttpClient.conESIns(ESHttpClient.createQqueryByUuid(listjg));
			reData.put("inslist", jajg);
			reData.put("mapjg", mapjg);
		}
		
		
		return reData;
		
	}
	
	public JSONObject searchJouFaPaperNum(HttpServletRequest request,String uuid){
		JSONObject reData = new JSONObject();
		List<String> listfwl = getJouUuidAllByRelaJou(uuid);
		JSONObject jafwl = new JSONObject();
		
		if(listfwl.size()>0){
			jafwl = ESHttpClient.conES(ESHttpClient.createQqueryByUuid(listfwl,"json.contribGroup.contrib_full-name_bg","contrib_institution",20));
			reData.put("fwl", jafwl);
		}
		
		return reData;
	}

	public JSONObject searchJouPaperByRela(HttpServletRequest request,String uuid,int pageNo,String year){
		JSONObject reData = new JSONObject();
//		List<String> listfwl = getJouUuidAllByRelaJou(uuid);
//		JSONObject jafwl = new JSONObject();
//		int count = 0;
//		int pageCount = 0;
//		if(listfwl.size()>0){
//			jafwl = ESHttpClient.conES(ESHttpClient.createQueryUuidByPaper(listfwl,pageNo,year));
//			reData.put("fwl", jafwl);
//			JSONObject hitss = (JSONObject) jafwl.get("hits");
//			count = new Integer(hitss.get("total") + "");
//			pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
//		}
		
		JSONObject conES = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, pageNo, "source_is"));
		JSONObject hits = (JSONObject) conES.get("hits");
		JSONObject ja = new JSONObject();
		List<String> listlw = new ArrayList();
		JSONArray arr = (JSONArray) hits.get("hits");
		int count = 0;
		int pageCount = 0;
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listlw.add(data.get("relations_s") + "");
			}
			JSONObject hitss = (JSONObject) conES.get("hits");
			count = new Integer(hitss.get("total") + "");
			pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
		}
		
		if(listlw.size()>0){
			ja = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listlw,"paper"));
			reData.put("fwl", ja);
		}
		reData.put("pageCount", pageCount);
		reData.put("pageNo", pageNo);
		reData.put("count", count);
		
		
		return reData;
	}
	
	public JSONObject searchJouOneDiscovery(HttpServletRequest request, String uuid, int pageNo,int pageNojg,int pageNozj) {
		// JSONObject conES =
		// ESHttpClient.conESJou(ESHttpClient.createQqueryByUuid(uuid));

		//String jouId = "zzzzzzzzzzztestdata7";
		
		JSONObject reData = new JSONObject();
		JSONObject conES = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, pageNo, "source_is"));

		JSONObject hits = (JSONObject) conES.get("hits");
		JSONObject ja = new JSONObject();
		List<String> listlw = new ArrayList();
		JSONArray arr = (JSONArray) hits.get("hits");
		int count = 0;
		int pageCount = 0;
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
//				System.out.println(data.get("relations_s"));
				String type = data.get("relations_s") + "";
				listlw.add(data.get("relations_s") + "");
				
//				JSONObject hitslw = (JSONObject) job.get("hits");
//				JSONArray arrlw = (JSONArray) hitslw.get("hits");
//				if(arrlw.size()>0){
//					ja.add(job);
//				}
			}
			JSONObject hitss = (JSONObject) conES.get("hits");
			count = new Integer(hitss.get("total") + "");
			pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
		}
		
		if(listlw.size()>0){
			ja = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listlw,"paper"));
			reData.put("data", ja);
		}
		reData.put("pageCount", pageCount);
		reData.put("pageNo", pageNo);
		reData.put("count", count);
		
		
		/*//发文量：
		JSONObject conESfwl = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid, 1,count, "source_is"));
		JSONObject hitsfwl = (JSONObject) conESfwl.get("hits");
		JSONArray arrfwl = (JSONArray) hitsfwl.get("hits");
		JSONObject jafwl = new JSONObject();
		List<String> listfwl = new ArrayList();
		if (arrfwl.size() > 0) {
			for (int i = 0; i < arrfwl.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrfwl.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listfwl.add(data.get("relations_s") + "");
			}
		}
		if(listfwl.size()>0){
			jafwl = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listfwl,"contrib_full-name","contrib_institution"));
			reData.put("fwl", jafwl);
		}*/
		
		//编委
		Map<String,JSONObject> mapbw = new HashMap();
		JSONObject conESbw= ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_s(uuid, "contributor","source","enrichjournalpeople"));
		JSONObject hitsbw = (JSONObject) conESbw.get("hits");
		JSONArray jabw = new JSONArray();
		JSONArray arrbw = (JSONArray) hitsbw.get("hits");
		if (arrbw.size() > 0) {
			for (int i = 0; i < arrbw.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrbw.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				//System.out.println(data.get("relations_s"));
				String type = data.get("relations_o_index") + "";
				String aw = data.get("relations_rel_value") + "";
				String sou = data.get("source") + "";
				//System.out.println(type);
				job = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(data.get("relations_o") + ""));
				
				job.put("bw", data.get("relations_rel_value")+"");
				jabw.add(job);
			}
		}
		reData.put("jabw", jabw);
		
//		//期刊中论文的作者：
//		JSONObject conESzj = ESHttpClient.conESRelations(
//				ESHttpClient.createQqueryByRelations_s(uuid, pageNozj,10, "contributor","source","enrichjournalpeople"));
//		List<String> listzj = new ArrayList();
//		JSONObject hitszj = (JSONObject) conESzj.get("hits");
//		JSONObject jazj = new JSONObject();
//		JSONArray arrzj = (JSONArray) hitszj.get("hits");
//		int countzj = 0;
//		int pageCountzj = 0;
//		if (arrzj.size() > 0) {
//			for (int i = 0; i < arrzj.size(); i++) {
//				JSONObject job = new JSONObject();
//				JSONObject jo = (JSONObject) (arrzj.get(i));
//				JSONObject data = (JSONObject) jo.get("_source");
//				//System.out.println(data.get("relations_s"));
//				String type = data.get("relations_o_index") + "";
//				String aw = data.get("relations_rel_value") + "";
//				String sou = data.get("source") + "";
//				listzj.add(data.get("relations_o") + "");
//				
//			}
//			JSONObject hitsszj = (JSONObject) conESzj.get("hits");
//			countzj = new Integer(hitsszj.get("total") + "");
//			//countzj = jazj.size();
//			pageCountzj = (countzj % 10 == 0 ? countzj / 10 : countzj / 10 + 1);
//		}
//		
//		if(listzj.size()>0){
//			jazj = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listzj));
//			reData.put("jazj", jazj);
//		}
//		reData.put("pageNozj", pageNozj);
//		reData.put("pageCountzj", pageCountzj);
//		reData.put("countzj", countzj);
		
		
		
		
		return reData;
	}
	public JSONObject searchJouOneExpertDis(HttpServletRequest request, String uuid,int pageNozjfx){
		JSONObject reData = new JSONObject();
		//期刊中论文的作者2：
//		JSONObject conESzj1 = ESHttpClient.conESRelations(
//				ESHttpClient.createQqueryByRelations_s(uuid, pageNozjfx,15, "contributor","source","enrichjournalpeople"));
//		List<String> listzj1 = new ArrayList();
//		JSONObject hitszj1 = (JSONObject) conESzj1.get("hits");
//		JSONObject jazj1 = new JSONObject();
//		JSONArray arrzj1 = (JSONArray) hitszj1.get("hits");
//		int countzjfx = 0;
//		int pageCountzjfx = 0;
//		if (arrzj1.size() > 0) {
//			for (int i = 0; i < arrzj1.size(); i++) {
//				JSONObject job = new JSONObject();
//				JSONObject jo = (JSONObject) (arrzj1.get(i));
//				JSONObject data = (JSONObject) jo.get("_source");
//				//System.out.println(data.get("relations_s"));
//				String type = data.get("relations_o_index") + "";
//				String aw = data.get("relations_rel_value") + "";
//				String sou = data.get("source") + "";
//				listzj1.add(data.get("relations_o") + "");
//				
//			}
//			JSONObject hitsszj = (JSONObject) conESzj1.get("hits");
//			countzjfx = new Integer(hitsszj.get("total") + "");
//			//countzj = jazj.size();
//			pageCountzjfx = (countzjfx % 5 == 0 ? countzjfx / 5 : countzjfx / 5 + 1);
//		}
//		
//		if(listzj1.size()>0){
//			jazj1 = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listzj1));
//			reData.put("jazj1", jazj1);
//		}
//		reData.put("pageNozjfx", pageNozjfx);
//		reData.put("pageCountzjfx", pageCountzjfx);
//		reData.put("countzjfx", countzjfx);
		
		
		
		//编委
		Map<String,JSONObject> mapbw = new HashMap();
		JSONObject conESbw= ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_s(uuid, "contributor","source","enrichjournalpeople"));
		JSONObject hitsbw = (JSONObject) conESbw.get("hits");
		JSONArray jabw = new JSONArray();
		JSONArray arrbw = (JSONArray) hitsbw.get("hits");
		if (arrbw.size() > 0) {
			for (int i = 0; i < arrbw.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrbw.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				//System.out.println(data.get("relations_s"));
				String type = data.get("relations_o_index") + "";
				String aw = data.get("relations_rel_value") + "";
				String sou = data.get("source") + "";
				//System.out.println(type);
				job = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(data.get("relations_o") + ""));
				
				job.put("bw", data.get("relations_rel_value")+"");
				jabw.add(job);
			}
		}
		reData.put("jabw", jabw);
		return reData;
	}
	public JSONObject searchIns(HttpServletRequest request, String param, String research, String city, String gj,int pageNo,
			String orderBy, String method) {
		JSONObject conES = new JSONObject();
		if (param != null && !param.equals("") && !param.equals("null")) {
			try {
				conES = ESHttpClient
						.conESIns(ESHttpClient.createQqueryInsJsonGL(param, research, city,gj, pageNo, orderBy, method));
				if(conES!=null){
					JSONObject hits = (JSONObject) conES.get("hits");
					int count = new Integer(hits.get("total") + "");
					int pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
					conES.put("pageCount", pageCount);
					conES.put("pageNo", pageNo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conES;
	}
	/*public JSONObject searchInsAgg(HttpServletRequest request, String param, String research, String city, String gj,int pageNo,
			String orderBy, String method) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient
					.conESIns(ESHttpClient.createQqueryInsJsonGLAgg(param, research, city,gj, pageNo, orderBy, method));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}*/
	public JSONObject searchInsOne(HttpServletRequest request, String uuid) {
		JSONObject conES = null;
		try {
			conES = ESHttpClient.conESIns(ESHttpClient.createQqueryByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conES;
	}

	public JSONObject searchInsExpertAndProjectNum(HttpServletRequest request, String insName){
		JSONObject reData = new JSONObject();
		String[] insNamearr = insName.split(",");
		JSONArray arrcgs = new JSONArray();
		JSONArray arrex = new JSONArray();
		for(String ins : insNamearr){
			JSONObject conESInsName = ESHttpClient.conESIns(
					ESHttpClient.createQqueryByInsName(ins));
			JSONObject hitsinsName = (JSONObject) conESInsName.get("hits");
			JSONArray arrinsName = (JSONArray) hitsinsName.get("hits");
			List<String> listuuid = new ArrayList();
			if (arrinsName.size() > 0) {
				for (int i = 0; i < arrinsName.size(); i++) {
					JSONObject job = new JSONObject();
					JSONObject jo = (JSONObject) (arrinsName.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					String uuids = (String)data.get("uuid");
					listuuid.add(uuids);
					
				}
			}
			int ExpertNum = 0;
			int ProjectNum = 0;
			if(listuuid.size()>0){
				JSONObject conESzj = ESHttpClient.conESRelations(
						ESHttpClient.createQqueryByRelations_o(listuuid, 1, "affiliation"));
				JSONObject hitszj = (JSONObject) conESzj.get("hits");
				ExpertNum = new Integer(hitszj.get("total") + "");
				JSONObject conEScgs = ESHttpClient.conESRelations(
						ESHttpClient.createQqueryByRelations_o(listuuid, "contribute_institution"));
				JSONObject hitscgs = (JSONObject) conEScgs.get("hits");
				JSONObject conEScgs1 = ESHttpClient.conESRelations(
						ESHttpClient.createQqueryByRelations_o(listuuid, "fundapplyinst"));
				JSONObject hitscgs1 = (JSONObject) conEScgs1.get("hits");
				ProjectNum = new Integer(hitscgs.get("total") + "")+new Integer(hitscgs1.get("total") + "");
			}
			arrex.add(ExpertNum);
			arrcgs.add(ProjectNum);
		}
		
		
		reData.put("ExpertNum", arrex);
		reData.put("ProjectNum", arrcgs);
		return reData;
	}

	public JSONObject searchInsFaPaperNum(HttpServletRequest request, String uuid,String insName){
		JSONObject reData = new JSONObject();
		JSONObject conESInsName = ESHttpClient.conESIns(
				ESHttpClient.createQqueryByInsName(insName));
		JSONObject hitsinsName = (JSONObject) conESInsName.get("hits"); 
		JSONArray arrinsName = (JSONArray) hitsinsName.get("hits");
		List<String> listuuid = new ArrayList();
		if (arrinsName.size() > 0) {
			for (int i = 0; i < arrinsName.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrinsName.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String uuids = (String)data.get("uuid");
				listuuid.add(uuids);
			}
		}
//		listuuid.add(uuid);
//		JSONObject conESlw = ESHttpClient.conESRelations(
//				ESHttpClient.createQqueryByRelations_o(listuuid, 1, "contribute_institution","Patent",1));
//		JSONObject hitslw = (JSONObject) conESlw.get("hits");
//		int count = new Integer(hitslw.get("total") + "");
		JSONObject conESfwl = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_o(listuuid, 1, 200,"contribute_institution","Patent",1));
		JSONObject hitsfwl = (JSONObject) conESfwl.get("hits");
		JSONArray arrfwl = (JSONArray) hitsfwl.get("hits");
		JSONObject jafwl = new JSONObject();
		List<String> listfwl = new ArrayList();
		if (arrfwl.size() > 0) {
			for (int i = 0; i < arrfwl.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrfwl.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listfwl.add(data.get("relations_s") + "");
			}
		}
		if(listfwl.size()>0){
//			//jafwl = ESHttpClient.conES(ESHttpClient.createQqueryByInsNamefromPaper(insName,"article_year",12));
//			jafwl = ESHttpClient.conES(ESHttpClient.createQqueryByInsNamefromPaper(listfwl,"article_year",12));
//			if(jafwl!=null){
//				JSONObject hits = (JSONObject) jafwl.get("hits");
//				JSONObject aggregations = (JSONObject) jafwl.get("aggregations");
//				if(aggregations!=null){
//					JSONObject article_year= (JSONObject) aggregations.get("article_year");
//					JSONArray bucketnian = (JSONArray)article_year.get("buckets");
//					List<Integer> janum= new ArrayList();
//			
//					Set<String> s = new TreeSet();
//					Map<String, Object> job = new LinkedHashMap<String, Object>();
//					if(bucketnian!=null && bucketnian!=null){
//						if(bucketnian.size()>0){
//							for(int i = 0;i<bucketnian.size();i++){
//								JSONObject jo = (JSONObject) (bucketnian.get(i));
//								String key = jo.get("key")+"";
//								if(!key.equals("") && !key.equals("null") && key!=null){
//									//job.put("name", key);
//									s.add(key);
//									job.put(key, jo.get("doc_count"));
//									
//								}
//							}
//						}
//					}
//					Iterator<String> it = s.iterator();
//					while(it.hasNext()){
//						String key = it.next();
//						if(job.containsKey(key)){
//							janum.add(new Integer(job.get(key)+""));
//						}
//					}
//					List<Object> zzl= new ArrayList();
//					//计算增长率
//					for(int z = 0;z<s.size();z++){
//						if(z==0){
//							zzl.add(0);
//						}else{
//							int hou = janum.get(z);
//							int qian = janum.get((z-1));
//							double zz = (hou-qian);
//							//new DecimalFormat("#.0").format(zz)
//							zzl.add(zz);
//						}
//						
//					}
//					reData.put("zzl", zzl);
//					reData.put("date", janum);
//					reData.put("sj", s);	
//				}
//			
//		}
//		reData.put("fwl", jafwl);
		
		//合作机构
		List<String> listhz = new ArrayList();
		JSONObject jahz = new JSONObject();
		Map<String,Integer> maphz = new HashMap();
		for(String kk : listfwl){
			JSONObject conEShzss = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,"contribute_institution"));
			JSONObject hitshzss = (JSONObject) conEShzss.get("hits");
			String totals = hitshzss.get("total")+ "";
			JSONObject conEShz = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,new Integer(totals),"contribute_institution"));
			JSONObject hitshz = (JSONObject) conEShzss.get("hits");
			JSONArray arrdd = (JSONArray) hitshz.get("hits");
			
			if (arrdd.size() > 0) {
				for (int i = 0; i < arrdd.size(); i++) {
					JSONObject jo = (JSONObject) (arrdd.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					String esuuid =  data.get("relations_o") + "";
					listhz.add(esuuid);
					if(maphz.containsKey(esuuid)){
						maphz.put(esuuid, maphz.get(esuuid)+1);
					}else{
						maphz.put(esuuid, 1);
					}
				}
			}
		}
		if(listhz.size()>0){
			jahz = ESHttpClient.conESIns(ESHttpClient.createQqueryByUuid(listhz,listhz.size()));
			reData.put("jahz", jahz);
		}
		reData.put("maphz", maphz);
		
		
		
		}
		return reData;
	}
	
	
	
	public JSONObject searchInsOneDiscovery(HttpServletRequest request, String uuid,String insName, int pageNo,int pageNojj,int pageNozj,int pageNozl) {
		JSONObject reData = new JSONObject();

		JSONObject conESInsName = ESHttpClient.conESIns(
				ESHttpClient.createQqueryByInsName(insName));
		JSONObject hitsinsName = (JSONObject) conESInsName.get("hits"); 
		JSONArray arrinsName = (JSONArray) hitsinsName.get("hits");
		List<String> listuuid = new ArrayList();
		if (arrinsName.size() > 0) {
			for (int i = 0; i < arrinsName.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrinsName.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String uuids = (String)data.get("uuid");
				listuuid.add(uuids);
				
			}
		}
		//专家
		JSONObject conESzj = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_o(listuuid, pageNozj, "affiliation"));
		JSONObject hitszj = (JSONObject) conESzj.get("hits");
		JSONObject jazj = new JSONObject();
		List<String> listzj = new ArrayList();
		JSONArray arrzj = (JSONArray) hitszj.get("hits");
		int countzj = 0;
		int pageCountzj = 0;
		if (arrzj.size() > 0) {
			for (int i = 0; i < arrzj.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrzj.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listzj.add(data.get("relations_s") + "");
			}
			JSONObject hitsszj = (JSONObject) conESzj.get("hits");
			countzj = new Integer(hitsszj.get("total") + "");
			//countzj = jazj.size();
			pageCountzj = (countzj % 10 == 0 ? countzj / 10 : countzj / 10 + 1);
		}
		
		if(listzj.size()>0){
			jazj = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listzj));
			reData.put("jazj", jazj);
			
		}
		reData.put("pageNozj", pageNozj);
		reData.put("pageCountzj", pageCountzj);
		reData.put("countzj", countzj);
		
		//项目
		JSONObject conESjj = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_o(listuuid, pageNojj, "fundapplyinst"));
		JSONObject hitsjj = (JSONObject) conESjj.get("hits");
		JSONObject jajj = new JSONObject();
		List<String> listjj = new ArrayList();
		JSONArray arrjj = (JSONArray) hitsjj.get("hits");
		int countjj = 0;
		int pageCountjj = 0;
		if (arrjj.size() > 0) {
			for (int i = 0; i < arrjj.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrjj.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listjj.add(data.get("relations_s") + "");
			}
			JSONObject hitssjg = (JSONObject) conESjj.get("hits");
			countjj = new Integer(hitssjg.get("total") + "");
			//countzj = jazj.size();
			pageCountjj = (countjj % 10 == 0 ? countjj / 10 : countjj / 10 + 1);
		}
		
		if(listjj.size()>0){
			jajj = ESHttpClient.conESJiJin(ESHttpClient.createQqueryByUuid(listjj));
			reData.put("jajj", jajj);
		}
		reData.put("pageNojj", pageNojj);
		reData.put("pageCountjj", pageCountjj);
		reData.put("countjj", countjj);
		
		//论文：
		JSONObject conESlw = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_o(listuuid, pageNo, "contribute_institution","Patent",1));
		JSONObject hitslw = (JSONObject) conESlw.get("hits");
		JSONObject jalw = new JSONObject();
		List<String> listlw = new ArrayList();
		JSONArray arrlw = (JSONArray) hitslw.get("hits");
		int count = 0;
		int pageCount = 0;
		if (arrlw.size() > 0) {
			for (int i = 0; i < arrlw.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrlw.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listlw.add(data.get("relations_s") + "");
			}
			JSONObject hitssjg = (JSONObject) conESlw.get("hits");
			count = new Integer(hitssjg.get("total") + "");
			//countzj = jazj.size();
			pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
		}
		if(listlw.size()>0){
			jalw = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listlw,"paper"));
			reData.put("data", jalw);
		}
		reData.put("pageCount", pageCount);
		reData.put("pageNo", pageNo);
		reData.put("count", count);
		
		//专利：
		JSONObject conESzl = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_o(listuuid, pageNozl, "contribute_institution","Patent",0));
		JSONObject hitszl = (JSONObject) conESzl.get("hits");
		JSONObject jazl = new JSONObject();
		List<String> listzl = new ArrayList();
		JSONArray arrzl = (JSONArray) hitszl.get("hits");
		int countzl = 0;
		int pageCountzl = 0;
		if (arrzl.size() > 0) {
			for (int i = 0; i < arrzl.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrzl.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listzl.add(data.get("relations_s") + "");
			}
			JSONObject hitsszl = (JSONObject) conESzl.get("hits");
			countzl = new Integer(hitsszl.get("total") + "");
			pageCountzl = (countzl % 10 == 0 ? countzl / 10 : countzl / 10 + 1);
		}
		if(listzl.size()>0){
			jazl = ESHttpClient.conESPatent(ESHttpClient.createQqueryByUuid(listzl));
			reData.put("jazl", jazl);
		}
		reData.put("pageCountzl", pageCountzl);
		reData.put("countzl", countzl);
		reData.put("pageNozl", pageNozl);
		
		
		
		
		return reData;
	}

	public JSONObject searchInsQkZt(HttpServletRequest request, String uuid){
		JSONObject reData = new JSONObject();
		//相关机构
		JSONObject conESxgjg = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_s(uuid, 1, "related_org"));
		JSONObject hitsxgjg  = (JSONObject) conESxgjg.get("hits");
		JSONObject jaxgjg = new JSONObject();
		List<String> listxgjg = new ArrayList();
		JSONArray arrxgjg= (JSONArray) hitsxgjg.get("hits");
		if (arrxgjg.size() > 0) {
			for (int i = 0; i < arrxgjg.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrxgjg.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				listxgjg.add(data.get("relations_o") + "");
			}
		}
		if(listxgjg.size()>0){
			jaxgjg = ESHttpClient.conESIns(ESHttpClient.createQqueryByUuid(listxgjg));
			reData.put("jaxgjg", jaxgjg);
		}
		
		//相关期刊
		JSONObject conESxgqk = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_s(uuid, 1, "publish"));
		JSONObject hitsxgqk  = (JSONObject) conESxgqk.get("hits");
		JSONObject jaxgqk = new JSONObject();
		List<String> listxgqk = new ArrayList();
		JSONArray arrxgqk= (JSONArray) hitsxgqk.get("hits");
		if (arrxgqk.size() > 0) {
			for (int i = 0; i < arrxgqk.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrxgqk.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				listxgqk.add(data.get("relations_o") + "");
			}
		}
		if(listxgqk.size()>0){
			jaxgqk = ESHttpClient.conESJou(ESHttpClient.createQqueryByUuid(listxgqk));
			reData.put("jaxgqk", jaxgqk);
		}
		//机构有关的主题：
		JSONObject conESzt = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_s(uuid,1,"subject_is"));
		JSONObject hitszt = (JSONObject) conESzt.get("hits");
		JSONArray arrzt = (JSONArray) hitszt.get("hits");
		List<String> listzt = new ArrayList();
		JSONObject jobzt = new JSONObject();
		if (arrzt.size() > 0) {
			for (int i = 0; i < arrzt.size(); i++) {
				JSONObject jo = (JSONObject) (arrzt.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_o_index")+"";
				listzt.add(data.get("relations_o") + "");
			}
		}
		if(listzt.size()>0){
			jobzt = ESHttpClient.conESSubject(ESHttpClient.createQqueryByUuid(listzt));
			reData.put("jszt", jobzt);
		}
		
		
		//会议
		JSONObject conEShy = ESHttpClient.conESRelations(
				ESHttpClient.createQqueryByRelations_s(uuid, 1, "undertake_conference"));
		JSONObject hitshy = (JSONObject) conEShy.get("hits");
		JSONObject jahy = new JSONObject();
		List<String> listhy = new ArrayList();
		JSONArray arrhy = (JSONArray) hitshy.get("hits");
		int counthy = 0;
		int pageCounthy = 0;
		if (arrhy.size() > 0) {
			for (int i = 0; i < arrhy.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrhy.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String type = data.get("relations_s") + "";
				listhy.add(data.get("relations_o") + "");
			}
			JSONObject hitssjg = (JSONObject) conEShy.get("hits");
			counthy = new Integer(hitssjg.get("total") + "");
			//countzj = jazj.size();
			pageCounthy = (counthy % 10 == 0 ? counthy / 10 : counthy / 10 + 1);
		}
		
		if(listhy.size()>0){
			jahy = ESHttpClient.conESConference(ESHttpClient.createQqueryByUuid(listhy));
			reData.put("jahy", jahy);
		}
//		reData.put("pageNohy", pageNohy);
//		reData.put("pageCounthy", pageCounthy);
//		reData.put("counthy", counthy);
		return reData;
	}
	public JSONObject searchExpertYjly(HttpServletRequest request, String uuid){
		JSONObject reData = new JSONObject();
		JSONObject conESyj = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,0,100,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",0));
		JSONObject hitsyj = (JSONObject) conESyj.get("hits");
		JSONArray jayj = new JSONArray();
		List<String> listlwyj = new ArrayList();
		JSONArray arryj = (JSONArray) hitsyj.get("hits");
		if (arryj.size() > 0) {
			for (int i = 0; i < arryj.size(); i++) {
				JSONObject jo = (JSONObject) (arryj.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_s_index")+"";
				listlwyj.add(data.get("relations_s") + "");
			}
		}
		List<Object> jasj = new ArrayList();
		List<Object> len = new ArrayList();
		JSONObject joblwyj = new JSONObject();
		if(listlwyj.size()>0){
		joblwyj = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByListUuid(listlwyj,"json.subjClassKwd.subj-class-kwd_kwdIK","article_year"));
		
		JSONObject aggregations = (JSONObject) joblwyj.get("aggregations");
		JSONObject article_year = (JSONObject) aggregations.get("json.subjClassKwd.subj-class-kwd_kwdIK");
		JSONArray arr = (JSONArray) article_year.get("buckets");
		
		if (arr != null && article_year != null) {
			if (arr.size() > 0) {
				for (int i = 0; i < arr.size(); i++) {
					JSONObject jo = (JSONObject) (arr.get(i));
					String key = jo.get("key") + "";
					JSONArray subarray = new JSONArray();
					if (!key.equals("") && !key.equals("null") && key != null) {
						len.add(key);
						JSONObject subjo = (JSONObject) jo.get("messages");
						JSONObject subBuk = (JSONObject) subjo.get("buckets");
						if (subjo != null && subBuk != null) {
							for (int k = 2013; k <= 2017; k++) {
								List<Object> sj = new ArrayList();
								JSONObject ss = new JSONObject();
								String ks = k + "";
								JSONObject ksjo = (JSONObject) subBuk.get(ks);
								sj.add(ks);
								sj.add(ksjo.get("doc_count"));
								sj.add(key);
								jasj.add(sj);
							}
						}
					}

				}
			}
		}
		}
		reData.put("len", len);
		reData.put("items", jasj);
		return reData;
	}
	
	public JSONObject searchExpertOneDiscovery(HttpServletRequest request, String uuid,int pageNo,int pageNojj,int pageNozl) {
		JSONObject reData = new JSONObject();
		List<String> hzarr = new ArrayList();
		JSONArray jazl = new JSONArray();
		
		int countzl = 0;
		int pageCountzl  = 0;
		
		
		//论文
		JSONObject conES = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,pageNo,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",0));
		JSONObject hits = (JSONObject) conES.get("hits");
		JSONArray ja = new JSONArray();
		JSONArray arr = (JSONArray) hits.get("hits");
		listhzlw.clear();
		JSONObject joblw = new JSONObject();
		int count = 0;
		int pageCount = 0;
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_s_index")+"";
				listhzlw.add(data.get("relations_s") + "");
			}
			JSONObject hitss = (JSONObject) conES.get("hits");
			count = new Integer(hitss.get("total") + "");
			pageCount = (count % 10 == 0 ? count / 10 : count / 10 + 1);
		}
		
//		JSONArray jalw = new JSONArray();
//		for (int i = (pageNo - 1) * 10; i < (((pageNo - 1) * 10) + 10 > count ? count
//				: ((pageNo - 1) * 10) + 10); i++) {
//			// 然后将数据存入afterList中
//			jalw.add(ja.get(i));
//		}
		
		if(listhzlw.size()>0){
			joblw = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listhzlw,"paper"));
			reData.put("data", joblw);
		}
		reData.put("pageCount", pageCount);
		reData.put("pageNo", pageNo);
		reData.put("count",count);
		
		
		
		
		
		//项目：
		JSONObject conESjj = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,pageNojj,"fundapply"));
		JSONObject hitsjj = (JSONObject) conESjj.get("hits");
		JSONObject jajj= new JSONObject();
		List<String> listjj = new ArrayList();
		JSONArray arrd = (JSONArray) hitsjj.get("hits");
		int countjj = 0;
		int pageCountjj  = 0;
		if (arrd.size() > 0) {
			for (int i = 0; i < arrd.size(); i++) {
				JSONObject job = new JSONObject();
				JSONObject jo = (JSONObject) (arrd.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				System.out.println(data.get("relations_s"));
				listjj.add(data.get("relations_s") + "");
				job = ESHttpClient.conESJiJin(ESHttpClient.createQqueryByUuid(data.get("relations_s") + ""));
				//job = findJJAuthorAndIns(job);
//				JSONObject hitssjj = (JSONObject) job.get("hits");
//				JSONArray arrdjj = (JSONArray) hitssjj.get("hits");
//				if(arrdjj.size()>0){
//					
//				}
				//ja1.add(job);
			}
			JSONObject hitssjj = (JSONObject) conESjj.get("hits");
			countjj = new Integer(hitssjj.get("total") + "");
			//countjj = ja1.size();
			pageCountjj = (countjj % 10 == 0 ? countjj / 10 : countjj / 10 + 1);
		}
//		JSONArray ja1jj = new JSONArray();
//		for (int i = (pageNojj - 1) * 10; i < (((pageNojj - 1) * 10) + 10 > countjj ? countjj
//				: ((pageNojj - 1) * 10) + 10); i++) {
//			// 然后将数据存入afterList中
//			ja1jj.add(ja1.get(i));
//		}
		
		if(listjj.size()>0){
			jajj = ESHttpClient.conESJiJin(ESHttpClient.createQqueryByUuid(listjj));
			reData.put("jajj", jajj);
		}
		reData.put("pageCountjj", pageCountjj);
		reData.put("countjj", countjj);
		reData.put("pageNojj", pageNojj);
		
		//专利
		JSONObject conESzl = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,pageNozl,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",1));
		JSONObject hitszl = (JSONObject) conESzl.get("hits");
		JSONArray arrzl = (JSONArray) hitszl.get("hits");
		List<String> listzl = new ArrayList();
		JSONObject jobzl = new JSONObject();
		if (arrzl.size() > 0) {
			for (int i = 0; i < arrzl.size(); i++) {
				JSONObject jo = (JSONObject) (arrzl.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_s_index")+"";
				listzl.add(data.get("relations_s") + "");
			}
			JSONObject hitss = (JSONObject) conESzl.get("hits");
			countzl = new Integer(hitss.get("total") + "");
			//count = ja.size();
			pageCountzl = (countzl % 10 == 0 ? countzl / 10 : countzl / 10 + 1);
		}
		
		if(listzl.size()>0){
			jobzl = ESHttpClient.conESPatent(ESHttpClient.createQqueryByUuid(listzl));
			reData.put("jazl", jobzl);
		}
		reData.put("pageNozl", pageNozl);
		reData.put("pageCountzl", pageCountzl);
		reData.put("countzl", countzl);
		
		
		return reData;
	}
	public JSONObject searchExpertQkZt(HttpServletRequest request, String uuid){
		JSONObject reData = new JSONObject();
		//查找专家对应的期刊：
		JSONObject conESqk = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,300,"contributor","relations_s_index","t_dm_las_nstl_collection","Patent",0));
		JSONObject hitsqk = (JSONObject) conESqk.get("hits");
		JSONArray arrqk = (JSONArray) hitsqk.get("hits");
		List<String> listqk = new ArrayList();
		JSONObject jobqk = new JSONObject();
		if (arrqk.size() > 0) {
			for (int i = 0; i < arrqk.size(); i++) {
				JSONObject jo = (JSONObject) (arrqk.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_s_index")+"";
				listqk.add(data.get("relations_s") + "");
			}
		}
		if(listqk.size()>0){
			jobqk = ESHttpClient.conESJou(ESHttpClient.createQqueryByUuidfactor(listqk));
			reData.put("jsqk", jobqk);
		}
		
		//专家有关的主题：
		JSONObject conESzt = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_s(uuid,1,100,"subject_is"));
		JSONObject hitszt = (JSONObject) conESzt.get("hits");
		JSONArray arrzt = (JSONArray) hitszt.get("hits");
		List<String> listzt = new ArrayList();
		JSONObject jobzt = new JSONObject();
		JSONObject joblw = new JSONObject();
		if (arrzt.size() > 0) {
			for (int i = 0; i < arrzt.size(); i++) {
				JSONObject jo = (JSONObject) (arrzt.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String in = data.get("relations_o_index")+"";
				listzt.add(data.get("relations_o") + "");
			}
		}
		if(listzt.size()>0){
			jobzt = ESHttpClient.conESSubject(ESHttpClient.createQqueryByUuidZtpx(listzt,100,"uuid"));
			reData.put("jszt", jobzt);
		}/*else{
			if(listhzlw.size()>0){
				joblw = ESHttpClient.conESRePaper(ESHttpClient.createQqueryByUuid(listhzlw,"json.subjClassKwd.subj-class-kwd_kwd","subj-class-kwd_kwdIK",10));
				reData.put("lwzt", joblw);
			}
		}*/
		return reData;
	}
	public JSONObject searchExpertHeZuoGx(HttpServletRequest request, String uuid){
		//合作学者
		JSONObject reData = new JSONObject();
		
		JSONObject conESgx = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,"coauthor"));
		JSONObject hitgx = (JSONObject) conESgx.get("hits");
		int total = new Integer(hitgx.get("total")+ "");
		List<String> listhz = new ArrayList();
		JSONObject jahz = new JSONObject();
		Map<String,Integer> maphz = new HashMap();
		
		
		JSONObject conEShz = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,total,"coauthor"));
		JSONObject hitshz = (JSONObject) conEShz.get("hits");
		JSONArray arrdd = (JSONArray) hitshz.get("hits");
		
		if (arrdd.size() > 0) {
			for (int i = 0; i < arrdd.size(); i++) {
				JSONObject jo = (JSONObject) (arrdd.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				String esuuid =  data.get("relations_s") + "";
				listhz.add(esuuid);
				if(maphz.containsKey(esuuid)){
					maphz.put(esuuid, maphz.get(esuuid)+1);
				}else{
					maphz.put(esuuid, 1);
				}
			}
		}
		Map<String,String> maphzhz = new HashMap();
		for(String huuid : listhz){
//			JSONObject conESgxhz = ESHttpClient
//					.conESRelations(ESHttpClient.createQqueryByRelations_o(huuid,1,"coauthor"));
//			JSONObject hitgxhz = (JSONObject) conESgxhz.get("hits");
//			int totalhz = new Integer(hitgxhz.get("total")+ "");
			
			JSONObject conEShzhz = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_o(huuid,1,30,"coauthor"));
			
			JSONObject hitshzhz = (JSONObject) conEShzhz.get("hits");
			JSONArray arrhzhz = (JSONArray) hitshzhz.get("hits");
			
			if (arrhzhz.size() > 0) {
				for (int i = 0; i < arrhzhz.size(); i++) {
					JSONObject jo = (JSONObject) (arrhzhz.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					String esuuid =  data.get("relations_s") + "";
					if(listhz.contains(esuuid) && !esuuid.equals(uuid)){
						if(maphzhz.containsKey(huuid)){
							maphzhz.put(huuid, maphzhz.get(huuid)+"**"+esuuid);
						}else{
							maphzhz.put(huuid, esuuid);
						}
					}
				}
			}
		}
		
		
		if(listhz.size()>0){
			jahz = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listhz,listhz.size()));
			reData.put("jahz", jahz);
		}
		reData.put("maphzhz",maphzhz);
		reData.put("maphz", maphz);
		return reData;
	}
	public JSONObject searchExpertPaper(String uuid,int pageNo){
		JSONObject reData = new JSONObject();
		JSONObject conEShzs = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",2));
		JSONObject hitshzs = (JSONObject) conEShzs.get("hits");
		String total = hitshzs.get("total")+ "";
		int countlw = 0;
		if(total!=null){
			countlw = new Integer(total);
		}
		JSONObject conESlw = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,countlw,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",2));
		JSONObject hitslw = (JSONObject) conESlw.get("hits");
		JSONArray arrdds = (JSONArray) hitslw.get("hits");
		List<String> listlwlw = new ArrayList();
		if (arrdds.size() > 0) {
			for (int i = 0; i < arrdds.size(); i++) {
				JSONObject jo = (JSONObject) (arrdds.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				listlwlw.add(data.getString("relations_s")+"");
			}
		}
		
		if(listlwlw.size()>0){
			reData = ESHttpClient.conES(ESHttpClient.createQqueryByUuidPage(listlwlw,pageNo));
		}
		return reData;
	}
	public JSONObject searchExpertHeZuo(HttpServletRequest request, String uuid){
		//合作学者
		JSONObject reData = new JSONObject();
		JSONObject conEShzs = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",2));
		JSONObject hitshzs = (JSONObject) conEShzs.get("hits");
		String total = hitshzs.get("total")+ "";
		int countlw = 0;
		if(total!=null){
			countlw = new Integer(total);
		}
		JSONObject conESlw = ESHttpClient
				.conESRelations(ESHttpClient.createQqueryByRelations_o(uuid,1,countlw,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",2));
		JSONObject hitslw = (JSONObject) conESlw.get("hits");
		JSONArray arrdds = (JSONArray) hitslw.get("hits");
		List<String> listlwlw = new ArrayList();
		if (arrdds.size() > 0) {
			for (int i = 0; i < arrdds.size(); i++) {
				JSONObject jo = (JSONObject) (arrdds.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				listlwlw.add(data.getString("relations_s")+"");
			}
		}
		List<String> listhz = new ArrayList();
		JSONObject jahz = new JSONObject();
		List<String> listhzjg = new ArrayList();
		JSONObject jahzjg = new JSONObject();
		Map<String,Integer> maphz = new HashMap();
		for(String kk : listlwlw){
			
			
//			JSONObject conEShzjg = ESHttpClient
//			.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,"contribute_institution"));
//			JSONObject hitshzjg = (JSONObject) conEShzjg.get("hits");
//			JSONArray arrhzjg = (JSONArray) hitshzjg.get("hits");
//			
//			if (arrhzjg.size() > 0) {
//				for (int i = 0; i < arrhzjg.size(); i++) {
//					JSONObject jo = (JSONObject) (arrhzjg.get(i));
//					JSONObject data = (JSONObject) jo.get("_source");
//					String esuuid =  data.get("relations_o") + "";
//					listhzjg.add(esuuid);
//				}
//			}
			
			JSONObject conEShzss = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,"contributor"));
			JSONObject hitshzss = (JSONObject) conEShzss.get("hits");
			String totals = hitshzss.get("total")+ "";
			JSONObject conEShz = ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,new Integer(totals),"contributor"));
			JSONObject hitshz = (JSONObject) conEShz.get("hits");
			JSONArray arrdd = (JSONArray) hitshz.get("hits");
			
			if (arrdd.size() > 0) {
				for (int i = 0; i < arrdd.size(); i++) {
					JSONObject jo = (JSONObject) (arrdd.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					String esuuid =  data.get("relations_o") + "";
					if(!uuid.equals(esuuid)){
						listhz.add(esuuid);
						if(maphz.containsKey(esuuid)){
							maphz.put(esuuid, maphz.get(esuuid)+1);
						}else{
							maphz.put(esuuid, 1);
						}
					}
				}
			}
		}
		Map<String,String> maphzhz = new HashMap();
		for(String keys : maphz.keySet()){
			JSONObject conEShzsss= ESHttpClient
					.conESRelations(ESHttpClient.createQqueryByRelations_o(keys,1,"contributor","relations_s_index","t_ods_las_nstl_value","Patent",2));
			JSONObject hitslws = (JSONObject) conEShzsss.get("hits");
			JSONArray arrddss = (JSONArray) hitslws.get("hits");
			List<String> listlwlws = new ArrayList();
			if (arrddss.size() > 0) {
				for (int i = 0; i < arrddss.size(); i++) {
					JSONObject jo = (JSONObject) (arrddss.get(i));
					JSONObject data = (JSONObject) jo.get("_source");
					listlwlws.add(data.getString("relations_s")+"");
				}
			}
			for(String kk : listlwlws){
				JSONObject conEShzsst = ESHttpClient
						.conESRelations(ESHttpClient.createQqueryByRelations_s(kk,1,"contributor"));
				JSONObject hitshzt = (JSONObject) conEShzsst.get("hits");
				JSONArray arrddtt = (JSONArray) hitshzt.get("hits");
				
				if(arrddtt.size() > 0) {
					for (int i = 0; i < arrddtt.size(); i++) {
						JSONObject jo = (JSONObject) (arrddtt.get(i));
						JSONObject data = (JSONObject) jo.get("_source");
						String esuuid =  data.get("relations_o") + "";
						if(maphz.containsKey(esuuid) && !uuid.equals(keys) && !esuuid.equals(keys) && !esuuid.endsWith(uuid)){
							if(maphzhz.containsKey(keys)){
								maphzhz.put(keys, maphzhz.get(keys)+"**"+esuuid);
							}else{
								maphzhz.put(keys, esuuid);
							}
						}
					}
				}
			}
			
			
		}
		
		if(listhz.size()>0){
			jahz = ESHttpClient.conESExpert(ESHttpClient.createQqueryByUuid(listhz,listhz.size()));
			reData.put("jahz", jahz);
		}
		reData.put("maphzhz",maphzhz);
//		if(listhzjg.size()>0){
//			jahzjg = ESHttpClient.conESIns(ESHttpClient.createQqueryByUuid(listhzjg));
//			reData.put("jahzjg", jahzjg);
//		}
		reData.put("maphz", maphz);
		return reData;
	}

	public JSONObject findJJXgQt(JSONObject conES,JSONObject reData){
		JSONArray jazj = new JSONArray(); 
		JSONArray jajg = new JSONArray(); 
		JSONArray jawx = new JSONArray(); 
		JSONObject hits = (JSONObject) conES.get("hits");
		JSONArray ja = new JSONArray();
		JSONArray arr = (JSONArray) hits.get("hits");
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				// System.out.println("i=="+i+"**********"+data.get("uuid")+"**********");
				// 查关系表的jjID
				JSONObject conESjj = ESHttpClient
						.conESRelations(ESHttpClient.createQqueryByRelations_s(data.get("uuid")+"",1,"fundapply"));
				JSONObject hits1 = (JSONObject) conESjj.get("hits");
				JSONArray arr1 = (JSONArray) hits1.get("hits");
				
				if (arr1.size() > 0) {
					for (int j = 0; j < arr1.size(); j++) {
						JSONObject jo1 = (JSONObject) (arr1.get(j));
						JSONObject data1 = (JSONObject) jo1.get("_source");
						String relations_p = data1.get("relations_p") + "";
						JSONObject expert = ESHttpClient
									.conESExpert(ESHttpClient.createQqueryByUuid(data1.get("relations_o") + ""));
						JSONObject hitsss = (JSONObject) expert.get("hits");
						JSONArray arrss = (JSONArray) hitsss.get("hits");
						if (arrss.size() > 0) {
							jazj.add(expert);
						}
						
//						JSONObject conESwx = ESHttpClient
//								.conESRelations(ESHttpClient.createQqueryByRelations_o(data1.get("relations_o")+"",1,"contributor"));
//						JSONObject hitswx = (JSONObject) conESwx.get("hits");
//						JSONArray arrwx = (JSONArray) hitswx.get("hits");
//						int count = 0;
//						int pageCount = 0;
//						if (arr.size() > 0) {
//							for (int a = 0; a < arrwx.size(); a++) {
//								JSONObject jowx = (JSONObject) (arrwx.get(a));
//								JSONObject datawx = (JSONObject) jowx.get("_source");
//								String in = data.get("relations_s_index") + "";
//								if (in.equals("t_ods_las_nstl_value")) {
//									JSONObject job = ESHttpClient
//											.conES(ESHttpClient.createQqueryByUuid(datawx.get("relations_s") + ""));
//									JSONObject hitss = (JSONObject) job.get("hits");
//									JSONArray arrsss = (JSONArray) hitss.get("hits");
//									if (arrsss.size() > 0) {
//										String source = data.get("source") + "";
//										if (source.contains("Article")) {
//											jawx.add(job);
//										}
//
//									}
//								}
//							}
//						}
					}
				}
			}
		}
		return conES;
	}
	public JSONObject findJJAuthorAndIns(JSONObject conES){
		JSONObject hits = (JSONObject) conES.get("hits");
		JSONArray ja = new JSONArray();
		JSONArray arr = (JSONArray) hits.get("hits");
		if (arr.size() > 0) {
			for (int i = 0; i < arr.size(); i++) {
				JSONObject jo = (JSONObject) (arr.get(i));
				JSONObject data = (JSONObject) jo.get("_source");
				// System.out.println("i=="+i+"**********"+data.get("uuid")+"**********");
				// 查关系表的jjID
				JSONObject job = ESHttpClient.conESRelations(ESHttpClient.createQqueryByRelations_s(data.get("uuid") + ""));
				JSONObject hits1 = (JSONObject) job.get("hits");
				JSONArray arr1 = (JSONArray) hits1.get("hits");
				if (arr1.size() > 0) {
					for (int j = 0; j < arr1.size(); j++) {
						JSONObject jo1 = (JSONObject) (arr1.get(j));
						JSONObject data1 = (JSONObject) jo1.get("_source");
						String relations_p = data1.get("relations_p") + "";
						if (relations_p.equals("13")) {
							JSONObject expert = ESHttpClient
									.conESExpert(ESHttpClient.createQqueryByUuid(data1.get("relations_o") + ""));
							//System.out.println("i==" + i + "**********" + data1.get("relations_o") + "**********");
							JSONObject hits2 = (JSONObject) expert.get("hits");
							JSONArray arr2 = (JSONArray) hits2.get("hits");
							if (arr2.size() > 0) {
								for (int k = 0; k < arr2.size(); k++) {
									JSONObject jo2 = (JSONObject) (arr2.get(k));
									JSONObject data2 = (JSONObject) jo2.get("_source");
									data.put("author", data2.get("contrib_full-name") + "");
									// System.out.println("**********zuozhe:"+data2.get("contrib_full-name"));

									JSONObject ins = ESHttpClient
											.conESRelations(ESHttpClient.createQqueryByRelations_s(data2.get("uuid") + ""));
									// System.out.println("exId:--------"+data2.get("uuid")+"-----------");
									JSONObject hits3 = (JSONObject) ins.get("hits");
									JSONArray arr3 = (JSONArray) hits3.get("hits");
									if (arr3.size() > 0) {
										for (int o = 0; o < arr3.size(); o++) {
											JSONObject jo3 = (JSONObject) (arr3.get(o));
											JSONObject data3 = (JSONObject) jo3.get("_source");
											String relations_p1 = data3.get("relations_p") + "";
											// System.out.println(relations_p1);

											JSONObject ii = ESHttpClient.conESIns(
													ESHttpClient.createQqueryByUuid(data3.get("relations_o") + ""));
											JSONObject hits4 = (JSONObject) ii.get("hits");
											JSONArray arr4 = (JSONArray) hits4.get("hits");
											if (arr4.size() > 0) {
												for (int n = 0; n < arr4.size(); n++) {
													JSONObject jo4 = (JSONObject) (arr4.get(n));
													JSONObject data4 = (JSONObject) jo4.get("_source");
													data.put("ins", data4.get("contrib_institution") + "");
												}
											}

										}
									}
								}
							}
						}
					}
				}
			}
		}
		return conES;
	}
	
}
