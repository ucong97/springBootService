package com.sbs.springBootService.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.springBootService.dto.Member;

@Mapper
public interface MemberDao {
	public void join(Map<String, Object> param);

	public Member getMemberByLoginId(@Param("loginId") String loginId);
}
