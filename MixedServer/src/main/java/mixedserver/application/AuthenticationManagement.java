package mixedserver.application;

import mixedserver.protocol.RPCException;

/**
 * 新版本的认证服务
 * 
 * @author zhangxiaohui
 * 
 */
public interface AuthenticationManagement {

	/**
	 * 登录，普通方式，是否记住我
	 * 
	 * @param logincode
	 * @param password
	 * @return 用户姓名
	 * @throws RPCException
	 */
	public AuthResult login(String logincode, String password,
			boolean rememberMe) throws RPCException;

	/**
	 * 登录， 带域的方式
	 * 
	 * @param domianid
	 * @param logincode
	 * @param password
	 * @return 用户姓名
	 * @throws RPCException
	 */
	public AuthResult login(String domianid, String logincode, String password,
			boolean rememberMe) throws RPCException;

	/**
	 * 注销，同时要注销掉token
	 * 
	 * @throws RPCException
	 */
	public void logout() throws RPCException;
}
