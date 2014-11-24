package com.saintangelo.application.impl;

import org.springframework.stereotype.Component;

import com.saintangelo.application.Echo;
import com.saintangelo.application.Person;

@Component
public class EchoImpl implements Echo {

	@Override
	public String say(String what) {
		return what;
	}

	@Override
	public Person createPerson() {
		return null;
	}

}
