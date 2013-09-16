package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.Menu;
import com.saintangelo.application.TestJsonRpc;

public class ReturnPOJOIT extends AbstractedRPC {
	public void testOpenProxy() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		Menu[] menus = jrpc.getVisible();

		assertEquals(4, menus.length);

		assertEquals(4, menus[0].getSubMenus().length);

		assertEquals("功能1", menus[0].getSubMenus()[0].getMenuname());

	}
}
