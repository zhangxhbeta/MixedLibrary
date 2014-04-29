package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.GenericClassType;
import com.saintangelo.application.Person;
import com.saintangelo.application.TestJsonRpc;

public class ReturnCustomGenericIT extends AbstractedRPC {
	public void testJsonRpc() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		GenericClassType<Person> g = jrpc.returnGenericType();

		assertEquals("Test", g.getName());

		assertNotNull(g.getPersons());

		assertEquals(1, g.getPersons().size());

		/*
		 * List<GenericClassType<Person>> superL = jrpc
		 * .returnNestListGenericType();
		 * 
		 * assertNotNull(superL);
		 * 
		 * assertEquals(3, superL.size());
		 */

		/*
		 * GenericClassType<List<Person>> superG = jrpc
		 * .returnNestCustomGenericType();
		 * 
		 * assertEquals("Test", superG.getName());
		 * 
		 * assertNotNull(superG.getPersons());
		 * 
		 * assertEquals(1, superG.getPersons().size());
		 * 
		 * assertNotNull(superG.getPersons().get(0));
		 * 
		 * assertEquals(1, superG.getPersons().get(0).size());
		 */
	}
}
