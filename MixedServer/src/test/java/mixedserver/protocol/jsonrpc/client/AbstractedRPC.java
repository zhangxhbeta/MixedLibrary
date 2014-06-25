package mixedserver.protocol.jsonrpc.client;

import mixedserver.application.SimpleAuthResult;
import junit.framework.TestCase;

public class AbstractedRPC extends TestCase {

	protected Session session;

	protected Client client;

	@Override
	protected void setUp() throws Exception {
		HTTPSession.register(TransportRegistry.i());

		session = TransportRegistry.i().createSession(
				"http://localhost:8080/JSON-RPC");

		client = new Client(session);

		client.setDencryptMessage(true);
		client.setEncryptMessage(false);

		SimpleAuthResult result = client.login2("", "", false);

		assertEquals("托尔斯泰", result.getUsername());

		String userId = result.getInfo("userId");
		assertEquals("1", userId);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
