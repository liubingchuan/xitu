package com.xitu.app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.request.AgPersonRequest;
import com.xitu.app.common.request.AgTypeRequest;
import com.xitu.app.common.request.PatentPageListRequest;
import com.xitu.app.common.request.PriceAvgRequest;
import com.xitu.app.mapper.ElementMapper;
import com.xitu.app.mapper.PatentMapper;
import com.xitu.app.mapper.PriceMapper;
import com.xitu.app.model.Expert;
import com.xitu.app.model.Org;
import com.xitu.app.model.Patent;
import com.xitu.app.model.PatentMysql;
import com.xitu.app.model.Price;
import com.xitu.app.repository.ExpertRepository;
import com.xitu.app.repository.OrgRepository;
import com.xitu.app.repository.PatentRepository;
import com.xitu.app.service.es.PatentService;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class PatentController {

	private static final Logger logger = LoggerFactory.getLogger(PatentController.class);
	
	@Autowired
    private PatentRepository patentRepository;
	@Autowired
	private ExpertRepository expertRepository;
	@Autowired
	private OrgRepository orgRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private PriceMapper priceMapper;
	
	@Autowired
	private PatentMapper patentMapper;
	
	@Autowired
	private ElementMapper elementMapper;
	
	@Autowired
	private PatentService patentService;
	
//	// 代理隧道验证信息
//    final static String ProxyUser = "H677S6B336VV189D";
//    final static String ProxyPass = "8E65F1A7219B95AB";
//
//    // 代理服务器
//    final static String ProxyHost = "http-dyn.abuyun.com";
//    final static Integer ProxyPort = 9020;
//
//    // 设置IP切换头
//    final static String ProxyHeadKey = "Proxy-Switch-Ip";
//    final static String ProxyHeadVal = "yes";
    // 代理隧道验证信息
    final static String ProxyUser = "H1H17D0134OD999D";
    final static String ProxyPass = "2A44BD5B12A05398";
    
    // 代理服务器
    final static String ProxyHost = "http-dyn.abuyun.com";
    final static Integer ProxyPort = 9020;
    
    // 设置IP切换头
    final static String ProxyHeadKey = "Proxy-Switch-Ip";
    final static String ProxyHeadVal = "yes";
	
	
	@GetMapping(value = "patent/get")
	public String getPatent(@RequestParam(required=false,value="id") String id, Model model) {
		Patent patent = new Patent();
		if(id != null) {
			patent = patentRepository.findById(id).get();
		}
		model.addAttribute("patent", patent);
		return "result-zlCon";
	}
	
//	@RequestMapping(value = "patent/list")
//	public String patents(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="year") String year,
//			@RequestParam(required=false,value="ipc") String ipc,
//			@RequestParam(required=false,value="cpc") String cpc,
//			@RequestParam(required=false,value="person") String person,
//			@RequestParam(required=false,value="creator") String creator,
//			@RequestParam(required=false,value="lawstatus") String lawstatus,
//			@RequestParam(required=false,value="country") String country,
//			@RequestParam(required=false,value="pageSize") Integer pageSize, 
//			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
//			Model model) {
//		if(pageSize == null) {
//			pageSize = 10;
//		}
//		if(pageIndex == null) {
//			pageIndex = 0;
//		}
//		long totalCount = 0L;
//		long totalPages = 0L;
//		List<Patent> patentList = new ArrayList<Patent>();
//		String view = "result-zl";
//		if(esTemplate.indexExists(Patent.class)) {
//			if(q == null) {
//				totalCount = patentRepository.count();
//				if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Patent> patentsPage = patentRepository.search(searchQuery);
//					patentList = patentsPage.getContent();
//				}
//			}else {
//				// 分页参数
//				Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
//						.should(QueryBuilders.matchPhraseQuery("title", q))
//						.should(QueryBuilders.matchPhraseQuery("subject", q));
//				if(year != null) {
//					String[] years = year.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
//				}
//				if(ipc != null) {
//					String[] ipcs = ipc.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("ipc", ipcs));
//				}
//				if(cpc != null) {
//					String[] cpcs = cpc.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("cpc", cpcs));
//				}
//				if(person != null) {
//					String[] persons = person.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("person", persons));
//				}
//				if(lawstatus != null) {
//					String[] lawstatuses = lawstatus.split("%");
//					queryBuilder.filter(QueryBuilders.termsQuery("lawstatus", lawstatuses));
//				}
//				if(creator != null) {
//					String[] creators = creator.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("creator", creators));
//				}
//				if(country != null) {
//					String[] countries = country.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("country", countries));
//				}
//				
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Patent> searchPageResults = patentRepository.search(searchQuery);
//				patentList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Patent.class);
//				
//				
//				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery()
//						.should(QueryBuilders.matchQuery("title", q))
//						.should(QueryBuilders.matchQuery("subject", q));
//				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
//				List<String> pList=new ArrayList<>();
//				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//						.withQuery(functionScoreQueryBuilderAgg)
//						.withSearchType(SearchType.QUERY_THEN_FETCH)
//						.withIndices("patent").withTypes("pt")
//						.addAggregation(AggregationBuilders.terms("agyear").field("publicyear").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcpc").field("cpc").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcountry").field("country").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("aglawstatus").field("lawstatus").order(Terms.Order.count(false)).size(10))
//						.build();
//				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
//			        @Override
//			        public Aggregations extract(SearchResponse response) {
//			            return response.getAggregations();
//			        }
//			    });
//				
//				if(aggregations != null) {
//					StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agyear");
//					Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
//					Map<String, Long> yearMap = new HashMap<String, Long>();
//					while(yearbit.hasNext()) {
//						Bucket yearBucket = yearbit.next();
//						yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
//					}
//					model.addAttribute("agyear", yearMap);
//					
//					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
//					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
//					Map<String, Long> ipcMap = new HashMap<String, Long>();
//					while(ipcbit.hasNext()) {
//						Bucket ipcBucket = ipcbit.next();
//						ipcMap.put(ipcBucket.getKey().toString(), Long.valueOf(ipcBucket.getDocCount()));
//					}
//					model.addAttribute("agipc", ipcMap);
//					
//					StringTerms lawstatusTerms = (StringTerms) aggregations.asMap().get("aglawstatus");
//					Iterator<Bucket> lawstatusbit = lawstatusTerms.getBuckets().iterator();
//					Map<String, Long> lawstatusMap = new HashMap<String, Long>();
//					while(lawstatusbit.hasNext()) {
//						Bucket lawstatusBucket = lawstatusbit.next();
//						lawstatusMap.put(lawstatusBucket.getKey().toString(), Long.valueOf(lawstatusBucket.getDocCount()));
//					}
//					model.addAttribute("aglawstatus", lawstatusMap);
//					
//					StringTerms cpcTerms = (StringTerms) aggregations.asMap().get("agcpc");
//					Iterator<Bucket> cpcbit = cpcTerms.getBuckets().iterator();
//					Map<String, Long> cpcMap = new HashMap<String, Long>();
//					while(cpcbit.hasNext()) {
//						Bucket cpcBucket = cpcbit.next();
//						cpcMap.put(cpcBucket.getKey().toString(), Long.valueOf(cpcBucket.getDocCount()));
//					}
//					
//					model.addAttribute("agcpc", cpcMap);
//					
//					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
//					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
//					Map<String, Long> personMap = new HashMap<String, Long>();
//					while(personbit.hasNext()) {
//						Bucket personBucket = personbit.next();
//						personMap.put(personBucket.getKey().toString(), Long.valueOf(personBucket.getDocCount()));
//					}
//					model.addAttribute("agperson", personMap);
//					
//					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
//					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
//					Map<String, Long> creatorMap = new HashMap<String, Long>();
//					while(creatorbit.hasNext()) {
//						Bucket creatorBucket = creatorbit.next();
//						creatorMap.put(creatorBucket.getKey().toString(), Long.valueOf(creatorBucket.getDocCount()));
//					}
//					model.addAttribute("agcreator", creatorMap);
//					
//					StringTerms countryTerms = (StringTerms) aggregations.asMap().get("agcountry");
//					Iterator<Bucket> countrybit = countryTerms.getBuckets().iterator();
//					Map<String, Long> countryMap = new HashMap<String, Long>();
//					while(countrybit.hasNext()) {
//						Bucket countryBucket = countrybit.next();
//						countryMap.put(countryBucket.getKey().toString(), Long.valueOf(countryBucket.getDocCount()));
//					}
//					model.addAttribute("agcountry", countryMap);
//					
//				}
////				nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
////				nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
////				TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("aglist").field("list").order(Terms.Order.count(false)).size(10);
////				nativeSearchQueryBuilder.addAggregation(termsAggregation);
////		    	NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
////		    	Page<Project> search = projectRepository.search(nativeSearchQuery);
////		    	List<Project> content = search.getContent();
////		    	for (Project project : content) {
////		    		pList.add(esBlog.getUsername());
////				}
//				//totalPages = Math.round(totalCount/pageSize);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				
//				
//			}
//		}
//		model.addAttribute("patentList", patentList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
//			
//		return view;
//	}
	
	
	
	@GetMapping(value = "patent/transfer")
	@ResponseBody
	public R transferPaper() {
		int currentPage = 0;
		int pageSize = 0;
		int index = 0;
		for(int i=544;i<957;i++){
			
			currentPage = (currentPage <= 0) ?1:currentPage;
			pageSize = (pageSize<=0) ? 100:pageSize;
			index = (currentPage - 1) * pageSize;
			System.out.println(i);
			List<PatentMysql> patents = patentMapper.getPatents(index, pageSize);
			currentPage++;
			List<Patent> tobesaved = new LinkedList<Patent>();
			for(PatentMysql patentMysql: patents) {
				Patent patentES = new Patent();
				
				patentES.setId(patentMysql.getId()+"");
				
				if (patentMysql.getTitle() != null) {
					patentES.setTitle(patentMysql.getTitle().trim());
				}
				
				String subject = patentMysql.getSubject();
				if (subject != null) {
					patentES.setSubject(subject);
				}
				
				String person = patentMysql.getPerson();
				if (person != null && !person.trim().equals("")) {
					person = person.trim();
					
					List<String> personlist = new ArrayList<String>();
					if (person.contains(";")) {
						String[] persons = person.split(";");
						for(String s:persons){
							personlist.add(s);
						}
					}else{
						personlist.add(person);
					}
					patentES.setPerson(personlist);
				}
				
				String creator = patentMysql.getCreator();
				if (creator != null && !creator.trim().equals("")) {
					creator = creator.trim();
					
					List<String> creatorlist = new ArrayList<String>();
					if (creator.contains(";")) {
						String[] creators = creator.split(";");
						for(String s:creators){
							creatorlist.add(s);
						}
					}else{
						creatorlist.add(creator);
					}
					patentES.setCreator(creatorlist);
				}
				
				if (patentMysql.getApplytime() != null) {
					patentES.setApplytime(patentMysql.getApplytime().trim());
				}
				
				if (patentMysql.getPublictime() != null) {
					patentES.setPublictime(patentMysql.getPublictime().trim());
				}
				
				if (patentMysql.getApplyyear() != null) {
					patentES.setApplyyear(patentMysql.getApplyyear().trim());
				}
				
				if (patentMysql.getPublicyear() != null) {
					patentES.setPublicyear(patentMysql.getPublicyear().trim());
				}
				
				if (patentMysql.getPtype() != null) {
					patentES.setType(patentMysql.getPtype().trim());
				}
				
				if (patentMysql.getDescription() != null) {
					patentES.setDescription(patentMysql.getDescription().trim());
				}
				
				if (patentMysql.getClaim() != null) {
					patentES.setClaim(patentMysql.getClaim().trim());
				}
				
				if (patentMysql.getPublicnumber() != null) {
					patentES.setPublicnumber(patentMysql.getPublicnumber().trim());
				}
				
				if (patentMysql.getApplynumber() != null) {
					patentES.setApplynumber(patentMysql.getApplynumber().trim());
				}
				
				String ipc = patentMysql.getIpc();
				if (ipc != null && !ipc.trim().equals("")) {
					ipc = ipc.trim();
					
					List<String> ipclist = new ArrayList<String>();
					if (ipc.contains(";")) {
						String[] ipcs = ipc.split(";");
						for(String s:ipcs){
							ipclist.add(s);
						}
					}else{
						ipclist.add(ipc);
					}
					patentES.setIpc(ipclist);
				}
				
				if (patentMysql.getPiroryear() != null) {
					patentES.setPiroryear(patentMysql.getPiroryear().trim());
				}
				
				if (patentMysql.getCountry() != null) {
					patentES.setCountry(patentMysql.getCountry().trim());
				}
				
				if (patentMysql.getLawstatus() != null) {
					patentES.setLawstatus(patentMysql.getLawstatus().trim());
				}
				
				if (patentMysql.getNow() != null) {
					patentES.setNow(patentMysql.getNow());
				}
				tobesaved.add(patentES);
//				paperRepository.save(paperES);
			}
			patentRepository.saveAll(tobesaved);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return R.ok();
	}
	
	@RequestMapping(value = "patent/list")
	public String patents(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="person") String person,
			@RequestParam(required=false,value="creator") String creator,
			@RequestParam(required=false,value="publicyear") String publicyear,
			@RequestParam(required=false,value="ipc") String ipc,
			@RequestParam(required=false,value="country") String country,
			@RequestParam(required=false,value="lawstatus") String lawstatus,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("pageSize", pageSize);
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		patentService.execute(pageIndex, pageSize, i,q,person,creator,publicyear,ipc,country,lawstatus);
		ThreadLocalUtil.remove();
		String view = "result-zl";
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/pageList", method = RequestMethod.POST,consumes = "application/json")
	public R patentPageList(@RequestBody PatentPageListRequest request) {
		Integer pageSize = request.getPageSize();
		Integer pageIndex = request.getPageIndex();
		String year = request.getYear();
		String ipc = request.getIpc();
		String cpc = request.getCpc();
		String person = request.getPerson();
		String creator = request.getCreator();
		String country= request.getCountry();
		String q = request.getQ();
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		long totalCount = 0L;
		long totalPages = 0L;
		List<Patent> patentList = new ArrayList<Patent>();
		if(esTemplate.indexExists(Patent.class)) {
			if(q == null) {
				totalCount = patentRepository.count();
				if(totalCount >0) {
					Sort sort = new Sort(Direction.DESC, "now");
					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
					SearchQuery searchQuery = new NativeSearchQueryBuilder()
							.withPageable(pageable).build();
					Page<Patent> patentsPage = patentRepository.search(searchQuery);
					patentList = patentsPage.getContent();
					if(totalCount % pageSize == 0){
						totalPages = totalCount/pageSize;
					}else{
						totalPages = totalCount/pageSize + 1;
					}
				}
			}else {
				// 分页参数
				Pageable pageable = new PageRequest(pageIndex, pageSize);

				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
						.should(QueryBuilders.matchPhraseQuery("title", q))
						.should(QueryBuilders.matchPhraseQuery("subject", q));
				if(year != null) {
					String[] years = year.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
				}
				if(ipc != null) {
					String[] ipcs = ipc.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("ipc", ipcs));
				}
				if(cpc != null) {
					String[] cpcs = cpc.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("cpc", cpcs));
				}
				if(person != null) {
					String[] persons = person.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("person", persons));
				}
				if(creator != null) {
					String[] creators = creator.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("creator", creators));
				}
				if(country != null) {
					String[] countries = country.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("country", countries));
				}
				
				// 分数，并自动按分排序
				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));

				// 分数、分页
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
						.withQuery(functionScoreQueryBuilder).build();

				Page<Patent> searchPageResults = patentRepository.search(searchQuery);
				patentList = searchPageResults.getContent();
				totalCount = esTemplate.count(searchQuery, Patent.class);
				if(totalCount % pageSize == 0){
					totalPages = totalCount/pageSize;
				}else{
					totalPages = totalCount/pageSize + 1;
				}
				
				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery()
						.should(QueryBuilders.matchQuery("title", q))
						.should(QueryBuilders.matchQuery("subject", q));
				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
				List<String> pList=new ArrayList<>();
				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
						.withQuery(functionScoreQueryBuilderAgg)
						.withSearchType(SearchType.QUERY_THEN_FETCH)
						.withIndices("patent").withTypes("pt")
						.addAggregation(AggregationBuilders.terms("agyear").field("publicyear").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcpc").field("cpc").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcountry").field("country").order(Terms.Order.count(false)).size(10))
						.build();
				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
			        @Override
			        public Aggregations extract(SearchResponse response) {
			            return response.getAggregations();
			        }
			    });
				
				if(aggregations != null) {
					StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agyear");
					Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
					Map<String, Long> yearMap = new HashMap<String, Long>();
					while(yearbit.hasNext()) {
						Bucket yearBucket = yearbit.next();
						yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
					}
//					model.addAttribute("agyear", yearMap);
					
					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
					Map<String, Long> ipcMap = new HashMap<String, Long>();
					while(ipcbit.hasNext()) {
						Bucket ipcBucket = ipcbit.next();
						ipcMap.put(ipcBucket.getKey().toString(), Long.valueOf(ipcBucket.getDocCount()));
					}
//					model.addAttribute("agipc", ipcMap);
					
					StringTerms cpcTerms = (StringTerms) aggregations.asMap().get("agcpc");
					Iterator<Bucket> cpcbit = cpcTerms.getBuckets().iterator();
					Map<String, Long> cpcMap = new HashMap<String, Long>();
					while(cpcbit.hasNext()) {
						Bucket cpcBucket = cpcbit.next();
						cpcMap.put(cpcBucket.getKey().toString(), Long.valueOf(cpcBucket.getDocCount()));
					}
					
//					model.addAttribute("agcpc", cpcMap);
					
					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
					Map<String, Long> personMap = new HashMap<String, Long>();
					while(personbit.hasNext()) {
						Bucket personBucket = personbit.next();
						personMap.put(personBucket.getKey().toString(), Long.valueOf(personBucket.getDocCount()));
					}
//					model.addAttribute("agperson", personMap);
					
					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
					Map<String, Long> creatorMap = new HashMap<String, Long>();
					while(creatorbit.hasNext()) {
						Bucket creatorBucket = creatorbit.next();
						creatorMap.put(creatorBucket.getKey().toString(), Long.valueOf(creatorBucket.getDocCount()));
					}
//					model.addAttribute("agcreator", creatorMap);
					
					StringTerms countryTerms = (StringTerms) aggregations.asMap().get("agcountry");
					Iterator<Bucket> countrybit = countryTerms.getBuckets().iterator();
					Map<String, Long> countryMap = new HashMap<String, Long>();
					while(countrybit.hasNext()) {
						Bucket countryBucket = countrybit.next();
						countryMap.put(countryBucket.getKey().toString(), Long.valueOf(countryBucket.getDocCount()));
					}
//					model.addAttribute("agcountry", countryMap);
					
				}
