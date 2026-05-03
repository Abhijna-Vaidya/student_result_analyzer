package com.resultanalysis.student_result_analysis.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resultanalysis.student_result_analysis.serviceImpl.ExcelService;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/admin")
public class ExcelController {
	
	@Autowired
	private ExcelService excelService;
	

	@GetMapping("/excel/download")
	public ResponseEntity<Resource> downloadExcelsheet() throws FileNotFoundException, IOException{
		Resource resource = excelService.downloadExcelSheet();    //new FileSystemResource("path/to/file.zip");
		 return ResponseEntity.ok()
		            .contentType(MediaType.parseMediaType(
		                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
		            .header(HttpHeaders.CONTENT_DISPOSITION,
		                "attachment; filename=\"example.xlsx\"")
		            .contentLength(resource.contentLength())
		            .body(resource);
	}
}
