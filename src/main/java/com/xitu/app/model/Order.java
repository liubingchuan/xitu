package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_order")
public class Order {

	public Order() {
		
	}
	
	@IdFlag
	private Integer id;
	private String ordernumber;
	private String title;
	private String chaxinfanwei;
	private String mudi;
	private String kexueyaodian;
	private String jiansuodian;
	private String jiansuoci;
	private String xueke;
	private String chanye;
	private String beizhu;
	private String shenqingfujianId;
	private String shenqingshijian;
	private String chulishijian;
	private String chulizhuangtai;
	private String chuliren;
	private String chuliyijian;
	private String institution;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getChaxinfanwei() {
		return chaxinfanwei;
	}
	public void setChaxinfanwei(String chaxinfanwei) {
		this.chaxinfanwei = chaxinfanwei;
	}
	public String getMudi() {
		return mudi;
	}
	public void setMudi(String mudi) {
		this.mudi = mudi;
	}
	public String getKexueyaodian() {
		return kexueyaodian;
	}
	public void setKexueyaodian(String kexueyaodian) {
		this.kexueyaodian = kexueyaodian;
	}
	public String getJiansuodian() {
		return jiansuodian;
	}
	public void setJiansuodian(String jiansuodian) {
		this.jiansuodian = jiansuodian;
	}
	public String getJiansuoci() {
		return jiansuoci;
	}
	public void setJiansuoci(String jiansuoci) {
		this.jiansuoci = jiansuoci;
	}
	public String getXueke() {
		return xueke;
	}
	public void setXueke(String xueke) {
		this.xueke = xueke;
	}
	public String getChanye() {
		return chanye;
	}
	public void setChanye(String chanye) {
		this.chanye = chanye;
	}
	public String getBeizhu() {
		return beizhu;
	}
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}
	public String getShenqingfujianId() {
		return shenqingfujianId;
	}
	public void setShenqingfujianId(String shenqingfujianId) {
		this.shenqingfujianId = shenqingfujianId;
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
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	
}
