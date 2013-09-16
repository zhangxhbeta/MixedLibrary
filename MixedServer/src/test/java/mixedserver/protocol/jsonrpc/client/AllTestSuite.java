package mixedserver.protocol.jsonrpc.client;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTestSuite.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(ClientIT.class);
		suite.addTestSuite(ReturnListAndMapIT.class);
		suite.addTestSuite(ReturnPOJOIT.class);
		suite.addTestSuite(RpcComplexIT.class);
		suite.addTestSuite(RpcGenericListIT.class);
		suite.addTestSuite(RpcSessionIT.class);

		suite.addTestSuite(BatchRpcIT.class);
		suite.addTestSuite(GzipCompressIT.class);
		// $JUnit-END$
		return suite;
	}

}
