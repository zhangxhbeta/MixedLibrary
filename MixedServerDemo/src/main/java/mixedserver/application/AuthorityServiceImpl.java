package mixedserver.application;

import java.util.HashMap;
import java.util.Map;

import mixedserver.protocol.RPCException;
import mixedserver.protocol.SessionAttributes;
import mixedserver.protocol.SessionRelatedModul;

public class AuthorityServiceImpl implements AuthorityService,
		SessionRelatedModul {

	SessionAttributes session;

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
		// 删掉token

		// 会话属性干掉
		session.removeAttribute("userId");
	}

	@Override
	public void setSession(SessionAttributes session) {
		this.session = session;
	}

}
