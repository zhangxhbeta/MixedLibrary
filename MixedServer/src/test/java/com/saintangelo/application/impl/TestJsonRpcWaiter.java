package com.saintangelo.application.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saintangelo.application.Menu;
import com.saintangelo.application.Person;
import com.saintangelo.application.PersonExt;
import com.saintangelo.application.TestJsonRpc;
import com.saintangelo.application.User;
import com.saintangelo.application.UserGroup;

public class TestJsonRpcWaiter implements TestJsonRpc {

	@Override
	public float add(float a, float b) {
		return a + b;
	}

	@Override
	public int calcCommaNum(String s) {
		int count = 0;
		char[] d = s.toCharArray();
		for (int i = 0; i < d.length; i++) {
			if (d[i] == ',') {
				count++;
			}
		}
		return count;
	}

	@Override
	public String concat(String a, String b) {
		return a + b;
	}

	@Override
	public boolean hasComma(String s) {
		return s.contains(",");
	}

	@Override
	public Boolean hasComma2(String[] s) {
		for (int i = 0; i < s.length; i++) {
			if (s[i].contains(",")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public UserGroup queryUserGroup(User user) {
		UserGroup ug = new UserGroup();
		ug.setName(user.getName() + "所在的部门");
		return ug;
	}

	@Override
	public UserGroup queryUserGroup(User user, boolean parttime) {
		UserGroup ug = new UserGroup();
		ug.setName("信息部-开发组" + parttime);
		return ug;
	}

	@Override
	public List<UserGroup> queryUserGroups(boolean all) {
		List<UserGroup> list = new ArrayList<UserGroup>();

		UserGroup ug = new UserGroup();
		ug.setName("信息部-开发组");
		list.add(ug);

		if (all) {

			ug = new UserGroup();
			ug.setName("信息部-实施组");
			list.add(ug);
		}

		return list;
	}

	@Override
	public Menu[] getVisible() {
		Menu[] returnMenus = new Menu[4];

		returnMenus[0] = new Menu("门店管理");

		Menu subMenu = new Menu("功能1");
		Menu subMenu2 = new Menu("功能2");
		Menu subMenu3 = new Menu("功能3");
		Menu subMenu4 = new Menu("功能4");

		returnMenus[0].setSubMenus(new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });

		returnMenus[1] = new Menu("系统管理");
		returnMenus[1].setSubMenus(new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });

		returnMenus[2] = new Menu("货品管理");
		returnMenus[2].setSubMenus(new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });

		returnMenus[3] = new Menu("店员管理");
		returnMenus[3].setSubMenus(new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });

		return returnMenus;
	}

	@Override
	public Map<String, UserGroup> getUserGroupMap() {
		Map<String, UserGroup> map = new HashMap<String, UserGroup>();

		UserGroup ug = new UserGroup();
		ug.setName("信息部-开发组");

		map.put("信息部-开发组", ug);

		ug = new UserGroup();
		ug.setName("信息部-实施组");

		map.put("信息部-开发组", ug);

		return map;
	}

	@Override
	public List<Menu> getVisibleMenuList() {
		List<Menu> returnMenus = new ArrayList<Menu>();

		Menu subMenu = new Menu("功能1");
		Menu subMenu2 = new Menu("功能2");
		Menu subMenu3 = new Menu("功能3");
		Menu subMenu4 = new Menu("功能4");

		Menu menu1 = new Menu("门店管理", new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });
		menu1.setUgs(queryUserGroups(true));
		returnMenus.add(menu1);

		returnMenus.add(new Menu("系统管理", new Menu[] { subMenu, subMenu2,
				subMenu3, subMenu4 }));

		returnMenus.add(new Menu("货品管理", new Menu[] { subMenu, subMenu2,
				subMenu3, subMenu4 }));

		returnMenus.add(new Menu("店员管理", new Menu[] { subMenu, subMenu2,
				subMenu3, subMenu4 }));

		return returnMenus;
	}

	@Override
	public Map<String, Menu> getVisibleMenuMap() {
		Map<String, Menu> returnMenus = new HashMap<String, Menu>();

		Menu subMenu = new Menu("功能1");
		Menu subMenu2 = new Menu("功能2");
		Menu subMenu3 = new Menu("功能3");
		Menu subMenu4 = new Menu("功能4");

		Menu menu1 = new Menu("门店管理", new Menu[] { subMenu, subMenu2, subMenu3,
				subMenu4 });
		menu1.setUgs(queryUserGroups(true));

		returnMenus.put("门店管理", menu1);

		returnMenus.put("系统管理", new Menu("系统管理", new Menu[] { subMenu,
				subMenu2, subMenu3, subMenu4 }));

		returnMenus.put("货品管理", new Menu("货品管理", new Menu[] { subMenu,
				subMenu2, subMenu3, subMenu4 }));

		returnMenus.put("店员管理", new Menu("店员管理", new Menu[] { subMenu,
				subMenu2, subMenu3, subMenu4 }));

		return returnMenus;
	}

	@Override
	public List<Menu> echo(List<Menu> menus) {
		return menus;
	}

	@Override
	public Map<String, Menu> echo2(Map<String, Menu> menus) {
		return menus;
	}

	@Override
	public byte[] uploadSmallImage(byte[] image) {
		return image;
	}

	@Override
	public Byte[] uploadSmallImageByte(Byte[] image) {
		return image;
	}

	@Override
	public Person uploadPersonWithImage(Person person) {
		return person;
	}

	@Override
	public PersonExt getPersonExt() {
		PersonExt ext = new PersonExt();

		ext.setName("i am superman");

		return ext;
	}

	@Override
	public Person returnPojoNull() {
		return null;
	}

	@Override
	public String returnStringNull() {
		return null;
	}

	@Override
	public byte[] returnBytesNull() {
		return null;
	}
}