package mixedserver.protocol.jsonrpc.client;

import mixedserver.protocol.RPCException;

import com.saintangelo.application.TestJsonRpc;

public class RuntimeExceptionIT extends AbstractedRPC {

	public void testRuntimeException() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		try {
			jrpc.nullPoint();
		} catch (RPCException e) {
			System.out.println(e);
		}

		try {
			jrpc.runtimeException();
		} catch (RPCException e) {
			System.out.println(e);
		}
	}
}
