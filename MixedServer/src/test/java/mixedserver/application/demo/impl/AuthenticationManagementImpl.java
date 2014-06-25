package mixedserver.application.demo.impl;

import mixedserver.application.AuthenticationManagement;
import mixedserver.application.SimpleAuthResult;
import mixedserver.protocol.RPCException;

public class AuthenticationManagementImpl implements AuthenticationManagement {

	@Override
	public SimpleAuthResult login(String logincode, String password,
			boolean rememberMe) throws RPCException {
		return login("-1", logincode, password, false);
	}

	@Override
	public SimpleAuthResult login(String domianid, String logincode,
			String password, boolean rememberMe) throws RPCException {

		SimpleAuthResult result = new SimpleAuthResult();
		result.setUsername("托尔斯泰");
		result.setLongtimeToken("8309-2342-3976-3413");

		result.addInfo("userId", "1");

		return result;
	}

	@Override
	public void logout() throws RPCException {

	}

	@Override
	public SimpleAuthResult getRememberdUser() throws RPCException {
		SimpleAuthResult result = new SimpleAuthResult();
		result.setUsername("托尔斯泰");
		result.setLongtimeToken("8309-2342-3976-3413");

		return result;
	}

}
