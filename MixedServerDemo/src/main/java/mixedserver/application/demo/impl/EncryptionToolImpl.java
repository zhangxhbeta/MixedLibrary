package mixedserver.application.demo.impl;

import mixedserver.application.demo.EncryptionTool;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.SessionAttributes;
import mixedserver.protocol.SessionRelatedModul;
import mixedserver.tools.EncrpytionTool;

public class EncryptionToolImpl implements EncryptionTool, SessionRelatedModul {
	private SessionAttributes session;

	@Override
	public String encrypt(String message) throws RPCException {
		if (session.getAttribute("userId") == null) {
			throw new RPCException("非法的访问",
					RPCException.ERROR_CODE_INVALID_SESSION);
		}
		return EncrpytionTool.encryptByBase64_3DES(message);
	}

	@Override
	public String dencrypt(String message) throws RPCException {
		if (session.getAttribute("userId") == null) {
			throw new RPCException("非法的访问",
					RPCException.ERROR_CODE_INVALID_SESSION);
		}
		return EncrpytionTool.dencryptFromBase64_3DES(message);
	}

	@Override
	public void setSession(SessionAttributes session) {
		this.session = session;
	}

}
