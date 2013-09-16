package com.saintangelo.application;

import java.util.List;
import java.util.Map;

public interface TestJsonRpc {
	/**
	 * 普通原生数据类型
	 * 
	 * @param a
	 * @param b
	 */
	public float add(float a, float b);

	/**
	 * 字符串类型
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public String concat(String a, String b);

	/**
	 * 计算有几个逗号
	 * 
	 * @param s
	 * @return
	 */
	public int calcCommaNum(String s);

	/**
	 * 有逗号吗？
	 * 
	 * @param s
	 * @return
	 */
	public boolean hasComma(String s);

	/**
	 * 有逗号吗？
	 * 
	 * @param s
	 * @return
	 */
	public Boolean hasComma2(String[] s);

	/**
	 * 查询用户所在用户组
	 * 
	 * @param user
	 * @return
	 */
	public UserGroup queryUserGroup(User user);

	/**
	 * 查询用户所在用户组
	 * 
	 * @param user
	 * @return
	 */
	public UserGroup queryUserGroup(User user, boolean parttime);

	/**
	 * 查询用户所在用户组
	 * 
	 * @param user
	 * @return
	 */
	public List<UserGroup> queryUserGroups(boolean all);

	/**
	 * 查询数据对象
	 * 
	 * @param user
	 * @return
	 */
	public Menu[] getVisible();

	/**
	 * 查询list对象
	 * 
	 * @param user
	 * @return
	 */
	public List<Menu> getVisibleMenuList();

	/**
	 * 查询map对象
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, UserGroup> getUserGroupMap();

	/**
	 * 查询map对象
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, Menu> getVisibleMenuMap();

	/**
	 * 参数是list
	 * 
	 * @param ugs
	 * @return
	 */
	public List<Menu> echo(List<Menu> menus);

	/**
	 * 参数是map
	 * 
	 * @param ugs
	 * @return
	 */
	public Map<String, Menu> echo2(Map<String, Menu> menus);
}
