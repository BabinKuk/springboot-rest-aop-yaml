package org.babinkuk.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.StudentRepository;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Student;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.StudentMapper;
import org.babinkuk.validator.ActionType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StudentServiceImpl implements StudentService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	public static String STUDENT_SAVE_SUCCESS = "student_save_success";
	public static String STUDENT_DELETE_SUCCESS = "student_delete_success";
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ObjectMapper mapper;
		
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}
	
	public StudentServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	@Transactional
	public StudentVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Student> result = studentRepository.findById(id);
		
		Student student = null;
		StudentVO studentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			//log.info("student ({})", student);
			
			// mapping
			studentVO = studentMapper.toVO(student);
			//log.info("studentVO ({})", studentVO);
			
			return studentVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_student_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	@Transactional
	public StudentVO findByEmail(String email) {
		
		Optional<Student> result = studentRepository.findByEmail(email);
		
		Student student = null;
		StudentVO studentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			
			// mapping
			studentVO = studentMapper.toVO(student);
			//log.info("studentVO ({})", studentVO);
		} else {
			// not found
			String message = String.format(getMessage("error_code_student_email_not_found"), email);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}

		return studentVO;
	}
		
	@Override
	@Transactional
	public ApiResponse saveStudent(StudentVO studentVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(STUDENT_SAVE_SUCCESS));
		
		Optional<Student> entity = studentRepository.findById(studentVO.getId());
		
		Student student = null;
		
		if (entity.isPresent()) {
			student = entity.get();
			//log.info("mapping for update");
			
			// mapping
			student = studentMapper.toEntity(studentVO, student);
		} else {
			// instructor not found
			//log.info("mapping for insert");
			
			// mapping
			student = studentMapper.toEntity(studentVO);
		}
		
		studentRepository.save(student);
		
		return response;
	}
	
	@Override
	@Transactional
	public ApiResponse deleteStudent(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(STUDENT_DELETE_SUCCESS));
		
		studentRepository.deleteById(id);
		
		return response;
	}

	@Override
	@Transactional
	public Iterable<StudentVO> getAllStudents() {
		return studentMapper.toVO(studentRepository.findAll());
	}

	@Override
	@Transactional
	public ApiResponse setCourse(StudentVO studentVO, CourseVO courseVO, ActionType action) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(STUDENT_SAVE_SUCCESS));
		
		Optional<Student> result = studentRepository.findById(studentVO.getId());
		
		Student student = null;
		
		if (result.isPresent()) {
			student = result.get();
			//log.info("student ({})", student);
		} else {
			// not found
			String message = String.format(getMessage("error_code_student_id_not_found"), studentVO.getId());
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		Optional<Course> entity = courseRepository.findById(courseVO.getId());
		
		Course course = null;
		
		if (entity.isPresent()) {
			course = entity.get();
			//log.info("courseVO ({})", courseVO);
		} else {
			// not found
			String message = String.format(getMessage("error_code_course_id_not_found"), courseVO.getId());
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		if (action.equals(ActionType.ENROLL)) {
			student.addCourse(course);
		}
		
		if (action.equals(ActionType.WITHDRAW)) {
			student.removeCourse(course);
		}
		
		//log.info("saving student");
		studentRepository.save(student);
		
		return response;
	}
}