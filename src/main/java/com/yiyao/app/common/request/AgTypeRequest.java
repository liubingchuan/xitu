package com.yiyao.app.common.request;

import com.yiyao.app.common.BaseRequest;

public class AgTypeRequest extends BaseRequest{

	private String type;
	
	private String trend;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	
	
	
}
