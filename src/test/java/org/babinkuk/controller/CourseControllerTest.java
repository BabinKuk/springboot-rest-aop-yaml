package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import static org.hamcrest.Matchers.contains;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class CourseControllerTest {
	
	public static final Logger log = LogManager.getLogger(CourseControllerTest.class);
	
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
	private CourseService courseService;
	
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.course.update}")
	private String sqlUpdateCourse;
	
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
	void getAllCourses() throws Exception {
		
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		courseService.saveCourse(courseVO);
				
		// get all courses (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
			//	.param(VALIDATION_ROLE, "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all courses (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getCourse() throws Exception {
		
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(COURSE))) // verify json element
			;

		// get course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
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
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			;

		// get course with id=2 (non existing) (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			;

		// get course with id=2 (non existing) (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
		
		// get course with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				//.param(VALIDATION_ROLE, "ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			.andExpect(jsonPath("$.studentsVO[0].email", is(STUDENT_EMAIL)))
			;
		
		// get course with id=2 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				//.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
		
		// get course with id=1 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				//.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			.andExpect(jsonPath("$.studentsVO[0].lastName", is(STUDENT_LASTNAME)))
			;
	
		// get course with id=2 (non existing) (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
	}
	
	@Test
	void addCourseRoleAdmin() throws Exception {

		addCourseSuccess(ROLE_ADMIN);
	}

	@Test
	void addCourseRoleInstructor() throws Exception {

		addCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	private void addCourseSuccess(String validationRole) throws Exception {
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			.andExpect(jsonPath("$[0].title", is(COURSE)))
			.andExpect(jsonPath("$[1].title", is(COURSE_NEW)))
			;
	}
	
	@Test
	void addCourseRoleStudent() throws Exception {

		addCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void addCourseNoRole() throws Exception {

		addCourseFail(null);
	}
	
	private void addCourseFail(String validationRole) throws Exception {
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is(COURSE)))
			;
	}
	
	@Test
	void addCourseRoleNotExist() throws Exception {
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourse();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			.andExpect(jsonPath("$[0].title", is(COURSE)))
			;
	}
	
	@Test
	void updateCourseRoleAdmin() throws Exception {

		updateCourseSuccess(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseRoleInstructor() throws Exception {

		updateCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseSuccess(String validationRole) throws Exception {
		
		String courseTitle = COURSE_UPDATED;
		
		// update course id=1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(COURSE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(courseTitle))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
		
		// update course id=1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
			
		// update course id=1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(getMessage("validation_failed")))) // verify json element
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.errors", contains(getMessage(ValidatorCodes.ERROR_CODE_TITLE_EMPTY.getMessage())))) // verify json element
			;
	}
	
	@Test
	void updateCourseRoleStudent() throws Exception {

		updateCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void updateCourseNoRole() throws Exception {

		updateCourseFail(null);
	}
	
	private void updateCourseFail(String validationRole) throws Exception {
		
		String courseTitle = COURSE_UPDATED;
		
		// update course id=1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get course with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(COURSE))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.studentsVO[0].firstName", is(STUDENT_FIRSTNAME)))
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element
			;
		
		// update course id=1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
		
		// update course id 1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void updateCourseRoleNotExist() throws Exception {
		
		String courseTitle = COURSE_UPDATED;
				
		// update course id=1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id=1 without title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				//.param("title", courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update course id=1 with empty title (required parameter)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.param("title", "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseRoleAdmin() throws Exception {
		
		String validationRole = ROLE_ADMIN;
		
		// check if course id=1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
				
		// delete course
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(COURSE_DELETE_SUCCESS)))) // verify json element
			;
		
		// get course with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), id)))) //verify json element
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
		
		// get student with id=1 (coursesVO does not exist/empty)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id=2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify json root element
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), 1)))) // verify json element
			;

	}
	
	@Test
	void deleteCourseRoleInstructor() throws Exception {

		deleteCourseFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteCourseRoleStudent() throws Exception {

		deleteCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void deleteCourseNoRole() throws Exception {

		deleteCourseFail(null);
	}
	
	private void deleteCourseFail(String validationRole) throws Exception {
		
		// check if course id=1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete course
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteCourseRoleNotExist() throws Exception {
		
		// check if course id=1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// delete course
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + COURSES + "/{id}", id)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
