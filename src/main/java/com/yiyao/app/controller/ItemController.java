package com.yiyao.app.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiyao.app.common.R;
import com.yiyao.app.common.SystemConstant;
import com.yiyao.app.common.request.RegisterRequest;
import com.yiyao.app.common.request.SaveItemRequest;
import com.yiyao.app.mapper.ItemMapper;
import com.yiyao.app.model.Item;
import com.yiyao.app.model.User;
import com.yiyao.app.utils.JwtUtils;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ItemController {

	private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
    private ItemMapper itemMapper;
	
	
	@GetMapping(value = "item/config")
	public String jump() {
		return "fenleishezhi";
	}
	
	@ResponseBody
	@GetMapping(value = "item/getServiceItems")
	public R getItems() {
		List<Item> items = itemMapper.getServiceItems();
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		map.put("xxcjlm", new ArrayList<String>());
		map.put("xxcjlyjg", new ArrayList<String>());
		map.put("cyfl", new ArrayList<String>());
		map.put("xkfl", new ArrayList<String>());
		map.put("rczc", new ArrayList<String>());
		map.put("xmfl", new ArrayList<String>());
		map.put("bglx", new ArrayList<String>());
		if(items==null || items.size()==0) {
			return R.notFound().put("services", map);
		}
		for(Item item: items) {
			if(item.getItem() != null) {
				map.put(item.getService(), Arrays.asList(item.getItem().split(";")));
			}
		}
		return R.ok().put("services", map);
	}
	
	@ResponseBody
	@RequestMapping(value = "item/save", method = RequestMethod.POST,consumes = "application/json")
	public R saveItem(@RequestBody SaveItemRequest request) {
		Class clazz = request.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field: fields) {
			String methodName = (new StringBuilder()).append("get").append(Character.toUpperCase(field.getName().charAt(0))).append(field.getName().substring(1)).toString();
			Method method;
			try {
				method = clazz.getDeclaredMethod(methodName, null);
				Item item = new Item();
				item.setService(field.getName());
				List<String> items = (List<String>) method.invoke(request);
				StringBuilder buffer = new StringBuilder();
				items.forEach(o->buffer.append(o).append(";"));
				item.setItem(buffer.length()==0?"":buffer.substring(0, buffer.length()-1));
				itemMapper.deleteItemByService(item.getService());
				itemMapper.insertItem(item);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return R.ok();
	}
	
}
