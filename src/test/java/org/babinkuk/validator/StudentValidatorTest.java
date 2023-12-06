package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.StudentService;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class StudentValidatorTest {
	
	public static final Logger log = LogManager.getLogger(StudentValidatorTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	private static String VALIDATION_FAILED = "validation_failed";
	
	private static MockHttpServletRequest request;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Autowired
	private StudentService studentService;
		
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.user.insert-instructor}")
	private String sqlAddUserInstructor;
	
	@Value("${sql.script.user.insert-student}")
	private String sqlAddUserStudent;
	
	@Value("${sql.script.user.delete}")
	private String sqlDeleteUser;
	
	@Value("${sql.script.instructor.insert}")
	private String sqlAddInstructor;
	
	@Value("${sql.script.instructor.delete}")
	private String sqlDeleteInstructor;
	
	@Value("${sql.script.instructor-detail.insert}")
	private String sqlAddInstructorDetail;
	
	@Value("${sql.script.instructor-detail.delete}")
	private String sqlDeleteInstructorDetail;
	
	@Value("${sql.script.student.insert}")
	private String sqlAddStudent;
	
	@Value("${sql.script.student.delete}")
	private String sqlDeleteStudent;
	
	@Value("${sql.script.course-student.insert}")
	private String sqlAddCourseStudent;
	
	@Value("${sql.script.course-student.delete}")
	private String sqlDeleteCourseStudent;
	
	@Value("${sql.script.image.insert-instructor-1}")
	private String sqlAddImageInstructor1;
	
	@Value("${sql.script.image.insert-instructor-2}")
	private String sqlAddImageInstructor2;
	
	@Value("${sql.script.image.insert-student-1}")
	private String sqlAddImageStudent1;
	
	@Value("${sql.script.image.insert-student-2}")
	private String sqlAddImageStudent2;
	
	@Value("${sql.script.image.delete}")
	private String sqlDeleteImage;
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@BeforeAll
	public static void setup() {

		// init
		request = new MockHttpServletRequest();
	}
	
	@BeforeEach
    public void setupDatabase() {
		
		jdbc.execute(sqlAddInstructorDetail);
		jdbc.execute(sqlAddUserInstructor);
		jdbc.execute(sqlAddInstructor);
		jdbc.execute(sqlAddImageInstructor1);
		jdbc.execute(sqlAddImageInstructor2);
		jdbc.execute(sqlAddCourse);
		jdbc.execute(sqlAddReview);
		jdbc.execute(sqlAddUserStudent);
		jdbc.execute(sqlAddStudent);
		jdbc.execute(sqlAddImageStudent1);
		jdbc.execute(sqlAddImageStudent2);
		jdbc.execute(sqlAddCourseStudent);
	}
	
	@AfterEach
	public void setupAfterTransaction() {

		jdbc.execute(sqlDeleteCourseStudent);
		jdbc.execute(sqlDeleteStudent);
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
		jdbc.execute(sqlDeleteInstructorDetail);
		jdbc.execute(sqlDeleteImage);
		jdbc.execute(sqlDeleteUser);
		
//		// check
//		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
//		userList = jdbc.queryForList("select * from user");
//		log.info("size() " + userList.size());
//		for (Map m : userList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
	}
	
	@Test
	void addEmptyStudentRoleAdmin() throws Exception {

		addEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyStudentRoleInstructor() throws Exception {

		addEmptyStudent(ROLE_INSTRUCTOR);
	}

	private void addEmptyStudent(String validationRole) throws Exception {
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(4))) // verify that json root element $ is size 4
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage("error_code_status_invalid"), ActionType.CREATE))))
			.andExpect(jsonPath("$.errorCount", is(4))) // verify that json root element $ is size 4
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}

	@Test
	void addStudentInvalidEmailRoleAdmin() throws Exception {

		addStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void addStudentInvalidEmailRoleIstructor() throws Exception {

		addStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void addStudentInvalidEmail(String validationRole) throws Exception {
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		String emailAddress = "this is invalid email";
		studentVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(4))) // verify that json root element $ is size 4
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage("error_code_status_invalid"), ActionType.CREATE))))
			.andExpect(jsonPath("$.errorCount", is(4))) // verify that json root element $ is size 4
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addStudentEmailNotUniqueRoleAdmin() throws Exception {

		addStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addStudentEmailNotUniqueRoleInstructor() throws Exception {

		addStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addStudentEmailNotUnique(String validationRole) throws Exception {
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		// this email already exists in db
		String emailAddress = STUDENT_EMAIL;
		studentVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateStudentInvalidIdRoleAdmin() throws Exception {

		updateStudentInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidIdRoleInstructor() throws Exception {

		updateStudentInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidId(String validationRole) throws Exception {
		
		int id = 22;
		
		// check if student id=22 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
		
		// create invalid student 
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyStudentRoleAdmin() throws Exception {

		updateEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyStudentRoleInstructor() throws Exception {

		updateEmptyStudent(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyStudent(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals getFirstName() failure");
		
		// update student
		studentVO.setFirstName("");
		studentVO.setLastName("");
		studentVO.setEmail("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentInvalidEmailRoleAdmin() throws Exception {

		updateStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidEmailRoleInstructor() throws Exception {

		updateStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidEmail(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals getFirstName() failure");
		
		// update student
		studentVO.setEmail("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentEmailNotUniqueRoleAdmin() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentEmailNotUniqueRoleInstructor() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentEmailNotUnique(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals getFirstName() failure");
		
		// create new student
		StudentVO newStudentVO = ApplicationTestUtils.createStudent();
		
		// save new student
		studentService.saveStudent(newStudentVO);
		
		// check if new student exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		StudentVO dbNewStudentVO = studentService.findByEmail(newStudentVO.getEmail());
		
		assertNotNull(dbNewStudentVO,"dbNewStudentVO null");
		//assertEquals(1, dbNewStudentVO.getId());
		assertNotNull(dbNewStudentVO.getFirstName(),"dbNewStudentVO.getFirstName() null");
		assertEquals(dbNewStudentVO.getFirstName(), dbNewStudentVO.getFirstName(),"assertEquals dbNewStudentVO.getFirstName() failure");
		assertEquals(dbNewStudentVO.getEmail(), dbNewStudentVO.getEmail(),"assertEquals dbNewStudentVO.getEmail() failure");
		
		// update student email (value belong to other instructor id 2)
		dbNewStudentVO.setEmail(studentVO.getEmail());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewStudentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size
			.andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentNotExistRoleAdmin() throws Exception {
		
		updateStudentNotExist(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentNotExistRoleInstructor() throws Exception {
		
		updateStudentNotExist(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentNotExist(String validationRole) throws Exception {
		
		int id = 22;
		
		// get student with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
		
		// create new student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) // verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}