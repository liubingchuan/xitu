package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_linkuser")
public class linkuser {

	public linkuser() {
		
	}
	
	@IdFlag
	private Integer id;
	private String name;
	private String email;
	private String telephone;
	
	
}
