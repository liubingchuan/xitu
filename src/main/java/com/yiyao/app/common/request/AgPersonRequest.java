package com.yiyao.app.common.request;

import com.yiyao.app.common.BaseRequest;

public class AgPersonRequest extends BaseRequest{

	private String person;
	
	private String creator;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	
}
