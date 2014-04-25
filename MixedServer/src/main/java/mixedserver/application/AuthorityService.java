package mixedserver.application;

import java.util.Map;

import mixedserver.protocol.RPCException;

/**
 * 请使用新版本的认证服务 AuthenticationManagement 接口
 * 
 * @deprecated use {@link AuthenticationManagement} instead.
 * @author zhangxiaohui
 * 
 */
@Deprecated
public interface AuthorityService {
	// 用户名
	public static final String AS_USER_NAME = "AS_USER_NAME";

	// 用户长期会话令牌，免去每次登录
	public static final String AS_LONGTIME_TOKEN = "AS_LONGTIME_TOKEN";

	/**
	 * 登录，普通方式
	 * 
	 * @param logincode
	 * @param password
	 * @return 用户姓名
	 * @throws RPCException
	 */
	public Map<String, String> login(String logincode, String password)
			throws RPCException;

	/**
	 * 登录， 带域的方式
	 * 
	 * @param domianid
	 * @param logincode
	 * @param password
	 * @return 用户姓名
	 * @throws RPCException
	 */
	public Map<String, String> login(String domianid, String logincode,
			String password) throws RPCException;

	/**
	 * 注销，同时要注销掉token
	 * 
	 * @throws RPCException
	 */
	public void logout() throws RPCException;

}