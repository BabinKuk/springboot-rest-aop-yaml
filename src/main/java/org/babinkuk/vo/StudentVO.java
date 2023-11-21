package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.babinkuk.entity.Status;
import org.babinkuk.vo.diff.DiffField;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent student data
 * 
 * @author BabinKuk
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class StudentVO extends UserVO {
	
	private int id;
	
	@DiffField
	@NotBlank(message = "error_code_first_name_empty")
	private String firstName;
	
	@DiffField
	@NotBlank(message = "error_code_last_name_empty")
	private String lastName;
	
	@DiffField
	@NotBlank(message = "error_code_email_empty")
	@Email(message = "error_code_email_invalid", regexp="[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
	private String email;
	
	@DiffField
	@NotNull(message = "error_code_status_invalid")
	private Status status;
	
	@DiffField
	private SortedMap<String, String> images;
	
	@DiffField
	private String street;
	
	@DiffField
	private String city;
	
	@DiffField
	private String zipCode;
	
	@DiffField
	private List<CourseVO> coursesVO = new ArrayList<CourseVO>();
	
	public StudentVO() {
		// TODO Auto-generated constructor stub
	}
	
	public StudentVO(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public StudentVO(String firstName, String lastName, String email, Status status) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.status = status;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<CourseVO> getCoursesVO() {
		return coursesVO;
	}

	public void setCoursesVO(List<CourseVO> coursesVO) {
		this.coursesVO = coursesVO;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setImages(SortedMap<String, String> images) {
		this.images = images;
	}

	public SortedMap<String, String> getImages() {
		return images;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	@Override
	public String toString() {
		return "StudentVO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", images=" + images	+ ", status=" + status
				+ ", street=" + street + ", city=" + city + ", zipCode=" + zipCode
				//+ ", courses=" + coursesVO
				+ "]";
	}
}
