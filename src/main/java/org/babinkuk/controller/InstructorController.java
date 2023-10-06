package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.validator.ActionType;
//import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.INSTRUCTORS;
import static org.babinkuk.config.Api.ROOT;

import java.util.Optional;;

@RestController
@RequestMapping(ROOT + INSTRUCTORS)
public class InstructorController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private InstructorService instructorService;
	
	private CourseService courseService;
	
//	@Autowired
//	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public InstructorController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public InstructorController(InstructorService instructorService, CourseService courseService) {
		this.instructorService = instructorService;
		this.courseService = courseService;
	}

	/**
	 * get instructor list
	 * expose GET "/instructors"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<InstructorVO>> getAllInstructors() {
		log.info("Called InstructorController.getAllInstructors");
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.getAllInstructors()));
	}
	
	/**
	 * get instructor
	 * expose GET "/instructors/{instructorId}"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{instructorId}")
	public ResponseEntity<InstructorVO> getInstructor(@PathVariable int instructorId) {
		//log.info("Called InstructorController.getInstructor(instructorId={})", instructorId);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.findById(instructorId)));
	}
	
	/**
	 * expose POST "/instructors"
	 * add new instructor
	 * 
	 * @param instructorVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addInstructor(
			@RequestBody InstructorVO instructorVO,@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		//log.info("Called InstructorController.addInstructor({})", mapper.writeValueAsString(instructorVO));
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		instructorVO.setId(0);
		
//		validatorFactory.getValidator(validationRole).validate(instructorVO, ActionType.CREATE, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.saveInstructor(instructorVO)));
	}
	
	/**
	 * expose PUT "/instructors"
	 * update instructor
	 * 
	 * @param instructorVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateInstructor(
			@RequestBody InstructorVO instructorVO,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		//log.info("Called InstructorController.updateInstructor({})", mapper.writeValueAsString(instructorVO));

//		validatorFactory.getValidator(validationRole).validate(instructorVO, ActionType.UPDATE, ValidatorType.INSTRUCTOR);

		return ResponseEntity.of(Optional.ofNullable(instructorService.saveInstructor(instructorVO)));
	}
	
	/**
	 * expose DELETE "instructors/{instructorId}"
	 * 
	 * @param instructorId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{instructorId}")
	public ResponseEntity<ApiResponse> deleteInstructor(
			@PathVariable int instructorId, 
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) {
		//log.info("Called InstructorController.deleteInstructor(InstructorId={}, validationRole={})", instructorId, validationRole);
		
//		validatorFactory.getValidator(validationRole).validate(instructorId, ActionType.DELETE, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.deleteInstructor(instructorId)));
	}
	
	/**
	 * enroll instructor on a course
	 * expose PUT "/{instructorId}/enroll/{courseId}"
	 * 
	 * @param instructorId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{instructorId}/enroll/{courseId}")
	public ResponseEntity<ApiResponse> enrollCourse(
			@PathVariable int instructorId,
			@PathVariable int courseId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		//log.info("Called CourseController.enrollInstructor(id={}) for courseId={}", instructorId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);
		
//		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.ENROLL, ValidatorType.INSTRUCTOR);
		
		//courseVO.setInstructorVO(instructorVO);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.setCourse(instructorVO, courseVO, ActionType.ENROLL)));
	}
	
	/**
	 * withdraw instructor from a course
	 * expose PUT "/{instructorId}/withdraw/{courseId}"
	 * 
	 * @param instructorId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{instructorId}/withdraw/{courseId}")
	public ResponseEntity<ApiResponse> withdrawCourse(
			@PathVariable int instructorId,
			@PathVariable int courseId,
			@RequestParam(name="validationRole", required = false) ValidatorRole validationRole) throws JsonProcessingException {
		log.info("Called CourseController.withdrawInstructor(id={}) for courseId={}", instructorId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);

//		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.WITHDRAW, ValidatorType.INSTRUCTOR);

		//courseVO.setInstructorVO(null);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.setCourse(instructorVO, courseVO, ActionType.WITHDRAW)));
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(Exception exc) {
		
		return new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectException exc) {

		return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage()).toEntity();
	}

	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectNotFoundException exc) {

		return new ApiResponse(HttpStatus.OK, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectValidationException exc) {
		ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
		apiResponse.setErrors(exc.getValidationErrors());
		return apiResponse.toEntity();
	}
}
