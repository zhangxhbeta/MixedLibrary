package mixedserver.protocol.jsonrpc.client;

import junit.framework.TestCase;
import mixedserver.protocol.RPCException;
import mixedserver.tools.EncrpytionTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BatchRpcIT extends TestCase {

	private Session session;

	@Override
	protected void setUp() throws Exception {
		HTTPSession.register(TransportRegistry.i());

		session = TransportRegistry.i().createSession(
				"http://localhost:8080/JSON-RPC?debug=true");
	}

	@Override
	protected void tearDown() {

	}

	/**
	 * @param args
	 */
	public void testEchoSay() throws Exception {

		int testBatchNum = 1000;

		JSONArray jsonRequestBatch = new JSONArray();

		JSONObject jsonRequest;
		for (int i = 0; i < testBatchNum; i++) {
			jsonRequest = new JSONObject();

			jsonRequest.put("id", i);
			jsonRequest.put("method", "echo.say");
			jsonRequest.put("jsonrpc", "2.0");

			JSONArray params = new JSONArray(new Object[] { String.format(
					"你好, The %d", i) });
			jsonRequest.put("params", params);

			jsonRequestBatch.put(jsonRequest);
		}

		JSONArray jsonResponseBatch = null;

		String responseMessage = session.sendAndReceive(jsonRequestBatch
				.toString());

		responseMessage = EncrpytionTool
				.dencryptFromBase64_3DES(responseMessage);

		try {
			Object response = null;
			JSONTokener tokener = new JSONTokener(responseMessage);
			response = tokener.nextValue();

			if (response == null || !(response instanceof JSONArray)) {
				fail("非法响应的响应内容 -\n" + responseMessage);
			}

			jsonResponseBatch = (JSONArray) response;
		} catch (JSONException e) {
			fail("协议解析错误");
		}

		assertNotNull(jsonResponseBatch);

		assertTrue(jsonRequestBatch.length() == testBatchNum);

		JSONObject jsonResponse = (JSONObject) jsonResponseBatch.get(0);

		assertNotNull(jsonResponse);

		assertTrue(jsonResponse.has("jsonrpc"));
		assertEquals("2.0", jsonResponse.getString("jsonrpc"));

		assertFalse(jsonResponse.has("error"));
		assertTrue(jsonResponse.has("result"));

		assertEquals("你好, The 0", jsonResponse.getString("result"));
	}

}
