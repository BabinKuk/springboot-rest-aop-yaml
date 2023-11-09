package org.babinkuk.vo;

import java.util.List;
import java.util.SortedMap;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.babinkuk.entity.Status;
import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent instructor data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class InstructorVO extends UserVO {

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
	private Double salary;
	
	@DiffField
	private String youtubeChannel;
	
	@DiffField
	private String hobby;
	
	@DiffField
	private List<CourseVO> courses;
		
	public InstructorVO() {
		// TODO Auto-generated constructor stub
	}

	public InstructorVO(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public InstructorVO(String firstName, String lastName, String email, String youtubeChannel, String hobby) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.youtubeChannel = youtubeChannel;
		this.hobby = hobby;
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
	
	public String getYoutubeChannel() {
		return youtubeChannel;
	}

	public void setYoutubeChannel(String youtubeChannel) {
		this.youtubeChannel = youtubeChannel;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
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
	
	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public List<CourseVO> getCourses() {
		return courses;
	}

	public void setCourses(List<CourseVO> courses) {
		this.courses = courses;
	}

	@Override
	public String toString() {
		return "InstructorVO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", images=" + images + ", status=" + status + ", salary=" + salary
				+ ", courses=" + courses
				+ ", youtubeChannel=" + youtubeChannel+ ", hobby=" + hobby
				+ "]";
	}

}