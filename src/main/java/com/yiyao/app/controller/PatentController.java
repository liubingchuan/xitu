package com.yiyao.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.yiyao.app.common.R;
import com.yiyao.app.common.request.AgPersonRequest;
import com.yiyao.app.common.request.AgTypeRequest;
import com.yiyao.app.common.request.RegisterRequest;
import com.yiyao.app.model.Paper;
import com.yiyao.app.model.Patent;
import com.yiyao.app.repository.PatentRepository;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class PatentController {

	private static final Logger logger = LoggerFactory.getLogger(PatentController.class);
	
	@Autowired
    private PatentRepository patentRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
//	@PostMapping(value = "papepr/save")
//	public String savePaper(SavePaperRequest request,Model model) {
//		
//		Paper paper = new Project();
//		BeanUtil.copyBean(request, project);
//		if(project.getId() == null || "".equals(project.getId())) {
//			project.setId(UUID.randomUUID().toString());
//		}
//		project.setDescription(request.getInfo());
//		project.setNow(System.currentTimeMillis());
////		List<String> list = new ArrayList<String>();
////		list.add("sdf");
////		list.add("gasdf");
////		list.add("kkkkkk");
////		project.setList(list);
//		projectRepository.save(project);
//		return "redirect:/project/list";
//	}
	
	@GetMapping(value = "patent/get")
	public String getPatent(@RequestParam(required=false,value="id") String id, Model model) {
		Patent patent = new Patent();
		if(id != null) {
			patent = patentRepository.findById(id).get();
		}
		model.addAttribute("patent", patent);
		return "result-zlCon";
	}
	
	@GetMapping(value = "patent/list")
	public String patents(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="year") String year,
			@RequestParam(required=false,value="ipc") String ipc,
			@RequestParam(required=false,value="cpc") String cpc,
			@RequestParam(required=false,value="person") String person,
			@RequestParam(required=false,value="creator") String creator,
			@RequestParam(required=false,value="country") String country,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		long totalCount = 0L;
		long totalPages = 0L;
		List<Patent> patentList = new ArrayList<Patent>();
		String view = "result-zl";
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
					model.addAttribute("agyear", yearMap);
					
					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
					Map<String, Long> ipcMap = new HashMap<String, Long>();
					while(ipcbit.hasNext()) {
						Bucket ipcBucket = ipcbit.next();
						ipcMap.put(ipcBucket.getKey().toString(), Long.valueOf(ipcBucket.getDocCount()));
					}
					model.addAttribute("agipc", ipcMap);
					
					StringTerms cpcTerms = (StringTerms) aggregations.asMap().get("agcpc");
					Iterator<Bucket> cpcbit = cpcTerms.getBuckets().iterator();
					Map<String, Long> cpcMap = new HashMap<String, Long>();
					while(cpcbit.hasNext()) {
						Bucket cpcBucket = cpcbit.next();
						cpcMap.put(cpcBucket.getKey().toString(), Long.valueOf(cpcBucket.getDocCount()));
					}
					model.addAttribute("agcpc", cpcMap);
					
					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
					Map<String, Long> personMap = new HashMap<String, Long>();
					while(personbit.hasNext()) {
						Bucket personBucket = personbit.next();
						personMap.put(personBucket.getKey().toString(), Long.valueOf(personBucket.getDocCount()));
					}
					model.addAttribute("agperson", personMap);
					
					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
					Map<String, Long> creatorMap = new HashMap<String, Long>();
					while(creatorbit.hasNext()) {
						Bucket creatorBucket = creatorbit.next();
						creatorMap.put(creatorBucket.getKey().toString(), Long.valueOf(creatorBucket.getDocCount()));
					}
					model.addAttribute("agcreator", creatorMap);
					
					StringTerms countryTerms = (StringTerms) aggregations.asMap().get("agcountry");
					Iterator<Bucket> countrybit = countryTerms.getBuckets().iterator();
					Map<String, Long> countryMap = new HashMap<String, Long>();
					while(countrybit.hasNext()) {
						Bucket countryBucket = countrybit.next();
						countryMap.put(countryBucket.getKey().toString(), Long.valueOf(countryBucket.getDocCount()));
					}
					model.addAttribute("agcountry", countryMap);
					
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
		model.addAttribute("patentList", patentList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("query", q);
			
		return view;
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
	public String agmount() {
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
	@GetMapping(value = "patent/agclassis")
	public String agclassis() {
		return "zhuanlifenxijishufenlei";
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
	
	
}
