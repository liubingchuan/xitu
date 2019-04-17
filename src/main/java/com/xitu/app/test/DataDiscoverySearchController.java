package com.xitu.app.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xitu.app.controller.JianceController;
import com.xitu.app.model.Jiance;
import com.xitu.app.repository.JianceRepository;
import com.xitu.app.test.DataDiscoveryServiceImpl;


@RestController
public class DataDiscoverySearchController {

	
	@RequestMapping("/searchPaper")
	public String searchPaper(HttpServletRequest request) {
		 
		
		String param = (String)request.getParameter("param"); 
		String pageNo = (String)request.getParameter("pageNo");
		String author = (String)request.getParameter("author");
		String source = (String)request.getParameter("source");
		String orderBy = (String)request.getParameter("orderBy");
		String method = (String)request.getParameter("method");
		String date = (String)request.getParameter("date");
		String ins = (String)request.getParameter("ins");
		String jou = (String)request.getParameter("jou");
		String sub = (String)request.getParameter("sub");
		String lwlx = (String)request.getParameter("lwlx");
		String dhtype = (String)request.getParameter("dhtype");
		try {
			dhtype = URLDecoder.decode(dhtype, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String del = (String)request.getParameter("del");
		HttpSession session = request.getSession();
		Set<String> dhSet;
		if(session.getAttribute("dhpaper")!=null){
			dhSet = (Set<String>)session.getAttribute("dhpaper");
		}else{
			dhSet = new LinkedHashSet();
		}
		
		
		if (dhtype != "" && dhtype != null && dhtype != "null") {
			dhSet.add(dhtype);
		}
		if (del != "" && del != null && del != "null") {
			if(del.equals("soudatabaseDel")){
				dhSet.remove("来源数据库");
			}
			if(del.equals("dateDel")){
				dhSet.remove("时间");
			}
			if(del.equals("jouDel")){
				dhSet.remove("期刊");
			}
			if(del.equals("insDel")){
				dhSet.remove("机构");
			}
			if(del.equals("subDel")){
				dhSet.remove("关键词");
			}
			if(del.equals("lwlxDel")){
				dhSet.remove("资源类型");
			}
		}
		String userip = (String)request.getParameter("userip");
		session.setAttribute("dhpaper", dhSet);
		long startTime=System.currentTimeMillis();
		String uesremail = (String)request.getParameter("uesremail");
		DataDiscoveryServiceImpl dataDiscoveryService = new DataDiscoveryServiceImpl();
		String uuid =userip+""+ uesremail+""+param+""+author+""+date+""+jou+""+ins+""+sub+""+lwlx+""+new Integer(pageNo)+""+source+""+orderBy+""+method;
		String json_send= dataDiscoveryService.searchPaper(request,param,author,date,jou,ins,sub,lwlx,uesremail,userip,new Integer(pageNo),source,orderBy,method).toString();
			
		long startTime1=System.currentTimeMillis();
		float excTime=(float)(startTime1-startTime);
		String retStr = json_send; 
		
		return retStr;
	}
private static final Logger logger = LoggerFactory.getLogger(JianceController.class);
	
	@Autowired
    private JianceRepository paperRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@GetMapping(value = "jiance/jiancelist1")
	public String projects(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="year") String year,
			@RequestParam(required=false,value="institution") String institution,
			@RequestParam(required=false,value="lanmu") String lanmu,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		
		DataDiscoveryServiceImpl dataDiscoveryService = new DataDiscoveryServiceImpl();
		String json_send= dataDiscoveryService.jiance(q,year,institution,lanmu,pageIndex).toString();
		System.out.println(json_send);
		
		
		String view = "T-jiance1";			
		return view;
	}
	
	
}
