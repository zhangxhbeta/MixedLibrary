package com.saintangelo.application;

import java.io.Serializable;

public class Person implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7252062150026518876L;

	byte[] image;

	Byte[] image2;

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Byte[] getImage2() {
		return image2;
	}

	public void setImage2(Byte[] image2) {
		this.image2 = image2;
	}
}
