package com.saintangelo.application;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mixedserver.protocol.RPCException;

public interface TestJsonRpc {
	/**
	 * 普通原生数据类型
	 * 
	 * @param a
	 * @param b
	 */
	public float add(float a, float b);

	/**
	 * long
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public long addLong(long a, long b);

	/**
	 * int
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public int addInt(int a, int b);

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

	/**
	 * 二进制测试
	 * 
	 * @param image
	 * @return
	 */
	public byte[] uploadSmallImage(byte[] image);

	/**
	 * 二进制测试
	 * 
	 * @param image
	 * @return
	 */
	public Byte[] uploadSmallImageByte(Byte[] image);

	/**
	 * 二进制在类里面
	 * 
	 * @param person
	 * @return
	 */
	public Person uploadPersonWithImage(Person person);

	/**
	 * 测试子类
	 * 
	 * @return
	 */
	public Person getPersonExt();

	/**
	 * 测试返回null
	 * 
	 * @return
	 */
	public Person returnPojoNull();

	/**
	 * 测试返回null
	 * 
	 * @return
	 */
	public String returnStringNull();

	/**
	 * 测试返回null
	 * 
	 * @return
	 */
	public byte[] returnBytesNull();

	/**
	 * 测试返回日期类型
	 * 
	 * @return
	 */
	public Date returnDate(Date sendDate);

	/**
	 * 测试日期类型在对象里面
	 * 
	 * @return
	 */
	public Person returnPersonWithDate();

	/**
	 * 测试返回 pojo 范型
	 * 
	 * @return
	 */
	public GenericClassType<Person> returnGenericType();

	/**
	 * 测试返回 pojo 范型
	 * 
	 * @return
	 */
	public GenericClassType<List<Person>> returnNestCustomGenericType();

	/**
	 * 测试返回 List 嵌套范型 pojo 范型
	 * 
	 * @return
	 */
	public List<GenericClassType<Person>> returnNestListGenericType();

	/**
	 * 测试返回 List 嵌套范型 pojo 范型
	 * 
	 * @return
	 */
	public List<GenericClassType<List<Person>>> returnSuperNestGenericType();

	/**
	 * 测试空指针异常
	 * 
	 */
	public void nullPoint() throws RPCException;

	/**
	 * 测试运行时异常
	 */
	public void runtimeException() throws RPCException;

}
