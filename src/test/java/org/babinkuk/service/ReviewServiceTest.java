package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Review;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReviewServiceTest {
	
	public static final Logger log = LogManager.getLogger(ReviewServiceTest.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private JdbcTemplate jdbc;
	
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
	void getAllReviews() {
		
		// get all reviews
		Iterable<ReviewVO> reviews = reviewService.getAllReviews();
		
		// assert
		if (reviews instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		// add new review
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id=0: this is to force a save of new item
		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
		reviewVO.setId(0);
		
		// add to course
		courseVO.addReviewVO(reviewVO);
		
		reviewService.saveReview(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all reviews
		reviews = reviewService.getAllReviews();
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
		List<ReviewVO> reviewList = new ArrayList<ReviewVO>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW) && rev.getId() == 1
		));
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW_NEW) && rev.getId() == 2
		));
	}
	
	@Test
	void getReview() {
		
		// get review id=1
		ReviewVO reviewVO = reviewService.findById(1);
		
		assertNotNull(reviewVO,"reviewVO null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"getComment() null");
		assertEquals(REVIEW, reviewVO.getComment(),"getComment() failure");
		
		assertNotEquals("test review ", reviewVO.getComment(),"getComment() intentional failure");
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(2);
		});
		
		String expectedMessage = "Review with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addReview() {
		
		// first find course
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
		reviewVO.setId(0);
		
		// add to course
		courseVO.addReviewVO(reviewVO);
		
		reviewService.saveReview(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all reviews
		Iterable<ReviewVO> reviews = reviewService.getAllReviews();
		//log.info(reviews);
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
		List<ReviewVO> reviewList = new ArrayList<ReviewVO>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW) && rev.getId() == 1
		));
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW_NEW)
		));
	}
	
	@Test
	void updateReview() {
		
		// create review
		// set id=1: this is to force an update of existing item
		ReviewVO review = new ReviewVO(REVIEW_UPDATE);
		review.setId(1);
		
		reviewService.saveReview(review);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		ReviewVO savedReview = reviewService.findById(1);
		
		// assert
		assertNotNull(savedReview,"savedReview null");
		assertEquals(REVIEW_UPDATE, savedReview.getComment(),"savedReview.getComment() failure");
		assertEquals(1, savedReview.getId(),"savedReview.getId() failure");
	}
	
	@Test
	void deleteReview() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);
		
		// first get review
		ReviewVO reviewVO = reviewService.findById(1);
		
		// assert
		assertNotNull(reviewVO, "return null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"getComment() null");
		
		// delete review
		reviewService.deleteReview(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		// course must be unchanged except reviews (size=0)
		assertNotNull(courseVO, "courseVO null");
		assertEquals(1, courseVO.getId(), "course.getId()");
		assertEquals(COURSE, courseVO.getTitle(), "course.getTitle()");
		assertEquals(0, courseVO.getReviewsVO().size(), "course.getReviews().size()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(), "course.getInstructor().getFirstName()");
		assertEquals(1, courseVO.getStudentsVO().size(), "course.getStudents().size()");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(1);
		});
				
		String expectedMessage = "Review with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing review
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			reviewService.deleteReview(2);
		});
	}
}