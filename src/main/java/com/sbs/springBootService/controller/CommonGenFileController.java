package com.sbs.springBootService.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import com.sbs.springBootService.dto.GenFile;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.exceptions.GenFileNotFoundException;
import com.sbs.springBootService.service.GenFileService;

@Controller
public class CommonGenFileController extends BaseController {
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Autowired
	private GenFileService genFileService;

	@RequestMapping("/common/genFile/doUpload")
	@ResponseBody
	public ResultData doUpload(@RequestParam Map<String, Object> param, MultipartRequest multipartRequest) {
		return genFileService.saveFiles(param, multipartRequest);
	}

	@GetMapping("/common/genFile/doDownload")
	public ResponseEntity<Resource> downloadFile(int id, HttpServletRequest request) throws Exception {
		GenFile genFile = genFileService.getGenFile(id);
		String filePath = genFile.getFilePath(genFileDirPath);

		String browser = "Chrome";
		String filename = getEncodedFileName(genFile.getOriginFileName(), browser);
		Resource resource = new InputStreamResource(new FileInputStream(filePath));

		// Try to determine file's content type
		String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.parseMediaType(contentType)).body(resource);
	}

	public static String getEncodedFileName(String filename, String browser) throws Exception {

		String encodedFilename = null;
		if (browser.equals("MSIE")) {
			// 한글 파일명 깨짐현상을 해결하기 위해 URLEncoder.encode 메소드를 사용하는데,
			// 파일명에 공백이 존재할 경우 URLEncoder.encode 메소드에의해 공백이 '+' 로 변환됩니다.
			// 변환된 '+' 값을 다시 공백으로(%20)으로 replace처리하시면 됩니다.
			// \\+의 의미는 정규표현식에서 역슬래시(\)는 확장문자로
			// 역슬래시 다음에 일반문자가 오면 특수문자로 취급하고
			// 역슬래시 다음에 특수문자가 오면 그 문자 자체를 의미
			// 기존 파일명에 있던 '+' 는 URLEncoder.encode() 메소드에 의해 '%2B' 로 변환이 됩니다.
			encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < filename.length(); i++) {
				char c = filename.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					// ASCII문자(0X00 ~ 0X7E)는 URLEncoder.encode를 적용하지 않는다.
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else {
			throw new RuntimeException("Not supported browser");
		}
		return encodedFilename;
	}

	@GetMapping("/common/genFile/file/{relTypeCode}/{relId}/{typeCode}/{type2Code}/{fileNo}")
	public ResponseEntity<Resource> showFile(HttpServletRequest request, @PathVariable String relTypeCode,
			@PathVariable int relId, @PathVariable String typeCode, @PathVariable String type2Code,
			@PathVariable int fileNo) throws FileNotFoundException {
		GenFile genFile = genFileService.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);

		if (genFile == null) {
			throw new GenFileNotFoundException();
		}

		String filePath = genFile.getFilePath(genFileDirPath);
		Resource resource = new InputStreamResource(new FileInputStream(filePath));

		// Try to determine file's content type
		String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	}

}