package mixedserver.protocol.jsonrpc.client;

import java.util.List;
import java.util.Map;

import com.saintangelo.application.Menu;
import com.saintangelo.application.TestJsonRpc;
import com.saintangelo.application.UserGroup;

public class ReturnListAndMapIT extends AbstractedRPC {
	public void testOpenProxy() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		List<Menu> menus = jrpc.getVisibleMenuList();

		menus = jrpc.echo(menus);

		assertEquals(4, menus.size());

		assertEquals(4, menus.get(0).getSubMenus().length);

		Menu menu = menus.get(0);
		List<UserGroup> ugs = menu.getUgs();

		assertEquals(2, ugs.size());
		assertEquals("信息部-开发组", ugs.get(0).getName());

		assertEquals("功能1", menus.get(0).getSubMenus()[0].getMenuname());

		Map<String, Menu> mapMenus = jrpc.getVisibleMenuMap();

		mapMenus = jrpc.echo2(mapMenus);

		assertEquals(4, mapMenus.size());

		assertEquals(4, mapMenus.get("门店管理").getSubMenus().length);

		menu = mapMenus.get("门店管理");
		ugs = menu.getUgs();

		assertEquals(2, ugs.size());
		assertEquals("信息部-开发组", ugs.get(0).getName());

		assertEquals("功能1", mapMenus.get("门店管理").getSubMenus()[0].getMenuname());
	}
}
