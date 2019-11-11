package com.xitu.app.mapper;

import com.xitu.app.model.Xiangtu;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface XiangtuMapper {
    @InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
    int insertXiangtu(Xiangtu xiangtu);

    @Select("SELECT * FROM xitu_xiangtu where unitse = #{unitse} and arease = #{arease} order by ID limit #{pageIndex}, #{pageSize}")
    List<Xiangtu> getXiangtuList(@Param("unitse") String unitse,
                                        @Param("arease") String arease,
                                        @Param("pageIndex") int pageIndex,
                                        @Param("pageSize") int pageSize);
}
