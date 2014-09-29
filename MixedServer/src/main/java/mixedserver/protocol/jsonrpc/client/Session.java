/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2009 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Client, a Java client extension to JSON-RPC-Java
 * (C) Copyright CodeBistro 2007, Sasha Ovsankin <sasha at codebistro dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package mixedserver.protocol.jsonrpc.client;

import mixedserver.protocol.RPCException;

/**
 * Transport session. May have state associated with it.
 */
public interface Session {
	/**
	 * 发送并接收json消息
	 * 
	 * @param message
	 *            A JSON message to send
	 * @return the JSON result message
	 * @throws RPCException
	 */
	String sendAndReceive(String message) throws RPCException;

	/**
	 * 获取会话id
	 * 
	 * @return
	 */
	String getSessionId();

	/**
	 * 获取会话标识符
	 * 
	 * @return
	 */
	String getSessionKey();

	/**
	 * 注册 cookie
	 * 
	 * @param name
	 * @param value
	 */
	void setCookie(String name, String value);

	/**
	 * 获取 cookie
	 * 
	 * @param name
	 * @param value
	 */
	String getCookie(String name);

	/**
	 * 设置会话参数
	 * 
	 * @param key
	 * @param value
	 */
	void setAttribute(String key, Object value);

	/**
	 * 删除会话参数
	 * 
	 * @param key
	 * @param value
	 */
	void removeAttribute(String key);

	/**
	 * 删除全部会话参数
	 * 
	 * @param key
	 * @param value
	 */
	void removeAllAttribute();

	/**
	 * 获取会话参数
	 * 
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);

	/**
	 * Close the session and release the resources if necessary
	 */
	void close();
}
