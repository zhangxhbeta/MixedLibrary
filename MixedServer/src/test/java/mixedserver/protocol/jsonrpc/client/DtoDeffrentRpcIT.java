package mixedserver.protocol.jsonrpc.client;

import junit.framework.TestCase;
import mixedserver.tools.EncrpytionTool;

public class DtoDeffrentRpcIT extends TestCase {

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

		String message = "{\"id\":10,\"method\":\"testJsonRpc.queryUserGroup\",\"params\":[{\"id\":1,\"phone\":\"18611112222\",\"email\":\"superadmin@163.com\",\"name\":\"jack\",\"other\":\"foobar\"}],\"jsonrpc\":\"2.0\"}";

		String responseMessage = session.sendAndReceive(message);

		responseMessage = EncrpytionTool
				.dencryptFromBase64_3DES(responseMessage);

	}

}
