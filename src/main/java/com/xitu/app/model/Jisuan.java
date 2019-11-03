package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_jisuan")
public class Jisuan {

	public Jisuan() {
		
	}
	
	@IdFlag
	private Integer id;
	private String ordernumber;
	private String description;
	private String shenqingshijian;
	private String chulishijian;
	private String chulizhuangtai;
	private String chuliren;
	private String chuliyijian;
	private String chulirenfujianId;
	private String userId;
	private String uuid;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShenqingshijian() {
		return shenqingshijian;
	}
	public void setShenqingshijian(String shenqingshijian) {
		this.shenqingshijian = shenqingshijian;
	}
	public String getChulishijian() {
		return chulishijian;
	}
	public void setChulishijian(String chulishijian) {
		this.chulishijian = chulishijian;
	}
	public String getChulizhuangtai() {
		return chulizhuangtai;
	}
	public void setChulizhuangtai(String chulizhuangtai) {
		this.chulizhuangtai = chulizhuangtai;
	}
	public String getChuliren() {
		return chuliren;
	}
	public void setChuliren(String chuliren) {
		this.chuliren = chuliren;
	}
	public String getChuliyijian() {
		return chuliyijian;
	}
	public void setChuliyijian(String chuliyijian) {
		this.chuliyijian = chuliyijian;
	}
	public String getChulirenfujianId() {
		return chulirenfujianId;
	}
	public void setChulirenfujianId(String chulirenfujianId) {
		this.chulirenfujianId = chulirenfujianId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
