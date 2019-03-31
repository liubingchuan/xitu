package com.yiyao.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import com.yiyao.app.common.R;
import com.yiyao.app.common.SystemConstant;
import com.yiyao.app.common.request.LoginRequest;
import com.yiyao.app.common.request.RegisterRequest;
import com.yiyao.app.common.request.UpdateUserRequest;
import com.yiyao.app.mapper.UserMapper;
import com.yiyao.app.model.User;
import com.yiyao.app.utils.BeanUtil;
import com.yiyao.app.utils.JwtUtils;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private UserMapper userMapper;
	
	
	
	@RequestMapping("/")
	public String index(Model model){
		User user = new User();
		model.addAttribute("user", user);
		return "index";
	}

	
	@ResponseBody
	@RequestMapping(value = "user/bind", method = RequestMethod.POST,consumes = "application/json")
	public R bind(@RequestBody RegisterRequest request) {
		String account = request.getAccount();
		String password = request.getPassword();
		String email = request.getEmail();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		User user = userMapper.getUserByAccount(account);
		if(user != null) {
			return R.error().put("token", "0");
		}
		user = new User();
		user.setAccount(account);
		user.setPassword(password);
		user.setEmail(email);
		user.setStamp(df.format(new Date()));
		userMapper.insertUser(user);
		String JWT = JwtUtils.createJWT("1", account, SystemConstant.JWT_TTL);
		
		return R.ok().put("token", JWT).put("account", account);
	}
	
	@PostMapping(value = "user/login")
	public String login(LoginRequest request,Model model) {
		System.out.println();
		String account = "";
		if(request.getToken() != null && !"".equals(request.getToken())) {
			try {
				account = JwtUtils.parseJWT(request.getToken()).getSubject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		account = request.getAccount();
		User user = userMapper.getUserByAccount(account);
		String password = request.getPassword();
		if(user !=null) {
			model.addAttribute("user", user);
			if(password != null && !password.equals(user.getPassword())) {
				model.addAttribute("token", "1");
			}else {
				String JWT = JwtUtils.createJWT("1", account, SystemConstant.JWT_TTL);
				model.addAttribute("token", JWT);
				model.addAttribute("account", user.getAccount());
				model.addAttribute("role", user.getRole());
			}
		}else {
			model.addAttribute("token", "2");
			model.addAttribute("user", new User());
		}
		return "index";
	}
	
	
	@PostMapping(value = "user/update")
	public String updateUser(UpdateUserRequest request,Model model, RedirectAttributes redirectAttributes) {
		User user = new User();
		BeanUtil.copyBean(request, user);
		userMapper.updateById(user);
		redirectAttributes.addAttribute("token", request.getToken());
		return "redirect:/user/list";
	}
	
	@GetMapping(value = "user/list")
	public String users(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
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
		int totalCount = userMapper.getUserCount();
		int end = totalCount-1;
		int totalPages = totalCount/Integer.valueOf(pageSize) + 1;
		if(pageIndex.equals(String.valueOf(totalPages))) {
			pageIndex = pageIndex - 1;
		}else {
			end = pageSize * (pageIndex+1);
		}
		List<User> userList = userMapper.getUsers(start, end);
		model.addAttribute("userList", userList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
			
		return "manage";
	}
	
	@GetMapping(value = "user/getUser")
	public String getUser(@RequestParam("account") String account, Model model) {
		User user = userMapper.getUserByAccount(account);
		model.addAttribute("user", user);
		return "manage_mess";
	}
	
	
	
}
