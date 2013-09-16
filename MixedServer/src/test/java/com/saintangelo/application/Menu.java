package com.saintangelo.application;

import java.io.Serializable;
import java.util.List;

public class Menu implements Serializable {
	private static final long serialVersionUID = 4339146612471143343L;

	private String menuname;

	private Menu[] subMenus;

	private List<UserGroup> ugs;

	public Menu() {

	}

	public Menu(String menuname) {
		super();
		this.menuname = menuname;
	}

	public Menu(String menuname, Menu[] subMenus) {
		super();
		this.menuname = menuname;
		this.subMenus = subMenus;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public Menu[] getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(Menu[] subMenus) {
		this.subMenus = subMenus;
	}

	public List<UserGroup> getUgs() {
		return ugs;
	}

	public void setUgs(List<UserGroup> ugs) {
		this.ugs = ugs;
	}
}
