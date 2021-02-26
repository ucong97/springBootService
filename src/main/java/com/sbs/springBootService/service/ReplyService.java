package com.sbs.springBootService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.springBootService.dao.ReplyDao;
import com.sbs.springBootService.dto.Reply;

@Service
public class ReplyService {
	@Autowired
	private ReplyDao replyDao;

	public List<Reply> getForPrintReplies(String relTypeCode, Integer relId) {
		return replyDao.getForPrintReplies(relTypeCode, relId);
	}

}
