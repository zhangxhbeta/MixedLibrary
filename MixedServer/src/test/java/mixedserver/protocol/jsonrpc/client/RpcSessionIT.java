package mixedserver.protocol.jsonrpc.client;

import junit.framework.TestCase;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.HTTPSession;
import mixedserver.protocol.jsonrpc.client.Session;
import mixedserver.protocol.jsonrpc.client.TransportRegistry;
import mixedserver.tools.EncrpytionTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RpcSessionIT extends TestCase {

	private Session session;

	@Override
	protected void setUp() throws Exception {
		HTTPSession.register(TransportRegistry.i());

		session = TransportRegistry.i().createSession(
				"http://localhost:8080/JSON-RPC");
	}

	@Override
	protected void tearDown() {

	}

	/**
	 * @param args
	 */
	public void testEchoSay() throws Exception {

		// 调用 Echo.say(String what)
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("method", "echo.say");
		jsonRequest.put("jsonrpc", "2.0");

		JSONArray params = new JSONArray(new Object[] { "你好，世界" });
		jsonRequest.put("params", params);

		String responseMessage = session.sendAndReceive(EncrpytionTool
				.encryptByBase64_3DES(jsonRequest.toString()));
		responseMessage = EncrpytionTool
				.dencryptFromBase64_3DES(responseMessage);

		JSONObject jsonResponse = null;
		try {
			Object response = null;
			JSONTokener tokener = new JSONTokener(responseMessage);
			response = tokener.nextValue();

			if (response == null || !(response instanceof JSONObject)) {
				throw new RPCException("非法响应的响应内容 -\n" + responseMessage);
			}

			jsonResponse = (JSONObject) response;
		} catch (JSONException e) {
			throw new RPCException("协议解析错误");
		}

		assertNotNull(jsonResponse);

		assertTrue(jsonResponse.has("jsonrpc"));
		assertEquals("2.0", jsonResponse.getString("jsonrpc"));

		assertFalse(jsonResponse.has("error"));
		assertTrue(jsonResponse.has("result"));

		assertEquals("你好，世界", jsonResponse.getString("result"));
	}

}
