package org.babinkuk.entity;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.annotations.SortComparator;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Id
	// TABLE generation strategy is required when using TABLE_PER_CLASS inheritance
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	protected int id;
	
	@Column(name = "first_name")
	protected String firstName;
	
	@Column(name = "last_name")
	protected String lastName;
	
	@Column(name = "email")
	protected String email;
	
	@ElementCollection
	@CollectionTable(name = "image",
					joinColumns = @JoinColumn(name = "user_id"))
	@MapKeyColumn(name = "file_name") // column for map key
	@Column(name = "image_name") // column for map value
	//@OrderBy //default to map key asc column
	//@org.hibernate.annotations.OrderBy(clause = "image_name desc")
	@SortComparator(ReverseStringComparator.class)
	protected SortedMap<String, String> images = new TreeMap<String, String>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	protected Status status;
	
	// custom sorting implemenation
	public static class ReverseStringComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return o2.compareTo(o1);
		}
		
	}
	
	public User() {
	}

	public User(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public User(String firstName, String lastName, String email, Status status) {
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

	public SortedMap<String, String> getImages() {
		return images;
	}

	public void setImages(SortedMap<String, String> images) {
		this.images = images;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	// convenience method for bi-directional relationship
	public void addImage(String fileName, String imageName) {
		images.put(fileName, imageName);
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", images=" + images	+ ", status=" + status
				+ "]";
	}
}
