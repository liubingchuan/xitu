package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_order")
public class order {

	public order() {
		
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
	private String shenqingfujian_id;
	private String shenqingshijian;
	private String chulishijian;
	private String chulizhuangtai;
	private String chuliren;
	private String chuliyijian;
	private String chulirenfujian_id;
	
	
}
