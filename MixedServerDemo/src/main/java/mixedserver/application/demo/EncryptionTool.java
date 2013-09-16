package mixedserver.application.demo;

import mixedserver.protocol.RPCException;

public interface EncryptionTool {
	/**
	 * 对消息进行加密并返回
	 * 
	 * @param message
	 * @return
	 */
	public String encrypt(String message) throws RPCException;

	/**
	 * 对消息进行解密并返回
	 * 
	 * @param message
	 * @return
	 */
	public String dencrypt(String message) throws RPCException;
}
