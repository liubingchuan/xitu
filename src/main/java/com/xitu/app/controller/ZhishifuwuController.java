package com.xitu.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.xitu.app.common.R;
import com.xitu.app.common.SystemConstant;
import com.xitu.app.common.request.LoginRequest;
import com.xitu.app.common.request.RegisterRequest;
import com.xitu.app.common.request.SaveOrderRequest;
import com.xitu.app.common.request.UpdateUserRequest;
import com.xitu.app.mapper.UserMapper;
import com.xitu.app.mapper.ZhishifuwuMapper;
import com.xitu.app.model.Linkuser;
import com.xitu.app.model.Order;
import com.xitu.app.model.Relation;
import com.xitu.app.model.User;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.JwtUtils;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ZhishifuwuController {

	private static final Logger logger = LoggerFactory.getLogger(ZhishifuwuController.class);
	@Autowired
    private ZhishifuwuMapper zhishifuwuMapper;
	
	@GetMapping(value = "zhishifuwu/zhishifuwu")
	public String WenxianChuandi(Model model) {
		SaveOrderRequest saveOrderRequest = new SaveOrderRequest();
		model.addAttribute("saveOrderRequest", saveOrderRequest);
		return "T-zhishi";
	}
	
	@PostMapping(value = "order/save")
	public String saveOrder(SaveOrderRequest request,Model model, RedirectAttributes redirectAttributes) throws Exception {
		Order order = new Order();
		if (request.getTitle() != null) {
			order.setTitle(request.getTitle());
		}
		
		if (request.getChaxinfanwei() != null && !request.getChaxinfanwei().equals("请选择查新范围")) {
			order.setChaxinfanwei(request.getChaxinfanwei());
		}
		
		if (request.getMudi() != null && !request.getMudi().equals("请选择项目目的")) {
			order.setMudi(request.getMudi());
		}
		
		if (request.getKexueyaodian() != null) {
			order.setKexueyaodian(request.getKexueyaodian());
		}
		
		if (request.getJiansuodian() != null) {
			order.setJiansuodian(request.getJiansuodian());
		}
		
		if (request.getJiansuoci() != null) {
			order.setJiansuoci(request.getJiansuoci());
		}
		if (request.getXueke() != null && !request.getXueke().equals("请选择学科分类")) {
			order.setXueke(request.getXueke());
		}
		if (request.getChanye() != null && !request.getChanye().equals("请选择产业分类")) {
			order.setChanye(request.getChanye());
		}
		if (request.getInstitution()!= null) {
			order.setInstitution(request.getInstitution());
		}
		order.setUserId(1);
		order.setChulizhuangtai("待处理");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		order.setShenqingshijian(df.format(new Date()));
		
		String orderuuid = UUID.randomUUID().toString();
		order.setUuid(orderuuid);
		zhishifuwuMapper.insertOrder(order);
		
		Linkuser linkuser = new Linkuser();
		if (request.getName() != null) {
			linkuser.setName(request.getName());
		}
		if (request.getEmail() != null) {
			linkuser.setEmail(request.getEmail());
		}
		if (request.getInstitution()!= null) {
			linkuser.setInstitution(request.getInstitution());
		}
		if (request.getTelephone() != null) {
			linkuser.setTelephone(request.getTelephone());
		}
		String linkuseruuid = UUID.randomUUID().toString();
		linkuser.setUuid(linkuseruuid);
		
		zhishifuwuMapper.insertLinkuser(linkuser);
		
		Relation relation = new Relation();
		relation.setLinkuserId(linkuseruuid);
		relation.setOrderId(orderuuid);
		relation.setTag(0);
		zhishifuwuMapper.insertRelation(relation);
		
		redirectAttributes.addAttribute("token", request.getToken());
		return "redirect:/zhishifuwu/shenqinglist";
	}
	
	@GetMapping(value = "zhishifuwu/shenqinglist")
	public String ShenqingList(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			@RequestParam(required=false,value="front") Integer front,
			Model model) {
		String view="T-shenqing";
		if (front!= null && front == 0) {
			view ="T-manageOrder";
		}
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		if("-1".equals(String.valueOf(pageIndex))) {
			pageIndex = 0;
		}
		int start = Integer.valueOf(pageIndex) * Integer.valueOf(pageSize);
		int totalCount = zhishifuwuMapper.getOrderCount();
		int end = totalCount-1;
		int totalPages = totalCount/Integer.valueOf(pageSize) + 1;
		if(pageIndex.equals(String.valueOf(totalPages))) {
			pageIndex = pageIndex - 1;
		}else {
			end = pageSize * (pageIndex+1);
		}
		List<Order> orderList = zhishifuwuMapper.getOrders(start, end);
		model.addAttribute("orderList", orderList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
			
		return view;
	}
	
	@GetMapping(value = "zhishifuwu/xiangqing")
	public String XiangQing(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="front") Integer front,
			Model model) {
		
		Order order = zhishifuwuMapper.getOrderByUUID(uuid);
		model.addAttribute("order", order);
		String linkuseruuid = zhishifuwuMapper.getRelationByUUID(uuid);
		Linkuser linkuser = zhishifuwuMapper.getLinkuserByUUID(linkuseruuid);
		model.addAttribute("linkuser", linkuser);
		
		String view="T-orderCon";
		if (front!= null && front == 0) {
			view ="T-manageordercon";
		}
		return view;
	}
	
	@GetMapping(value = "zhishifuwu/jiedan")
	public String jiedan(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="front") Integer front,
			Model model) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		String chulishijian = df.format(new Date());
		zhishifuwuMapper.updateChulizhuangtaiByUUID(uuid,"处理中",chulishijian,"暂无");
		return "redirect:/zhishifuwu/shenqinglist?front=0";
		
	}
	@GetMapping(value = "zhishifuwu/tijiao")
	public String tijiao(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="chuliyijian") String chuliyijian,
			@RequestParam(required=false,value="chulirenfujianId") String chulirenfujianId,
			@RequestParam(required=false,value="front") Integer front,
			Model model,Order order) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		String chulishijian = df.format(new Date());
		
		zhishifuwuMapper.updateChulizhuangtaiByUUID(uuid,"已完成",chulishijian,"已完成，如有问题请联系张老师，联系电话：123456789");
		return "redirect:/zhishifuwu/shenqinglist?front=0";
		
	}
	
}
