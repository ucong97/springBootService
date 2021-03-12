package com.sbs.springBootService.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.springBootService.dao.MemberDao;
import com.sbs.springBootService.dto.Member;
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

	public Member getMemberByLoginId(String loginId) {
		return memberDao.getMemberByLoginId(loginId);
	}

	public ResultData modifyMember(Map<String, Object> param) {
		memberDao.modifyMember(param);

		return new ResultData("S-1", "회원정보가 수정되었습니다.");
	}

	public Member getMember(int id) {
		return memberDao.getMember(id);
	}

	public boolean isAdmin(Member actor) {
		return actor.getAuthLevel() == 7;
	}

	public Member getMemberByAuthKey(String authKey) {
		return memberDao.getMemberByAuthKey(authKey);
	}
	
	public List<Member> getForPrintMembers(String searchKeywordType, String searchKeyword, int page, int itemsInAPage) {
		int limitStart = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;

		return memberDao.getForPrintMembers(searchKeywordType, searchKeyword, limitStart, limitTake);
	}

}
