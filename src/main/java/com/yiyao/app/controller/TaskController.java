package com.yiyao.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yiyao.app.mapper.TaskMapper;
import com.yiyao.app.model.Task;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class TaskController {

	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
    private TaskMapper taskMapper;
	
	@GetMapping(value = "task/getTask")
	public String getTask(@RequestParam(value="id",required=false) Integer id, Model model) {
		
		Task task = new Task();
		if(id != null) {
			task = taskMapper.getTaskById(id);
		}
		model.addAttribute("task", task);
		return "xinxicaijicaijirenwuxinjianrenwu";
	}
	
	
}
