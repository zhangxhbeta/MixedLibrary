package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.Echo;

public class ClientIT extends AbstractedRPC {

	public void testOpenProxy() {
		Echo echo = client.openProxy("echo", Echo.class);

		String ret = echo.say("你好");
		assertEquals("你好", ret);

		String anthorMessage = "今天我心情不好，我就讲四句话，包括这句和前面的两句，我的话讲完了";
		String result = echo.say(anthorMessage);
		assertEquals(anthorMessage, result);
	}
}
