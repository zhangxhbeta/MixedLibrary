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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import mixedserver.application.AuthorityService;
import mixedserver.protocol.RPCException;
import mixedserver.tools.EncrpytionTool;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory to create proxies for access to remote RPC services.
 * 
 * @author zhangxh
 */
public class Client implements InvocationHandler {

	private static Logger logger = LoggerFactory.getLogger(Client.class);

	// 会话里面保存的用户姓名 key
	public static final String SESSION_USERNAME = "_SESSION_USERNAME";
	// 会话里面保存的登录名 key
	public static final String SESSION_LOGINCODE = "_SESSION_LOGINCODE";
	// 会话里面保存的域代码 key
	public static final String SESSION_DOMAINCODE = "_SESSION_DOMAINCODE";

	private static ObjectMapper objectmapper = new ObjectMapper();
	private static Hashtable<String, Client> clients = new Hashtable<String, Client>();

	private Session session;

	private boolean encryptMessage = false;

	private boolean dencryptMessage = false;

	/**
	 * Maintain a unique id for each message
	 */
	private int id = 0;

	/**
	 * Create a client given a session
	 * 
	 * @param session
	 *            -- transport session to use for this connection
	 */
	protected Client(Session session) {
		this.session = session;

	}

	/**
	 * 获取url 关联的 Client 对象
	 * 
	 * @param url
	 * @return
	 */
	public synchronized static Client getClient(String url) {
		Client client = clients.get(url);

		if (client == null) {
			HTTPSession.register(TransportRegistry.i());
			Session session = TransportRegistry.i().createSession(url);

			client = new Client(session);
			clients.put(url, client);
		}

		return client;
	}

	/**
	 * 登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws RPCException
	 */
	public Map<String, String> login(String logincode, String password)
			throws RPCException {
		return login(null, logincode, password);
	}

