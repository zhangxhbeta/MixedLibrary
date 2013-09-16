package mixedserver.protocol.jsonrpc.client;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

public class UriTest extends TestCase {

	public void testUriContext() {
		String address = "https://localhost:8080/JSON-RPC?debug=true";
		try {
			URI url = new URI(address);
			assertEquals("/JSON-RPC", url.getPath());

			address = "https://localhost:8080/somecontext/JSON-RPC?debug=true";
			url = new URI(address);

			assertEquals("/somecontext/JSON-RPC", url.getPath());

		} catch (URISyntaxException e) {
			fail("地址无法被识别");
		}
	}
}
