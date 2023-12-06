package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.ReviewService;
import org.babinkuk.vo.ReviewVO;
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
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ReviewValidatorTest {
	
	public static final Logger log = LogManager.getLogger(ReviewValidatorTest.class);
	
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
	private ReviewService reviewService;
	
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
	void addEmptyReviewRoleAdmin() throws Exception {

		addEmptyReview(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyReviewRoleInstructor() throws Exception {

		addEmptyReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addEmptyReviewRoleStudent() throws Exception {

		addEmptyReview(ROLE_STUDENT);
	}
	
	private void addEmptyReview(String validationRole) throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.fieldErrors[0]", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errorCount", is(1))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
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
	void addEmptyReviewNoRole() throws Exception {
		
		//String validationRole = ROLE_STUDENT;
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.fieldErrors[0]", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errorCount", is(1))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addReviewInvalidCourseRoleAdmin() throws Exception {

		addReviewInvalidCourse(ROLE_ADMIN);
	}
	
	@Test
	void addReviewInvalidCourseRoleInstructor() throws Exception {

		addReviewInvalidCourse(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addReviewInvalidCourseRoleStudent() throws Exception {

		addReviewInvalidCourse(ROLE_STUDENT);
	}
	
	private void addReviewInvalidCourse(String validationRole) throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id=2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
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
	void addReviewRoleInvalidCourseNoRole() throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id=2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage(COURSE_ID_NOT_FOUND), 2)))) // verify json element.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateInvalidReviewRoleAdmin() throws Exception {
		
		updateInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void updateInvalidReviewRoleInstructor() throws Exception {
		
		updateInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateInvalidReviewRoleStudent() throws Exception {
		
		updateInvalidReview(ROLE_STUDENT);
	}
	
	private void updateInvalidReview(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) // verify json element
			;
		
		// create invalid review (set invalid id=2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		if (validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath("$.message", is(String.format(getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.UPDATE)))) // verify json root element message
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) // verify json element
				;
		}	
	}
	
	@Test
	void updateInvalidReviewNoRole() throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
			//	.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) // verify json element
			;
		
		// create invalid review (set invalid id 2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteInvalidReviewRoleAdmin() throws Exception {
		
		deleteInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void deleteInvalidReviewRoleInstructor() throws Exception {
		
		deleteInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInvalidReviewRoleStudent() throws Exception {
		
		deleteInvalidReview(ROLE_STUDENT);
	}

	private void deleteInvalidReview(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if review id =2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) // verify json element
			;
		
		// delete review
		if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) //verify json element
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
				;
		}
	}
	
	@Test
	void deleteInvalidReviewNoRole() throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				//.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage(REVIEW_ID_NOT_FOUND), id)))) // verify json element
			;
				
		// delete review
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
			//	.param(VALIDATION_ROLE, ROLE_STUDENT)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
