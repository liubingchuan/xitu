package com.yiyao.app.model;

import com.yiyao.app.annotation.IdFlag;
import com.yiyao.app.annotation.TableName;

@TableName("yiyao_item")
public class Item {

	public Item() {
		
	}
	
	@IdFlag
	private Integer id;
	private String service;
	private String item;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}

}
