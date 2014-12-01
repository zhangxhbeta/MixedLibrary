package mixedserver.protocol.jsonrpc.mockclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mixedserver.application.AuthenticationManagement;
import mixedserver.application.AuthorityService;
import mixedserver.application.SimpleAuthResult;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;
import mixedserver.protocol.jsonrpc.client.Session;

public class MockClient extends Client {

	protected MockClient(Session session) {
		super(session);
	}

	public static MockClient client;

	public static Session session = new MockSession();

	private static HashMap<String, Class> mockClassMap;

	public static MockClient getClient() {
		if (client == null) {
			client = new MockClient(null);
		}
		return client;
	}

	@Override
	public Session getSession() {
		return session;
	}

	public void setMockClassMap(HashMap<String, Class> hashmap) {
		this.mockClassMap = hashmap;
	}

	@Override
	public Object openProxy(String key, Class klass) {
		Class cls = mockClassMap.get(key);
		try {
			return cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> login(String domainId, String logincode,
			String password) {
		session.setAttribute(Client.SESSION_DOMAINCODE, domainId);
		session.setAttribute(Client.SESSION_LOGINCODE, logincode);
		session.setAttribute(Client.SESSION_USERNAME, "管理员");

		HashMap<String, String> result = new HashMap<String, String>();
		result.put(AuthorityService.AS_USER_NAME, "管理员");
		return result;
	}

	@Override
	public Map<String, String> login(String logincode, String password) {
		return login(null, logincode, password);
	}

	@Override
	public SimpleAuthResult login2(String domainId, String logincode,
			String password, boolean rememberMe) throws RPCException {
		session.removeAllAttribute();

		AuthenticationManagement auth = (AuthenticationManagement) openProxy(
				loginRpcName, AuthenticationManagement.class);
		try {
			SimpleAuthResult result = auth.login(logincode, password,
					rememberMe);
			if (result.getUsername() != null) {
				session.setAttribute(SESSION_USERNAME, result.getUsername());
			}

			if (result.getAllInfo() != null) {
				Set<String> allInfoKey = result.getAllInfo().keySet();
				for (String key : allInfoKey) {
					session.setAttribute(key, result.getInfo(key));
				}
			}

			session.setAttribute(SESSION_LOGINCODE, logincode);

			if (domainId != null) {
				session.setAttribute(SESSION_DOMAINCODE, domainId);
			}

			return result;
		} finally {
			closeProxy(auth);
		}
	}

	@Override
	public SimpleAuthResult login2(String logincode, String password,
			boolean rememberMe) throws RPCException {
		return login2("", logincode, password, rememberMe);
	}

	@Override
	public void logout() throws RPCException {
		AuthenticationManagement auth = (AuthenticationManagement) openProxy(
				loginRpcName, AuthenticationManagement.class);

		try {
			auth.logout();
			session.removeAllAttribute();

		} finally {
			closeProxy(auth);
		}
	}

	/**
	 * 是否已登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean isLogin() {
		return session.getAttribute(SESSION_USERNAME) != null;
	}

	/**
	 * 返回登录代码（登录时填写的）
	 * 
	 * @return
	 */
	public String getLoginCode() {
		return (String) session.getAttribute(SESSION_LOGINCODE);
	}

	/**
	 * 返回域代码（登录时填写的）
	 * 
	 * @return
	 */
	public String getDomainCode() {
		return (String) session.getAttribute(SESSION_DOMAINCODE);
	}
}
