package com.xitu.app.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	
//	public static void main(String[] args) {
//		final String url="http://nm.sci99.com/news/s8784.html" ;
//		String single = "http://nm.sci99.com";
//
//        try {
//
//            Document doc = Jsoup.connect(url).get();
//
//
//            Elements module = doc.getElementsByClass("ul_w690");
//
//            Document moduleDoc = Jsoup.parse(module.toString());
//
//            //Elements clearfix = moduleDoc.getElementsByClass("clearfix");  //DOM的形式
//
//            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式
//
//jump:
//            for (Element li : lis){
//                Document liDoc = Jsoup.parse(li.toString());
//                Elements hrefs = liDoc.select("a[href]");
//                for(Element elem: hrefs) {
//                	if(!"".equals(elem.attr("href"))){
//                		String href = elem.attr("href");
//                		single = single + href;
//                		break jump;
//                	}
//                }
//
//            }
//            
//            Document singleDoc = Jsoup.connect(single).get();
//            Element zoom = singleDoc.getElementById("zoom");
//            Elements trElements = zoom.select("tr");
//            boolean ignore = true;
//            for(Element tdelement : trElements) {
//            	if(ignore) {
//            		ignore = false;
//            		continue;
//            	}
//            	Elements tdes = tdelement.select("td");
//            	for(int i = 0; i < tdes.size(); i++){
//            		if(i==0) {
//            			
//            		}
//					System.out.println(tdes.get(i).text());
//				}
//
//            }
//              //  String title = clearfixli.getElementsByTag("a").text();
//            System.out.println("fasdf");
//
//          //  System.out.println(clearfix);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
	public static void main(String[] args) {
//		final String url="http://www2.soopat.com/Home/Result" ;
//		final String url="http://www.soopat.com/Home/Result" ;
//		String base = "http://www.soopat.com";
//		List<String> innerPaths = new ArrayList<String>();
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("SearchWord", "稀土");
//		map.put("FMZL", "Y");
//		map.put("SYXX", "Y");
//		map.put("WGZL", "Y");
//		map.put("FMSQ", "Y");
//		map.put("PatentIndex", "10");
//		try {
//			Connection conn = Jsoup.connect(url);
//			conn.data(map);
//			Document doc = conn.get();
//			
//			System.out.println(doc.toString());
//			
//			Elements patentBlocks = doc.getElementsByClass("PatentBlock");
//			
//			for(Element patentBlock: patentBlocks) {
//				Document patentDoc = Jsoup.parse(patentBlock.toString());
//				Elements patentTypeElements = patentDoc.getElementsByClass("PatentTypeBlock");
//				if(patentTypeElements.size() == 0) {
//					continue;
//				}
//jump:
//				for(Element pte: patentTypeElements) {
//					Document pteDoc = Jsoup.parse(pte.toString());
//					Elements hrefs = pteDoc.select("a[href]");
//					for(Element elem: hrefs) {
//	                	if(!"".equals(elem.attr("href"))){
//	                		String href = elem.attr("href");
//	                		innerPaths.add(href);
//	                		break jump;
//	                	}
//	                }
//				}
//			}
//			
//			System.out.println("fasdf");
//
//		for(String path: innerPaths) {
//			String target = base + path;
//			Connection singlePageConn = Jsoup.connect(target);
//			Document singleDoc = singlePageConn.get();
//			System.out.println(singleDoc.getElementsByTag("h1").val());
//			System.out.println(singleDoc);
//		}
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		String url = "http://www.soopat.com/Patent/201280048571";
		Connection singlePageConn = Jsoup.connect(url);
		try {
			Document singleDoc = singlePageConn.get();
			System.out.println(singleDoc);
			Elements h1Elements = singleDoc.getElementsByTag("h1");
			for(Element h1Element: h1Elements) {
				String title = h1Element.text();
				if(title != null) {
					break;
				}
			}
			Elements datainfoElements = singleDoc.getElementsByClass("datainfo");
			for(Element dataInfo : datainfoElements) {
				System.out.println(dataInfo.text());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
