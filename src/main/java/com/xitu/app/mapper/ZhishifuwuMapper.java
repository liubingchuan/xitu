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
public interface ZhishifuwuMapper {
	
	@Select("select count(*) from xitu_order")
    int getOrderCount();
	
	@Select("select * from xitu_order limit #{pageIndex}, #{pageSize}")
    List<Order> getOrders(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertOrder(Order order);
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertRelation(Relation relation);
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertLinkuser(Linkuser linkuser);
	
	@Select("SELECT * FROM xitu_order where uuid = #{uuid} limit 1")
	Order getOrderByUUID(@Param("uuid") String uuid);
	
	@Select("SELECT linkuser_id FROM xitu_relation where order_id = #{uuid} limit 1")
	String getRelationByUUID(@Param("uuid") String uuid);
	
	@Select("SELECT * FROM xitu_linkuser where uuid = #{uuid} limit 1")
	Linkuser getLinkuserByUUID(@Param("uuid") String uuid);
	
	@Update("UPDATE xitu_order SET chulizhuangtai = #{chulizhuangtai}, chulishijian = #{chulishijian}, chuliyijian= #{chuliyijian} WHERE uuid = #{uuid} limit 1")
	void updateChulizhuangtaiByUUID(@Param("uuid") String uuid,@Param("chulizhuangtai") String chulizhuangtai,@Param("chulishijian") String chulishijian,@Param("chuliyijian") String chuliyijian);
	
	@Update("UPDATE xitu_order SET chulizhuangtai = #{user.account}, password = #{user.password}, name = #{user.name}, "
		    + "identity = #{user.identity}, unit = #{user.unit}, job = #{user.job}, "
		    + "duty = #{user.duty}, major = #{user.major}, email = #{user.email}, "
		    + "phone = #{user.phone}, stamp = #{user.stamp} "
	        + "WHERE id = #{user.id}")
	void updateById(@Param("user") User user);
	
	
//    @Select("SELECT * FROM car_user")
//    List<User> getAll();
//
//
//    @Delete("DELETE FROM car_user")
//    void deleteAll();
//    
//    @Update("update car_user set password = #{password} where account = #{account}")
//    void updatePassword(@Param("account") String account, @Param("password") String password);
//    
//    @Update("update car_user set nick = #{nick} where account = #{account}")
//    void updateNick(@Param("account") String account, @Param("nick") String nick);
	
	
}