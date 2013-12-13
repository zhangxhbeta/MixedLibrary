package com.saintangelo.application.impl;

import java.util.HashMap;
import java.util.Map;

import mixedserver.application.AuthorityService;
import mixedserver.protocol.RPCException;

public class AuthorityServiceImpl implements AuthorityService {

	@Override
	public Map<String, String> login(String logincode, String password)
			throws RPCException {
		return login(null, logincode, password);
	}

	@Override
	public Map<String, String> login(String domianid, String logincode,
			String password) throws RPCException {
		Map<String, String> result = new HashMap<String, String>();

		result.put(AuthorityService.AS_USER_NAME, "托尔斯泰");

		result.put(AuthorityService.AS_LONGTIME_TOKEN, "8309-2342-3976-3413");
		return result;
	}

	@Override
	public void logout() throws RPCException {

	}

}
