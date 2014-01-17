package mixedserver.protocol.jsonrpc.client;

import java.util.Calendar;
import java.util.Date;

import com.saintangelo.application.Menu;
import com.saintangelo.application.Person;
import com.saintangelo.application.TestJsonRpc;

public class ReturnPOJOIT extends AbstractedRPC {
	public void testOpenProxy() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		Menu[] menus = jrpc.getVisible();

		assertEquals(4, menus.length);

		assertEquals(4, menus[0].getSubMenus().length);

		assertEquals("功能1", menus[0].getSubMenus()[0].getMenuname());

		// Person extPerson = jrpc.getPersonExt();
		// assertNull(extPerson.getImage());

		byte[] b = jrpc.returnBytesNull();
		assertNull(b);

		Person p = jrpc.returnPojoNull();
		assertNull(p);

		String s = jrpc.returnStringNull();
		assertNull(s);

		p = jrpc.returnPersonWithDate();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int compare = p.getDate().compareTo(cal.getTime());

		assertEquals(0, compare);

	}
}
