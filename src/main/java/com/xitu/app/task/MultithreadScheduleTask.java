package com.xitu.app.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xitu.app.mapper.PriceMapper;
import com.xitu.app.model.Price;

@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
public class MultithreadScheduleTask {

	@Autowired
    private PriceMapper priceMapper;
	
	@Async
    @Scheduled(cron = "0 0 0,8,16,21 * * ?")  //间隔1秒
    public void first() throws InterruptedException {

		final String url="http://nm.sci99.com/news/s8784.html" ;
		String single = "http://nm.sci99.com";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String ymd = formatter.format(date);

        try {
        	
        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
        	
        	if(exist != null) {
        		return;
        	}

            Document doc = Jsoup.connect(url).get();

            Elements module = doc.getElementsByClass("ul_w690");

            Document moduleDoc = Jsoup.parse(module.toString());

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
//            if(!singleDoc.toString().contains(ymd)){
//            	return;
//            }
            Element zoom = singleDoc.getElementById("zoom");
            Elements trElements = zoom.select("tr");
            boolean ignore = true;
            for(Element tdelement : trElements) {
            	if(ignore) {
            		ignore = false;
            		continue;
            	}
            	Elements tdes = tdelement.select("td");
            	Price price = new Price();
            	price.setUpdateTime(formatter.format(date));
            	for(int i = 0; i < tdes.size(); i++){
            		if(i==0) {
            			price.setName(tdes.get(i).text());
            		}else if(i==1) {
            			price.setDescription(tdes.get(i).text());
            		}else if(i==6) {
            			price.setUnit(tdes.get(i).text());
            		}else if(i==3) {
            			price.setPrice(tdes.get(i).text());
            		}else if(i==5) {
            			price.setFloating(tdes.get(i).text());
            		}
				}
            	priceMapper.insertPrice(price);
            }
              //  String title = clearfixli.getElementsByTag("a").text();
            System.out.println("fasdf");

          //  System.out.println(clearfix);

        } catch (IOException e) {
            e.printStackTrace();
        }
	
    }
}
