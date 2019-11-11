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

import com.xitu.app.mapper.*;
import com.xitu.app.model.*;
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
import org.springframework.web.bind.annotation.*;
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
import com.xitu.app.repository.PatentRepository;
import com.xitu.app.service.es.JianceService;
import com.xitu.app.service.es.PatentService;
import com.xitu.app.utils.HttpClientUtils;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class MaterialController {

	@Autowired
	private XiangtuMapper xiangtuMapper;

	private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);
	private static final String MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={TOKEN}";
	@Autowired
    private JisuanfuwuMapper jisuanfuwuMapper;
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
    	SaveOrderRequest saveOrderRequest = new SaveOrderRequest();
		model.addAttribute("saveOrderRequest", saveOrderRequest);
		return "jisuanfuwupingtai";
	}
    
    
//    @PostMapping(value = "material/save")
//	public String jisuanbiaodan(SaveOrderRequest request,Model model, RedirectAttributes redirectAttributes) throws Exception {
//		Jisuan jisuan = new Jisuan();
//		if (request != null) {
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
//		jisuanfuwuMapper.insertOrder(order);
////		//刘冰川
////		//微信发送给通知给用户  待处理
////		//微信发送通知给操作员
////		String url = MESSAGE_URL.replace("{TOKEN}", Constant.ACCESS_TOKEN);
////		System.out.println("order userId------->" + order.getUserId());
////		JSONObject jsonObject = new JSONObject();
//////        jsonObject.put("touser", order.getUserId());   // openid
////		jsonObject.put("touser", "okT7G03g6kRUX7BI0FY048D0BA9o");
////        jsonObject.put("template_id", "xp9DfByRBXy5znxzyNW9o5TJh4_1zDRSCaV3BJhz3Sg");
//////        jsonObject.put("url", "http://www.baidu.com");
//// 
////        JSONObject data = new JSONObject();
////        JSONObject first = new JSONObject();
////        first.put("value", "hello");
////        first.put("color", "#173177");
////        JSONObject keyword1 = new JSONObject();
////        keyword1.put("value", "hello");
////        keyword1.put("color", "#173177");
//////        JSONObject keyword2 = new JSONObject();
//////        keyword2.put("value", "hello");
//////        keyword2.put("color", "#173177");
//////        JSONObject keyword3 = new JSONObject();
//////        keyword3.put("value", "hello");
//////        keyword3.put("color", "#173177");
//////        JSONObject remark = new JSONObject();
//////        remark.put("value", "hello");
//////        remark.put("color", "#173177");
////        
////        data.put("first",first);
////        data.put("keyword1",keyword1);
//////        data.put("keyword2",keyword2);
//////        data.put("keyword3",keyword3);
//////        data.put("remark",remark);
//// 
////        jsonObject.put("data", data);
//// 
////        String string = HttpClientUtils.sendPostJsonStr(url, jsonObject.toJSONString());
////        JSONObject result = JSON.parseObject(string);
////        System.out.println("response 结果------>" + string);
////        int errcode = result.getIntValue("errcode");
////        if(errcode == 0){
////            // 发送成功
////            System.out.println("发送成功");
////        } else {
////            // 发送失败
////            System.out.println("发送失败");
////
////        }		
//		
//		Linkuser linkuser = new Linkuser();
//		if (request.getName() != null) {
//			linkuser.setName(request.getName());
//		}
//		if (request.getEmail() != null) {
//			linkuser.setEmail(request.getEmail());
//		}
//		if (request.getInstitution()!= null) {
//			linkuser.setInstitution(request.getInstitution());
//		}
//		if (request.getTelephone() != null) {
//			linkuser.setTelephone(request.getTelephone());
//		}
//		String linkuseruuid = UUID.randomUUID().toString();
//		linkuser.setUuid(linkuseruuid);
//		
//		jisuanfuwuMapper.insertLinkuser(linkuser);
//		
//		Relation relation = new Relation();
//		relation.setLinkuserId(linkuseruuid);
//		relation.setOrderId(orderuuid);
//		//主联系是0，其他联系人是1
//		relation.setTag(0);
//		
//		
//		jisuanfuwuMapper.insertRelation(relation);
//		
//		
//		model.addAttribute("display", "1");
//		redirectAttributes.addAttribute("token", request.getToken());
//		return "T-jisuanfuwupingtai";
//	}
//    @GetMapping(value = "material/xiangtu")
//	public String xiangtushujuku(Model model) {
//
//
//		return "xiangtushujuku";
//	}

	@GetMapping(value = "material/xiangtu")
	public String xiangtuList(@RequestParam("unitse") String unitse,
									 @RequestParam("arease") String arease,
									 @RequestParam(required=false, value="pageIndex") Integer pageIndex,
									 @RequestParam(required=false, value="pageSize") Integer pageSize,
									 Model model) {
		if(pageSize == null) {
			pageSize = 3;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("pageSize", pageSize);
		List<Xiangtu> xiangtuList = xiangtuMapper.getXiangtuList(unitse, arease, pageIndex, pageSize);
		model.addAttribute("xiangtuList", xiangtuList);

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
