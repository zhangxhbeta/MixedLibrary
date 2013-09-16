package mixedserver.application.demo;

import mixedserver.protocol.RPCException;

/**
 * 服务端接口，一般会打包放到客户端 这里为了演示，直接拖过来了
 * 
 * @author zhangxiaohui
 * 
 */
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