//				nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
//				nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
//				TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("aglist").field("list").order(Terms.Order.count(false)).size(10);
//				nativeSearchQueryBuilder.addAggregation(termsAggregation);
//		    	NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
//		    	Page<Project> search = projectRepository.search(nativeSearchQuery);
//		    	List<Project> content = search.getContent();
//		    	for (Project project : content) {
//		    		pList.add(esBlog.getUsername());
//				}
				totalPages = Math.round(totalCount/pageSize);
				
				
			}
		}
//		model.addAttribute("patentList", patentList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
			
		return R.ok().put("patentList", patentList).put("pageIndex", pageIndex);
	}
	
	
	
	@GetMapping(value = "patent/analysis")
	public String analysis() {
		return "zhuanlifenxifamingrenjizhuanliquanren";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/agpersons", method = RequestMethod.POST,consumes = "application/json")
	public R persons(@RequestBody AgPersonRequest request) {
		List<List<Object>> personList = new ArrayList<List<Object>>();
//		String view = "zhuanlifenxifamingrenjizhuanliquanren";
		String person = request.getPerson();
		String creator = request.getCreator();
		if(esTemplate.indexExists(Patent.class)) {
				
			MatchAllQueryBuilder queryBuilderAgg = QueryBuilders.matchAllQuery();
			FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
					.withQuery(functionScoreQueryBuilderAgg)
					.withSearchType(SearchType.QUERY_THEN_FETCH)
					.withIndices("patent").withTypes("pt");
			if(person != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(true)).size(10));
			}
			if(creator != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(true)).size(10));
			}
			Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder.build(), new ResultsExtractor<Aggregations>() {
				@Override
				public Aggregations extract(SearchResponse response) {
					return response.getAggregations();
				}
			});
			
			if(aggregations != null) {
				if(person != null) {
					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
					List<Object> titleList = new ArrayList<Object>();
					titleList.add("score");
					titleList.add("amount");
					titleList.add("product");
					personList.add(titleList);
					while(personbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket personBucket = personbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(personBucket.getDocCount());
						list.add(personBucket.getKey().toString());
						personList.add(list);
					}
				}
				
				if(creator != null) {
					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
					while(creatorbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket creatorBucket = creatorbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(creatorBucket.getDocCount());
						list.add(creatorBucket.getKey().toString());
						personList.add(list);
					}
				}
			}
		}
		
		return R.ok().put("agpersons", personList);
	}
	
	
	@GetMapping(value = "patent/agtype")
	public String agtype() {
		return "zhuanlifenxizhuanlileixing";
	}
	@GetMapping(value = "patent/agmount")
	public String agmount(Model model) {
		String time = priceMapper.getLatestUpdateTime();
		if(time != null) {
			List<Price> prices = priceMapper.getPricesByUpdateTime(time);
			model.addAttribute("prices", prices);
		}else {
			model.addAttribute("prices", new ArrayList<String>());
		}
		List<String> items = priceMapper.getPricesGroupByName();
		model.addAttribute("items", items);
		return "zhuanlifenxizhuanlishenqingliang";
	}
	@GetMapping(value = "patent/agcountry")
	public String agcountry() {
		return "zhuanlifenxizhuanlishenqingguo";
	}
	@GetMapping(value = "patent/agpeople")
	public String agpeople() {
		return "zhuanlifenxifamingrenjizhuanliquanren";
	}
	@GetMapping(value = "patent/agpeoplecon")
	public String agpeoplecon() {
		return "T-hangyeCon";
	}
	
	@GetMapping(value = "patent/agclassis")
	public String agclassis() {
		return "zhuanlifenxijishufenlei";
	}
	
	@GetMapping(value = "patent/agclassiscon")
	public String agclassiscon() {
		return "T-jigouCon";
	}
	
	@GetMapping(value = "patent/agtypecon")
	public String agtypecon() {
		return "T-rencaiCon";
	}
	
	
	
	@GetMapping(value = "price")
	public void price() {
		

		for(int i=7;i>0;i--) {
			String url="http://nm.sci99.com/news/?page=" + i + "&sid=8784&siteid=10" ;
			String base = "http://nm.sci99.com";
			try {
	        	
//	        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
	        	
//	        	if(exist != null) {
//	        		return;
//	        	}

	            Document doc = Jsoup.connect(url).get();

	            Elements module = doc.getElementsByClass("ul_w690");

	            Document moduleDoc = Jsoup.parse(module.toString());

	            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式

	            Map< String, String> urls= new TreeMap<String, String>();
	            for (Element li : lis){
	                Document liDoc = Jsoup.parse(li.toString());
	                Elements hrefs = liDoc.select("a[href]");
	                for(Element elem: hrefs) {
	                	System.out.println(elem.text().substring(elem.text().length()-9,elem.text().length()-1));
	                	
	                	if(!"".equals(elem.attr("href"))){
	                		String href = elem.attr("href");
	                		urls.put(base + href, elem.text().substring(elem.text().length()-9,elem.text().length()-1));
	                	}
	                }

	            }
	            for(Map.Entry<String, String> entry: urls.entrySet()) {
	            	
	            	Document singleDoc = Jsoup.connect(entry.getKey()).get();
//	            if(!singleDoc.toString().contains(ymd)){
//	            	return;
//	            }
	            	Element zoom = singleDoc.getElementById("zoom");
	            	Elements trElements = zoom.select("tr");
	            	boolean ignore = true;
	            	for(Element tdelement : trElements) {
	            		if(ignore) {
	            			ignore = false;
	            			continue;
	            		}
	            		Elements tdes = tdelement.select("td");
	            		System.out.println(tdes.size());
	            		Price price = new Price();
	            		if(tdes.size()==7) {
	            			price.setUpdateTime(entry.getValue());
	            			price.setName(tdes.get(0).text());
	            			price.setDescription(tdes.get(1).text());
	            			price.setUnit(tdes.get(6).text());
	            			price.setPrice(tdes.get(3).text());
	            			price.setAvg(tdes.get(4).text());
	            			price.setFloating(tdes.get(5).text());
	            		}else {
	            			price.setUpdateTime(entry.getValue());
	            			price.setName(tdes.get(0).text());
	            			price.setDescription(tdes.get(1).text());
	            			price.setUnit(tdes.get(7).text());
	            			price.setPrice(tdes.get(3).text()+"-"+tdes.get(4).text());
	            			price.setAvg(tdes.get(5).text());
	            			price.setFloating(tdes.get(6).text());
	            			
	            		}
//	            		Price yesterday = priceMapper.getLatestPrice(price.getName());
//	            		if(yesterday == null) {
//	            			price.setFloating("100%");
//	            		}else {
//	            			float before = Float.valueOf(yesterday.getAvg());
//	            			float now = Float.valueOf(tdes.get(4).text());
//	            			float delta = now - before;
//	            			if(delta != 0) {
//	            				System.out.println();
//	            			}
//	            			NumberFormat numberFormat = NumberFormat.getInstance();
//	            			numberFormat.setMaximumFractionDigits(2);
//	            			String result = numberFormat.format(delta / before * 100);
//	            			price.setFloating(result + "%");
//	            		}
	            		priceMapper.insertPrice(price);
	            		try {
	        				Thread.sleep(1000);
	        			} catch (InterruptedException e) {
	        				// TODO Auto-generated catch block
	        				e.printStackTrace();
	        			}
	            		System.out.println("插入成功" + price.getName());
	            	}
	            	//  String title = clearfixli.getElementsByTag("a").text();
	            	System.out.println("fasdf");
	            }


	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		final String url="http://nm.sci99.com/news/s8784.html" ;
//		String single = "http://nm.sci99.com";
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//		Date date = new Date();
//		String ymd = formatter.format(date);
//
//        try {
//        	
//        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
//        	
////        	if(exist != null) {
////        		return;
//////        		System.out.println("exist");
////        	}
//
//            Document doc = Jsoup.connect(url).get();
//
//            Elements module = doc.getElementsByClass("ul_w690");
//            if(!module.text().contains(ymd)) {
//            	return;
////            	System.out.println("");
//            }
//            System.out.println(module.text());
//
//            Document moduleDoc = Jsoup.parse(module.toString());
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
////            if(!singleDoc.toString().contains(ymd)){
////            	return;
////            }
//            Element zoom = singleDoc.getElementById("zoom");
//            Elements trElements = zoom.select("tr");
//            boolean ignore = true;
//            for(Element tdelement : trElements) {
//            	if(ignore) {
//            		ignore = false;
//            		continue;
//            	}
//            	Elements tdes = tdelement.select("td");
//            	Price price = new Price();
//            	price.setUpdateTime(formatter.format(date));
//        		price.setName(tdes.get(0).text());
//        		price.setDescription(tdes.get(1).text());
//        		price.setUnit(tdes.get(6).text());
//        		price.setPrice(tdes.get(3).text());
//        		price.setAvg(tdes.get(4).text());
//        		Price yesterday = priceMapper.getLatestPrice(price.getName());
//        		if(yesterday == null) {
//        			price.setFloating("100%");
//        		}else {
//        			float before = Float.valueOf(yesterday.getAvg());
//        			float now = Float.valueOf(tdes.get(4).text());
//        			float delta = now - before;
//        			if(delta != 0) {
//        				System.out.println();
//        			}
//        			NumberFormat numberFormat = NumberFormat.getInstance();
//        			numberFormat.setMaximumFractionDigits(2);
//        			String result = numberFormat.format(delta / before * 100);
//        			price.setFloating(result + "%");
//        		}
////            	priceMapper.insertPrice(price);
//            }
//              //  String title = clearfixli.getElementsByTag("a").text();
//            System.out.println("fasdf");
//
//          //  System.out.println(clearfix);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		return "T-rencaiCon";
	}
	
	@ResponseBody
	@RequestMapping(value = "price/avg", method = RequestMethod.POST,consumes = "application/json")
	public R priceAvg(@RequestBody PriceAvgRequest request) {
		String time = request.getTime();
		String name = request.getName();
		Map<String, String> map = new HashMap<String, String>();
		String date = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if("3".equals(time)) {
			for(int i=0;i<3;i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.MONTH, -i);
				Date lastmonth = c.getTime();
				date = formatter.format(lastmonth);
				String start = date.substring(0, 6) + "00";
				String end = date.substring(0,6) + "32";
				String avg = priceMapper.getAvgPricesGroupByName(start, end , name);
				if(avg == null) {
					avg = "0";
				}else if(avg.contains(".")) {
					avg = avg.split("\\.")[0];
				}
				map.put(date.substring(4, 6), avg);
				
			}
		}else if("1".equals(time)) {
			for(int i=0;i<12;i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.MONTH, -i);
				Date lastmonth = c.getTime();
				date = formatter.format(lastmonth);
				String start = date.substring(0, 6) + "00";
				String end = date.substring(0,6) + "32";
				String avg = priceMapper.getAvgPricesGroupByName(start, end , name);
				if(avg == null) {
					continue;
//					avg = "0";
				}else if(avg.contains(".")) {
					avg = avg.split("\\.")[0];
				}
				map.put(date.substring(4, 6), avg);
				
			}
			
		}
		return R.ok().put("avg", map);
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/agtypes", method = RequestMethod.POST,consumes = "application/json")
	public R types(@RequestBody AgTypeRequest request) {
		List<List<Object>> typeList = new ArrayList<List<Object>>();
//		String view = "zhuanlifenxifamingrenjizhuanliquanren";
		String type = request.getType();
		String trend = request.getTrend();
		if(esTemplate.indexExists(Patent.class)) {
				
			MatchAllQueryBuilder queryBuilderAgg = QueryBuilders.matchAllQuery();
			FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
					.withQuery(functionScoreQueryBuilderAgg)
					.withSearchType(SearchType.QUERY_THEN_FETCH)
					.withIndices("patent").withTypes("pt");
			if(type != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(true)).size(10));
			}
			if(trend != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(true)).size(10).subAggregation(AggregationBuilders.terms("agyear").field("year").order(Terms.Order.count(true)).size(10)));
			}
			Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder.build(), new ResultsExtractor<Aggregations>() {
				@Override
				public Aggregations extract(SearchResponse response) {
					return response.getAggregations();
				}
			});
			
			if(aggregations != null) {
				if(type != null) {
					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
					List<Object> titleList = new ArrayList<Object>();
					titleList.add("score");
					titleList.add("amount");
					titleList.add("product");
					typeList.add(titleList);
					while(ipcbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket ipcBucket = ipcbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(ipcBucket.getDocCount());
						list.add(ipcBucket.getKey().toString());
						typeList.add(list);
					}
				}
				
				if(trend != null) {
//					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
//					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
//					while(creatorbit.hasNext()) {
//						List<Object> list = new ArrayList<Object>();
//						Bucket creatorBucket = creatorbit.next();
//						list.add((int)(Math.random()*90)+10);
//						list.add(creatorBucket.getDocCount());
//						list.add(creatorBucket.getKey().toString());
//						personList.add(list);
//					}
				}
			}
		}
		
		return R.ok().put("agpersons", typeList);
	}
	
	
	
	
	
	
	private String getLastMonth(int month){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)-month); //减少一月
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
	
	private List<String> extractMessageByRegular(String msg){
		
		List<String> list=new ArrayList<String>();
		Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
		Matcher m = p.matcher(msg);
		while(m.find()){
			list.add(m.group().substring(1, m.group().length()-1));
		}
		return list;
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/patentInsList", method = RequestMethod.POST,consumes = "application/json")
	public R expertInsList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = patentService.executeIns(insname.getString("insname"),pageIndex, pageSize, "person",0);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
	@ResponseBody
	@RequestMapping(value = "patent/patentExpList", method = RequestMethod.POST,consumes = "application/json")
	public R expertpatentList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = patentService.executeIns(insname.getString("insname"),pageIndex, pageSize, "creator",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
	@GetMapping(value = "patent/travel")
	public String updatePatent() {
		Iterator<Patent> patents = patentRepository.findAll().iterator();
//		Iterator<Org> orgs = orgRepository.findAll().iterator();
//		Iterator<Expert> experts = expertRepository.findAll().iterator();
		Map<String, Expert> eMap = new HashMap<String, Expert>();
		Map<String, Org> oMap = new HashMap<String, Org>();
//		while(experts.hasNext()) {
//			Expert e = experts.next();
//			String ren = e.getName();
//			String jigou = e.getUnit();
//			eMap.put(ren + "%" + jigou, e);
//		}
//		while(orgs.hasNext()) {
//			Org o = orgs.next();
//			String jigou = o.getName();
//			oMap.put(jigou, o);
//		}
//		List<ElementMaster> masters = elementMapper.selectAllMasters();
//		List<ElementSlave> slaves = elementMapper.selectAllSlaves();
//		Map<String, Set<String>> expertMap = new HashMap<String, Set<String>>();
//		Map<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
		int i=0;
		while(patents.hasNext()) {
			System.out.println(i);
			Set<String> tags = new HashSet<String>();
			Patent patent = patents.next();
			List<String> rens = patent.getCreator();
			List<String> jigous = patent.getPerson();
			
//			String title = patent.getTitle();
//			String subject = patent.getSubject();
//			for(ElementMaster master: masters) {
//				if( title.contains( master.getName())) {
//					tags.add(master.getName());
//				}else if(subject.contains(master.getName())) {
//					tags.add(master.getName());
//				}
//			}
			
//			for(ElementSlave slave: slaves) {
//				if( title.contains( slave.getName())) {
//					tags.add(slave.getName());
//				}else if(subject.contains(slave.getName())) {
//					tags.add(slave.getName());
//				}
//			}
			
			for(String jigou: jigous) {
				for(String ren: rens) {
					String key = ren + "%" + jigou;
					if (eMap.containsKey(key)) {
//						Expert expert = eMap.get(key);
//						List<String> expertTags = expert.getTags();
//						for(String tag : tags) {
//							if(expertTags == null) {
//								expertTags = new ArrayList<String>();
//							}
//							if(!expertTags.contains(tag)) {
//								expertTags.add(tag);
//							}
//						}
//						expertRepository.save(expert);
					}else {
//						expertMap.put(key, tags);
					}
				}
				if(oMap.containsKey(jigou)) {
					Org org = oMap.get(jigou);
					List<String> orgTags = org.getTags();
					for(String tag: tags) {
						if(orgTags == null) {
							orgTags = new ArrayList<String>();
						}
						if(!orgTags.contains(tag)) {
							orgTags.add(tag);
						}
					}
					orgRepository.save(org);
				}else {
//					orgMap.put(jigou, tags);
				}
			}
			i++;
		}
//		for(Map.Entry<String, Set<String>> entry: expertMap.entrySet()) {
//			String key = entry.getKey();
//			String[] eo = key.split("%");
//			Expert e = new Expert();
//			e.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
//			e.setName(eo[0]);
//			e.setAnotherName(eo[0]);
//			e.setNow(System.currentTimeMillis());
//			e.setUnit(eo[1]);
//			e.setTop("0");
//			List<String> tags = new ArrayList<String>();
//			tags.addAll(entry.getValue());
//			e.setTags(tags);
//			expertRepository.save(e);
//		}
		
//		for(Map.Entry<String, Set<String>> entry: orgMap.entrySet()) {
//			if(entry.getValue()!= null) {
//				System.out.println();
//			}
//			String key = entry.getKey();
//			List<String> tags = new ArrayList<String>();
//			tags.addAll(entry.getValue());
//			Org o = new Org();
//			o.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
//			o.setName(key);
//			o.setTags(tags);
//			o.setNow(System.currentTimeMillis());
//			o.setAnotherName(key);
//			o.setTop("0");
//			orgRepository.save(o);
//		}
		return "bsdf";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "patent/fetch/proxy/local", method = RequestMethod.GET)
	public R fetchProxyLocal(@RequestParam(required=false,value="interval") Integer interval,
			@RequestParam(required=false,value="patentIndex") Integer patentIndex,
			@RequestParam(required=false,value="tail") Integer tail) {
		String[] url={"http://www.soopat.com/Home/Result","http://www1.soopat.com/Home/Result", "http://www2.soopat.com/Home/Result"};
		String[] base = {"http://www.soopat.com","http://www1.soopat.com", "http://www2.soopat.com"};
		List<String> missedList = new ArrayList<String>();
		Random random = new Random();
		Map<String, String> map = new HashMap<String, String>();
		int month = 0;
		while(month<=5) {
//			if(month==10) {
//				System.out.println();
//			}
			System.out.println("已经到了"+month);
			String date = getLastMonth(month);
//			map.put("SearchWord", "(ZY:( 生物医药 ) OR MC:( 生物医药 ) OR SMS:(生物医药)) AND GKRQ:(" + date + ")");
//			map.put("SearchWord", "(ZY:( 医疗器械 ) OR MC:( 医疗器械 ) OR SMS:(医疗器械) OR QLYQ (医疗器械)) AND GKRQ:( " + date + " )");
			map.put("SearchWord", "(ZY:( 新松医疗 ) OR MC:( 新松医疗 ) OR SMS:(新松医疗) OR QLYQ (新松医疗))");
//		map.put("SearchWord", "稀土");
			map.put("FMZL", "Y");
			map.put("SYXX", "Y");
			map.put("WGZL", "Y");
			map.put("FMSQ", "Y");
			List<Patent> patents = new LinkedList<Patent>();
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(ProxyUser, ProxyPass.toCharArray());
				}
			});
			
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHost, ProxyPort));
			int out=0;
			boolean cleaning = false;
			patentIndex = 10;
			tail = 20;
			for(;patentIndex<tail;){
				System.out.println("正在执行的date---------->" + date);
				if(patents.size() >= 1) {
					patentRepository.saveAll(patents);
					patents.clear();
				}
				// TODO
				boolean retry = true;
				System.out.println("开启新的一页------>patentIndex---->" + patentIndex );
				
				Map<String,String> innerPathMap = new HashMap<String,String>();
				if(missedList.size() > 0) {
					for(String s: missedList) {
						String[] str = s.split("%");
						innerPathMap.put(str[0], str[1]);
					}
					missedList.clear();
				}
				map.put("PatentIndex", String.valueOf(patentIndex));
				patentIndex += 10;
				
				try {
					
					// 看是否是在清扫
					if(!cleaning) {
						
						out++;
						String pageUrl = url[out%3];
						System.out.println("pageUrl is ->" + pageUrl);
						Connection conn = Jsoup.connect(pageUrl);
						conn.data(map);
						Document doc = conn.timeout(5000).header(ProxyHeadKey, ProxyHeadVal).proxy(proxy).get();
//					Document doc = conn.get();
//					System.out.println(doc.toString());
						if(doc.toString().contains("请按图片上的要求依次点击图片上对应的字符")) {
							System.out.println("已被拦截，当前PatentIndex为"+ (patentIndex-10) + "手动干预后放开断点，并继续执行");
//					System.out.println("当前被封ip--》" + System.getProperties().getProperty("http.proxyHost") + System.getProperties().getProperty("http.proxyPort"));
							System.out.println("正在尝试的url是 " + url[out%3]);
							doc = conn.timeout(5000).header(ProxyHeadKey, ProxyHeadVal).proxy(proxy).get();
//						doc = conn.get();
//							if(doc.toString().contains("请按图片上的要求依次点击图片上对应的字符")) {
//								
//							}
							
						}
						
						retry = false;
//				System.out.println(doc.toString());
						Elements patentBlocks = doc.getElementsByClass("PatentBlock");
						if(doc.toString().contains("没有搜索到相关专利，原因可能是")) {
							System.out.println(date + "没有搜索到相关专利");
							break;
						}
						if(patentBlocks.size()>0) {
							Elements right = doc.getElementsByClass("right");
//							System.out.println(right.text());
							s:
								for(Element e: right) {
									Elements ele = e.getElementsByTag("b");
									for(Element elet: ele) {
										if(!"".equals(elet.text())) {
											tail = Integer.valueOf(elet.text());
											if(tail > 1000) {
												tail = 1000;
											}
										}
										break s;
									}
//									System.out.println(e.text());
								}
							for(Element patentBlock: patentBlocks) {
								Document patentDoc = Jsoup.parse(patentBlock.toString());
								Elements patentTypeElements = patentDoc.getElementsByClass("PatentTypeBlock");
								if(patentTypeElements.size() == 0) {
									continue;
								}
								jump:
									for(Element pte: patentTypeElements) {
										System.out.println(pte.text());
										String type = extractMessageByRegular(pte.text()).get(0);
										Document pteDoc = Jsoup.parse(pte.toString());
										Elements hrefs = pteDoc.select("a[href]");
										for(Element elem: hrefs) {
											if(!"".equals(elem.attr("href"))){
												String href = elem.attr("href");
												innerPathMap.put(href, type);
												break jump;
											}
										}
									}
							}
						}else {
							patentIndex -= 10;
							retry = true;
						}
					}
					
					
//				int max=5000;
//				int min=2000;
					
					int in=0;
					for(Map.Entry<String, String> entry: innerPathMap.entrySet()) {
//					int sleep = random.nextInt(max)%(max-min+1) + min;
//					System.out.println("休眠" + sleep + "毫秒");
//					try {
//						Thread.sleep(sleep);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
						Patent patent = new Patent();
						PatentMysql patentMysql = new PatentMysql();
						String singleUrl = base[in%3] + entry.getKey();
						in++;
						
						System.out.println("正在调用-------------------------->" + entry.getKey());
						Connection singlePageConn = Jsoup.connect(singleUrl);
						try {
							System.out.println("正在执行的date---------->" + date);
							Document singleDoc = singlePageConn.timeout(3000).header(ProxyHeadKey, ProxyHeadVal).proxy(proxy).get();
//						Document singleDoc = singlePageConn.get();
							System.out.println("成功调用-------------------->" + entry.getKey());
							System.out.println("******************" + singleDoc.toString());
							System.out.println("当前的month是--------------->" + month);
							if(singleDoc.toString().contains("请按图片上的要求依次点击图片上对应的字符")) {
								java.awt.Toolkit.getDefaultToolkit().beep();
								System.out.println("已被拦截，当前PatentIndex为"+ (retry==true?patentIndex:(patentIndex-10)) + "手动干预后放开断点，并继续执行");
								System.out.println("正在尝试的url是 " + singleUrl);
								
								// retry
								singleDoc = singlePageConn.timeout(3000).header(ProxyHeadKey, ProxyHeadVal).proxy(proxy).get();
//							singleDoc = singlePageConn.get();
								
								
							}
							System.out.println("******** step1");
							Elements h1Elements = singleDoc.getElementsByTag("h1");
							if(h1Elements==null || h1Elements.size()==0) {
								missedList.add(entry.getKey() + "%" + entry.getValue());
								System.out.println("未获取block，进入补偿队列的是---->" + entry.getKey() + "%" + entry.getValue());
								continue;
							}
							System.out.println("********** step2");
							for(Element h1Element: h1Elements) {
								String title = h1Element.text();
								if(title != null) {
									patent.setType(entry.getValue());
									patent.setTitle(title.split(" ")[0]);
									String lawStatus = title.split(" ")[1];
									if("".equals(lawStatus)) {
										System.out.println("法律状态异常");
										patent.setLawstatus("未知");
									}
									System.out.println(lawStatus);
									patent.setLawstatus(title.split(" ")[1]);
									patentMysql.setPtype(entry.getValue());
									patentMysql.setTitle(title.split(" ")[0]);
									patentMysql.setLawstatus(title.split(" ")[1]);
									break;
								}
							}
							System.out.println("正在执行的date---------->" + date);
							System.out.println("************ step3");
							Elements grayElements = singleDoc.getElementsByClass("gray");
							for(Element grayElement: grayElements) {
								String appliance = grayElement.text();
								if(appliance != null) {
									String[] s = appliance.split(" ");
									if(s.length>=2) {
										patent.setApplynumber(s[0].substring(4, s[0].length()));
//									patent.setId(s[0].substring(4, s[0].length()));
										patentMysql.setApplynumber(s[0].substring(4, s[0].length()));
										String applytime = s[1].substring(4, s[1].length());
										patent.setApplytime(applytime);
										patentMysql.setApplytime(applytime);
										patent.setApplyyear(applytime.substring(0, 4));
										patentMysql.setApplyyear(applytime.substring(0, 4));
										break;
									}
								}
							}
							
							Elements datainfoElements = singleDoc.getElementsByClass("datainfo");
							for(Element dataInfo : datainfoElements) {
								Elements tdelements = dataInfo.getElementsByTag("td");
								for(Element td: tdelements) {
									if(td.text().contains("摘要：")){
										patent.setSubject(td.text());
										patentMysql.setSubject(td.text());
									}else if(td.text().contains("申请人：")){
										List<String> persons = new ArrayList<String>();
										String personstr = td.text().replace("申请人：", "").trim();
										String[] personArray = personstr.split(" ");
										for(String s: personArray) {
											persons.add(s);
										}
										patentMysql.setPerson(personstr.replace(" ", ";"));
										patent.setPerson(persons);
									}else if(td.text().contains("发明(设计)人：")) {
										List<String> creators = new ArrayList<String>();
										String creatorstr = td.text().replace("发明(设计)人：", "").trim();
										String[] creatorArray = creatorstr.split(" ");
										for(String s: creatorArray) {
											creators.add(s);
										}
										patentMysql.setCreator(creatorstr.replace(" ", ";"));
										patent.setCreator(creators);
									}else if(td.text().contains("分类号：") && (!td.text().contains("主分类号："))) {
										List<String> ipcs = new ArrayList<String>();
										String ipc = td.text().replace("分类号：", "").trim();
										String[] ipcArray = ipc.split(" ");
										for(String s: ipcArray) {
											ipcs.add(s);
										}
										patentMysql.setIpc(ipc.replace(" ", ";"));
										patent.setIpc(ipcs);
									}
									System.out.println(td.text());
								}
							}
							Elements vipcomElements = singleDoc.getElementsByClass("vipcom");
							for(Element vipcom: vipcomElements) {
								String s = vipcom.toString();
								if(s.contains("其他信息")) {
									int i = 0;
									Elements tdelements = vipcom.getElementsByTag("td");
									for(Element td: tdelements) {
										if(i==1) {
											String claim = td.text().length()>2000?td.text().substring(0,2000):td.text();
											patent.setClaim(claim);
											patentMysql.setClaim(td.text());
										}else if(i==4) {
											patent.setPublicnumber(td.text());
											patentMysql.setPublicnumber(td.text());
										}else if(i==7) {
											String publictime = td.text().replace("&nbsp;", "").trim();
											patent.setPublictime(publictime);
											patentMysql.setPublictime(publictime);
											if(publictime.contains("-")) {
												patent.setPublicyear(publictime.split("-")[0]);
												patentMysql.setPublicyear(publictime.split("-")[0]);
											}
										}else if(i==19) {
											System.out.println("priority-----" + td.text());
											patent.setPiroryear(td.text().equals("&nbsp;")?"":td.text());
											patentMysql.setPiroryear(td.text().equals("&nbsp;")?"":td.text());
										}
										i++;
									}
								}
							}
							System.out.println("****************last step");
							patent.setId(UUID.randomUUID().toString());
							patent.setCountry("中国");
							patent.setNow(System.currentTimeMillis());
							patentMysql.setCountry("中国");
//						patentMysql.setNow(System.currentTimeMillis());
							System.out.println("开始插入mysql");
							patentMapper.insertPatent(patentMysql);
							System.out.println("结束插入mysql");
							System.out.println("开始插入es");
//							patentRepository.save(patent);
							System.out.println("结束插入es");
							patents.add(patent);
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println("出现异常，进入补偿队列的是---->" + entry.getKey() + "%" + entry.getValue());
							missedList.add(entry.getKey() + "%" + entry.getValue());
						}
					}
					
					System.out.println("fasdf");
					
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("当前month是：" + month);
					System.out.println("当前pageindex是：" +patentIndex);
					if(retry) {
						patentIndex -= 10;
					}
					// 将补偿队列还原回来
					for(Map.Entry<String, String> entry: innerPathMap.entrySet()) {
						missedList.add(entry.getKey() + "%" + entry.getValue());
					}
					if(patents.size()>10) {
						patentRepository.saveAll(patents);
						patents.clear();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				// 末轮清空补偿队列
				if(patentIndex >= tail && (!missedList.isEmpty())) {
					patentIndex -= 10;
					cleaning = true;
				}
			}
			if(patents.size() > 0) {
				patentRepository.saveAll(patents);
			}
			System.out.println(date + "这一轮全部结束");
			month++;
		}
		return R.ok();
	}
	
	
	
	@GetMapping(value = "patent/eout")
	public String eout() {
		Iterator<Patent> patents = patentRepository.findAll().iterator();
		Set<String> eos = new HashSet<String>();
		Set<String> os= new HashSet<String>();
		int i=0;
		while(patents.hasNext()) {
			System.out.println(i);
			Patent patent = patents.next();
//			if(patent.getCountry() == null || patent.getCountry().contains("中国")) {
//				continue;
//			}
			List<String> rens = patent.getCreator();
			List<String> jigous = patent.getPerson();
			
			if(jigous == null || rens==null) {
				continue;
			} 
			for(String jigou: jigous) {
				for(String ren: rens) {
					String key = ren + "," + jigou;
					if(!eos.contains(key)) {
						eos.add(key);
					}
				}
				if(!os.contains(jigou)) {
					os.add(jigou);
				}
			}
			i++;
		}
		File file1 = new File("/Users/liubingchuan/zhuanjia_jilin.csv");
		File file2 = new File("/Users/liubingchuan/jigou_jilin.csv");
		exportCsv(file1, eos);
		exportCsv(file2, os);
		
		return "bsdf";
	}
	
	
	
	
	
	public boolean exportCsv(File file, Set<String> dataList){
		boolean isSucess=false;
		FileOutputStream out=null;
		OutputStreamWriter osw=null;
		BufferedWriter bw=null;
		try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
		return isSucess;
    }

	
	public String ndayago(int i) {
		  Calendar calendar1 = Calendar.getInstance();
		  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		  calendar1.add(Calendar.DATE, -i);
		  String three_days_ago = sdf1.format(calendar1.getTime());
		  System.out.println(three_days_ago);
		  return three_days_ago;
	}
	

	public int count(String str) { 
		if(str == null || str.length() == 0) { 
			return 0; 
		} 
		int count = 0; 
		char[] chs = str.toCharArray(); 
		for(int i = 0; i < chs.length; i++) {
			count += (chs[i] > 0xff) ? 2 : 1; 
			}
		    return count; 
		}
	public String getDate(String text) { 
        String dateStr = text.replaceAll("r?n", " ");
        try { 
            List matches = null; 
            Pattern p = Pattern.compile("(\\d{1,4}[-|\\/]\\d{1,2}[-|\\/]\\d{1,2})", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE); 
            Matcher matcher = p.matcher(dateStr); 
            if (matcher.find() && matcher.groupCount() >= 1) { 
                matches = new ArrayList(); 
                for (int i = 1; i <= matcher.groupCount(); i++) { 
                    String temp = matcher.group(i); 
                    matches.add(temp); 
                } 
            } else { 
                matches = Collections.EMPTY_LIST; 
            }            
            
            if (matches.size() > 0) { 
                return ((String) matches.get(0)).trim(); 
            } else { 
                return ""; 
            } 
            
        } catch (Exception e) { 
            return ""; 
        } 
    }
	
}
