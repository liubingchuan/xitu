package com.xitu.app.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import com.xitu.app.common.PagedRequest;
import com.xitu.app.mapper.BasedProvider;
import com.xitu.app.model.Linkuser;
import com.xitu.app.model.Order;
import com.xitu.app.model.Relation;
import com.xitu.app.model.User;

import java.util.List;

@Mapper
@Component
public interface JisuanfuwuMapper {
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertOrder(Order order);
}
