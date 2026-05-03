package com.resultanalysis.student_result_analysis.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import technology.tabula.Page;
import technology.tabula.Table;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resultanalysis.student_result_analysis.enums.ResultStatus;
import com.resultanalysis.student_result_analysis.exception.DuplicateResultSheetException;
import com.resultanalysis.student_result_analysis.exception.FileProcessingException;
import com.resultanalysis.student_result_analysis.exception.InvalidResultSheetException;
import com.resultanalysis.student_result_analysis.exception.SubjectNotfoundException;
import com.resultanalysis.student_result_analysis.pojo.Student;
import com.resultanalysis.student_result_analysis.pojo.StudentResult;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarks;
import com.resultanalysis.student_result_analysis.pojo.StudentSubjectMarksId;
import com.resultanalysis.student_result_analysis.pojo.Subjects;
import com.resultanalysis.student_result_analysis.repository.StudentRepository;
import com.resultanalysis.student_result_analysis.repository.StudentResultRepository;
import com.resultanalysis.student_result_analysis.repository.StudentSubjectMarksRepository;
import com.resultanalysis.student_result_analysis.repository.SubjectsRepository;

import jakarta.transaction.Transactional;
import technology.tabula.ObjectExtractor;
import technology.tabula.RectangularTextContainer;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private SubjectsRepository subjectsRepository;
	
	@Autowired
	private StudentResultRepository studentResultRepository;
	
	@Autowired
	private StudentSubjectMarksRepository studentSubjectMarksRepository;

	public boolean checkUSN(String usn) {

		// TODO Auto-generated method stub
		System.out.println(usn);
		return studentRepository.existsByUsn(usn);
	}
	
	

	@Transactional
	public void extractTableFromPdf(MultipartFile resSheet) {
		// TODO Auto-generated method stub

		try {
			saveResultSheet(resSheet);

			boolean usnVerified = false;
			
			Student student=null;
			
			List<StudentSubjectMarks> list = new ArrayList<>();
			
			String currStudent=SecurityContextHolder.getContext().getAuthentication().getName();
			
			Optional<StudentResult> existingStudent= studentResultRepository.findById(currStudent);
			
			if(existingStudent.isPresent()) {
				throw new DuplicateResultSheetException("Your result has already been recorded");
			}

			PDDocument document = PDDocument.load(resSheet.getInputStream());

			ObjectExtractor extractor = new ObjectExtractor(document);
			SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();

			int totalPages = document.getNumberOfPages();
			
			System.out.println("Hi");

			for (int page = 1; page <= totalPages; page++) {
				
				System.out.println("Hello");

				Page pdfPage = extractor.extract(page);
				List<Table> tables = algorithm.extract(pdfPage);

				for (Table table : tables) {
					
					System.out.println("Table:   "+table.getRows());
					
					if ((table.getRows().getFirst().getFirst().getText()).equals("University Seat Number")) {

						if (table.getRows().getFirst().get(1).getText().substring(1).trim().equals(currStudent)) {
							student=Student.builder()
							.usn(table.getRows().getFirst().get(1).getText().substring(1).trim())
							.studentName(table.getRows().get(1).get(0).getText())
							.build();
							
							usnVerified = true;

						} else {
							throw new InvalidResultSheetException("Kindly upload your result sheet");
						}
					}

					if (usnVerified && table.getRows().get(0).get(0).getText().contains("Code")) {

						List<List<RectangularTextContainer>> rows = table.getRows();
						
						System.out.println(rows);

						for (List<RectangularTextContainer> cells : rows) {

							List<String> rowValues = new ArrayList<>();

							for (RectangularTextContainer cell : cells) {
								rowValues.add(cell.getText());
							}
							
							System.out.println(rowValues);

							rowValues.remove(1);      //exclude subject_name
							rowValues.remove(3);      //exclude total_marks
//							rowValues.remove(4);      //exclude result_status
							rowValues.removeLast();   //exclude result_announced date
							
							if(rowValues.get(0).contains("Code")) { // ignoring header row
								continue;
							}else {
								System.out.println(rowValues);
								StudentSubjectMarksId id = StudentSubjectMarksId.builder()
										.studentUsn(student.getUsn())
										.subjectCode(rowValues.get(0))
										.build();
								
								System.out.println(id);
								
								Subjects subject = subjectsRepository
								        .findById(rowValues.get(0))
								        .orElseThrow(() -> new SubjectNotfoundException("Subject not found"));

								StudentSubjectMarks marks = StudentSubjectMarks.builder()
								        .id(id)
								        .student(student)
								        .subjects(subject)
								        .internalMarks(Integer.parseInt(rowValues.get(1)))
								        .externalMarks(Integer.parseInt(rowValues.get(2)))
								        .resultStatus(ResultStatus.valueOf(rowValues.get(3)))
								        .build();

								System.out.println(marks);
								
								list.add(marks);
							}
						}
						studentSubjectMarksRepository.saveAll(list);
						student.setStudentSubjectMarks(list);
					}
				}
			}

			extractor.close();
			document.close();
			
			
			List<Subjects> subjects=subjectsRepository.findAll();
			
			System.out.println(subjects);
			
			Integer totalCredits=subjects.stream()
					.map(subject -> subject.getCredits())
					.reduce(0, Integer::sum);
			
			Double SGPA=Math.round(caluculateSGPA(student,subjects,totalCredits)*100.00)/100.00;
			
			Integer sumTotalMarks=calculateSumTotalMarks(currStudent);
			
//			Integer totalMAxMarks=subjects.stream()
//					.map(subject -> subject.getMaxMarks())
//					.reduce(0, Integer::sum);
			
//			Integer totalMAxMarks=800;
			
//			System.out.println("totalMarks:  "+totalMAxMarks);
			
//			Double percentage=sumTotalMarks*100.00/totalMAxMarks;
			
			Double percentage=Math.round((SGPA*10)*100.00)/100.00;
			
			System.out.println("percentage:  "+percentage);
			
			ResultStatus resStatus=CheckResultStatus(currStudent);
			
			
			StudentResult result=StudentResult.builder()
					.student(student)
					.sgpa(SGPA)
					.totalCredits(totalCredits)
					.percentage(percentage)
					.total_marks(sumTotalMarks)
					.resultStatus(resStatus)
					.build();
			
			studentResultRepository.save(result);
			
		} catch (IOException e) {
			throw new FileProcessingException("Corrupted resultSheet");
		}
	}
	
	private ResultStatus CheckResultStatus(String currStudent) {
		// TODO Auto-generated method stub
		ResultStatus resStatus=ResultStatus.P;
		List<ResultStatus> list=new ArrayList<>();
		
		list=studentSubjectMarksRepository.findAllByStudentUsn(currStudent);
		
		System.out.println(list);
		
		for(ResultStatus res:list) {
			if(res.equals(ResultStatus.F)) {
				resStatus=ResultStatus.F;
				break;
			}
		}
		return resStatus;
	}



	private Integer calculateSumTotalMarks(String currStudent) {
		// TODO Auto-generated method stub
		return studentSubjectMarksRepository.countSumTotalMarksbyUSN(currStudent);
		
	}



	private double caluculateSGPA(Student student, List<Subjects> subjects, Integer totalCredits) {
		
		List<StudentSubjectMarks> studentMarks=studentSubjectMarksRepository.findByIdStudentUsn(student.getUsn());
		
		double gpCreditProduct=0.0;
		
		 double SGPA;
		 for (StudentSubjectMarks mark : studentMarks) {
			    for (Subjects subject : subjects) {
			        if (subject.getCode().equals(mark.getId().getSubjectCode())) {
			            gpCreditProduct += mark.getGradePoints() * subject.getCredits();
			        }
			    }
			}
		 
		SGPA=gpCreditProduct/totalCredits;
		return SGPA;
	}

	private void saveResultSheet(MultipartFile resSheet) throws IOException {
		String uploadDir = "result_sheets/";
		File directory = new File(uploadDir);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		Path path = Paths.get(uploadDir + resSheet.getOriginalFilename());
		Files.write(path, resSheet.getBytes());

	}
	}
