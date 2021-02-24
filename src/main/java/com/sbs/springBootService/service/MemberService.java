package com.sbs.springBootService.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.springBootService.dao.MemberDao;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.util.Util;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;

	public ResultData join(Map<String, Object> param) {
		memberDao.join(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", String.format("%s님 환영합니다.", param.get("nickname")), "id", id);
	}
}