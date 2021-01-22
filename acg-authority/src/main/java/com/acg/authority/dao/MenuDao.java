package com.acg.authority.dao;

import com.acg.authority.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuDao {
    List<Menu> queryAllMenu();

    List<String> selectAllPermissionValue();

    List<String> selectMenuValueByUserId(String id);
}
