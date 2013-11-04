package mixedserver.protocol.jsonrpc.mockclient;

import java.util.HashMap;

import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Session;

public class MockSession implements Session {

	HashMap<String, Object> map = new HashMap<String, Object>();

	@Override
	public void close() {

	}

	@Override
	public Object getAttribute(String key) {
		return map.get(key);
	}

	@Override
	public String getSessionId() {
		return "abcdefg";
	}

	@Override
	public String getSessionKey() {
		return "jsessionid";
	}

	@Override
	public void removeAllAttribute() {

	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public String sendAndReceive(String arg0) throws RPCException {
		return null;
	}

	@Override
	public void setAttribute(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public void setCookie(String name, String value) {
		// TODO Auto-generated method stub
	}
}
