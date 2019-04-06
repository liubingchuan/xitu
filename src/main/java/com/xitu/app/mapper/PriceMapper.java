package com.xitu.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.Price;
import com.xitu.app.model.User;

@Mapper
@Component
public interface PriceMapper {

	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertPrice(Price price);
	
	
	@Select("SELECT * FROM xitu_price where update_time = #{updateTime} limit 1")
	Price getPriceByUpdateTime(@Param("updateTime") String updateTime);
	
	@Select("SELECT * FROM xitu_price where update_time = #{updateTime}")
	List<Price> getPricesByUpdateTime(@Param("updateTime") String updateTime);
	
	@Select("SELECT MAX(update_time) FROM xitu_price;")
	String getLatestUpdateTime();
}
