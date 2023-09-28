package org.babinkuk.vo;

import javax.validation.constraints.NotBlank;

import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

/**
 * instance of this class is used to represent review data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
public class ReviewVO {

	private int id;
	
	@DiffField
	@NotBlank(message = "error_code_review_empty")
	private String comment;
	
	public ReviewVO() {
		// TODO Auto-generated constructor stub
	}
	
	public ReviewVO(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
		
	@Override
	public String toString() {
		return "ReviewVO [id=" + id + ", comment=" + comment + "]";
	}	
}
