package com.wenxuanduan.wiki.mapper;

import com.wenxuanduan.wiki.domain.User;
import com.wenxuanduan.wiki.domain.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(String user);

    int insert(User row);

    int insertSelective(User row);

    List<User> selectByExampleWithBLOBs(UserExample example);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(String user);

    int updateByExampleSelective(@Param("row") User row, @Param("example") UserExample example);

    int updateByExampleWithBLOBs(@Param("row") User row, @Param("example") UserExample example);

    int updateByExample(@Param("row") User row, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKeyWithBLOBs(User row);

    int updateByPrimaryKey(User row);
}