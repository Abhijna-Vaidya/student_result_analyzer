package com.resultanalysis.student_result_analysis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resultanalysis.student_result_analysis.exception.EmptyFileException;
import com.resultanalysis.student_result_analysis.serviceImpl.AdminResultService;
import com.resultanalysis.student_result_analysis.serviceImpl.CsvService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
    private CsvService csvService;
	
	@Autowired
	private AdminResultService adminResultService;

	@PostMapping("/uploadCsv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file){
		
		String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

		if (extension == null || !extension.equalsIgnoreCase("csv")) {
		    throw new IllegalArgumentException("Only CSV files are allowed");
		}
		
        if (file.isEmpty()) {
            throw new EmptyFileException("CSV file is empty");
        }

        csvService.readCsv(file);
        return ResponseEntity.ok("CSV file processed successfully");
    }

	@PostMapping("/process-folder/{folderName}")
	public ResponseEntity<String> processFolder(@PathVariable String folderName){

        try {
            adminResultService.extractTableFromPdf(folderName);
            return ResponseEntity.ok("Folder processed successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing folder: " + e.getMessage());
        }
    }
	
}
