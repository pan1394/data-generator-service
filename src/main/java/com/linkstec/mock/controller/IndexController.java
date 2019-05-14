package com.linkstec.mock.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.linkstec.mock.processor.MockData4DB;

@Controller
public class IndexController {

	@Value("${file.folder.template}")
	private String templatePath;

	@Value("${file.folder.upload}")
	private String uploadPath;

	@Value("${file.folder.download}")
	private String downloadPath;

	@Autowired
	private MockData4DB mockData4DB;

	@Value("${file.folder.templateName}")
	private String templateName = "template.xlsx";

	@Autowired
	private com.linkstec.mock.excel.v2.ExcelWriter writer2;

	@Autowired
	private com.linkstec.mock.excel.v2.ExcelReader reader2;

	@RequestMapping("/index")
	public String index() {
		return "/data-generator";
	}

	@GetMapping("/download")
	public String download(HttpServletResponse response) {
		String downloadFilePath = templatePath + File.separator + templateName;// 被下载的文件在服务器中的路径,
		String ret = IndexController.download(response, downloadFilePath, templateName);
		return ret;
	}

	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public String uploadImg(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
		String contentType = file.getContentType();
		String fileName = file.getOriginalFilename();
		int start = fileName.lastIndexOf(File.separator);
		fileName = fileName.substring(start + 1);
		int suffix = fileName.lastIndexOf(".");
		String suffixStr = fileName.substring(suffix + 1);
		String preStr = fileName.substring(0, suffix);
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYmmddHHMMSSSS");
		String appender = sdf.format(new Date());
		fileName = String.format("%s%s.%s", preStr, appender, suffixStr);
		try {
			String inputFile = IndexController.uploadFile(file.getBytes(), this.uploadPath, fileName);
			String templateName = String.format("output%s.xlsx", appender);
			String downloadFilePath = downloadPath + File.separator + templateName;
			mockData4DB.MockData(inputFile, downloadFilePath);
			String ret = IndexController.download(response, downloadFilePath, templateName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/index";
	}


	@ResponseBody
	@RequestMapping(path = "/upload/v2", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
		String contentType = file.getContentType();
		String fileName = file.getOriginalFilename();
		int start = fileName.lastIndexOf(File.separator);
		fileName = fileName.substring(start + 1);
		int suffix = fileName.lastIndexOf(".");
		String suffixStr = fileName.substring(suffix + 1);
		String preStr = fileName.substring(0, suffix);
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYmmddHHMMSSSS");
		String appender = sdf.format(new Date());
		fileName = String.format("%s%s.%s", preStr, appender, suffixStr);
		try {
			String inputFile = IndexController.uploadFile(file.getBytes(), this.uploadPath, fileName);
			String templateName = String.format("output.%s.xlsx", appender);
			String downloadFilePath = downloadPath + File.separator + templateName;
			writer2.write(reader2.read(inputFile), downloadFilePath);
			String ret = IndexController.download(response, downloadFilePath, templateName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/index";
	}

	private static String download(HttpServletResponse response, String downloadFile, String fileName) {
		File file = new File(downloadFile);
		if (file.exists()) {
			response.setContentType("application/vnd.ms-excel; charset=utf-8");
			response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
			response.setCharacterEncoding("utf-8");
			response.setContentLength((int) file.length());
			BufferedInputStream bis = null;
			try (
				OutputStream outputStream = response.getOutputStream();){
				//outputStream.flush();
				bis = new BufferedInputStream(new FileInputStream(file));
				/*byte[] b = new byte[bis.available()];
				bis.read(b);
				outputStream.write(b);
				outputStream.flush();*/

				byte[] buffer = new byte[1024 * 512];
				int i = bis.read(buffer);
				while (i != -1) {
					outputStream.write(buffer, 0, i);
					i = bis.read(buffer);
				}
				outputStream.flush() ;
				return "下载成功";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "下载失败";
	}

	public static String uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + File.separator + fileName);
		out.write(file);
		out.flush();
		out.close();
		return filePath + File.separator + fileName;
	}

}
