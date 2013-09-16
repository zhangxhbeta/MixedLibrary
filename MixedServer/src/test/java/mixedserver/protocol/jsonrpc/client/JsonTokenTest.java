package mixedserver.protocol.jsonrpc.client;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonTokenTest extends TestCase {

	public void testUriContext() {
		String json = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html><head></head><body>与远端服务器建立连接失败</body></html>";
		Object responseMessage = null;

		JSONTokener tokener = new JSONTokener(json);
		try {
			responseMessage = tokener.nextValue();
		} catch (JSONException e) {
			fail("json 解析失败");
		}

		assertNotNull(responseMessage);
		assertTrue(!(responseMessage instanceof JSONObject));

		assertNotNull(responseMessage);
		assertTrue(!(responseMessage instanceof JSONObject));

		json = "[]";
		tokener = new JSONTokener(json);
		try {
			responseMessage = tokener.nextValue();
		} catch (JSONException e) {
			fail("json 解析失败");
		}

		assertNotNull(responseMessage);
		assertTrue(!(responseMessage instanceof JSONObject));

	}

}
