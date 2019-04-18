package com.xitu.app.service.es;

public interface ESService {

	void execute(int pageIndex, int pageSize, String...args);
}
