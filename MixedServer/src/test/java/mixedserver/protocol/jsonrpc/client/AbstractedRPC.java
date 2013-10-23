package mixedserver.protocol.jsonrpc.client;

import java.util.Map;

import junit.framework.TestCase;
import mixedserver.application.AuthorityService;

public class AbstractedRPC extends TestCase {

	protected Session session;

	protected Client client;

	@Override
	protected void setUp() throws Exception {
		HTTPSession.register(TransportRegistry.i());

		session = TransportRegistry.i().createSession(
				"http://localhost:8080/JSON-RPC");

		client = new Client(session);

		client.setDencryptMessage(false);
		client.setEncryptMessage(false);

		Map<String, String> au = client.login("SA", "");

		if (au.containsKey(AuthorityService.AS_LONGTIME_TOKEN)) {
			client.registLongtimeToken(au
					.get(AuthorityService.AS_LONGTIME_TOKEN));
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
