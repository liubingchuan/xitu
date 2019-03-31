package com.yiyao.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yiyao.app.common.request.SaveExpertRequest;
import com.yiyao.app.model.Expert;
import com.yiyao.app.repository.ExpertRepository;
import com.yiyao.app.utils.BeanUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ExpertController {

	private static final Logger logger = LoggerFactory.getLogger(ExpertController.class);
	
	@Autowired
    private ExpertRepository expertRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@PostMapping(value = "expert/save")
	public String saveExpert(SaveExpertRequest request,Model model) {
		
		Expert expert = new Expert();
		BeanUtil.copyBean(request, expert);
		if(expert.getId() == null || "".equals(request.getId())) {
			expert.setId(UUID.randomUUID().toString());
		}
		List<String> areaList =new ArrayList<String>();
		List<String> dutyList =new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		areaList.add(request.getArea());
		dutyList.add(request.getDuty());
		expert.setArea(areaList);;
		expert.setDuty(dutyList);
		expert.setResume(request.getInfo());
		expert.setNow(System.currentTimeMillis());
		expert.setCtime(df.format(new Date()));
		expert.setUnit("中国科学院青岛生物能源与过程研究所");
		expertRepository.save(expert);
		return "redirect:/expert/list";
	}
	
	
	@GetMapping(value = "expert/delete")
	public String deleteExpert(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			expertRepository.deleteById(id);
		}
		return "redirect:/expert/list";
	}
	
	
	@GetMapping(value = "expert/get")
	public String getExpert(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="id") String id, Model model) {
		String view = "qiyezhikuhangyerencaixiangqing";
		Expert expert = new Expert();
		if(id != null) {
			expert = expertRepository.findById(id).get();
		}
		if(front != null) {
			view = "qiyezhikuhangyerencaizhuanjiaxiangqingyemian";
		}
		
		model.addAttribute("expert", expert);
		return view;
	}
	
	@GetMapping(value = "expert/list")
	public String experts(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="q") String q,
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
		List<Expert> expertList = new ArrayList<Expert>();
		String view = "qiyezhikuhangyerencailiebiao";
		if(esTemplate.indexExists(Expert.class)) {
			if(q == null) {
				totalCount = expertRepository.count();
				if(totalCount > 0) {
					Sort sort = new Sort(Direction.DESC, "now");
					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
					SearchQuery searchQuery = new NativeSearchQueryBuilder()
							.withPageable(pageable).build();
					Page<Expert> expertPage = expertRepository.search(searchQuery);
					expertList = expertPage.getContent();
					if (front != null) {
						view = "qiyezhikuhangyerencaiqiantai";
					}
				}
			}else {
				// 分页参数
				Pageable pageable = new PageRequest(pageIndex, pageSize);

				// 分数，并自动按分排序
				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)), ScoreFunctionBuilders.weightFactorFunction(1000));

				// 分数、分页
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
						.withQuery(functionScoreQueryBuilder).build();

				Page<Expert> searchPageResults = expertRepository.search(searchQuery);
				expertList = searchPageResults.getContent();
				totalCount = esTemplate.count(searchQuery, Expert.class);
				totalPages = Math.round(totalCount/pageSize);
				if (front != null) {
					view = "qiyezhikuhangyerencaiqiantai";
				}
			}
		}
		model.addAttribute("expertList", expertList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
			
		return view;
	}
	
	
	
	
}
