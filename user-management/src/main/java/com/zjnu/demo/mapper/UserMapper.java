package com.zjnu.demo.mapper;

import com.zjnu.demo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user ORDER BY id")
    List<User> selectAll();

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Insert("INSERT INTO user(username, password, email) "
          + "VALUES(#{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET username = #{username}, email = #{email} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
