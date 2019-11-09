package com.xitu.app.controller;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.request.AgPersonRequest;
import com.xitu.app.common.request.AgTypeRequest;
import com.xitu.app.common.request.PatentPageListRequest;
import com.xitu.app.common.request.PriceAvgRequest;
import com.xitu.app.common.request.SaveOrderRequest;
import com.xitu.app.constant.Constant;
import com.xitu.app.mapper.PatentMapper;
import com.xitu.app.mapper.PriceMapper;
import com.xitu.app.model.Linkuser;
import com.xitu.app.model.Order;
import com.xitu.app.model.Patent;
import com.xitu.app.model.PatentMysql;
import com.xitu.app.model.Price;
import com.xitu.app.model.Relation;
import com.xitu.app.repository.PatentRepository;
import com.xitu.app.service.es.JianceService;
import com.xitu.app.service.es.PatentService;
import com.xitu.app.utils.HttpClientUtils;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class MaterialController {

	private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);
	
	
	// 代理隧道验证信息
    final static String ProxyUser = "H677S6B336VV189D";
    final static String ProxyPass = "8E65F1A7219B95AB";

    // 代理服务器
    final static String ProxyHost = "http-dyn.abuyun.com";
    final static Integer ProxyPort = 9020;

    // 设置IP切换头
    final static String ProxyHeadKey = "Proxy-Switch-Ip";
    final static String ProxyHeadVal = "yes";
	
	
    @GetMapping(value = "material/calculate")
	public String jisuanfuwu(Model model) {
		
		return "jisuanfuwupingtai";
	}
    
    @GetMapping(value = "material/xiangtu")
	public String xiangtushujuku(Model model) {
		
		return "xiangtushujuku";
	}
    
    
//    @PostMapping(value = "material/save")
//	public String saveOrder(SaveOrderRequest request,Model model, RedirectAttributes redirectAttributes) throws Exception {
//		Order order = new Order();
//		if (request.getTitle() != null) {
//			order.setTitle(request.getTitle());
//		}
//		if (request.getUserId() != null) {
//			order.setUserId(request.getUserId());
//		}
//		if (request.getChaxinfanwei() != null && !request.getChaxinfanwei().equals("请选择查新范围")) {
//			order.setChaxinfanwei(request.getChaxinfanwei());
//		}
//		
//		if (request.getMudi() != null && !request.getMudi().equals("请选择项目目的")) {
//			order.setMudi(request.getMudi());
//		}
//		
//		if (request.getKexueyaodian() != null) {
//			order.setKexueyaodian(request.getKexueyaodian());
//		}
//		
//		if (request.getJiansuodian() != null) {
//			order.setJiansuodian(request.getJiansuodian());
//		}
//		if (request.getBeizhu() != null) {
//			order.setBeizhu(request.getBeizhu());
//		}
//		
//		if (request.getJiansuoci() != null) {
//			order.setJiansuoci(request.getJiansuoci());
//		}
//		if (request.getShenqingfujianId() != null) {
//			order.setShenqingfujianId(request.getShenqingfujianId());
//		}
//		if (request.getXueke() != null && !request.getXueke().equals("请选择学科分类")) {
//			order.setXueke(request.getXueke());
//		}
//		if (request.getChanye() != null && !request.getChanye().equals("请选择产业分类")) {
//			order.setChanye(request.getChanye());
//		}
//		if (request.getInstitution()!= null) {
//			order.setInstitution(request.getInstitution());
//		}
//		//order.setUserId(1);
//		order.setChulizhuangtai("待处理");
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//		//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
//		order.setShenqingshijian(df.format(new Date()));
//		
//		String orderuuid = UUID.randomUUID().toString();
//		order.setUuid(orderuuid);
//		zhishifuwuMapper.insertOrder(order);
//		model.addAttribute("display", "1");
//		redirectAttributes.addAttribute("token", request.getToken());
//		return "T-zhishi";
//	}
	
}
