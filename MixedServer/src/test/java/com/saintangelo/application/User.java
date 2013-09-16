package com.saintangelo.application;

import java.io.Serializable;

/**
 * �û�����
 * 
 * @author zhangxha
 * 
 */
public class User implements Serializable {
	private static final long serialVersionUID = -4570262281574364800L;

	private int id;

	private String name;

	private String email;

	private String phone;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
