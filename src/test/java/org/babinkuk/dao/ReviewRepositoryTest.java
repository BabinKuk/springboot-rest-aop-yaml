package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ActiveProfiles("test")
@DataJpaTest
public class ReviewRepositoryTest {
	
	public static final Logger log = LogManager.getLogger(ReviewRepositoryTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
//	@Autowired
//	private TransactionTemplate transactionTemplate;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
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
	
	@BeforeAll
	public static void setup() {
		
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
	void getAllReviews() {
		
		// get all reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		//log.info(reviews);
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		List<Review> reviewList = new ArrayList<Review>();
		reviews.forEach(reviewList::add);

		assertTrue(reviewList.stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
	}
	
	@Test
	void getReview() {
		
		// get review id=1
		Optional<Review> review = reviewRepository.findById(1);
		
		// assert
		assertTrue(review.isPresent());
		assertNotNull(review,"review null");
		assertEquals(REVIEW, review.get().getComment(),"getComment() failure");
		assertEquals(1, review.get().getId(),"getId() failure");
		
		// get non-existing review id=2
		review = reviewRepository.findById(2);
		
		// assert
		assertFalse(review.isPresent());
	}

	@Test
	void updateReview() {
		
		// create review
		// set id=1: this is to force an update of existing item
		Review review = new Review(REVIEW_UPDATE);
		review.setId(1);
		
		Review savedReview = reviewRepository.save(review);
		
		// assert
		assertNotNull(savedReview,"savedReview null");
		assertEquals(REVIEW_UPDATE, savedReview.getComment(),"savedReview.getComment() failure");
		assertEquals(1, savedReview.getId(),"savedReview.getId() failure");
	}
	
	@Test
	void addReview() {
		
		// create review
		// set id=0: this is to force a save of new item
		Review review = new Review(REVIEW_NEW);
		review.setId(0);
		
		// get course with id=1
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		assertTrue(course.isPresent());
		assertEquals(1, course.get().getId(), "course.get().getId()");
		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
		assertEquals(1, course.get().getReviews().size(), "course.get().getReviews().size()");
		
		// add review to course
		course.get().addReview(review);
		
		// save course
		courseRepository.save(course.get());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		//log.info(reviews);
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
		List<Review> reviewList = new ArrayList<Review>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW) && rev.getId() == 1
		));
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW_NEW) && rev.getId() == 2
		));
	}

	@Test
	void deleteReview() {
		
		// set course for instructor
		jdbc.execute(sqlUpdateCourse);
		
		// check if review id=1 exists
		Optional<Review> review = reviewRepository.findById(1);
		
		// assert
		assertTrue(review.isPresent());
		
		// delete review
		reviewRepository.deleteById(1);
		
		review = reviewRepository.findById(1);
		
		// assert
		assertFalse(review.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		Optional<Course> course = courseRepository.findById(1);
		
		// assert
		// course must be unchanged except reviews (size=0)
		assertTrue(course.isPresent());
		assertEquals(1, course.get().getId(), "course.get().getId()");
		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
		assertEquals(0, course.get().getReviews().size(), "course.get().getReviews().size()");
		assertEquals(INSTRUCTOR_FIRSTNAME, course.get().getInstructor().getFirstName(), "course.get().getInstructor().getFirstName()");
		assertEquals(1, course.get().getStudents().size(), "course.get().getStudents().size()");
		assertTrue(course.get().getStudents().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
}
