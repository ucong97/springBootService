package com.sbs.springBootService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sbs.springBootService.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member extends EntityDto{
	private int id;
	private String regDate;
	private String updateDate;
	private String loginId;
	@JsonIgnore
	private String loginPw;
	private int authLevel;
	@JsonIgnore
	private String authKey;
	private String name;
	private String nickname;
	private String cellphoneNo;
	private String email;
	private String extra__thumbImg;
	
	public String getAuthLevelName() {
		return MemberService.getAuthLevelName(this);
	}
	
	public String getAuthLevelNameColor() {
		return MemberService.getAuthLevelNameColor(this);
	}
}
