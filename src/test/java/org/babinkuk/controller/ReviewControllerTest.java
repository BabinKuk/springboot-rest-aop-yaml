package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.service.ReviewServiceImpl;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.config.Api.REVIEWS;
import static org.babinkuk.config.Api.ROOT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ReviewControllerTest {
	
	public static final Logger log = LogManager.getLogger(ReviewControllerTest.class);
	
	private static String ROLE_ADMIN = "ROLE_ADMIN";
	private static String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
	private static String ROLE_STUDENT = "ROLE_STUDENT";
	private static String ROLE_NOT_EXIST = "ROLE_NOT_EXIST";
	
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
	void getAllReviews() throws Exception {
		log.info("getAllReviews");
		
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another review
		// first find course
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO("new review");
		reviewVO.setId(0);
		
		// add to course
		courseVO.addReviewVO(reviewVO);
		
		reviewService.saveReview(courseVO);
		
		// get all reviews (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", "")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param("validationRole", ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getReviewRoleAdmin() throws Exception {
		
		getReview(ROLE_ADMIN);
	}
	
	@Test
	void getReviewRoleInstructor() throws Exception {
		
		getReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getReviewRoleStudent() throws Exception {
		
		getReview(ROLE_STUDENT);
	}
	
	@Test
	void getReviewNoRole() throws Exception {
		
		getReview("");
	}
	
	@Test
	void getReviewRoleNotExist() throws Exception {
		
		getReview(ROLE_NOT_EXIST);
	}
	
	private void getReview(String validationRole) throws Exception {
		log.info("getReview {}", validationRole);
	
		//if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR) || validationRole.equals(ROLE_STUDENT)) {
			// get review with id=1
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
				.andExpect(jsonPath("$.comment", is("test review"))) // verify json element
				;

			// get review with id=2 (non existing)
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 2)
					.param("validationRole", ROLE_ADMIN)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), 2)))) // verify json element
				;

	}
	@Test
	void addReviewRoleAdmin() throws Exception {

		addReview(ROLE_ADMIN);
	}
	
	@Test
	void addReviewRoleInstructor() throws Exception {

		addReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addReviewRoleStudent() throws Exception {

		addReview(ROLE_STUDENT);
	}
	
	@Test
	void addReviewNoRole() throws Exception {
			
		addReview(null);
	}
	
	@Test
	void addReviewRoleNotExist() throws Exception {
			
		addReview(ROLE_NOT_EXIST);
	}
	
	private void addReview(String validationRole) throws Exception {
		log.info("addReview {}", validationRole);
		
		// create review
		ReviewVO reviewVO = new ReviewVO("another review");
		
		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR) || validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(getMessage(ReviewServiceImpl.REVIEW_SAVE_SUCCESS)))) // verify json element
				;
			
			// additional check
			// get all reviews
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
				.andDo(MockMvcResultHandlers.print())
				;
		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
			
			// additional check
			// get all reviews
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
				;
		}
	}
	
	@Test
	void updateReviewRoleAdmin() throws Exception {

		updateReview(ROLE_ADMIN);
	}
	
	@Test
	void updateReviewRoleInstructor() throws Exception {

		updateReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateReviewRoleStudent() throws Exception {

		updateReview(ROLE_STUDENT);
	}
	
	@Test
	void updateReviewNoRole() throws Exception {
		
		updateReview("");
	}
	
	@Test
	void updateReviewRoleNotExist() throws Exception {
		
		updateReview(ROLE_NOT_EXIST);
	}
	
	private void updateReview(String validationRole) throws Exception {
		log.info("updateReview {}", validationRole);
		
		// check if review id 1 exists
		ReviewVO reviewVO = reviewService.findById(1);
		log.info(reviewVO.toString());
		
		assertNotNull(reviewVO,"reviewVO null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"reviewVO.getComment() null");
		assertEquals("test review", reviewVO.getComment(),"assertEquals reviewVO.getComment() failure");
		
		// update review
		reviewVO.setComment("another review");
		
		if (validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(getMessage(ReviewServiceImpl.REVIEW_SAVE_SUCCESS)))) // verify json element
				;
			
			// additional check
			// get review with id=1
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
				.andExpect(jsonPath("$.comment", is("another review"))) // verify json element
				;
		} else if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
				;
		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param("validationRole", validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
		}
	}
	
	@Test
	void deleteReviewRoleAdmin() throws Exception {
		
		deleteReview(ROLE_ADMIN);
	}
	
	@Test
	void deleteReviewRoleInstructor() throws Exception {
		
		deleteReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteReviewRoleStudent() throws Exception {
		
		deleteReview(ROLE_STUDENT);
	}
	
	@Test
	void deleteReviewNoRole() throws Exception {
		
		deleteReview(null);
	}
	
	@Test
	void deleteReviewRoleNotExist() throws Exception {
		
		deleteReview(ROLE_NOT_EXIST);
	}

	private void deleteReview(String validationRole) throws Exception {
		log.info("deleteReview {}", validationRole);
		
		// check if review id 1 exists
		int id = 1;
		ReviewVO reviewVO = reviewService.findById(id);
		log.info(reviewVO.toString());
		
		assertNotNull(reviewVO,"reviewVO null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"reviewVO.getComment() null");
		assertEquals("test review", reviewVO.getComment(),"assertEquals reviewVO.getComment() failure");
		
		// delete review
		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT) || validationRole.equals(ROLE_INSTRUCTOR)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
				;
		} else if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(getMessage(ReviewServiceImpl.REVIEW_DELETE_SUCCESS)))) // verify json element
				;
			
			// get review with id=1 (non existing)
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				).andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_review_id_not_found"), id)))) //verify json element
				;
		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param("validationRole", validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
		}
	}
		
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
