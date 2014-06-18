package com.saintangelo.application.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.saintangelo.application.Echo;

@Component
public class EchoImpl implements Echo {

	@Override
	public String say(String what) {
		return what;
	}

}
