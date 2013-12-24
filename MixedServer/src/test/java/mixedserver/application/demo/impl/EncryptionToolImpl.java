package mixedserver.application.demo.impl;

import mixedserver.application.demo.EncryptionTool;
import mixedserver.protocol.RPCException;
import mixedserver.tools.EncrpytionTool;

import org.springframework.stereotype.Service;

@Service
public class EncryptionToolImpl implements EncryptionTool {

	@Override
	public String encrypt(String message) throws RPCException {
		return EncrpytionTool.encryptByBase64_3DES(message);
	}

	@Override
	public String dencrypt(String message) throws RPCException {
		return EncrpytionTool.dencryptFromBase64_3DES(message);
	}

}
