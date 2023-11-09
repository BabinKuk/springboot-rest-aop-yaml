package org.babinkuk.entity;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
	
	ACTIVE("ACTIVE"),
	INACTIVE("INACTIVE"),
	BLOCKED("BLOCKED");
	
	public String label;
	
	Status(String label) {
		this.label = label;
	}

	@JsonValue
	public String getValue() {
		return this.label;
	}
	
	/**
	 * bind this String value with the Enum constant
	 * and set to null if wrong value
	 * @param value
	 * @return
	 */
	@JsonCreator
	public static Status fromString(String value) {
		if(StringUtils.isNotBlank(value)) {
			for (Status st : Status.values()) {
				if (String.valueOf(st.toString()).equalsIgnoreCase(value)) {
					return st;
				}
			}
		}
		return null;
	}
}
