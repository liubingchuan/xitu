package com.xitu.app.test;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	
	public static void main(String[] args) {
		final String url="http://nm.sci99.com/news/s8784.html" ;
		String single = "http://nm.sci99.com";

        try {

            Document doc = Jsoup.connect(url).get();


            Elements module = doc.getElementsByClass("ul_w690");

            Document moduleDoc = Jsoup.parse(module.toString());

            //Elements clearfix = moduleDoc.getElementsByClass("clearfix");  //DOM的形式

            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式

jump:
            for (Element li : lis){
                Document liDoc = Jsoup.parse(li.toString());
                Elements hrefs = liDoc.select("a[href]");
                for(Element elem: hrefs) {
                	if(!"".equals(elem.attr("href"))){
                		String href = elem.attr("href");
                		single = single + href;
                		break jump;
                	}
                }

            }
            
            Document singleDoc = Jsoup.connect(single).get();
            Element zoom = singleDoc.getElementById("zoom");
            Elements trElements = zoom.select("tr");
            boolean ignore = true;
            for(Element tdelement : trElements) {
            	if(ignore) {
            		ignore = false;
            		continue;
            	}
            	Elements tdes = tdelement.select("td");
            	for(int i = 0; i < tdes.size(); i++){
            		if(i==0) {
            			
            		}
					System.out.println(tdes.get(i).text());
				}

            }
              //  String title = clearfixli.getElementsByTag("a").text();
            System.out.println("fasdf");

          //  System.out.println(clearfix);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
