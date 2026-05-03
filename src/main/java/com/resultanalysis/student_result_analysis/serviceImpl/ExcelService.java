package com.resultanalysis.student_result_analysis.serviceImpl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.resultanalysis.student_result_analysis.enums.ResultStatus;
import com.resultanalysis.student_result_analysis.pojo.Student;
import com.resultanalysis.student_result_analysis.pojo.StudentResult;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarks;
import com.resultanalysis.student_result_analysis.pojo.Subjects;
import com.resultanalysis.student_result_analysis.repository.StudentRepository;
import com.resultanalysis.student_result_analysis.repository.StudentResultRepository;
import com.resultanalysis.student_result_analysis.repository.StudentSubjectMarksRepository;
import com.resultanalysis.student_result_analysis.repository.SubjectsRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelService {

	@Autowired
	private StudentResultRepository studentResultRepository;

	@Autowired
	private SubjectsRepository subjectsRepository;

	@Autowired
	private StudentSubjectMarksRepository studentSubjectMarksRepository;

	@Autowired
	private StudentRepository studentRepository;

	public Resource downloadExcelSheet() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return generateExcelSheet();
	}

	private Resource generateExcelSheet() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		Workbook workbook = new XSSFWorkbook();

		// Bold style
		CellStyle boldStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldStyle.setFont(boldFont);
		boldStyle.setAlignment(HorizontalAlignment.CENTER);

		// Normal style
		CellStyle normalStyle = workbook.createCellStyle();
		Font normalFont = workbook.createFont();
		normalFont.setBold(false);
		normalStyle.setFont(normalFont);
		normalStyle.setAlignment(HorizontalAlignment.CENTER);

		createResultSheet(workbook, normalStyle, boldStyle);

		createTopperSheet(workbook, normalStyle, boldStyle);

		String uploadDir = "generatedExcel/";
		File directory = new File(uploadDir);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		// Write the output to a file
		String filePath = uploadDir + "Result.xlsx";
		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
			workbook.write(fileOut);
		}

		workbook.close();

		return new FileSystemResource(new File(filePath));
	}

	private void createResultSheet(Workbook workbook, CellStyle normalStyle, CellStyle boldStyle) {

		Sheet sheet = workbook.createSheet("result sheet");

		List<Subjects> subject = subjectsRepository.findAll();

		createSubjectHeaders(subject, sheet, boldStyle);

		createMarksHeader(subject, sheet, boldStyle);

		addStudentResults(subject, sheet, normalStyle);

	}

	private void createMarksHeader(List<Subjects> subject, Sheet sheet, CellStyle boldStyle) {
		// TODO Auto-generated method stub

		// add marks headers
		int cellNumber = 0;

		Row marksHeader = sheet.createRow(1);

		// leave a column for USN
		marksHeader.createCell(cellNumber++);

		int no_of_subjects = subject.size();

		for (int rowCell = 1; rowCell <= no_of_subjects; rowCell++) {
			Cell cell1 = marksHeader.createCell(cellNumber++);
			cell1.setCellValue("IN");
			cell1.setCellStyle(boldStyle);

			Cell cell2 = marksHeader.createCell(cellNumber++);
			cell2.setCellValue("EX");
			cell2.setCellStyle(boldStyle);

			Cell cell3 = marksHeader.createCell(cellNumber++);
			cell3.setCellValue("TOT");
			cell3.setCellStyle(boldStyle);

			Cell cell4 = marksHeader.createCell(cellNumber++);
			cell4.setCellValue("C");
			cell4.setCellStyle(boldStyle);

			Cell cell5 = marksHeader.createCell(cellNumber++);
			cell5.setCellValue("G");
			cell5.setCellStyle(boldStyle);

			Cell cell6 = marksHeader.createCell(cellNumber++);
			cell6.setCellValue("C*G");
			cell6.setCellStyle(boldStyle);

			Cell cell7 = marksHeader.createCell(cellNumber++);
			cell7.setCellValue("RES");
			cell7.setCellStyle(boldStyle);

		}

		Cell summ1 = marksHeader.createCell(cellNumber++);
		summ1.setCellValue("Total C*GP");
		summ1.setCellStyle(boldStyle);

		Cell summ2 = marksHeader.createCell(cellNumber++);
		summ2.setCellValue("Total C");
		summ2.setCellStyle(boldStyle);

		Cell summ3 = marksHeader.createCell(cellNumber++);
		summ3.setCellValue("SGPA");
		summ3.setCellStyle(boldStyle);

		Cell summ4 = marksHeader.createCell(cellNumber++);
		summ4.setCellValue("Total Marks");
		summ4.setCellStyle(boldStyle);

		Cell summ5 = marksHeader.createCell(cellNumber++);
		summ5.setCellValue("%");
		summ5.setCellStyle(boldStyle);

		Cell summ6 = marksHeader.createCell(cellNumber);
		summ6.setCellValue("Result");
		summ6.setCellStyle(boldStyle);

	}
	
	
	private void addStudentResults(List<Subjects> subject, Sheet sheet, CellStyle normalStyle) {
		// TODO Auto-generated method stub

		int no_of_subjects = subject.size();

		List<Student> student = studentRepository.findAll();

		Row studentMarks;
		
		int summCellNumber;
		
		String studentUSN;

		for (int row = 0; row < student.size(); row++) {
			studentMarks = sheet.createRow(row + 2);
			
			studentUSN=student.get(row).getUsn();

			Cell studentUsnCell = studentMarks.createCell(0);
			studentUsnCell.setCellValue(studentUSN);
			studentUsnCell.setCellStyle(normalStyle);
			
			List<StudentSubjectMarks> stuSubMarks = studentSubjectMarksRepository.findByIdStudentUsn(studentUSN);
			
			Optional<StudentResult> stuResult = studentResultRepository.findById(studentUSN);
			
			if(stuSubMarks.isEmpty() || stuResult.isEmpty()) {
				continue;
			}else {
				
				for (int sub = 0; sub < no_of_subjects; sub++) {
					for (int stusub = 0; stusub <stuSubMarks.size(); stusub++) {
						if (subject.get(sub).getCode().equals(stuSubMarks.get(stusub).getSubjects().getCode())) {
							
							Cell internalMarksCell = studentMarks.createCell(7*sub+1);
							internalMarksCell.setCellValue(stuSubMarks.get(stusub).getInternalMarks());
							internalMarksCell.setCellStyle(normalStyle);
							
							Cell externalMarksCell = studentMarks.createCell(7*sub+2);
							externalMarksCell.setCellValue(stuSubMarks.get(stusub).getExternalMarks());
							externalMarksCell.setCellStyle(normalStyle);
							
							Cell totalMarksCell = studentMarks.createCell(7*sub+3);
							totalMarksCell.setCellValue(stuSubMarks.get(stusub).getTotalMarks());
							totalMarksCell.setCellStyle(normalStyle);
							
							Cell creditsCell = studentMarks.createCell(7*sub+4);
							creditsCell.setCellValue(subject.get(sub).getCredits());
							creditsCell.setCellStyle(normalStyle);
							
							Cell gradepointCell = studentMarks.createCell(7*sub+5);
							gradepointCell.setCellValue(stuSubMarks.get(stusub).getGradePoints());
							gradepointCell.setCellStyle(normalStyle);
							
							Cell c_gCell = studentMarks.createCell(7*sub+6);
							c_gCell.setCellValue(stuSubMarks.get(stusub).getCredits_gradepoint());
							c_gCell.setCellStyle(normalStyle);
							
							Cell resultStatusCell = studentMarks.createCell(7*sub+7);
							resultStatusCell.setCellValue(stuSubMarks.get(stusub).getResultStatus().name());
							resultStatusCell.setCellStyle(normalStyle);

						}else {
							continue;
						}
					}
				}
				
				summCellNumber=no_of_subjects*7+1;
				
				Cell summ1 = studentMarks.createCell(summCellNumber++);
				summ1.setCellValue(stuResult.get().getTotalCCPoints());
				summ1.setCellStyle(normalStyle);

				Cell summ2 = studentMarks.createCell(summCellNumber++);
				summ2.setCellValue(stuResult.get().getTotalCredits());
				summ2.setCellStyle(normalStyle);

				Cell summ3 = studentMarks.createCell(summCellNumber++);
				summ3.setCellValue(stuResult.get().getSgpa());
				summ3.setCellStyle(normalStyle);

				Cell summ4 = studentMarks.createCell(summCellNumber++);
				summ4.setCellValue(stuResult.get().getTotal_marks());
				summ4.setCellStyle(normalStyle);

				Cell summ5 = studentMarks.createCell(summCellNumber++);
				summ5.setCellValue(stuResult.get().getPercentage());
				summ5.setCellStyle(normalStyle);

				Cell summ6 = studentMarks.createCell(summCellNumber);
				summ6.setCellValue(stuResult.get().getResultStatus().name());
				summ6.setCellStyle(normalStyle);
				
			}

		}
		
	}


	private void createSubjectHeaders(List<Subjects> subject, Sheet sheet, CellStyle boldStyle) {
		// TODO Auto-generated method stub

		int cellNumber = 0;

		Row row = sheet.createRow(0);

		row.createCell(cellNumber++);

		for (Subjects sub : subject) {

			// Merge cells from column 0 to 7 in row 0
			sheet.addMergedRegion(new CellRangeAddress(0, // first row
					0, // last row
					cellNumber, // first column
					cellNumber + 6 // last column
			));

			Cell rowCell = row.createCell(cellNumber);

			rowCell.setCellStyle(boldStyle);

			rowCell.setCellValue(sub.getCode());

			cellNumber += 7;
		}

	}

	private void createTopperSheet(Workbook workbook, CellStyle normalStyle, CellStyle boldStyle) {
		// Create Toppers sheet
		Sheet sheet = workbook.createSheet("Toppers");

		// Create a row and put some cells in it
		Row headerRow = sheet.createRow(0);

		Cell headerCell1 = headerRow.createCell(0);
		headerCell1.setCellValue("USN");
		headerCell1.setCellStyle(boldStyle);

		Cell headerCell2 = headerRow.createCell(1);
		headerCell2.setCellValue("Name");
		headerCell2.setCellStyle(boldStyle);

		Cell headerCell3 = headerRow.createCell(2);
		headerCell3.setCellValue("%");
		headerCell3.setCellStyle(boldStyle);

		List<StudentResult> listStudents = studentResultRepository.findTop10ByOrderByPercentageDesc();

		int rowNum = 1;
		for (StudentResult student : listStudents) {
			if (student.getResultStatus() != ResultStatus.F) {
				Row row = sheet.createRow(rowNum++);

				Cell cell1 = row.createCell(0);
				cell1.setCellValue(student.getStudent().getUsn());
				cell1.setCellStyle(normalStyle);

				Cell cell2 = row.createCell(1);
				cell2.setCellValue(student.getStudent().getStudentName());
				cell2.setCellStyle(normalStyle);

				Cell cell3 = row.createCell(2);
				cell3.setCellValue(student.getPercentage());
				cell3.setCellStyle(normalStyle);
			}
		}

	}

}
