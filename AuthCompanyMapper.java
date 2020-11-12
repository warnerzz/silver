package com.dao.auth;

import com.model.Page;
import com.model.auth.AuthCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthCompanyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AuthCompany record);

    int insertSelective(AuthCompany record);

    AuthCompany selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AuthCompany record);

    int updateByPrimaryKey(AuthCompany record);

    List<AuthCompany> selectParams(@Param("company")  AuthCompany company);

    List<AuthCompany> selectParamsPage(@Param("page") Page page, @Param("company")  AuthCompany company);
}
