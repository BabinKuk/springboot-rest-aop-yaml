package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.InstructorServiceImpl;
import org.babinkuk.utils.TestUtils;
import org.babinkuk.vo.InstructorVO;
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
import static org.babinkuk.config.Api.INSTRUCTORS;
import static org.babinkuk.config.Api.ROOT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class InstructorControllerTest {
	
	public static final Logger log = LogManager.getLogger(InstructorControllerTest.class);
	
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
	private InstructorService instructorService;
	
//	@Autowired
//	private CourseService courseService;
	
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
		log.info("BeforeAll");

		// init
		request = new MockHttpServletRequest();
	}
	
	@BeforeEach
    public void setupDatabase() {
		log.info("BeforeEach");
		
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
		log.info("AfterEach");

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
	void getAllInstructors() throws Exception {
		//log.info("getAllInstructors");
		
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, TestUtils.ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = TestUtils.createInstructor();
		
		instructorService.saveInstructor(instructorVO);
				
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, TestUtils.ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, TestUtils.ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
			//	.param(TestUtils.VALIDATION_ROLE, "TestUtils.ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, TestUtils.ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getInstructorRoleAdmin() throws Exception {

		getInstructor(TestUtils.ROLE_ADMIN);
	}
	
	@Test
	void getInstructorRoleInstructor() throws Exception {

		getInstructor(TestUtils.ROLE_INSTRUCTOR);
	}
	
	@Test
	void getInstructorRoleStudent() throws Exception {

		getInstructor(TestUtils.ROLE_STUDENT);
	}
	
	@Test
	void getInstructorNoRole() throws Exception {

		getInstructor("");
	}
	
	@Test
	void getInstructorRoleNotExist() throws Exception {

		getInstructor(TestUtils.ROLE_NOT_EXIST);
	}
	
	private void getInstructor(String validationRole) throws Exception {
		//log.info("getInstructor {}", validationRole);
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(TestUtils.INSTRUCTOR_FIRSTNAME))) // verify json element
			;

		// get instructor with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 2)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), 2)))) // verify json element
			;
	}
	
	@Test
	void addInstructorRoleAdmin() throws Exception {

		addInstructorSuccess(TestUtils.ROLE_ADMIN);
	}

	@Test
	void addInstructorRoleInstructor() throws Exception {

		addInstructorSuccess(TestUtils.ROLE_INSTRUCTOR);
	}
	
	private void addInstructorSuccess(String validationRole) throws Exception {
		//log.info("addInstructorSuccess {}", validationRole);
		
		// create instructor
		InstructorVO instructorVO = TestUtils.createInstructor();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		instructorVO = instructorService.findByEmail(TestUtils.INSTRUCTOR_EMAIL_NEW);
		
		log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmail(),"instructorVO.getEmail() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME_NEW, instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(TestUtils.INSTRUCTOR_LASTNAME_NEW, instructorVO.getLastName(),"instructorVO.getLastName() NOK");
	}
	
	@Test
	void addInstructorRoleStudent() throws Exception {

		addInstructorFail(TestUtils.ROLE_STUDENT);
	}
	
	@Test
	void addInstructorNoRole() throws Exception {
		
		addInstructorFail(null);
	}
	
	@Test
	void addInstructorRoleNotExist() throws Exception {
		
		addInstructorFail(TestUtils.ROLE_NOT_EXIST);
	}
	
	private void addInstructorFail(String validationRole) throws Exception {
		//log.info("addInstructorFail {}", validationRole);
		
		// create instructor
		InstructorVO instructorVO = TestUtils.createInstructor();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void updateInstructorRoleAdmin() throws Exception {

		updateInstructorSuccess(TestUtils.ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorRoleInstructor() throws Exception {

		updateInstructorSuccess(TestUtils.ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorSuccess(String validationRole) throws Exception {
		//log.info("updateInstructorSuccess {}", validationRole);
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO = TestUtils.updateExistingInstructor(instructorVO);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(TestUtils.INSTRUCTOR_FIRSTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.lastName", is(TestUtils.INSTRUCTOR_LASTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.email", is(TestUtils.INSTRUCTOR_EMAIL_UPDATED))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is(TestUtils.INSTRUCTOR_YOUTUBE_UPDATED))) // verify json element
			.andExpect(jsonPath("$.hobby", is(TestUtils.INSTRUCTOR_HOBBY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.status", is(TestUtils.INSTRUCTOR_STATUS_UPDATED.label))) // verify json element
			.andExpect(jsonPath("$.salary", is(TestUtils.INSTRUCTOR_SALARY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.images", IsMapWithSize.aMapWithSize(3))) // verify json element
			;
	}
	
	@Test
	void updateInstructorRoleStudent() throws Exception {

		updateInstructorFail(TestUtils.ROLE_STUDENT);
	}
	
	@Test
	void updateInstructorNoRole() throws Exception {

		updateInstructorFail("");
	}
	
	@Test
	void updateInstructorRoleNotExist() throws Exception {

		updateInstructorFail(TestUtils.ROLE_NOT_EXIST);
	}
	
	private	void updateInstructorFail(String validationRole) throws Exception {
		//log.info("updateInstructorFail {}", validationRole);
		
		// check if instructor id 1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
		
		// update instructor
		instructorVO = TestUtils.updateExistingInstructor(instructorVO);
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(TestUtils.INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(TestUtils.INSTRUCTOR_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.email", is(TestUtils.INSTRUCTOR_EMAIL))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is(TestUtils.INSTRUCTOR_YOUTUBE))) // verify json element
			.andExpect(jsonPath("$.hobby", is(TestUtils.INSTRUCTOR_HOBBY))) // verify json element
			;
	}
	
	@Test
	void deleteInstructorRoleAdmin() throws Exception {

		deleteInstructorSuccess(TestUtils.ROLE_ADMIN);
	}
	
	@Test
	void deleteInstructorRoleInstructor() throws Exception {

		deleteInstructorFail(TestUtils.ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInstructorRoleStudent() throws Exception {

		deleteInstructorFail(TestUtils.ROLE_STUDENT);
	}

	@Test
	void deleteInstructorNoRole() throws Exception {
		
		deleteInstructorFail(null);
	}
	
	@Test
	void deleteInstructorRoleNotExist() throws Exception {

		deleteInstructorFail(TestUtils.ROLE_NOT_EXIST);
	}
	
	private void deleteInstructorSuccess(String validationRole) throws Exception {
		//log.info("deleteInstructorSuccess {}", validationRole);
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(InstructorServiceImpl.INSTRUCTOR_DELETE_SUCCESS)))) // verify json element
			;
		
		// get instructor with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_instructor_id_not_found"), id)))) //verify json element
			;
	}
	
	private void deleteInstructorFail(String validationRole) throws Exception {
		//log.info("deleteInstructor {}", validationRole);
		
		// check if instructor id 1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"assertEquals instructorVO.getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param(TestUtils.VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