	/**
	 * 登录
	 * 
	 * @param domainId
	 * @param username
	 * @param password
	 * @return
	 * @throws RPCException
	 */
	public Map<String, String> login(String domainId, String logincode,
			String password) throws RPCException {
		AuthorityService auth = openProxy("authority", AuthorityService.class);

		try {
			Map<String, String> result = null;
			if (domainId != null) {
				result = auth.login(domainId, logincode, password);
			} else {
				result = auth.login(logincode, password);
			}
			session.setAttribute(SESSION_USERNAME, result);
			session.setAttribute(SESSION_LOGINCODE, logincode);

			if (domainId != null) {
				session.setAttribute(SESSION_DOMAINCODE, domainId);
			}

			return result;
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

	/**
	 * 更改用户
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws RPCException
	 */
	public Map<String, String> changeUser(String username, String password)
			throws RPCException {
		return login(username, password);
	}

	public void registLongtimeToken(String token) {
		session.setCookie(AuthorityService.AS_LONGTIME_TOKEN, token);
	}

	/**
	 * 协议里面用到的id
	 * 
	 * @return
	 */
	private synchronized int getId() {
		return id++;
	}

	/** Manual instantiation of HashMap<String, Object> */
	private static class ProxyMap extends HashMap {
		public String getString(Object key) {
			return (String) super.get(key);
		}

		public Object putString(String key, Object value) {
			return super.put(key, value);
		}
	}

	private ProxyMap proxyMap = new ProxyMap();

	/**
	 * Create a proxy for communicating with the remote service.
	 * 
	 * @param key
	 *            the remote object key
	 * @param klass
	 *            the class of the interface the remote object should adhere to
	 * @return created proxy
	 */
	public <T> T openProxy(String key, Class<T> klass) {
		T result = (T) java.lang.reflect.Proxy.newProxyInstance(
				klass.getClassLoader(), new Class[] { klass }, this);
		proxyMap.put(result, key);
		return result;
	}

	/**
	 * Dispose of the proxy that is no longer needed
	 * 
	 * @param proxy
	 */
	public void closeProxy(Object proxy) {
		proxyMap.remove(proxy);
	}

	/**
	 * This method is public because of the inheritance from the
	 * InvokationHandler -- should never be called directly.
	 */
	public Object invoke(Object proxyObj, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if (methodName.equals("hashCode")) {
			return new Integer(System.identityHashCode(proxyObj));
		} else if (methodName.equals("equals")) {
			return (proxyObj == args[0] ? Boolean.TRUE : Boolean.FALSE);
		} else if (methodName.equals("toString")) {
			return proxyObj.getClass().getName() + '@'
					+ Integer.toHexString(proxyObj.hashCode());
		}

		boolean generic = false;
		Type[] actualTypes = null;
		Type type = method.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			generic = true;
			ParameterizedType paramType = (ParameterizedType) type;
			actualTypes = paramType.getActualTypeArguments();
		}

		return invoke(proxyMap.getString(proxyObj), method.getName(), args,
				method.getReturnType(), generic, actualTypes);
	}

	private Object invoke(String objectTag, String methodName, Object[] args,
			Class returnType, boolean generic, Type[] actualTypes)
			throws Throwable {
		final int id = getId();
		JSONObject sendJson = new JSONObject();

		sendJson.put("jsonrpc", "2.0");

		String methodTag = objectTag == null ? "" : objectTag + ".";
		methodTag += methodName;
		sendJson.put("method", methodTag);

		if (args != null && args.length != 0) {
			String jsonParams;
			jsonParams = objectmapper.writeValueAsString(args);
			sendJson.put("params", new JSONArray(jsonParams));
		} else {
			sendJson.put("params", new JSONArray());
		}
		sendJson.put("id", id);

		String sendMessage = sendJson.toString();
		// 打印发送消息
		if (logger.isDebugEnabled()) {
			logger.debug((encryptMessage ? "发送的消息（将加密）" : "发送的消息: ")
					+ sendMessage);
		}

		// 加密
		if (encryptMessage) {
			sendMessage = EncrpytionTool.encryptByBase64_3DES(sendMessage);
		}

		// 发送 and 接收
		String responseMessage = session.sendAndReceive(sendMessage);

		// 解密
		if (dencryptMessage) {
			responseMessage = EncrpytionTool
					.dencryptFromBase64_3DES(responseMessage);
		}

		// 打印接收消息
		if (logger.isDebugEnabled()) {
			logger.debug((dencryptMessage ? "收到的消息（已解密）：" : "收到的消息：")
					+ responseMessage);
		}

		JSONObject responseJson = null;

		try {
			Object response = null;
			JSONTokener tokener = new JSONTokener(responseMessage);
			response = tokener.nextValue();

			if (response == null || !(response instanceof JSONObject)) {
				throw new RPCException("非法响应的响应内容 -\n" + responseMessage);
			}

			responseJson = (JSONObject) response;
		} catch (JSONException e) {
			throw new RPCException("协议解析错误");
		}

		if (!responseJson.has("result")) {
			processException(responseJson);
		}
		Object rawResult = responseJson.get("result");
		if (rawResult == null) {
			processException(responseJson);
		}

		if (generic) {
			TypeFactory factory = objectmapper.getTypeFactory();
			if (returnType.equals(java.util.List.class)
					&& actualTypes.length == 1) {
				return objectmapper.readValue(rawResult.toString(), factory
						.constructCollectionType(returnType,
								(Class<?>) actualTypes[0]));
			} else if (returnType.equals(java.util.Map.class)
					&& actualTypes.length == 2) {
				return objectmapper.readValue(rawResult.toString(), factory
						.constructMapType(returnType,
								(Class<?>) actualTypes[0],
								(Class<?>) actualTypes[1]));
			} else {
				throw new RPCException("不支持的返回值泛型，目前仅支持 List、Map");
			}
		}

		if (returnType.equals(Void.TYPE)) {
			return null;
		} else if (returnType.equals(java.lang.Float.class)
				|| returnType.equals(float.class)) {
			return Float.parseFloat(rawResult.toString());
		} else if (returnType.equals(java.lang.Integer.class)
				|| returnType.equals(int.class)) {
			return Integer.parseInt(rawResult.toString());
		} else if (returnType.equals(java.lang.Double.class)
				|| returnType.equals(double.class)) {
			return Double.parseDouble(rawResult.toString());
		} else if (returnType.isEnum()) {
			return Enum.valueOf(returnType, rawResult.toString());
		} else if (returnType.isPrimitive()
				|| returnType.getName().equals("java.lang.String")) {

			return rawResult;
		} else {
			return objectmapper.readValue(rawResult.toString(), returnType);
		}
	}

	/**
	 * Generate and throw exception based on the data in the 'responseMessage'
	 * 
	 * @throws RPCException
	 */
	protected void processException(JSONObject responseMessage)
			throws JSONException, RPCException {
		JSONObject error = (JSONObject) responseMessage.get("error");
		if (error != null) {
			Integer code = new Integer(error.has("code") ? error.getInt("code")
					: 0);
			String trace = error.has("trace") ? error.getString("trace") : null;
			String msg = error.has("message") ? error.getString("message")
					: null;
			throw new RPCException(msg, code);
		} else
			throw new RPCException("其他错误:" + responseMessage.toString(2),
					new Integer(-32600));
	}

	public Session getSession() {
		return session;
	}

	public boolean isEncryptMessage() {
		return encryptMessage;
	}

	public void setEncryptMessage(boolean encryptMessage) {
		this.encryptMessage = encryptMessage;
	}

	public boolean isDencryptMessage() {
		return dencryptMessage;
	}

	public void setDencryptMessage(boolean dencryptMessage) {
		this.dencryptMessage = dencryptMessage;
	}

}
