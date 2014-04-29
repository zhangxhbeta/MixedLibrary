package com.saintangelo.application;

import java.util.List;

public class GenericClassType<T> {

	private String name;

	private List<T> persons;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getPersons() {
		return persons;
	}

	public void setPersons(List<T> persons) {
		this.persons = persons;
	}

}
