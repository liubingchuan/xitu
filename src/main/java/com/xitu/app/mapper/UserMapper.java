package com.xitu.app.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import com.xitu.app.common.PagedRequest;
import com.xitu.app.mapper.BasedProvider;
import com.xitu.app.model.User;

import java.util.List;

@Mapper
@Component
public interface UserMapper {

	@Select("SELECT * FROM xitu_user where account = #{account} limit 1")
	User getUserByAccount(@Param("account") String account);
	
	@Select("select * from xitu_user limit #{pageIndex}, #{pageSize}")
    List<User> getUsers(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
	
	@Select("select count(*) from xitu_user")
    int getUserCount();
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertUser(User user);
	
	@Update("UPDATE xitu_user SET account = #{user.account}, password = #{user.password}, name = #{user.name}, "
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
