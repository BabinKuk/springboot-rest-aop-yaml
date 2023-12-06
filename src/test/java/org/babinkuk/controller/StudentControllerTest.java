package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.StudentService;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.StudentVO;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class StudentControllerTest {
	
	public static final Logger log = LogManager.getLogger(StudentControllerTest.class);
	
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
	
	@Autowired
	private CourseService courseService;
	
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
	void getAllStudents() throws Exception {

		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		studentService.saveStudent(studentVO);
				
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
			//	.param(TestUtils.VALIDATION_ROLE, "TestUtils.ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getStudentRoleAdmin() throws Exception {

		getStudent(ROLE_ADMIN);
	}
	
	@Test
	void getStudentRoleInstructor() throws Exception {

		getStudent(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getStudentRoleStudent() throws Exception {

		getStudent(ROLE_STUDENT);
	}
	
	@Test
	void getStudentNoRole() throws Exception {

		getStudent("");
	}
	
	@Test
	void getStudentRoleNotExist() throws Exception {

		getStudent(ROLE_NOT_EXIST);
	}
	
	private void getStudent(String validationRole) throws Exception {
		
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			;

		// get student with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 22)))) // verify json element
			;
	}
	
	@Test
	void addStudentRoleAdmin() throws Exception {
	
		addStudentSuccess(ROLE_ADMIN);
	}

	@Test
	void addStudentRoleInstructor() throws Exception {

		addStudentSuccess(ROLE_INSTRUCTOR);
	}
	
	private void addStudentSuccess(String validationRole) throws Exception {
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
	}
	
	@Test
	void addStudentRoleStudent() throws Exception {

		addStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void addStudentNoRole() throws Exception {

		addStudentFail("");
	}
	
	private void addStudentFail(String validationRole) throws Exception {
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addStudentRoleNotExist() throws Exception {
		
		String validationRole = ROLE_NOT_EXIST;
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
			;
	}
	
	@Test
	void updateStudentRoleAdmin() throws Exception {

		updateStudentSuccess(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentRoleInstructor() throws Exception {

		updateStudentSuccess(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentSuccess(String validationRole) throws Exception {
		
		// check if student id 2 exists
		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO = ApplicationTestUtils.updateExistingStudent(studentVO);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.email", is(STUDENT_EMAIL_UPDATED))) // verify json element
			.andExpect(jsonPath("$.status", is(STUDENT_STATUS_UPDATED.label))) // verify json element
			.andExpect(jsonPath("$.street", is(STUDENT_STREET_UPDATED))) // verify json element
			.andExpect(jsonPath("$.city", is(STUDENT_CITY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.zipCode", is(STUDENT_ZIPCODE_UPDATED))) // verify json element
			.andExpect(jsonPath("$.images", IsMapWithSize.aMapWithSize(3))) // verify json element
			;
	}
	
	@Test
	void updateStudentRoleStudent() throws Exception {

		updateStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void updateStudentNoRole() throws Exception {

		updateStudentFail(null);
	}
	
	@Test
	void updateStudentRoleNotExist() throws Exception {

		updateStudentFail(ROLE_NOT_EXIST);
	}
	
	private void updateStudentFail(String validationRole) throws Exception {
		
		// check if student id 2 exists
		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		studentVO = ApplicationTestUtils.updateExistingStudent(studentVO);
						
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.email", is(STUDENT_EMAIL))) // verify json element
			;
	}
	
	@Test
	void deleteStudentRoleAdmin() throws Exception {
		
		String validationRole = ROLE_ADMIN;
		
		// check if student id 2 exists
		int id = 2;
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_DELETE_SUCCESS)))) // verify json element
			;
		
		// get student with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), id)))) //verify json element
			;
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id=1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(COURSE))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO").doesNotExist()) // verify json element
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is(REVIEW))) // verify json element
			;
	}
	
	@Test
	void deleteStudentRoleInstructor() throws Exception {

		deleteStudentFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteStudentRoleStudent() throws Exception {

		deleteStudentFail(ROLE_STUDENT);
	}

	@Test
	void deleteStudentNoRole() throws Exception {

		deleteStudentFail("");
	}
	
	@Test
	void deleteStudentRoleNotExist() throws Exception {

		deleteStudentFail(ROLE_NOT_EXIST);
	}
	
	private void deleteStudentFail(String validationRole) throws Exception {
		
		// check if student id 2 exists
		int id = 2;
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"assertEquals getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void enrollCourseRoleAdmin() throws Exception {

		enrollCourseSuccess(ROLE_ADMIN);
	}

	@Test
	void enrollCourseRoleInstructor() throws Exception {

		enrollCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. create new student
	 * 3. associate new student with course
	 * 4. validate
	 */
	private void enrollCourseSuccess(String validationRole) throws Exception {
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 22)))) // verify json element
			;
	}
	
	@Test
	void enrollCourseRoleStudent() throws Exception {

		enrollCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void enrollCourseNoRole() throws Exception {

		enrollCourseFail(null);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. create new student
	 * 3. associate new student with course
	 * 4. validate
	 */
	private void enrollCourseFail(String validationRole) throws Exception {

		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		// save student
		studentService.saveStudent(studentVO);
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 22)))) // verify json element
			;
	}
	
	@Test
	void enrollCourseRoleNotExist() throws Exception {

		String validationRole = ROLE_NOT_EXIST;
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudent();
		
		// save student
		studentService.saveStudent(studentVO);
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element id
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element id
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void withdrawCourseRoleAdmin() throws Exception {

		withdrawCourseSuccess(ROLE_ADMIN);
	}
	
	@Test
	void withdrawCourseRoleInstructor() throws Exception {

		withdrawCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. withdraw new student from course
	 * 4. validate
	 */
	private void withdrawCourseSuccess(String validationRole) throws Exception {
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 22)))) // verify json element
			;
	}
	
	@Test
	void withdrawCourseRoleStudent() throws Exception {

		withdrawCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void withdrawCourseNoRole() throws Exception {

		withdrawCourseFail(null);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. withdraw new student from course
	 * 4. validate
	 */
	private void withdrawCourseFail(String validationRole) throws Exception {
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_course_id_not_found"), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_student_id_not_found"), 22)))) // verify json element
			;
	}
	
	@Test
	void withdrawCourseRoleNotExist() throws Exception {
		
		String validationRole = ROLE_NOT_EXIST;
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	private void validateStudent() {

		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals(STUDENT_STREET, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		assertEquals(STUDENT_STATUS, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(2, studentVO.getImages().size(), "studentVO.getImages size not 2");
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
	}
	
	private void validateCourse(CourseVO courseVO) {
		
		// check if course id=1 exists
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		//assertEquals(1, courseVO.getStudentsVO().size());
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
