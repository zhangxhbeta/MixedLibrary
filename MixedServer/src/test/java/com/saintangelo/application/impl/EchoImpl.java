package com.saintangelo.application.impl;

import com.saintangelo.application.Echo;

public class EchoImpl implements Echo {

	@Override
	public String say(String what) {
		return what;
	}

}
