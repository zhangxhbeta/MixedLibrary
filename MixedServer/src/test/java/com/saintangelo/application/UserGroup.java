package com.saintangelo.application;

import java.io.Serializable;

/**
 * �û������
 * 
 * @author zhangxha
 * 
 */
public class UserGroup implements Serializable {
	private static final long serialVersionUID = -8444397503755159258L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
