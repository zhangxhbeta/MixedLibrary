package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.Echo;

public class GzipCompressIT extends AbstractedRPC {

	public void testOpenProxy() {
		Echo echo = client.openProxy("echo", Echo.class);

		String message = "vNmC8stso0eg+rUAEPrlYvWFY7b0ga3tbEb61zhhwlaIk88n523lNCzGq7bL";
		String ret = echo.say(message);

		assertEquals(message, ret);
	}
}
