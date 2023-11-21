package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.TestUtils;
import org.babinkuk.vo.InstructorVO;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class InstructorServiceTest {
	
	public static final Logger log = LogManager.getLogger(InstructorServiceTest.class);
	
	@Autowired
	private InstructorService instructorService;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private JdbcTemplate jdbc;
	
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
	void getInstructorById() {
		//log.info("getInstructorByid");
		
		InstructorVO instructorVO = instructorService.findById(1);
		
		validatePrimaryInstructor(instructorVO);
	}
	
	@Test
	void getInstructorByEmail() {
		//log.info("getInstructorByEmail");
		
		InstructorVO instructorVO = instructorService.findByEmail(TestUtils.INSTRUCTOR_EMAIL);
		log.info(instructorVO);
		validatePrimaryInstructor(instructorVO);
	}
	
	@Test
	void addInstructor() {
		//log.info("addInstructor");
		
		// create instructor
		InstructorVO instructorVO = TestUtils.createInstructor();
		
		instructorService.saveInstructor(instructorVO);
		
		InstructorVO instructorVO2 = instructorService.findByEmail(TestUtils.INSTRUCTOR_EMAIL_NEW);
		
		//log.info(instructorVO2);

		// assert
		//assertEquals(2, instructorVO2.getId());
		assertEquals(instructorVO.getFirstName(), instructorVO2.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(instructorVO.getLastName(), instructorVO2.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals(instructorVO.getEmail(), instructorVO2.getEmail(),"instructorVO.getEmail() NOK");
		assertEquals(instructorVO.getYoutubeChannel(), instructorVO2.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals(instructorVO.getHobby(), instructorVO2.getHobby(),"instructorVO.getHobby() NOK");
	}	
	
	@Test
	void updateInstructor() {
		//log.info("updateInstructor");
		
		InstructorVO instructorVO = instructorService.findById(1);
				
		// update with new data
		instructorVO = TestUtils.updateExistingInstructor(instructorVO);
		
		instructorService.saveInstructor(instructorVO);
		
		// fetch again
		InstructorVO instructorVO2 = instructorService.findById(1);
		
		// assert
		assertEquals(instructorVO.getId(), instructorVO2.getId());
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME_UPDATED, instructorVO2.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(TestUtils.INSTRUCTOR_LASTNAME_UPDATED, instructorVO2.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals(TestUtils.INSTRUCTOR_EMAIL_UPDATED, instructorVO2.getEmail(),"instructorVO.getEmail() NOK");
		assertEquals(TestUtils.INSTRUCTOR_YOUTUBE_UPDATED, instructorVO2.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals(TestUtils.INSTRUCTOR_HOBBY_UPDATED, instructorVO2.getHobby(),"instructorVO.getHobby() NOK");
		assertEquals(3, instructorVO.getImages().size(), "instructors.getImages size not 3");
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(TestUtils.INSTRUCTOR_FILE_1, TestUtils.INSTRUCTOR_IMAGE_1));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(TestUtils.INSTRUCTOR_FILE_11, TestUtils.INSTRUCTOR_IMAGE_11));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(TestUtils.INSTRUCTOR_FILE_UPDATED, TestUtils.INSTRUCTOR_IMAGE_UPDATED));
	}
	
	@Test
	void deleteInstructor() {
		//log.info("deleteInstructor");
		
		// first get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		// assert
		assertNotNull(instructorVO, "return true");
		assertEquals(1, instructorVO.getId());
		
		// delete
		instructorService.deleteInstructor(1);
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(1);
		});
				
		String expectedMessage = "Instructor with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing instructor
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			instructorService.deleteInstructor(2);
		});
	}
	
	@Test
	void getAllInstructors() {
		//log.info("getAllInstructors");
		
		Iterable<InstructorVO> instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		// add another instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = TestUtils.createInstructor();
				
		instructorService.saveInstructor(instructorVO);
		
		instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) instructors).size(), "instructors size not 2 after insert");
		}
	}
	
	private void validatePrimaryInstructor(InstructorVO instructorVO) {
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"instructorVO.getLastName() null");
		assertNotNull(instructorVO.getEmail(),"instructorVO.getEmail() null");
		assertNotNull(instructorVO.getSalary(),"instructorVO.getSalary() null");
		assertNotNull(instructorVO.getStatus(),"instructorVO.getStatus() null");
		assertNotNull(instructorVO.getImages(),"instructorVO.getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"instructorVO.getHobby() null");
		assertEquals(TestUtils.INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"instructorVO.getFirstName() NOK");
		assertEquals(TestUtils.INSTRUCTOR_LASTNAME, instructorVO.getLastName(),"instructorVO.getLastName() NOK");
		assertEquals(TestUtils.INSTRUCTOR_EMAIL, instructorVO.getEmail(),"instructorVO.getEmail() NOK");
		assertEquals(1000, instructorVO.getSalary(),"instructorVO.getSalary() NOK");
		assertEquals(Status.ACTIVE, instructorVO.getStatus(),"instructorVO.getStatus() NOK");
		assertEquals(2, instructorVO.getImages().size(), "instructors.getImages size not 2");
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(TestUtils.INSTRUCTOR_FILE_1, TestUtils.INSTRUCTOR_IMAGE_1));
		assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(TestUtils.INSTRUCTOR_FILE_11, TestUtils.INSTRUCTOR_IMAGE_11));
		assertEquals(TestUtils.INSTRUCTOR_YOUTUBE, instructorVO.getYoutubeChannel(),"instructorVO.getYoutubeChannel() NOK");
		assertEquals(TestUtils.INSTRUCTOR_HOBBY, instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"instructorVO.getHobby() NOK");
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(22);
		});
		
		String expectedMessage = "Instructor with id=22 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
}
