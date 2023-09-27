package org.babinkuk.vo;

import java.util.SortedMap;

import org.babinkuk.entity.Status;
import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

/**
 * instance of this class is used to represent instructor data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
public class InstructorVO extends UserVO {

	private int id;
	
	@DiffField
	private String firstName;
	
	@DiffField
	private String lastName;
	
	@DiffField
	private String email;
	
	@DiffField
	private Status status;
	
	@DiffField
	private SortedMap<String, String> images;
	
	@DiffField
	private Double salary;
	
	@DiffField
	private String youtubeChannel;
	
	@DiffField
	private String hobby;
	
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

	@Override
	public String toString() {
		return "InstructorVO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", images=" + images	+ ", status=" + status + ", salary=" + salary
				+ ", youtubeChannel=" + youtubeChannel+ ", hobby=" + hobby + "]";
	}

}
