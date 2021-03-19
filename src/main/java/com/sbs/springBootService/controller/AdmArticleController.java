package com.sbs.springBootService.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import com.sbs.springBootService.dto.Article;
import com.sbs.springBootService.dto.Board;
import com.sbs.springBootService.dto.GenFile;
import com.sbs.springBootService.dto.Member;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.service.ArticleService;
import com.sbs.springBootService.service.GenFileService;
import com.sbs.springBootService.util.Util;

@Controller
public class AdmArticleController extends BaseController{
	@Autowired
	private ArticleService articleService;
	@Autowired
	private GenFileService genFileService;

	@RequestMapping("/adm/article/detail")
	@ResponseBody
	public ResultData showDetail(Integer id) {
		if (id == null) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticle(id);

		if (article == null) {
			return new ResultData("F-2", "존재하지 않는 게시물번호 입니다.");
		}

		return new ResultData("S-1", "성공", "article", article);
	}

	@RequestMapping("/adm/article/list")
	public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page) {
		// @RequestParam(defaultValue = "titleAndTitle") String searchKeywordType
		Board board = articleService.getBoard(boardId);
		req.setAttribute("board", board);
		
		if (board == null) {
			return msgAndBack(req, "존재하지 않는 게시판입니다.");
		}


		if (searchKeywordType != null) {
			searchKeywordType = searchKeywordType.trim();
		}

		if (searchKeywordType == null || searchKeywordType.length() == 0) {
			searchKeywordType = "titleAndBody";
		}

		if (searchKeyword != null && searchKeyword.length() == 0) {
			searchKeyword = null;
		}

		if (searchKeyword != null) {
			searchKeyword = searchKeyword.trim();
		}

		if (searchKeyword == null) {
			searchKeywordType = null;
		}
		
		int totalItemsCount = articleService.getArticlesTotalCount(boardId, searchKeywordType, searchKeyword);

		int itemsInAPage = 20;
		
		int totalPage = (int)Math.ceil(totalItemsCount / (double)itemsInAPage);

		int pageMenuArmSize = 2;
		int pageMenuStart = page - pageMenuArmSize;

		if (pageMenuStart < 1) {
			pageMenuStart = 1;
		}
		

		int pageMenuEnd = page + pageMenuArmSize;
		if (pageMenuEnd > totalPage) {
			pageMenuEnd = totalPage;
		}
		
		if ( page < 3 ) {
			pageMenuEnd = 5;
		}

		List<Article> articles = articleService.getForPrintArticles(boardId, searchKeywordType, searchKeyword, page,
				itemsInAPage);

		req.setAttribute("totalItemsCount", totalItemsCount);
		req.setAttribute("articles", articles);
		req.setAttribute("page", page);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("pageMenuArmSize", pageMenuArmSize);
		req.setAttribute("pageMenuStart", pageMenuStart);
		req.setAttribute("pageMenuEnd", pageMenuEnd);
		
		return "adm/article/list";
	}
	
	@RequestMapping("/adm/article/add")
	public String showAdd(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		return "adm/article/add";
	}
	
	@RequestMapping("/adm/article/doAdd")
	public String doAdd(@RequestParam Map<String, Object> param, HttpServletRequest req, MultipartRequest multipartRequest) {		
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (param.get("boardId") == null) {
			return msgAndBack(req, "게시판번호를 입력해주세요.");
		}
		
		if (param.get("title") == null) {
			return msgAndBack(req, "제목을 입력해주세요.");
		}

		if (param.get("body") == null) {
			return msgAndBack(req,"내용을 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);

		ResultData addArticleRd = articleService.addArticle(param);

		int newArticleId = (int) addArticleRd.getBody().get("id");

		return msgAndReplace(req, String.format("%d번 게시물이 작성되었습니다.", newArticleId), "../article/detail?id=" + newArticleId);
	}

	@RequestMapping("/adm/article/doDelete")
	@ResponseBody
	public ResultData doDelete(Integer id, HttpServletRequest req) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");

		if (id == null) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}
		Article article = articleService.getArticle(id);

		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}

		ResultData actorCanDeleteRd = articleService.getActorCanDeleteRd(article, loginedMember);

		if (actorCanDeleteRd.isFail()) {
			return actorCanDeleteRd;
		}

		return articleService.deleteArticle(id);

	}
	
	@RequestMapping("/adm/article/modify")
	public String showModify(Integer id, HttpServletRequest req) {
		if (id == null) {
			return msgAndBack(req, "id를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticle(id);

		if (article == null) {
			return msgAndBack(req, "존재하지 않는 게시물번호 입니다.");
		}

		List<GenFile> files = genFileService.getGenFiles("article", article.getId(), "common", "attachment");

		Map<String, GenFile> filesMap = new HashMap<>();

		for (GenFile file : files) {
			filesMap.put(file.getFileNo() + "", file);
		}

		article.getExtraNotNull().put("file__common__attachment", filesMap);
		req.setAttribute("article", article);


		return "adm/article/modify";
	}

	@RequestMapping("/adm/article/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, String body, HttpServletRequest req) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");

		int id = Util.getAsInt(param.get("id"),0);
		
		if (id == 0) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}

		if ( Util.isEmpty(param.get("title")) ) {
			return new ResultData("F-1", "title을 입력해주세요.");
		}

		if ( Util.isEmpty(param.get("body")) ) {
			return new ResultData("F-1", "body를 입력해주세요.");
		}
		
		Article article = articleService.getArticle(id);
		
		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}
		
		ResultData actorCanModifyRd = articleService.getActorCanModifyRd(article, loginedMember);

		if (actorCanModifyRd.isFail()) {
			return actorCanModifyRd;
		}

		System.out.printf("param:",param);
		return articleService.modifyArticle(param);

	}

	@RequestMapping("/adm/article/doAddReply")
	@ResponseBody
	public ResultData doAddReply(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (param.get("articleId") == null) {
			return new ResultData("F-1", "게시물번호를 입력해주세요.");
		}

		if (param.get("body") == null) {
			return new ResultData("F-1", "내용을 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);

		return articleService.addReply(param);
	}

}
