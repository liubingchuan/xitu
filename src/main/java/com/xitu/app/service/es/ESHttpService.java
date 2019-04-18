package com.xitu.app.service.es;

import com.alibaba.fastjson.JSONObject;

public interface ESHttpService extends ESService {

    ESHttpClient getHttpClient();

    String composeDSL(int pageIndex, int pageSize, String...args);
    
    void convert(JSONObject response);
}
