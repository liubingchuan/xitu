package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_relation")
public class relation {

	public relation() {
		
	}
	
	@IdFlag
	private Integer id;
	private String order_id;
	private String linkuser_id;
	private Integer tag;
	
	
}
