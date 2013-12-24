package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.TestJsonRpc;
import com.saintangelo.application.User;
import com.saintangelo.application.UserGroup;

public class RpcComplexIT extends AbstractedRPC {
	public void testJsonRpc() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		User user2 = new User();
		user2.setId(1);
		user2.setEmail("superadmin@163.com");
		user2.setName("jack");
		user2.setPhone("18611112222");
		UserGroup ug2 = jrpc.queryUserGroup(user2, false);

		assertEquals("信息部-开发组false", ug2.getName());

		boolean has = jrpc.hasComma2(new String[] { "hello world",
				"hello foobar" });
		assertFalse(has);

		float a = 1.0f, b = 1.0f;
		float c = jrpc.add(a, b);
		assertTrue(c == a + b);

		int r = jrpc.calcCommaNum("hello, world!");
		assertEquals(1, r);

		String h = jrpc.concat("", "");
		assertEquals("", h);

		h = jrpc.concat("a", "");
		assertEquals("a", h);

		h = jrpc.concat("", "b");
		assertEquals("b", h);

		h = jrpc.concat("a", "b");
		assertEquals("ab", h);

		has = jrpc.hasComma("hello, world, and hello, foobar");
		assertTrue(has);

		User user = new User();
		user.setId(1);
		user.setEmail("superadmin@163.com");
		user.setName("jack");
		user.setPhone("18611112222");
		UserGroup ug = jrpc.queryUserGroup(user);

		assertEquals("jack所在的部门", ug.getName());

		assertEquals(2, jrpc.addInt(1, 1));

		long x = jrpc.addLong(1, 1);
		assertEquals(2L, x);
	}
}
