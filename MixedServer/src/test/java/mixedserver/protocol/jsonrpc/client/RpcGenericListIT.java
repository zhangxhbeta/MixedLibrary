package mixedserver.protocol.jsonrpc.client;

import java.util.List;

import com.saintangelo.application.TestJsonRpc;
import com.saintangelo.application.UserGroup;

public class RpcGenericListIT extends AbstractedRPC {

	public void testJsonRpc() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		List<UserGroup> usergroups = jrpc.queryUserGroups(true);

		assertEquals(2, usergroups.size());

		assertEquals("信息部-开发组", usergroups.get(0).getName());
		assertEquals("信息部-实施组", usergroups.get(1).getName());
	}
}
